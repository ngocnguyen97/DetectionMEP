package main;

public class Point {
	private double x;
	private double y;
	private double prob;
	
	public Point() {
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(String x, String y) {
		this.x = Double.valueOf(x);
		this.y = Double.valueOf(y);
	}
	
	public double distance(Point p) {
		double d1 = (this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y);
		double d = Math.sqrt(d1);
		return d;
	}
	
	public boolean isValid() {
		if (this.getX() < 0 || this.getX() > Config.W || this.getY() < 0 || this.getY() > Config.H) {
			return false;
		}
		
		return true;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	
}
