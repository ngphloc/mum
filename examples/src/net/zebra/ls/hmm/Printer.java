/**
 * 
 */
package net.zebra.ls.hmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Printer implements AutoCloseable {

	
	/**
	 * 
	 */
	protected PrintWriter printer = null;

	
	/**
	 * 
	 */
	protected boolean system = false;
	
	
	/**
	 * 
	 */
	protected boolean paused = false;

	
	/**
	 * 
	 */
	public Printer() {

	}

	/**
	 * 
	 * @param out
	 */
	public Printer(Writer out) {
		open(out);
	}
	
	
	/**
	 * 
	 * @param out
	 */
	public Printer(OutputStream out) {
		open(out);
	}

	
	/**
	 * 
	 * @param file
	 */
	public Printer(File file) {
		open(file);
	}

	
	/**
	 * 
	 * @param filePath
	 */
	public Printer(String filePath) {
		open(filePath);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void print(Object message) {
		if (isEnable()) {
			printer.print(message);
			printer.flush();
		}
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void println(Object message) {
		if (isEnable()) {
			printer.println(message);
			printer.flush();
		}
	}
	
	
	/**
	 * 
	 * @param out
	 */
	public void open(Writer out) {
		close();
		printer = new PrintWriter(out, true);
		paused = false;
		system = false;
	}
	
	
	/**
	 * 
	 * @param out
	 */
	public void open(OutputStream out) {
		close();
		printer = new PrintWriter(out, true);
		paused = false;
		system = (out == System.out);
	}

	
	/**
	 * 
	 * @param file
	 */
	public void open(File file) {
		try {
			close();
			printer = new PrintWriter(file);
			paused = false;
			system = false;
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			close();
		}
	}

	
	/**
	 * 
	 * @param filePath
	 */
	public void open(String filePath) {
		try {
			close();
			printer = new PrintWriter(filePath);
			paused = false;
			system = false;
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			close();
		}
	}
	

	/**
	 * 
	 * @return
	 */
	public boolean isEnable() {
		return (printer != null) && (!paused);
	}
	
	
	/**
	 * 
	 */
	public void pause() {
		paused = true;
	}
	
	
	/**
	 * 
	 */
	public void resume() {
		paused = false;
	}

	
	@Override
	public void close() {
		if (printer == null)
			return;
		
		try {
			printer.flush();
			if (!system) 
				printer.close();
			printer = null;
			system = false;
			paused = false;
		}
		catch (Throwable e) {
			e.printStackTrace();
			printer = null;
		}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		close();
		super.finalize();
	}
	
}
