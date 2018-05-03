/**
 * 
 */
package net.zebra.ls.hmm;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ContinuousDistribution extends AtomicDistribution {


	/**
	 * 
	 */
	protected float epsilon = Util.PROB_EPSILON;
	
	
	/**
	 * 
	 */
	public ContinuousDistribution() {
		super();
	}
	

	/**
	 * 
	 * @return
	 */
	public float getEpsilon() {
		// TODO Auto-generated method stub
		return epsilon;
	}

	
	/**
	 * 
	 * @param epsilon
	 */
	public void setEpsilon(float epsilon) {
		// TODO Auto-generated method stub
		this.epsilon = epsilon;
	}

	
}
