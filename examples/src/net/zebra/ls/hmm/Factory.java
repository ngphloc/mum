/**
 * 
 */
package net.zebra.ls.hmm;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Factory {
	
	
	/**
	 * Creating discrete hidden Markov model.
	 * 
	 * @param A
	 * @param PI
	 * @param B
	 * @return Discrete hidden Markov model
	 */
	HMM createDiscreteHMM(float[][] A, float[] PI, float[][] B);


	/**
	 * 
	 * @param nState
	 * @param mObs
	 * @return
	 */
	HMM createDiscreteHMM(int nState, int mObs);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @param variances
	 * @return
	 */
	HMM createNormalHMM(float[][] A, float[] PI, float[] means, float[] variances, float epsilon);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @return
	 */
	HMM createExponentialHMM(float[][] A, float[] PI, float[] means, float epsilon);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @param variances
	 * @return
	 */
	HMM createNormalMixtureHMM(float[][] A, float[] PI, float[][] means, float[][] variances, float[][] weights, float epsilon);
}
