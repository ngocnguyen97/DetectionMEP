package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class Main {
	public static BufferedImage bitmap = new BufferedImage((int) (Config.W + 40), (int) (Config.H + 40),
			BufferedImage.TYPE_INT_ARGB);

	public static void main(String[] args) {
		String type = args[0];
		// for (int k = 0; k < 1; k++) {
		// String type = k == 0 ? "e" : k == 1 ? "g" : "r";
		File f = new File("data/data" + type);
		// File f = new File("data/datatest");
		for (int idf = 0; idf < f.listFiles().length; ++idf) {
			String input = "data/data" + type + "/" + f.list()[idf];
			// String input = "data/datatest/" + f.list()[idf];
			System.out.println("Processing: " + input);

			Topo topo = new Topo();
			topo.construct(input);

			double averageTime = 0;
			double averageBest = 0;
			double best = Double.POSITIVE_INFINITY;
			int bestRun = 0;
			for (int ir = 0; ir < Config.NUM_RUN; ++ir) {
				System.out.println("lan chay: " + ir);
				Population p = new Population();
				String convergence = "Convergence\n";
				double t1 = System.currentTimeMillis();
				p.initializeRandomStart();
				drawPop(p, topo);
				p.calFitness(topo);
				p.sort();

				Individual bestIndividual = p.getIndividualByIndex(0);
				convergence += bestIndividual.getFitness() + "\n";
				int noBetter = 0;
				for (int i = 0; i < Config.GENERATION_NUM; ++i) {
					try {
						Population offs = p.evolve(topo);
						offs.sort();
						p = p.mergeSort(offs);
						if (p.getIndividualByIndex(0).getFitness() < bestIndividual.getFitness()) {
							bestIndividual = p.getIndividualByIndex(0);
							noBetter = 0;
						} else {
							++noBetter;
						}
						if (Double.compare(bestIndividual.getFitness(), 0.0) == 0) {
							System.out.println("Found Optimal solution");
							break;
						}
						convergence += bestIndividual.getFitness() + "\n";
						System.out.println(bestIndividual.getFitness());
						// draw(bestIndividual, topo, input + "vdd.png", i);
					} catch (CloneNotSupportedException e) {
					}
				}
				double t2 = System.currentTimeMillis();
				averageTime += (t2 - t1);
				double bestValue = calculate(convergence, bestIndividual, topo, input, ir);
				if (bestValue < best) {
					best = bestValue;
					bestRun = ir;
				}
				averageBest += bestValue;
			}
			String sumary = "Average time: " + averageTime / (Config.NUM_RUN * 1000) + "\n";
			sumary += "Average mep: " + averageBest / Config.NUM_RUN + "\n";
			sumary += "Best value: " + best + "\n";
			sumary += "Best run: " + bestRun + "\n";
			// sumary += calDijkstra(topo, input) + "\n";
			writeSummary(sumary, input);
		}
		// }
	}

	public static void drawLine(Graphics g, int a, int b, int c, int d) {
		g.drawLine(a + 20, b + 20, c + 20, d + 20);
	}

	public static void draw(Individual indi, Topo topo, String filename, int ir) {
		filename = filename.replaceFirst("data/data", "result/result_");
		filename = filename.replaceFirst("\\.", "/lan_" + ir + "\\.");
		Graphics g = bitmap.getGraphics();
		int W = (int) Config.W;
		int H = (int) Config.H;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, W + 40, H + 40);

		g.setColor(Color.BLUE);
		for (int i = 0; i < topo.getNumNode(); i++) {
			Point p = topo.getSensorByIndex(i);
			g.fillArc((int) ((p.getX() - Config.Rs) + 20), (int) ((H - p.getY() - Config.Rs) + 20),
					(int) (2 * Config.Rs), (int) (2 * Config.Rs), -180, 360);
		}

		g.setColor(Color.MAGENTA);
		for (int i = 0; i < topo.getNumNode(); i++) {
			Point p = topo.getSensorByIndex(i);
			g.fillArc((int) ((p.getX() - 3) + 20), (int) ((H - p.getY() - 3) + 20), (int) (2 * 3), (int) (2 * 3), -180,
					360);
		}

		// ve toa do
		g.setColor(Color.MAGENTA);
		for (int i = 0; i <= H / 100; i++)
			g.drawString(String.valueOf(i * 100), 20, (H - i * 100) + 20);
		for (int i = 1; i <= W / 100; i++)
			g.drawString(String.valueOf(i * 100), i * 100 + 20, H + 20);
		// ve bounds
		g.setColor(Color.BLACK);
		drawLine(g, 0, 0, 0, H);
		drawLine(g, W, 0, W, H);
		drawLine(g, 0, 0, W, 0);
		drawLine(g, 0, W, W, W);
		// Ve ketqua
		g.setColor(Color.RED);
		Iterator<Point> iter = indi.getListGene().iterator();
		Point po = iter.next();
		while (iter.hasNext()) {
			Point p1 = iter.next();
			drawLine(g, (int) (po.getX()), (int) (H - po.getY()), (int) (p1.getX()), (int) (H - p1.getY()));
			po = p1;
		}
		// Ve minExp
		g.setColor(Color.BLACK);
		g.drawString("MinE: " + indi.getFitness(), 20, H + 35);
		if (filename != null) {
			try {
				ImageIO.write(bitmap, "png", new FileOutputStream(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void drawPop(Population pop, Topo topo) {
		String filename = "result//pop.png";
		Graphics g = bitmap.getGraphics();
		int W = (int) Config.W;
		int H = (int) Config.H;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, W + 40, H + 40);

		g.setColor(Color.BLUE);
		for (int i = 0; i < topo.getNumNode(); i++) {
			Point p = topo.getSensorByIndex(i);
			g.fillArc((int) ((p.getX() - Config.Rs) + 20), (int) ((H - p.getY() - Config.Rs) + 20),
					(int) (2 * Config.Rs), (int) (2 * Config.Rs), -180, 360);
		}

		// ve toa do
		g.setColor(Color.MAGENTA);
		for (int i = 0; i <= H / 100; i++)
			g.drawString(String.valueOf(i * 100), 20, (H - i * 100) + 20);
		for (int i = 1; i <= W / 100; i++)
			g.drawString(String.valueOf(i * 100), i * 100 + 20, H + 20);
		// ve bounds
		g.setColor(Color.BLACK);
		drawLine(g, 0, 0, 0, H);
		drawLine(g, W, 0, W, H);
		drawLine(g, 0, 0, W, 0);
		drawLine(g, 0, W, W, W);
		// Ve ketqua
		g.setColor(Color.RED);
		for (int i = 0; i < pop.getSize(); i++) {
			Individual indi = pop.getIndividualByIndex(i);
			Iterator<Point> iter = indi.getListGene().iterator();
			Point po = iter.next();
			while (iter.hasNext()) {
				Point p1 = iter.next();
				drawLine(g, (int) (po.getX()), (int) (H - po.getY()), (int) (p1.getX()), (int) (H - p1.getY()));
				po = p1;
			}
		}
		if (filename != null) {
			try {
				ImageIO.write(bitmap, "png", new FileOutputStream(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeSummary(String sum, String filename) {
		filename = filename.replaceFirst("data/data", "result/result_");
		filename = filename.replaceFirst("\\.", "/kq_summary\\.");
		File f1 = new File(filename).getParentFile();
		if (!f1.exists())
			f1.mkdirs();
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);

			bw.write(sum);
			bw.flush();
		} catch (Exception ex) {
			System.out.println("Write error 2");
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void writeResult(Population p, String filename) {
		filename = filename.replaceFirst("data/data", "result/result_");
		File f1 = new File(filename).getParentFile();
		if (!f1.exists())
			f1.mkdirs();
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			for (int i = 0; i < p.getSize(); ++i) {
				bw.write("Individual ");
				bw.flush();
				Iterator<Point> iter = p.getIndividualByIndex(i).getListGene().iterator();
				String str = "";

				while (iter.hasNext()) {
					Point po = iter.next();
					if (po.getX() != po.getX()) {
						System.out.println("indi" + i + "writefile x");
					}
					if (po.getY() != po.getY()) {
						System.out.println("indi" + i + "writefile Y");
					}
					str += "(" + df.format(po.getX()) + "," + df.format(po.getY()) + ")";
				}
				str += "\n";
				bw.write(str);
				bw.flush();
			}
		} catch (Exception ex) {
			System.out.println("Write error");
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void writeResult(String convergence, Individual indi, String filename, int ir) {
		filename = filename.replaceFirst("data/data", "result/result_");
		filename = filename.replaceFirst("\\.", "/kq_" + ir + "\\.");
		File f1 = new File(filename).getParentFile();
		if (!f1.exists())
			f1.mkdirs();
		File f = new File(filename);
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);

			bw.write(convergence);
			bw.flush();

			Iterator<Point> iter = indi.getListGene().iterator();
			String str = "NumberOfPoint \n";
			str += indi.getLength() + "\n";
			while (iter.hasNext()) {
				Point po = iter.next();
				str += "" + df.format(po.getX()) + " " + df.format(po.getY()) + "\n";
			}
			str += "Value\n";
			str += indi.getFitness();
			bw.write(str);
			bw.flush();
		} catch (Exception ex) {
			System.out.println("Write error 3");
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static double calculate(String convergence, Individual best, Topo topo, String filename, int ir) {
		int nb = best.getLength();
		int nbChia = (int) (Config.len);
		Individual in2 = new Individual();
		int nbPoint = 0;
		Point endp = new Point(Config.W, Config.endY);
		for (int i = 0; i < nb - 2; ++i) {
			in2.addGene(best.getGene(i));
			++nbPoint;
			double phi = Math.atan((best.getGene(i + 1).getY() - best.getGene(i).getY())
					/ (best.getGene(i + 1).getX() - best.getGene(i).getX()));
			for (int j = 1; j < nbChia; ++j) {
				double x1, y1;
				if (Double.compare(best.getGene(i + 1).getX(), best.getGene(i).getX()) == 0) {
					x1 = best.getGene(i).getX();
				} else {
					x1 = in2.getGene(nbPoint - 1).getX() + Config.len2 * Math.cos(phi);
				}
				y1 = in2.getGene(nbPoint - 1).getY() + Config.len2 * Math.sin(phi);
				in2.addGene(new Point(x1, y1));
				++nbPoint;
			}
		}
		in2.addGene(best.getGene(nb - 2));
		++nbPoint;
		double deltaX, deltaY;
		int nbp = (int) Math.floor(in2.getLastGene().distance(endp) / Config.len2);
		if (Double.compare(endp.getX(), in2.getLastGene().getX()) == 0) {
			deltaY = Config.len * ((endp.getY() - in2.getLastGene().getY()) < 0 ? -1 : 1);
			for (int ip = 0; ip < nbp; ++ip) {
				in2.addGene(new Point(endp.getX(), in2.getLastGene().getY() + deltaY));
			}
		} else {
			double phi = Math.atan((endp.getY() - in2.getLastGene().getY()) / (endp.getX() - in2.getLastGene().getX()));
			deltaX = Config.len * Math.cos(phi);
			deltaY = Config.len * Math.sin(phi);
			for (int ip = 0; ip < nbp; ++ip) {
				in2.addGene(new Point(in2.getLastGene().getX() + deltaX, in2.getLastGene().getY() + deltaY));
			}
		}
		if (Double.compare(in2.getLastGene().distance(endp), 0.0) != 0) {
			in2.addGene(endp);
		}
		/*
		 * if (Double.compare(best.getGene(nb - 1).getX(), best.getGene(nb - 2).getX())
		 * == 0) { boolean isBelow = best.getGene(nb - 1).getY() < endp.getY() ? true :
		 * false; if (isBelow) { while(in2.getGene(in2.getLength() - 1).getY() +
		 * Config.len2 < endp.getY()) { in2.addGene(new Point(endp.getX(),
		 * in2.getGene(in2.getLength()-1).getY() + Config.len2)); }
		 * 
		 * } else { while(in2.getGene(in2.getLength() - 1).getY() - Config.len2 >
		 * endp.getY()) { in2.addGene(new Point(endp.getX(),
		 * in2.getGene(in2.getLength()-1).getY() - Config.len2)); } } } else { double
		 * phi = Math.atan((best.getGene(nb-1).getY() - best.getGene(nb - 2).getY()) /
		 * (best.getGene(nb - 1).getX() - best.getGene(nb - 2).getX()));
		 * 
		 * for (int i = 0; i < nbp; ++i) {
		 * 
		 * } while (in2.getGene(nbPoint-1).distance(endp) > Config.len2) { double x1 =
		 * in2.getGene(nbPoint - 1).getX() + Config.len2 * Math.cos(phi); double y1 =
		 * in2.getGene(nbPoint - 1).getY() + Config.len2 * Math.sin(phi);
		 * in2.addGene(new Point(x1, y1)); ++nbPoint; if (x1 > Config.W || y1 >
		 * Config.H) { System.out.println("x: " + x1 + " - " + y1); in2.printPath2();
		 * break; } } } if (in2.getGene(in2.getLength()-1).distance(endp) != 0) {
		 * in2.addGene(endp);
		 * 
		 * }
		 */
		in2.calFitness(topo);
		writeResult(convergence, in2, filename, ir);
		draw(in2, topo, filename + ".png", ir);
		return in2.getFitness();
	}

	public static double calDijkstra(Topo topo, String filename) {
		filename = filename.replaceFirst("data/", "resultDi/");

		File f = new File(filename);
		FileReader fr = null;
		BufferedReader br = null;
		Individual indi = new Individual();
		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			String str = br.readLine();
			str = br.readLine();
			int nb = Integer.valueOf(str);
			str = br.readLine();
			for (int i = 0; i < nb; ++i) {
				str = br.readLine();
				double x = Double.valueOf(str.split(" ")[0]);
				double y = Double.valueOf(str.split(" ")[1]);
				indi.addGene(new Point(x, y));
			}
			indi.calFitness(topo);
			System.out.println("Dij fitness:" + indi.getFitness());

		} catch (Exception ex) {
			System.out.println("Dijkstra read error");
		} finally {
			if (null != fr) {
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return indi.getFitness();
	}
}
