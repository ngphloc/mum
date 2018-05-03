/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.List;
import java.util.Random;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MonoObs implements Obs {
	
	
	/**
	 * 
	 */
	public float value;
	
	
	/**
	 * 
	 * @param value
	 */
	public MonoObs(int value) {
		this.value = value;
	}

	
	/**
	 * 
	 * @param value
	 */
	public MonoObs(float value) {
		this.value = value;
	}
	
	
	/**
	 * 
	 * @param numbers
	 * @return
	 */
	public static List<Obs> createObsList(Number...numbers) {
		List<Obs> obsList = Util.newList(numbers.length);
		for (Number number : numbers) {
			obsList.add(new MonoObs(number.floatValue()));
		}
		
		return obsList;
	}


	/**
	 * 
	 * @param size
	 * @param maxExclusiveInteger
	 * @return
	 */
	public static List<Obs> createObsListRandomInteger(int size, int maxExclusiveInteger) {
		List<Obs> obsList = Util.newList(size);
		Random rnd = new Random();
		for (int i = 0; i < size; i++) {
			obsList.add(new MonoObs(rnd.nextInt(maxExclusiveInteger)));
		}
		
		return obsList;
	}
	
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public static List<Obs> createObsListRandomReal(int size) {
		List<Obs> obsList = Util.newList(size);
		Random rnd = new Random();
		for (int i = 0; i < size; i++) {
			obsList.add(new MonoObs(rnd.nextFloat()));
		}
		
		return obsList;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format(Util.DECIMAL_FORMAT, value);
	}
	
	
}
