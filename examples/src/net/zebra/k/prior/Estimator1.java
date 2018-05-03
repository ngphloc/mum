package net.zebra.k.prior;

import java.io.PrintWriter;

/**
 * 
 * @author Loc Nguyen
 *
 */
public class Estimator1 {

	
	/**
	 * 
	 */
	protected final static double EPSILON = 0.1;//0.000001;
	
	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public final static int  DECIMAL_PRECISION  = 2;
	
	
	/**
	 * 
	 */
	protected PrintWriter printer = null;
	
	
	/**
	 * 
	 * @param printer
	 */
	public Estimator1(PrintWriter printer) {
		this.printer = printer;
	}
	
	
	/**
	 * 
	 * @param n
	 * @return
	 */
	private long factorial(int n) {
		long fact = 1;
		for (int k = 2; k <= n; k++)
			fact *= k;
		return fact;
	}
	
	
	/**
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	private long c(int n, int k) {
		return factorial(n) / (factorial(k) * factorial(n - k));
	}
	
	
	/**
	 * 
	 * @param freq
	 * @return
	 */
	protected double l(double freq) {
		return Math.log(freq);
	}
	
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	protected double g(int a, int b) {
		if (a < 2 || b < 2)
			return Double.NaN;
		
		double g1 = 0;
		int n1 = a + b - 1;
		for (int k = 1; k <= n1; k++) {
			g1 += Math.pow(-1, k) * c(n1, k) / k;
		}

		double g2 = 0;
		int n2 = a - 1;
		for (int k = 1; k <= n2; k++) {
			g2 += Math.pow(-1, k) * c(n2, k) / k;
		}
		
		return g1 - g2;
	}
	
	
	/**
	 * 
	 * @param sampleFreq
	 * @param aRange
	 * @param bRange
	 * @return
	 */
	public double[] estimate(double sampleFreq, int aRange, int bRange) {
		double dmin = Double.MAX_VALUE;
		double aEstimate = 1, bEstimate = 1;
		for (int a = 1; a <= aRange; a++) {
			for (int b = 1; b <= bRange; b++) {
				double L1 = l(sampleFreq), L2 = l(1.0 - sampleFreq);
				double G1 = g(a, b), G2 = g(b, a);
				if (Double.isNaN(G1) || Double.isNaN(G2))
					continue;
				
				double d1 = 0, d2 = 0, d = 0;
				d1 = G1 - L1;
				d2 = G2 - L2;
				d = Math.sqrt(d1*d1 + d2*d2);
				
				if (d < dmin) {
					dmin = d;
					aEstimate = a;
					bEstimate = b;
				}
				
				out("a=" + round(a) + ", b=" + round(b) + 
						", L1=" + round(L1) + ", L2=" + round(L2) + 
						", G1=" + round(G1) + ", G2=" + round(G2) + 
						", d1=" + round(d1) + ", d2=" + round(d2) + ", d=" + round(d));
//				out(round(a) + "\t" + round(b) +
//						"\t" + round(L1) + "\t" + round(L2) + 
//						"\t" + round(G1) + "\t" + round(G2) + 
//						"\t" + round(d1) + "\t" + round(d2) + "\t" + round(d));
			}
		}
		
		out("a^=" + round(aEstimate) + ", b^=" + round(bEstimate));
		return new double[] {aEstimate, bEstimate};
		
	}
	
	
	/**
	 * 
	 * @param number
	 * @return
	 */
	protected double round(double number) {
		if (Double.isNaN(number))
			return Double.NaN;
		
		long d = (long) Math.pow(10, DECIMAL_PRECISION);
		return (double) Math.round(number * d) / d;
	}
	
	
	/**
	 * 
	 * @param o
	 */
	private void out(Object o) {
		printer.println(o);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				
		PrintWriter printer = new PrintWriter(System.out, true);
		Estimator1 estimator = new Estimator1(printer);
		
		estimator.estimate(3.0/5.0, 4, 4);
		printer.println("****************");

		estimator.estimate(2.0/3.0, 4, 4);
		printer.println("****************");

		estimator.estimate(0.5, 4, 4);
	}
	
	
}
