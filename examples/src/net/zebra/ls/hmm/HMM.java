/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.List;

/**
 * The main interface of hidden Markov model, which aims to separate design and implementation.
 * There are only three core public interfaces: {@link HMM}, {@link Obs}, and {@link Factory}.
 * The public class {@link FactoryImpl} that creates the default implementation of this interface can be replaced by advanced class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface HMM {

	
	/**
	 * 
	 * @return
	 */
	int n();
	
	
	/**
	 * 
	 * @param stateI
	 * @param stateJ
	 * @return
	 */
	float a(int stateI, int stateJ);
	
	
	/**
	 * 
	 * @param stateI
	 * @return
	 */
	float pi(int stateI);
	
	
	/**
	 * 
	 * @param stateI
	 * @param obs
	 * @return
	 */
	float b(int stateI, Obs obs);
	
	
	/**
	 * 
	 * @param obsSeq
	 * @return
	 */
	double evaluate(List<Obs> obsSeq);
	
	
	/**
	 * 
	 * @param obsSeq
	 * @return
	 */
	List<Integer> uncover(List<Obs> obsSeq);
	
	
	/**
	 * 
	 * @param obsSeq
	 */
	void learn(List<Obs> obsSeq);
}
