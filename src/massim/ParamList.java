package massim;

import java.util.HashMap;


public class ParamList {
	private HashMap<String,Object> list;
	
	/**
	 * The constructor
	 * 
	 * Instantiates the list object
	 */
	public ParamList() {
		list = new HashMap<String, Object>();		
	}
	
	/**
	 * Adds an integer parameter to the list
	 * 
	 * @param p			The full parameter name (namespace)
	 * @param v			The parameter value
	 */
	public void add(String p, int v) {
		if (!list.containsKey(p))
			list.put(p, (Integer)v);
		else
			throw new RuntimeException("The parameter "+p+" is already on the list!");
	}
	
	/**
	 * Adds a double parameter to the list
	 * 
	 * @param p			The full parameter name (namespace)
	 * @param v			The parameter value
	 */
	public void add(String p, double v) {
		if (!list.containsKey(p))
			list.put(p, (Double)v);
		else
			throw new RuntimeException("The parameter "+p+" is already on the list!");
	}
	
	/**
	 * Reads an integer parameter value from the list
	 * 
	 * @param p			The full parameter name (namespace)
	 * @return			The parameter's value
	 */
	public int paramI(String p) {
		if (list.containsKey(p))
			return (Integer)list.get(p);
		else
			throw new RuntimeException("The requested parameter does not exist in the list.");
	}
	
	/**
	 * Reads a double parameter value from the list
	 * 
	 * @param p			The full parameter name (namespace)
	 * @return			The parameter's value
	 */
	public Double paramD(String p) {
		if (list.containsKey(p))
			return (Double)list.get(p);
		else
			throw new RuntimeException("The requested parameter does not exist in the list.");
	}
	
	/**
	 * Changes an integer parameter's value
	 * 
	 * @param p			The full parameter name (namespace)
	 * @param nv		The parameter value
	 */
	public void change(String p, int nv) {
		if (list.containsKey(p))
			list.put(p,(Integer)nv);
		else
			throw new RuntimeException("The requested parameter does not exist in the list.");
	}
	
	/**
	 * Changes a double parameter's value
	 * 
	 * @param p			The full parameter name (namespace)
	 * @param nv		The parameter value
	 */
	public void change(String p, double nv) {
		if (list.containsKey(p))
			list.put(p,(Double)nv);
		else
			throw new RuntimeException("The requested parameter does not exist in the list.");
	}
}
