/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.Arrays;
import java.util.List;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class MixtureDistribution implements Distribution {


	/**
	 * 
	 */
	protected List<Distribution> dists;
	
	
	/**
	 * 
	 */
	protected List<Float> weights;
	
	
	/**
	 * 
	 */
	private MixtureDistribution() {
		super();
		dists = Util.newList(0);
		weights = Util.newList(0);
	}
	
	
	/**
	 * 
	 * @param dists
	 * @param weights
	 */
	public MixtureDistribution(Distribution[] dists, float[] weights) {
		if (dists.length != weights.length)
			throw new RuntimeException("Invalid parameters");
		
		this.dists.addAll(Arrays.asList(dists));
		float sum = 0;
		for (float weight : weights) {
			sum += weight;
			this.weights.add(weight);
		}
		if (sum != 1)
			throw new RuntimeException("Invalid parameters");
	}
	
	
	@Override
	public float getProb(Obs x) {
		// TODO Auto-generated method stub
		float mprob = 0;
		int K = dists.size();
		for (int k = 0; k < K; k++)
			mprob += weights.get(k) * dists.get(k).getProb(x);
		
		return mprob;
	}

	
	@Override
	public float getProb(Obs x, int kComp) {
		// TODO Auto-generated method stub
		if (kComp < 0)
			return getProb(x);
		else
			return weights.get(kComp) * dists.get(kComp).getProb(x);
	}

	
	/**
	 * 
	 * @param k
	 * @param dist
	 */
	public void replaceDist(int k, Distribution dist) {
		dists.set(k, dist);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getComponentCount() {
		return dists.size();
	}
	
	
	/**
	 * 
	 * @param kComp
	 * @return
	 */
	public Distribution getComponent(int kComp) {
		return dists.get(kComp);
	}
	
	
	/**
	 * 
	 * @param O
	 * @param glistByK
	 */
	public void learn(List<Obs> O, List<List<Double>> glistByK) {
		int K = dists.size();
		List<Double> numerators = Util.newList(K);
		double denominator = 0;
		
		for (int k = 0; k < K; k++) {
			Distribution dist = dists.get(k);
			if (dist instanceof MixtureDistribution) {
				((MixtureDistribution)dist).learn(O, glistByK);
			}
			else if (dist instanceof AtomicDistribution){
				((AtomicDistribution)dist).learn(O, glistByK.get(k));
			}
			
			List<Double> glist = glistByK.get(k);
			double numerator = 0;
			for (double g : glist) {
				numerator += g;
				denominator += g;
			}
			numerators.add(numerator);
		}//End for k
		
		for (int k = 0; k < K; k++) {
			float weight = (float)(numerators.get(k)/denominator);
			weights.set(k, weight);
		}
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		int K = dists.size();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Weights: ");
		for (int k = 0; k < K; k++) {
			if (k > 0)
				buffer.append(", ");
			buffer.append(String.format("w%d=" + Util.DECIMAL_FORMAT, k+1, weights.get(k)));
		}
		
		buffer.append("\nPartial components:\n");
		for (int k = 0; k < K; k++) {
			if (k > 0)
				buffer.append("\n");
			buffer.append("    " + dists.get(k));
		}
		return buffer.toString();
	}


	/**
	 * 
	 * @param means
	 * @param variances
	 * @param weights
	 * @return
	 */
	public static MixtureDistribution createNormalMixture(float[] means, float[] variances, float[] weights) {
		MixtureDistribution mdist = new MixtureDistribution();
		int n = weights.length;
		for (int i = 0; i < n; i++) {
			NormalDistribution normal = new NormalDistribution(means[i], variances[i]);
			mdist.dists.add(normal);
			mdist.weights.add(weights[i]);
		}
		
		return mdist;
	}
	
	
}
