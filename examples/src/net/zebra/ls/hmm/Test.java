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
public class Test {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Factory factory = new FactoryImpl();
		
		DefaultHMM discreteHMM = ((FactoryImpl.HMMWrapper)factory.createDiscreteHMM(
				new float[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new float[] {0.33f, 0.33f, 0.33f}, 
				new float[][] {
					{0.60f, 0.20f, 0.15f, 0.05f},
					{0.25f, 0.25f, 0.25f, 0.25f},
					{0.05f, 0.10f, 0.35f, 0.50f}})).getHMMImpl();
		discreteHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		discreteHMM.setObservationNames(Arrays.asList("dry", "dryish", "damp", "soggy"));
		
		@SuppressWarnings("unused")
		DefaultHMM randomDiscreteHMM = ((FactoryImpl.HMMWrapper)factory.createDiscreteHMM(50, 100)).
				getHMMImpl();
		
		DefaultHMM normalHMM = ((FactoryImpl.HMMWrapper)factory.createNormalHMM(
				new float[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new float[] {0.33f, 0.33f, 0.33f}, 
				new float[] {0.87f, 0.14f, 0.39f}, 
				new float[] {0.9f, 0.9f, 0.9f},
				Util.PROB_EPSILON)).getHMMImpl(); 
		normalHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		
		DefaultHMM exponentialHMM = ((FactoryImpl.HMMWrapper)factory.createExponentialHMM(
				new float[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new float[] {0.33f, 0.33f, 0.33f}, 
				new float[] {1.0f/0.87f, 1.0f/0.14f, 1.0f/0.39f},
				Util.PROB_EPSILON)).getHMMImpl(); 
		exponentialHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		
		DefaultHMM normalMixtureHMM = ((FactoryImpl.HMMWrapper)factory.createNormalMixtureHMM(
				new float[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new float[] {0.33f, 0.33f, 0.33f}, 
				new float[][] {
					{0.87f, 0.15f}, {0.39f, 0.89f}, {0.14f, 0.37f}},
				new float[][] {
					{1f, 1f}, {1f, 1f}, {1f, 1f}},
				new float[][] {
					{0.6f, 0.4f}, {0.5f, 0.5f}, {0.4f, 0.6f}},
				Util.PROB_EPSILON)).getHMMImpl();
		normalMixtureHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));

		DefaultHMM hmm = discreteHMM;
		
		List<Obs> O;
		O = MonoObs.createObsList(3f, 0f, 1f);
//		O = MonoObs.createObsListRandomInteger(100, hmm.getStateNumber());
//		O = MonoObs.createObsList(0.88f, 0.13f, 0.38f);
		
		hmm.getPrinter().open("/result.txt");
		hmm.em(O, DefaultHMM.EM_MAX_ITERATION);
//		hmm.viterbi(O);

		hmm.getPrinter().close();
	}

}
