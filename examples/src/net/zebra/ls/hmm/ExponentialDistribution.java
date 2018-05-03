/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.List;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class ExponentialDistribution extends ContinuousDistribution {


	/**
	 * 
	 */
	protected org.apache.commons.math3.distribution.ExponentialDistribution exp;
	
	
	/**
	 * 
	 * @param mean
	 */
	public ExponentialDistribution(float mean) {
		super();
		setParameters(mean);
	}


	@Override
	public float getProb(Obs x) {
		// TODO Auto-generated method stub
		float value = ((MonoObs)x).value;
		float epsilon = getEpsilon();
		return (float)exp.probability(value - epsilon, value + epsilon);
	}

	
	@Override
	public float getProb(Obs x, int kComp) {
		// TODO Auto-generated method stub
		return getProb(x);
	}

	
	@Override
	public void learn(List<Obs> O, List<Double> glist) {
		// TODO Auto-generated method stub
		int T = O.size() - 1;
		if (T < 0) return;

		double numerator = 0;
		double denominator = 0;
		for (int t = 0; t <= T; t++) {
			double g = glist.get(t);
			numerator += g;
			denominator += g * ((MonoObs)(O.get(t))).value;
		}
		if (denominator == 0)
			return;
		float mean = (float)(numerator/denominator);

		if (mean != 0)
			setParameters(mean);
	}


	/**
	 * 
	 * @param mean
	 */
	public void setParameters(float mean) {
		exp = new org.apache.commons.math3.distribution.ExponentialDistribution(mean);
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("Exponential distribution (mean=" + Util.DECIMAL_FORMAT + ")", exp.getNumericalMean());
	}

}
