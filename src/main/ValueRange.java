package main;

public class ValueRange {
	private double[] x;
	private double[] y;
	
	public ValueRange() {
		x = new double[(int)(Config.len / Config.precise) + 1];
		y = new double[(int)(Config.len / Config.precise) + 1];
		for (int i = 0; i < x.length; ++i) {
			x[i] = i * Config.precise;
			y[i] = Math.sqrt(Config.len * Config.len - x[i] * x[i]);
		}
	}
	
	public ValueRange(int xd) {
		/*x = new double[(180/Config.degree) + 1];
		y = new double[(180/Config.degree) + 1];
		for (int i = 0; i < x.length; ++i) {
			x[i] =  Math.cos(-90 + i * Config.degree);
			y[i] =  Math.sin((i-1) * Config.degree);
		}*/
		x = new double[3];
		y = new double[3];
		x[0] = 0; y[0] = -Config.len;
		x[1] = Config.len; y[1] = 0;
		x[2] = 0; y[2] = Config.len;
	}
		
	public double getXByIndex(int i) {
		return x[i];
	}
	
	public double getYByIndex(int i) {
		return y[i];
	}
	
	public static void main (String[] args){
		ValueRange r = new ValueRange();
		System.out.println(r.getXByIndex(9));
		System.out.println(r.getYByIndex(9));
		System.out.println(Math.sin(Math.atan(1/0.0)));
		System.out.println(1e-14);
	}
}
