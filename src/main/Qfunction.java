package main;

public class Qfunction {
	public double getProb(Point p, Topo topo) {
		double prob = 0;
		double d = 0;
		double x = 0;
		for (int i = 0; i < topo.getNumNode(); ++i) {
			d = topo.getSensorByIndex(i).distance(p);
			if (d != d){
				System.out.println("Q function  ");
				System.out.println("p: " + p.getX() + " - " + p.getY());
			}
			if (d > Config.Rs){
				continue;
			}
			
			if (Double.compare(d, 0.0) == 0) {
				prob += (Math.log(10e-100));
				continue;
			}
			
			x = (Config.threshold_A - Config.C / Math.pow(d, Config.alpha)) / Config.xichma;
			x = 1 - getQValue(x);
			if (Double.compare(x, 0.0) == 0) {
				prob += (Math.log(10e-100));
				//System.out.println("x bang 0");
				continue;
			}
			prob += Math.log(x);
		}
		//System.out.println("count: " + count);
		p.setProb(-prob);
		return -prob;
	}
	
	public double getProb(Individual indi, Topo topo) {
		double prob = 0;
		for (int i = 0; i < indi.getLength(); ++i){
			prob += getProb(indi.getGene(i), topo);
		}
		indi.setFitness(prob);
		return prob;
	}
	
	private double getQValue(double x) {
		double value = 0.5;
		/*if (x >= 5) {
			return 0;
		}
		if (x <= -5) {
			return 1;
		}*/
		if (x > 0) {
			value = (1 - Math.exp(-1.4 * x)) * Math.exp(-x * x / 2) / (1.135 * Math.sqrt(2 * Math.PI) * x);
		} else if (x < 0) {
			double y = -x;
			value = 1 - (1 - Math.exp(-1.4 * y)) * Math.exp(-y * y / 2) / (1.135 * Math.sqrt(2 * Math.PI) * y);
		}
		return value;
	}
}
