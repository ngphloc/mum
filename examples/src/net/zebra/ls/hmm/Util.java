/**
 * 
 */
package net.zebra.ls.hmm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
final public class Util {
	
	
	/**
	 * 
	 */
	public final static float PROB_EPSILON = 0.01f;

	
	/**
	 * 
	 */
	public final static String DECIMAL_FORMAT = "%.6f";

	
	/**
	 * Creating a custom list. List can be database record.
	 * 
	 * @param capacity Capacity of created list
	 * @return {@link List}
	 */
	public static <T> List<T> newList(int capacity) {
		ArrayList<T> array = new ArrayList<T>(capacity);
		return array;
	}
	
	
	public static <T> List<T> newList(int size, T initialValue) {
		List<T> array = newList(size);
		for (int i = 0; i < size; i++) {
			array.add(initialValue);
		}
		return array;
	}
	
	
	public static <T> List<List<T>> newList(int rows, int columns, T initialValue) {
		List<List<T>> matrix = newList(rows);
		for (int i = 0; i < rows; i++) {
			List<T> array = newList(columns, initialValue);
			matrix.add(array);
		}
		
		return matrix;
	}


}
