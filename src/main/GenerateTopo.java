package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class GenerateTopo {
	public static void main(String[] args) {
		int nb = 20;
		for (int k = 0; k < 10; k++) {
			for (int n = nb; n <= 100; n += 20)
				for (int i = 0; i < 3; ++i) {
					String filename = "data" + (i == 0 ? "g" : i == 1 ? "u" : "e") + "/d_" + n + "_" + k ;
					generate(filename, i, n);
				}
		}
	}

	public static void generate(String filename, int i, int nb) {
		switch (i) {
		case 0:
			generateGauss(filename, nb);
			// generateUniform(filename, nb);
			break;
		case 1:
			generateUniform(filename, nb);
			break;
		case 2:
			// generateUniform(filename, nb);
			generateExponent(filename, nb);
			break;
		default:
			break;
		}
	}

	public static void generateGauss(String filename, int nb) {
		Point[] sensors = new Point[nb];
		Random rand = new Random();
		int mean = 250;
		int deviation = 50;
		for (int i = 0; i < nb; ++i) {
			double x = Math.abs(rand.nextGaussian() * deviation + mean);
			double y = Math.abs(rand.nextGaussian() * deviation + mean);
			sensors[i] = new Point(x, y);
		}
		writeResult(filename, sensors);
	}

	public static void generateUniform(String filename, int nb) {
		Point[] sensors = new Point[nb];
		Random rand = new Random();
		double lb = 0;
		double ubx = Config.W;
		double uby = Config.H;
		for (int i = 0; i < nb; ++i) {
			double x = lb + rand.nextDouble() * (ubx - lb);
			double y = lb + rand.nextDouble() * (uby - lb);
			sensors[i] = new Point(x, y);
		}
		writeResult(filename, sensors);
	}

	public static void generateExponent(String filename, int nb) {
		Point[] sensors = new Point[nb];
		Random rand = new Random();
		double lambda = 100;
		double ubx = Config.W;
		double uby = Config.H;
		for (int i = 0; i < nb; ++i) {
			double x = Math.log(1 - rand.nextDouble()) / (-lambda);
			double y = Math.log(1 - rand.nextDouble()) / (-lambda);
			sensors[i] = new Point(x, y);
		}
		writeResult(filename, sensors);
	}

	public static void writeResult(String filename, Point[] p) {
		File f = new File("data/" + filename + ".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		DecimalFormat df = new DecimalFormat("###.##");
		String str = "";
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			str = "500 500\n";
			str += "" + p.length + " 100" + "\n";

			for (int i = 0; i < p.length; ++i) {
				str += df.format(p[i].getX()) + " " + df.format(p[i].getY()) + "\n";
			}
			str += "\0";
			bw.write(str);
			bw.flush();
		} catch (Exception ex) {
			System.out.println("write error");
		} finally {
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
