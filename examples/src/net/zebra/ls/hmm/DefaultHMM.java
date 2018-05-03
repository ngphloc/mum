/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.List;


/**
 * This is default implementation of hidden Markov model {@link HMM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class DefaultHMM implements AutoCloseable {
	
	
	/**
	 * 
	 */
	final static int EM_MAX_ITERATION = 10000;
	
	
	/**
	 * Variable A represents transition probability matrix.
	 */
	protected List<List<Float>> A = null;

	
	/**
	 * Variable PI represents initial probability matrix.
	 */
	protected List<Float> PI = null;

	
	/**
	 * Variable B represents observation probability distribution.
	 */
	protected List<Distribution> B = null;
	
	
	/**
	 * Optional list of state names
	 */
	protected List<String> S = null;
	
	
	/**
	 * Optional list of observation names
	 */
	protected List<String> OBS = null;

	
	/**
	 * 
	 */
	protected Printer printer = null;
	
	
	/**
	 * Hidden Markov model constructor
	 */
	protected DefaultHMM() {
		S = Util.newList(0);
		OBS = Util.newList(0);
		printer = new Printer();
	}
	
	
	/**
	 * Getting name of specified state.
	 * @param state
	 * @return Name of specified state
	 */
	public List<String> getStateNames(int state) {
		return S;
	}
	
	
	/**
	 * 
	 * @param stateNames
	 */
	public void setStateNames(List<String> stateNames) {
		S.clear();
		S.addAll(stateNames);
	}
	
	
	/**
	 * Getting observation name at specified index.
	 * 
	 * @return Observation names
	 */
	public List<String> getObservationNames() {
		return OBS;
	}
	
	
	/**
	 * 
	 * @param obsNames
	 */
	public void setObservationNames(List<String> obsNames) {
		OBS.clear();
		OBS.addAll(obsNames);
	}

	
	/**
	 * 
	 * @return
	 */
	public Printer getPrinter() {
		return printer;
	}
	
	
	/**
	 * Getting transition probability from state i=0,1,2... to state j=0,1,2....
	 * 
	 * @param i=0,1,2...
	 * @param j=0,1,2...
	 * @return Transition probability from state i=0,1,2... to state j=0,1,2...
	 */
	public float getA(int i, int j) {
		return A.get(i).get(j);
	}

	
	/**
	 * Assigning transition probability from state i=0,1,2... to state j=0,1,2.... by specified value.
	 * 
	 * @param i=0,1,2...
	 * @param j=0,1,2...
	 * @param value Specified value for transition probability from state i=0,1,2... to state j=0,1,2....
	 */
	public void setA(int i, int j, float value) {
		A.get(i).set(j, value);
	}

	
	/**
	 * Getting initial probability at state i=0,1,2....
	 * 
	 * @param i=0,1,2...
	 * @return Initial probability at state i=0,1,2....
	 */
	public float getPI(int i) {
		return PI.get(i);
	}

	
	/**
	 * 
	 * @param i
	 * @param value
	 */
	public void setPI(int i, float value) {
		PI.set(i, value);
	}

	
	/**
	 * Getting observation probability at state i and observation j in both discrete case 
	 * and continuous case. In discrete case, parameter j is the index of observation.
	 * In continuous case, parameter j is the value of observation.
	 * 
	 * @param i=0,1,2...
	 * @param x
	 * @return Observation probability at state i and observation j in both discrete case 
	 * and continuous case
	 */
	public float getB(int i, Obs x, int kComp) {
		return B.get(i).getProb(x, kComp);
	}
	

	/**
	 * 
	 * @param i
	 * @param dist
	 */
	public void setB(int i, Distribution dist) {
		B.set(i, dist);
	}

	
	/**
	 * Getting the number of states
	 * @return The number of states
	 */
	public int getStateNumber() {
		return A.size();
	}
	
	
	/**
	 * Evaluating forward variables exactly at specified time point given observation sequence.
	 * 
	 * @param O Observation sequence
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param J Specified state, J = 0, 1, 2,...
	 * @return List of evaluated forward variables exactly at specified time point given observation sequence
	 */
	public List<Double> alpha(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<Double> alphaSeq = Util.newList(n);
		Obs ot = O.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, kComp)*getPI(i);
			alphaSeq.add(alpha);
		}
		
		List<Double> alphaTemp = Util.newList(n);
		for (int t = 1; t <= timePoint; t++) {
			alphaTemp.addAll(alphaSeq);
			ot = O.get(t);
			for (int j = 0; j < n; j++) {
				double sumAlpha = 0;
				for (int i =0; i < n; i++) {
					sumAlpha += alphaTemp.get(i) * getA(i, j);
				}
				alphaSeq.set(j, sumAlpha * getB(j, ot, kComp));
			}
			alphaTemp.clear();
		}
		
		return alphaSeq;
	}
	
	
	/**
	 * Evaluating all forward variables until specified time point given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 * of given state and given time point.
	 * 
	 * @param O Observation sequence
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 */
	public List<List<Double>> alphaAll(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<List<Double>> alphaOut = Util.newList(timePoint+1, n, 0d);
		
		Obs ot = O.get(0);
		List<Double> alphaList = alphaOut.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, kComp)*getPI(i);
			alphaList.set(i, alpha);
		}
		
		for (int t = 1; t <= timePoint; t++) {
			List<Double> preAlphaList = alphaOut.get(t-1);
			alphaList = alphaOut.get(t);
			ot = O.get(t);
			for (int j = 0; j < n; j++) {
				double sumAlpha = 0;
				for (int i =0; i < n; i++) {
					sumAlpha += preAlphaList.get(i) * getA(i, j);
				}
				alphaList.set(j, sumAlpha * getB(j, ot, kComp));
			}
		}
		
		return alphaOut;
	}
	
	
	/**
	 * Evaluating all forward variable given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 * of given state and given time point.
	 * 
	 * @param O Observation sequence
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of forward variables evaluated.
	 */
	public List<List<Double>> alphaAll(List<Obs> O, int kComp) {
		return alphaAll(O, O.size()-1, kComp);
	}

	
	/**
	 * Evaluating backward variables exactly at specified time point given observation sequence.
	 * 
	 * @param O Observation sequence
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @param J Specified state, J = 0, 1, 2,...
	 * @return List of evaluated backward variables exactly at specified time point given observation sequence
	 */
	public List<Double> beta(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<Double> betaPost = Util.newList(n, 1d);
		
		List<Double> betaTemp = Util.newList(n);
		for (int t = T-1; t >= timePoint; t--) {
			betaTemp.addAll(betaPost);
			Obs ot_plus_1 = O.get(t+1);
			for (int i = 0; i < n; i++) {
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumBeta += getA(i, j) * getB(j, ot_plus_1, kComp) * betaTemp.get(j);
				}
				betaPost.set(i, sumBeta);
			}
			betaTemp.clear();
		}
		
		return betaPost;
	}

	
	/**
	 * Evaluating all backward variable until specified time point given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 * of given state and given time point.
	 * 
	 * @param O Observation sequence
	 * @param timePoint Specified time point, T = 0, 1, 2,...
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 */
	public List<List<Double>> betaAll(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> betaOut = Util.newList(T-timePoint+1, n, 1d);
		
		for (int t = T-1; t >= timePoint; t--) {
			List<Double> preBetaList = betaOut.get(t-timePoint+1);
			List<Double> betaList = betaOut.get(t-timePoint);
			Obs ot_plus_1 = O.get(t+1);
			for (int i = 0; i < n; i++) {
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumBeta += getA(i, j) * getB(j, ot_plus_1, kComp) * preBetaList.get(j);
				}
				betaList.set(i, sumBeta);
			}
		}
		
		return betaOut;
	}

	
	/**
	 * Evaluating all backward variable given observation sequence.
	 * The result is a list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 * of given state and given time point.
	 * 
	 * @param O Observation sequence
	 * @return A list whose elements are sub-list according to time point 0,1,2...
	 * Each sub-list is list of backward variables evaluated.
	 */
	public List<List<Double>> betaAll(List<Obs> O, int kComp) {
		return betaAll(O, 0, kComp);
	}
	
	
	public List<?>[] alphaBetaAll(List<Obs> O, int kComp) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		alphaBetaAll(O, alphas, betas, kComp);
		
		return new List<?>[] {alphas, betas};
	}
	
	
	/**
	 * 
	 * @param O
	 * @param alphaOut
	 * @param betaOut
	 * @param kComp
	 */
	private void alphaBetaAll(List<Obs> O, List<List<Double>> alphaOut, List<List<Double>> betaOut, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		
		Obs o_t = O.get(0);
		List<Double> alphaList = alphaOut.get(0);
		List<Double> betaList = betaOut.get(T);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, O.get(0), kComp)*getPI(i);
			alphaList.set(i, alpha);
			betaList.set(i, 1d);
		}
		
		for (int t = 1; t <= T; t++) {
			int rt = T - t;
			List<Double> preAlphaList = alphaOut.get(t-1);
			List<Double> preBetaList = betaOut.get(rt+1);
			Obs o_rt_pre =  O.get(rt+1);
			alphaList = alphaOut.get(t);
			betaList = betaOut.get(rt);
			o_t = O.get(t);
			for (int i = 0; i < n; i++) {
				double sumAlpha = 0;
				double sumBeta = 0;
				for (int j = 0; j < n; j++) {
					sumAlpha += preAlphaList.get(j) * getA(j, i);
					sumBeta += getA(i, j) * getB(j, o_rt_pre, kComp) * preBetaList.get(j);
				}
				alphaList.set(i, sumAlpha * getB(i, o_t, kComp));
				betaList.set(i, sumBeta);
			}
		}
	}


	/**
	 * 
	 * @param O
	 * @param timePoint
	 * @param kComp
	 * @return
	 */
	public List<Double> gamma(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<Double> alist = alpha(O, timePoint, kComp);
		List<Double> blist = beta(O, timePoint, kComp);
		List<Double> glist = Util.newList(n);
		for (int i = 0; i < n; i++) {
			double g = alist.get(i) * blist.get(i);
			glist.add(g);
		}
		alist.clear();
		blist.clear();
		
		return glist;
	}
	
	
	/**
	 * 
	 * @param O
	 * @param kComp
	 * @return
	 */
	public List<List<Double>> gammaAll(List<Obs> O, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> gammaOut = Util.newList(T+1);
		
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		for (int t = 0; t <= T; t++) {
			List<Double> alist = alphas.get(t);
			List<Double> blist = betas.get(t);
			List<Double> glist = Util.newList(n);
			for (int i = 0; i < n; i++) {
				double g = alist.get(i) * blist.get(i);
				glist.add(g);
			}
			gammaOut.add(glist);
		}
		alphas.clear();
		betas.clear();
		
		return gammaOut;
	}

	
	/**
	 * 
	 * @param O
	 * @param kComp
	 * @return
	 */
	public List<List<Double>> gammaAllByState(List<Obs> O, int kComp) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> gammaOut = Util.newList(n);
		
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		for (int i = 0; i < n; i++) {
			List<Double> glist = Util.newList(T+1);
			for (int t = 0; t <= T; t++) {
				double g = alphas.get(t).get(i) * betas.get(t).get(i);
				glist.add(g);
			}
			gammaOut.add(glist);
		}
		alphas.clear();
		betas.clear();
		
		return gammaOut;
	}

	
	/**
	 * 
	 * @param O
	 * @param kCompCount
	 * @param state
	 * @return
	 */
	public List<List<Double>> gammaAllByComp(List<Obs> O, int kCompCount, int state) {
		List<List<Double>> gammaOut = Util.newList(kCompCount);
		int T = O.size() - 1;
		for (int k = 0; k < kCompCount; k++) {
			List<?>[] abs = alphaBetaAll(O, k);
			@SuppressWarnings("unchecked")
			List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
			@SuppressWarnings("unchecked")
			List<List<Double>> betas = (List<List<Double>>)(abs[1]);
			
			List<Double> glist = Util.newList(T+1);
			for (int t = 0; t <= T; t++) {
				double g = alphas.get(t).get(state) * betas.get(t).get(state);
				glist.add(g);
			}
			gammaOut.add(glist);
			
			alphas.clear();
			betas.clear();
		}
		
		return gammaOut;
	}
	
	
	/**
	 * 
	 * @param O
	 * @param timePoint
	 * @param kComp
	 * @return
	 */
	public List<List<Double>> c(List<Obs> O, int timePoint, int kComp) {
		int n = getStateNumber();
		List<List<Double>> cmatrix = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);
		Obs ot = O.get(timePoint);
		for (int i = 0; i < n; i++) {
			List<Double> clist = Util.newList(n);
			cmatrix.add(clist);
			for (int j = 0; j < n; j++) {
				double c = 
						alphas.get(i) * 
						getA(i,j) * 
						getB(j, ot, kComp) *
						betas.get(j);
				clist.add(c);
			}
		}
		alphas.clear();
		betas.clear();
		
		return cmatrix;
	}
	
	
	/**
	 * 
	 * @param O
	 * @param timePoint
	 * @param preState
	 * @param kComp
	 * @return
	 */
	public List<Double> cForPre(List<Obs> O, int timePoint, int preState, int kComp) {
		int n = getStateNumber();
		List<Double> clist = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);
		
		double alpha = alphas.get(preState);
		alphas.clear();
		Obs ot = O.get(timePoint);
		for (int j = 0; j < n; j++) {
			double c = 
					alpha * 
					getA(preState,j) * 
					getB(j, ot, kComp) *
					betas.get(j);
			clist.add(c);
		}
		betas.clear();
		
		return clist;
	}

	
	/**
	 * 
	 * @param O
	 * @param timePoint
	 * @param postState
	 * @param kComp
	 * @return
	 */
	public List<Double> cForPost(List<Obs> O, int timePoint, int postState, int kComp) {
		int n = getStateNumber();
		List<Double> clist = Util.newList(n);
		List<Double> alphas = alpha(O, timePoint-1, kComp);
		List<Double> betas = beta(O, timePoint, kComp);

		double beta = betas.get(postState);
		betas.clear();
		Obs ot = O.get(timePoint);
		for (int i = 0; i < n; i++) {
			double c = 
					alphas.get(i) * 
					getA(i,postState) * 
					getB(postState, ot, kComp) *
					beta;
			clist.add(c);
		}
		alphas.clear();
		
		return clist;
	}

	
	/**
	 * 
	 * @param O
	 * @param kComp
	 * @return
	 */
	@Deprecated
	public List<List<List<Double>>> cAll(List<Obs> O, int kComp) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<List<List<Double>>> cout = Util.newList(T);
		List<?>[] abs = alphaBetaAll(O, kComp);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		
		for (int t = 1; t <= T; t++) {
			List<List<Double>> cmatrix = Util.newList(n);
			cout.add(cmatrix);
			for (int i = 0; i < n; i++) {
				List<Double> clist = Util.newList(n);
				cmatrix.add(clist);
				for (int j = 0; j < n; j++) {
					double c = 
							alphas.get(t-1).get(i) * 
							getA(i,j) * 
							getB(j, O.get(t), kComp) *
							betas.get(t).get(j);
					clist.add(c);
				}
			}
		}
		alphas.clear();
		betas.clear();
		
		return cout;
	}
	
	
	/**
	 * Implementing Viterbi algorithm to solve the uncovering problem that find out the state sequence
	 * that is appropriate mostly to given observation sequence.
	 *  
	 * @param O
	 * @return The state sequence that is appropriate mostly to given observation sequence
	 */
	public List<Integer> viterbi(List<Obs> O) {
		int T = O.size() - 1;
		int n = getStateNumber();
		
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"Viterbi algorithm on observation sequence O=" + 
							toObsString(O) + " with HMM:");
			printer.println(buffer.toString());
			printer.println(this);
			printer.println("\n-----t=0-----");
		}
		
		List<Double> deltaSeq = Util.newList(n);
		List<List<Integer>> tracks = Util.newList(T+1);
		for (int t = 0; t <= T; t++) {
			List<Integer> track = Util.newList(n);
			tracks.add(track);
		}
		List<Integer> track = tracks.get(0);
		Obs ot = O.get(0);
		for (int i = 0; i < n; i++) {
			double alpha = getB(i, ot, -1)*getPI(i);
			deltaSeq.add(alpha);
			track.add(0);
			
			if (printer.isEnable()) {
				printer.println(String.format("alpha0(%d)=" + Util.DECIMAL_FORMAT, i, alpha));
				printer.println(String.format("q0(%d)=0", i));
			}
		}
		
		List<Double> deltaTemp = Util.newList(n);
		for (int t = 1; t <= T; t++) {
			if (printer.isEnable())
				printer.println("\n-----t=" + t + "-----");
			
			deltaTemp.addAll(deltaSeq);
			ot = O.get(t);
			track = tracks.get(t);
			for (int j = 0; j < n; j++) {
				double maxalpha = -1;
				int maxstate = -1;
				for (int i =0; i < n; i++) {
					double alpha = deltaTemp.get(i) * getA(i, j);
					if (alpha > maxalpha) {
						maxalpha = alpha;
						maxstate = i;
					}
					
					if (printer.isEnable())
						printer.println(String.format("alpha%d(%d)*a%d(%d)=" + Util.DECIMAL_FORMAT, t-1, i, i, j, alpha));
				}
				double delta = maxalpha * getB(j, ot, -1);
				deltaSeq.set(j, delta);
				track.add(maxstate);
				
				if (printer.isEnable()) {
					printer.println(String.format("Max{alpha%d(i)*ai(%d)} = alpha%d(%d)*a%d(%d) = " + Util.DECIMAL_FORMAT,
							t-1, j, t-1, maxstate, maxstate, j, maxalpha));
					printer.println(String.format("delta%d(%d) = Max{alpha%d(i)*ai(%d)}*b%d(%d) = alpha%d(%d)*a%d(%d)*b%d(%d) = " + Util.DECIMAL_FORMAT,
							t, j, t-1, j, j, t, t-1, maxstate, maxstate, j, j, t, maxalpha));
					printer.println(String.format("q%d(%d)=%d", t, j, maxstate));
				}
			}
			deltaTemp.clear();
		}
		
		List<Integer> states = Util.newList(T+1, -1);
		double maxalpha = -1;
		int maxstate = -1;
		for (int j =0; j < n; j++) {
			double alpha = deltaSeq.get(j);
			if (alpha > maxalpha) {
				maxalpha = alpha;
				maxstate = j;
			}
		}
		states.set(T, maxstate);
		deltaSeq.clear();
		
		if (printer.isEnable())
			printer.println(String.format("Optimal state x(%d) = argmax{delta%d(j)} = %d", T, T, maxstate));
		
		for (int t = T-1; t >= 0; t--) {
			int postState = states.get(t+1);
			maxstate = tracks.get(t+1).get(postState);
			states.set(t, maxstate);
			
			if (printer.isEnable())
				printer.println(String.format("Optimal state x(%d) = q%d(x(%d)) = q%d(%d) = %d", t, t+1, t+1, t+1, postState, maxstate));
		}
		tracks.clear();
		
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"\nThe resulted optimal state sequence is X=" + 
							toStateString(states) + "");
			printer.println(buffer.toString());
		}
		
		return states;
	}


	/**
	 * 
	 * @param ot
	 * @param xt
	 * @return
	 */
	public double weight(Obs ot, int xt) {
		return getB(xt, ot, -1) * getPI(xt);
	}
	
	
	/**
	 * 
	 * @param ot
	 * @param pre_xt
	 * @param xt
	 * @return
	 */
	public double weight(Obs ot, int pre_xt, int xt) {
		double b = getB(xt, ot, -1);
		return b * b * getA(pre_xt, xt) * getPI(xt);
	}
	
	
	/**
	 * 
	 * @param O
	 * @param X
	 * @return
	 */
	public double path(List<Obs> O, List<Float> X) {
		double path = weight(O.get(0), X.get(0).intValue());
		int T = O.size() - 1;
		for (int t = 1; t <= T; t++) {
			path *= weight(O.get(t), X.get(t-1).intValue(), X.get(t).intValue());
		}
		
		return path;
	}
	
	
	/**
	 * 
	 * @param O
	 * @return
	 */
	public List<Integer> longestPath(List<Obs> O) {
		int T = O.size() - 1;
		List<Integer> states = Util.newList(T+1);

		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
				"Longest-path algorithm on observation sequence O=" + toObsString(O) + " with HMM:");
			printer.println(buffer.toString());
			printer.println(this);
			printer.println("\n-----t=0-----");
		}
		
		int maxstate = -1;
		double maxweight = -1;
		int n = getStateNumber();
		Obs o0 = O.get(0);
		for (int i = 0; i < n; i++) {
			double w = weight(o0, i);
			if (w > maxweight) {
				maxweight = w;
				maxstate = i;
			}

			if (printer.isEnable())
				printer.println(String.format("W011%d=" + Util.DECIMAL_FORMAT, i, w));
		}
		states.add(maxstate);
		
		if (printer.isEnable())
			printer.println(String.format("Max{W011k} k from 0 to %d is W011%d=" + Util.DECIMAL_FORMAT, n-1, maxstate, maxweight));
		
		int j = maxstate;
		for (int t = 1; t <= T; t++) {
			Obs ot = O.get(t);
			maxstate = -1;
			maxweight = -1;
			for (int k = 0; k < n; k++) {
				double w = weight(ot, j, k);
				if (w > maxweight) {
					maxweight = w;
					maxstate = k;
				}
				
				if (printer.isEnable())
					printer.println(String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, j, t, k, w));
			}
			states.add(maxstate);
			j = maxstate;
			
			if (printer.isEnable())
				printer.println(String.format("Max{W%d%d%dk} k from 0 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, 
						t-1, j, t, n-1, t-1, j, t, maxstate, maxweight));
		}
		
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"\nThe longest-path (optimal state sequence) is X=" + toStateString(states));
			printer.println(buffer.toString());
		}

		return states;
	}
	
	
	/**
	 * 
	 * @param O
	 * @return
	 */
	public List<Integer> longestPathAdvanced(List<Obs> O) {
		int T = O.size() - 1;
		int n = getStateNumber();
		List<Integer> states = Util.newList(T+1);
		
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"Advanced Longest-path algorithm on observation sequence O=" + toObsString(O) + " with HMM:");
			printer.println(buffer.toString());
			printer.println(this);
		}
		
		int i = 0;
		List<Double> W1 = Util.newList(n, 0d);
		List<Double> W2 = Util.newList(n, 0d);
		List<Integer> S2 = Util.newList(n, 0);
		for (int t = 0; t <= T; t+=2) {
			if (printer.isEnable())
				printer.println("\n-----t=" + t + "-----");
			
			if (t == 0) {
				Obs o0 = O.get(0);
				for (int j = 0; j < n; j++) {
					double w = weight(o0, j);
					W1.set(j, w);
					
					if (printer.isEnable())
						printer.println(String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, i, t, j, w));
				}
			}
			else {
				Obs ot = O.get(t);
				for (int j = 0; j < n; j++) {
					double w = weight(ot, i, j);
					W1.set(j, w);
					
					if (printer.isEnable())
						printer.println(String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t-1, i, t, j, w));
				}
			}
			
			if (t == T) {
				int maxstate = -1;
				double maxweight = -1;
				for (int j = 0; j < n; j++) {
					double w = W1.get(j);
					if (w > maxweight) {
						maxweight = w;
						maxstate = j;
					}
				}
				states.add(maxstate);
				i = maxstate;
				
				if (printer.isEnable()) {
					printer.println(String.format("Max{W%d%d%dk} k from 0 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, 
							t-1, i, t, n-1, t-1, i, t, maxstate, maxweight));
					printer.println(String.format("Optimal states: x%d=%d", t, maxstate));
				}
			}
			else {
				Obs ot_plus_1 = O.get(t+1);
				for (int j = 0; j < n; j++) {
					int maxstate = -1;
					double maxweight = -1;
					for (int k = 0; k < n; k++) {
						double w = weight(ot_plus_1, j, k);
						if (w > maxweight) {
							maxweight = w;
							maxstate = k;
						}
						
						if (printer.isEnable())
							printer.println(String.format("W%d%d%d%d=" + Util.DECIMAL_FORMAT, t, j, t+1, k, w));
					}
					W2.set(j, maxweight);
					S2.set(j, maxstate);
					
					if (printer.isEnable())
						printer.println(String.format("Max{W%d%d%dk} k from 1 to %d is W%d%d%d%d=" + Util.DECIMAL_FORMAT, 
								t, j, t+1, n-1, t, j, t+1, maxstate, maxweight));
				}
				
				int maxstate = -1;
				double maxweight = -1;
				for (int j = 0; j < n; j++) {
					double w = W1.get(j) * W2.get(j);
					if (w > maxweight) {
						maxweight = w;
						maxstate = j;
					}
					
					if (printer.isEnable())
						printer.println(String.format("W%d%d%d%d*W%d%d%d%d=" + Util.DECIMAL_FORMAT + "*" + Util.DECIMAL_FORMAT + "=" + Util.DECIMAL_FORMAT, 
								t-1, i, t, j,
								t, j, t+1, S2.get(j), W1.get(j), W2.get(j), w));
				}
				
				int maxstate2 = S2.get(maxstate);
				states.add(maxstate);
				states.add(maxstate2);
				i = maxstate2;
				
				if (printer.isEnable()) {
					printer.println(String.format("The product W%d%d%d[%d]*W%d%d%d[%d]=" + Util.DECIMAL_FORMAT + " is maximal and so:", 
							t-1, i, t, maxstate,
							t, maxstate, t+1, maxstate2, maxweight));
					printer.println(String.format("Optimal states is: x%d=%d, x%d=%d", t, maxstate, t+1, maxstate2));
				}
				
			}
		}//End for t
		W1.clear();
		W2.clear();
		S2.clear();
		
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"\nThe longest-path (optimal state sequence) is X=" + toStateString(states));
			printer.println(buffer.toString());
		}
		
		return states;
	}
	
	
	/**
	 * 
	 * @param O
	 */
	public void em(List<Obs> O, int maxIteration) {
		int n = getStateNumber();
		int T = O.size() - 1;

		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"EM learning algorithm on observation sequence O=" + toObsString(O) + " with HMM:");
			printer.println(buffer.toString());
			printer.println(this);
		}

		//Lists of alpha (s) and beta (s) are enhanced via data structure.
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		
		List<Double> numerators = Util.newList(n, 0d);
		List<Double> glist = Util.newList(T+1, 0d);
		double preCriterion = -1;
		int iteration = 0;
		while (true) {
			alphaBetaAll(O, alphas, betas, -1);
			
			double curCriterion = 0;
			List<Double> alphaT = alphas.get(T);
			for (double alpha : alphaT)
				curCriterion += alpha;
			
			if (printer.isEnable()) {
				printer.println("\n-----Iteration " + iteration + "-----");
				serializeQuantities(O);
				printer.println(String.format("\nGiven current parameters, terminating criterion is P(O)=" + Util.DECIMAL_FORMAT, curCriterion));
			}
			
			if (preCriterion > -1 && curCriterion == preCriterion) {
				if (printer.isEnable())
					printer.println("\nThe resulted estimate is:\n" + this);
				
				break;
			}
			preCriterion = curCriterion;
			
			//Updating transition probability matrix
			for (int i = 0; i < n; i++) {
				double denominator = 0;
				for (int j = 0; j < n; j++)
					numerators.set(j, 0d);
				
				for (int t = 1; t <= T; t++) {
					List<Double> preAlphaList = alphas.get(t-1);
					List<Double> betaList = betas.get(t);
					Obs ot = O.get(t);
					for (int k = 0; k < n; k++) {
						double c = 
								preAlphaList.get(i) * 
								getA(i,k) * 
								getB(k, ot, -1) *
								betaList.get(k);
						
						numerators.set(k, numerators.get(k) + c);
						denominator += c;
					}
				}
				if (denominator == 0)
					continue;
				
				for (int j = 0; j < n; j++) {
					this.setA(i, j, (float)(numerators.get(j)/denominator));
				}
			}//End for i
			
			//Updating initial probability matrix
			double denominator = 0;
			List<Double> alpha0 = alphas.get(0);
			List<Double> beta0 = betas.get(0);
			for (int j = 0; j < n; j++) {
				double g = alpha0.get(j) * beta0.get(j);
				numerators.set(j, g);
				denominator += g;
			}
			if (denominator != 0) {
				for (int j = 0; j < n; j++) {
					this.PI.set(j, (float)(numerators.get(j)/denominator));
				}
			}
			
			//Updating observation probability distribution
			for (int j = 0; j < n; j++) {
				Distribution dist = this.B.get(j);
				if (dist instanceof AtomicDistribution) {
					for (int t = 0; t <= T; t++) {
						double g = alphas.get(t).get(j) * betas.get(t).get(j);
						glist.set(t, g);
					}
					((AtomicDistribution) dist).learn(O, glist);
				}
				else if (dist instanceof MixtureDistribution) {
					int K = ((MixtureDistribution)dist).getComponentCount();
					List<List<Double>> glistByK = gammaAllByComp(O, K, j);
					((MixtureDistribution)dist).learn(O, glistByK);
					glistByK.clear();
				}
				
			}//End for j
			
			if (printer.isEnable())
				printer.println("\nThe resulted estimate is:\n" + this);
		
			iteration ++;
			if (maxIteration > 0 && iteration >= maxIteration)
				break;
		}
		
		alphas.clear();
		betas.clear();
		numerators.clear();
		glist.clear();
	}

	
	/**
	 * 
	 * @param O
	 */
	private void serializeQuantities(List<Obs> O) {
		int n = getStateNumber();
		int T = O.size() - 1;
		List<List<Double>> alphas = Util.newList(T+1, n, 0d);
		List<List<Double>> betas = Util.newList(T+1, n, 0d);
		alphaBetaAll(O, alphas, betas, -1);
		
		if (!(this.B.get(0) instanceof MixtureDistribution)) {
			for (int i = 0; i < n; i++) {
				for (int t = 0; t <= T; t++)
					printer.println(String.format("b%d(o%d=%s)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), getB(i, O.get(t), -1)));
			}
			printer.println("");
			
			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				for (int i = 0; i < n; i++)
					printer.println(String.format("alpha%d(%d)=" + Util.DECIMAL_FORMAT, t, i, alist.get(i)));
			}
			printer.println("");
			
			for (int t = 0; t <= T; t++) {
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++)
					printer.println(String.format("beta%d(%d)=" + Util.DECIMAL_FORMAT, t, i, blist.get(i)));
			}
			printer.println("");

			for (int t = 1; t <= T; t++) {
				List<Double> pre_alist = t > 0 ? alphas.get(t-1) : null;
				List<Double> blist = betas.get(t);
				Obs ot = O.get(t);
				for (int i = 0; i < n; i++) {
					for (int j = 0; t > 0 && j < n; j++) {
						double c = 
								pre_alist.get(i) * 
								getA(i,j) * 
								getB(j, ot, -2) *
								blist.get(j);
						printer.println(String.format("c%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, j, c));
					}
				}
			}
			printer.println("");

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					double g = alist.get(i) * blist.get(i);
					printer.println(String.format("gamma%d(%d)=" + Util.DECIMAL_FORMAT, t, i, g));
				}
			}
		}
		else {
			int K = ((MixtureDistribution)(this.B.get(0))).getComponentCount();
			List<List<List<Double>>> alphasK = Util.newList(K);
			List<List<List<Double>>> betasK = Util.newList(K);
			for (int k = 0; k < K; k++) {
				List<List<Double>> alphas_temp = Util.newList(T+1, n, 0d);
				List<List<Double>> betas_temp = Util.newList(T+1, n, 0d);
				alphaBetaAll(O, alphas_temp, betas_temp, k);
	
				alphasK.add(alphas_temp);
				betasK.add(betas_temp);
			}
			
			for (int i = 0; i < n; i++) {
				for (int t = 0; t <= T; t++) {
					for (int k = 0; k < K; k++) {
						printer.println(String.format("b%d(o%d=%s,%d)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), k, getB(i, O.get(t), k)));
					}
					printer.println(String.format("b%d(o%d=%s)=" + Util.DECIMAL_FORMAT, i, t, O.get(t).toString(), getB(i, O.get(t), -1)));
				}
			}
			printer.println("");

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> aklist = alphasK.get(k).get(t);
						printer.println(String.format("alpha%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, aklist.get(i)));
					}
					printer.println(String.format("alpha%d(%d)=" + Util.DECIMAL_FORMAT, t, i, alist.get(i)));
				}
			}
			printer.println("");
			
			for (int t = 0; t <= T; t++) {
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> bklist = betasK.get(k).get(t);
						printer.println(String.format("beta%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, bklist.get(i)));
					}
					printer.println(String.format("beta%d(%d)=" + Util.DECIMAL_FORMAT, t, i, blist.get(i)));
				}
			}
			printer.println("");

			for (int t = 1; t <= T; t++) {
				List<Double> pre_alist = t > 0 ? alphas.get(t-1) : null;
				List<Double> blist = betas.get(t);
				Obs ot = O.get(t);
				for (int i = 0; i < n; i++) {
					for (int j = 0; t > 0 && j < n; j++) {
						double c = 
								pre_alist.get(i) * 
								getA(i,j) * 
								getB(j, ot, -2) *
								blist.get(j);
						printer.println(String.format("c%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, j, c));
					}
				}
			}
			printer.println("");

			for (int t = 0; t <= T; t++) {
				List<Double> alist = alphas.get(t);
				List<Double> blist = betas.get(t);
				for (int i = 0; i < n; i++) {
					for (int k = 0; k < K; k++) {
						List<Double> aklist = alphasK.get(k).get(t);
						List<Double> bklist = betasK.get(k).get(t);
						double g = aklist.get(i) * bklist.get(i);
						printer.println(String.format("gamma%d(%d,%d)=" + Util.DECIMAL_FORMAT, t, i, k, g));
					}
					double g = alist.get(i) * blist.get(i);
					printer.println(String.format("gamma%d(%d)=" + Util.DECIMAL_FORMAT, t, i, g));
				}
			}

			alphasK.clear();
			betasK.clear();
		}
		alphas.clear();
		betas.clear();
	}
	
	
	/**
	 * 
	 * @param O
	 * @param maxIteration
	 * @param maxCriterion
	 */
	@Deprecated
	public void em2(List<Obs> O, int maxIteration) {
		if (printer.isEnable()) {
			StringBuffer buffer = new StringBuffer(
					"EM learning algorithm on observation sequence O=" + toObsString(O) + " with HMM:");
			printer.println(buffer.toString());
			printer.println(this);
		}
		
		double preCriterion = -1;
		double curCriterion = -1;
		int iteration = 0;
		while (true) {
			if (printer.isEnable())
				printer.println("\n-----Iteration " + iteration + "-----");

			curCriterion = emOneLoop(O);
			
			if (printer.isEnable()) {
				StringBuffer buffer = new StringBuffer(
						String.format("Given resulted estimate, terminating criterion is P(O)=" + Util.DECIMAL_FORMAT, curCriterion));
				printer.println(buffer.toString());
			}
			
			if (preCriterion > -1 && curCriterion == preCriterion)
				break;
			preCriterion = curCriterion;
			
			iteration ++;
			if (maxIteration > 0 && iteration >= maxIteration)
				break;
		}
	}
	
	
	/**
	 * 
	 * @param O
	 */
	@Deprecated
	protected double emOneLoop(List<Obs> O) {
		int n = getStateNumber();
		int T = O.size() - 1;

		//Lists of alpha (s) and beta (s) are enhanced via data structure.
		List<?>[] abs = alphaBetaAll(O, -1);
		@SuppressWarnings("unchecked")
		List<List<Double>> alphas = (List<List<Double>>)(abs[0]);
		@SuppressWarnings("unchecked")
		List<List<Double>> betas = (List<List<Double>>)(abs[1]);
		List<Double> numerators = Util.newList(n, 0d);
		
		if (printer.isEnable())
			serializeQuantities(O);
		
		//Updating transition probability matrix
		for (int i = 0; i < n; i++) {
			double denominator = 0;
			for (int j = 0; j < n; j++)
				numerators.set(j, 0d);
			
			for (int t = 1; t <= T; t++) {
				List<Double> preAlphaList = alphas.get(t-1);
				List<Double> betaList = betas.get(t);
				Obs ot = O.get(t);
				for (int k = 0; k < n; k++) {
					double c = 
							preAlphaList.get(i) * 
							getA(i,k) * 
							getB(k, ot, -1) *
							betaList.get(k);
					
					numerators.set(k, numerators.get(k) + c);
					denominator += c;
				}
			}
			if (denominator == 0)
				continue;
			
			for (int j = 0; j < n; j++) {
				double numerator = numerators.get(j);
				this.setA(i, j, (float)(numerator/denominator));
			}
		}
		
		//Updating initial probability matrix
		double denominator = 0;
		List<Double> alpha0 = alphas.get(0);
		List<Double> beta0 = betas.get(0);
		for (int j = 0; j < n; j++) {
			double g = alpha0.get(j) * beta0.get(j);
			numerators.set(j, g);
			denominator += g;
		}
		if (denominator != 0) {
			for (int j = 0; j < n; j++) {
				this.PI.set(j, (float)(numerators.get(j)/denominator));
			}
		}
		
		//Updating observation probability distribution
		for (int j = 0; j < n; j++) {
			Distribution dist = this.B.get(j);
			if (dist instanceof AtomicDistribution) {
				List<Double> glist = Util.newList(T+1);
				for (int t = 0; t <= T; t++) {
					double g = alphas.get(t).get(j) * betas.get(t).get(j);
					glist.add(g);
				}
				((AtomicDistribution) dist).learn(O, glist);
				glist.clear();
			}
			else if (dist instanceof MixtureDistribution) {
				alphas.clear();
				betas.clear();
				numerators.clear();

				int K = ((MixtureDistribution)dist).getComponentCount();
				List<List<Double>> glistByK = gammaAllByComp(O, K, j);
				((MixtureDistribution)dist).learn(O, glistByK);
				glistByK.clear();
			}
			
		}//End for j
		alphas.clear();
		betas.clear();
		numerators.clear();
		
		if (printer.isEnable())
			printer.println("\nThe resulted estimate is:\n" + this);

		return prob(O);
	}

	
	/**
	 * 
	 * @param O
	 * @param state
	 * @return
	 */
	public double condProb(List<Obs> O, List<Integer> X) {
		double p = 1;
		int T = O.size() - 1;
		for (int t = 0; t <= T; t++) {
			Obs o = O.get(t);
			int x = X.get(t);
			p *= getB(x, o, -1);
		}
		return p;
	}

	
	/**
	 * 
	 * @param O
	 * @param X
	 * @return
	 */
	public double jointProb(List<Obs> O, List<Integer> X) {
		double p = getPI(X.get(0));
		int T = O.size() - 1;
		for (int t = 1; t <= T; t++ ) {
			p *= getA(X.get(t-1), X.get(t));
		}
		return p * condProb(O, X);
	}

	
	/**
	 * 
	 * @param O
	 * @return
	 */
	public double prob(List<Obs> O) {
		int T = O.size() - 1;
		List<Double> alist = alpha(O, T, -1);
		double p = 0;
		for (double alpha : alist)
			p += alpha;
		
		alist.clear();
		return p;
	}
	
	
	/**
	 * 
	 * @param X
	 * @return
	 */
	public double prob2(List<Integer> X) {
		double p = getPI(X.get(0));
		
		int T = X.size() - 1;
		for (int t = 1; t <= T; t++)
			p *= getA(X.get(t-1), X.get(t));
		
		return p;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		int n = getStateNumber();
		
		if (S.size() > 0) {
			buffer.append("States S={");
			for (int i = 0; i < S.size(); i++) {
				if (i > 0)
					buffer.append(", ");
				buffer.append("s" + i + "=" + S.get(i));
			}
			buffer.append("}\n\n");
		}
		
		if (OBS.size() > 0) {
			buffer.append("Observations O={");
			for (int i = 0; i < OBS.size(); i++) {
				if (i > 0)
					buffer.append(", ");
				buffer.append("o" + i + "=" + OBS.get(i));
			}
			buffer.append("}\n\n");
		}

		buffer.append("Transition probability matrix A\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append("\n");
			
			for (int j = 0; j < n; j++) {
				if (j > 0)
					buffer.append(" ");
				buffer.append(String.format(Util.DECIMAL_FORMAT, getA(i, j)));
			}
		}
		
		buffer.append("\n\nInitial state probability PI\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append(" ");
			
			buffer.append(String.format(Util.DECIMAL_FORMAT, PI.get(i)));
		}

		buffer.append("\n\nObservation probability matrix or distribution B\n");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				buffer.append("\n");
			
			buffer.append("Distribution " + i + ":\n" + B.get(i).toString());
		}
		
		return buffer.toString();
	}

	
	/**
	 * 
	 * @param X
	 * @return
	 */
	protected static String toStateString(List<Integer> X) {
		int T = X.size() - 1;
		StringBuffer buffer = new StringBuffer("{");
		for (int t = 0; t <= T; t++) {
			if (t > 0)
				buffer.append(", ");
			buffer.append("x(" + t + ")=" + X.get(t));
		}
		buffer.append("}");
		
		return buffer.toString();
	}
	
	
	/**
	 * 
	 * @param O
	 * @return
	 */
	protected static String toObsString(List<Obs> O) {
		int T = O.size() - 1;
		StringBuffer buffer = new StringBuffer("{");
		for (int t = 0; t <= T; t++) {
			if (t > 0)
				buffer.append(", ");
			buffer.append("o(" + t + ")=" + O.get(t));
		}
		buffer.append("}");
		
		return buffer.toString();
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
		try {
			if (S != null)
				S.clear();
			
			if (OBS != null)
				OBS.clear();
			
			if (A != null) {
				A.clear();
				A = null;
			}
			
			if (PI != null) {
				PI.clear();
				PI = null;
			}
			
			if (B != null) {
				B.clear();
				B = null;
			}
			
			if (printer != null)
				printer.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		close();
		super.finalize();
	}
	
	


}
