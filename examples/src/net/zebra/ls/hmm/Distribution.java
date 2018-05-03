/**
 * 
 */
package net.zebra.ls.hmm;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Distribution {

	
	/**
	 * Getting the defined probability at point x according to application.
	 * 
	 * @param x
	 * @return defined probability at point x according to application.
	 */
	float getProb(Obs x);
	
	
	/**
	 * 
	 * @param x
	 * @param kComp
	 * @return
	 */
	float getProb(Obs x, int kComp);
	
	
}
