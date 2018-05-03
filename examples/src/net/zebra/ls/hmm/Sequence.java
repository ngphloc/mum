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
@Deprecated
public class Sequence<T> {

	
	/**
	 * 
	 */
	protected List<T> list;
	
	
	/**
	 * 
	 * @param n
	 */
	public Sequence(int n) {
		init(n);
	}

	
	/**
	 * 
	 * @param n
	 */
	public Sequence() {
		this(0);
	}
	
	
	/**
	 * 
	 * @param data
	 */
	public Sequence(T[] data) {
		init(data.length);
		for (int i = 0; i < data.length; i++)
			list.set(i, data[i]);
	}
	
	
	/**
	 * 
	 * @param seq
	 */
	public void copy(Sequence<T> seq) {
		list.clear();
		list.addAll(seq.list);
	}
	
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public T get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * 
	 * @param index
	 * @param element
	 * @return
	 */
	public T set(int index, T element) {
		return list.set(index, element);
	}
	
	
	/**
	 * 
	 * @param element
	 * @return
	 */
	public boolean add(T element) {
		return list.add(element);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * 
	 * @param n
	 */
	private void init(int n) {
		list = Util.newList(n);
		for (int i = 0; i < n; i++)
			list.add((T)null);
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return list.toString();
	}
	
	
}
