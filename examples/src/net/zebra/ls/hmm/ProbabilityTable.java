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
public class ProbabilityTable extends DiscreteDistribution {

	
	/**
	 * List of defined probabilities
	 */
	protected List<Float> probs;
	
	
	/**
	 * Constructor of discrete distribution.
	 * 
	 * @param n
	 */
	public ProbabilityTable(int n) {
		super();
		
		probs = Util.newList(n);
		if (n > 0)
			probs.add(1f);
		for (int i = 1; i < n; i++)
			probs.add(0f);
	}
	
	
	/**
	 * 
	 * @return The number of probabilities
	 */
	public int size() {
		return probs.size();
	}
	
	
	@Override
	public float getProb(Obs x) {
		// TODO Auto-generated method stub
		return probs.get((int)((MonoObs)x).value);
	}


	@Override
	public float getProb(Obs x, int kComp) {
		// TODO Auto-generated method stub
		return getProb(x);
	}


	/**
	 * 
	 * @param x
	 * @param prob
	 */
	public void setProb(float x, float prob) {
		// TODO Auto-generated method stub
		probs.set((int)x, prob);
	}

	
	@Override
	public void learn(List<Obs> O, List<Double> glist) {
		// TODO Auto-generated method stub
		int T = O.size() - 1;
		if (T < 0) return;
				
		double denominator = 0;
		for (int t = 0; t <= T; t++) {
			double g = glist.get(t);
			denominator += g; 
		}//End for t
		if (denominator == 0)
			return;

		int m = size();
		for (int k = 0; k < m; k++) {
			double numerator = 0;
			for (int t = 0; t <= T; t++) {
				numerator += ( (int)((MonoObs)(O.get(t))).value == k ) ? glist.get(t) : 0;
			}//End for t
			
			setProb(k, (float)(numerator/denominator));
		}//End for k
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		int n = probs.size();
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append(" ");
			
			buffer.append(String.format(Util.DECIMAL_FORMAT, probs.get(i)));
		}
		
		return buffer.toString();
	}

}
