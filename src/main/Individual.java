package main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class Individual implements Cloneable {
	private List<Point> listGene;
	private double fitness;
	
	public Individual() {
		this.listGene = new ArrayList<Point>();
	}

	public List<Point> getListGene() {
		return listGene;
	}

	public void setListGene(List<Point> listGene) {
		this.listGene = listGene;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public Point getGene(int index) {
		return this.getListGene().get(index);
	}
	
	public void addGene(Point p){
		this.listGene.add(p);
	}
	
	public String printPath(String result) {
		result += this.getLength() + "\n";
		DecimalFormat df = new DecimalFormat("###.##");
		for (int i = 0; i < this.getLength(); ++i) {
			result += "(" + df.format(this.getGene(i).getX()) + "," + df.format(this.getGene(i).getY()) + ")";
		}
		result += "\n";
		return result;
	}
	
	public void printPath2() {
		System.out.println(this.getLength());
		for(int i = 0; i < this.getListGene().size(); ++i) {
			System.out.printf("(%.2f, %.2f)", this.getGene(i).getX(), this.getGene(i).getY());
		}
		System.out.println();
	}
	
	public double differ(Individual indi) {
		double value = 0;
		try {
		
		int len = this.getLength() < indi.getLength() ? this.getLength() : indi.getLength();
		
		for (int i = 0; i < len; ++i) {
			value += this.getGene(i).distance(indi.getGene(i));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return value;
	}
	
	public int getLength() {
		return this.listGene.size();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
	public double calFitness(Topo topo) {
		Qfunction q = new Qfunction();
		double fitness = 0;
		for (int i = 0; i < this.getLength(); ++i) {
			fitness += q.getProb(this.getGene(i), topo);
		}
		this.setFitness(fitness);
		return fitness;
	}
	
	public Individual normalize(){
		int nb = this.getLength();
		int nbChia = (int)(Config.len1/Config.len);
		Individual in2 = new Individual();
		int nbPoint = 0;
		for (int i = 0; i < nb - 2; ++i) {
			in2.addGene(this.getGene(i));
			++nbPoint;
			double phi = Math.atan((this.getGene(i + 1).getY() - this.getGene(i).getY())
					/ (this.getGene(i + 1).getX() - this.getGene(i).getX()));
			for (int j = 1; j < nbChia; ++j) {
				double x1, y1;
				if (Double.compare(this.getGene(i + 1).getX(), this.getGene(i).getX()) == 0) {
					x1 = this.getGene(i).getX();
				} else {
					x1 = in2.getGene(nbPoint - 1).getX() + Config.len * Math.cos(phi);
				}
				y1 = in2.getGene(nbPoint - 1).getY() + Config.len * Math.sin(phi);
				in2.addGene(new Point(x1, y1));
				++nbPoint;
			}
		}
		in2.addGene(this.getGene(nb - 2)); ++nbPoint;
		double phi = Math.atan((this.getGene(nb-1).getY() - this.getGene(nb - 2).getY())
				/ (this.getGene(nb - 1).getX() - this.getGene(nb - 2).getX()));
		double y = this.getGene(nb - 2).getY();
		while (Math.abs(y - Config.endY) > Config.len) {
			double x1, y1;
			if (Double.compare(this.getGene(nb - 1).getX(), this.getGene(nb - 2).getX()) == 0) {
				x1 = this.getGene(nb - 2).getX();
			} else {
				x1 = in2.getGene(nbPoint - 1).getX() + (Config.len1 / nbChia) * Math.cos(phi);
			}
			y1 = in2.getGene(nbPoint - 1).getY() + (Config.len1 / nbChia) * Math.sin(phi);
			in2.addGene(new Point(x1, y1));
			++nbPoint;
			y = y1;
		}
		in2.addGene(this.getGene(nb - 1));
 		return in2;
	}
	
	public boolean isValid () {
		boolean result = true;
		Point endp = new Point(Config.W, Config.endY);
		if (this.getGene(this.getLength()-1).distance(endp) != 0) {
			System.out.println("no same end point");
			return false;
		}
		for (int i = 1; i < this.getLength(); ++i) {
			if (!this.getGene(i).isValid()) {
				System.out.println("Point is invalid");
				result = false;
				break;
			}
			if (this.getGene(i).distance(this.getGene(i-1)) > Config.len + 0.0001) {
				System.out.println("distance: " + this.getGene(i).distance(this.getGene(i-1)) + " vi tri: " + i + " tren tong so: " + this.getLength());
				result = false;
				break;
			}
		}
		return result;
	}
	
	public Point getLastGene() {
		return this.getGene(this.getLength()-1);
	}
}
