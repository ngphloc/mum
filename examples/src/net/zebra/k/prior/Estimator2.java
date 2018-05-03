package net.zebra.k.prior;

import java.io.PrintWriter;

public class Estimator2 extends Estimator1 {

	
	public Estimator2(PrintWriter printer) {
		super(printer);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected double g(int a, int b) {
		// TODO Auto-generated method stub
		double g = 0;
		int n = a + b - 1;
		for (int k = a; k <= n; k++)
			g += 1.0 / (double)k;
		return -g;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		PrintWriter printer = new PrintWriter(System.out, true);
		Estimator2 estimator = new Estimator2(printer);
		
		estimator.estimate(3.0/5.0, 4, 4);
		printer.println("****************");

		estimator.estimate(2.0/3.0, 4, 4);
		printer.println("****************");

		estimator.estimate(0.5, 4, 4);
	}
	
	
}
