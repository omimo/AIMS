package massim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


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
	
	
	/**
	 * Loads the parameters from a file
	 * 
	 * File format: 
	 * paramname,paramtype,paramvalue
	 * 
	 * paramtype can be:
	 *   D: double
	 *   I: integer
	 *   S: String
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public void loadFromFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader( new FileReader(filename));
		
		list.clear();
		
		String paramEntry;
		String paramName;
		String paramType;
		StringTokenizer st = null;
		
		while ((paramEntry = br.readLine()) != null)   {
			System.out.println(paramEntry);
			st = new StringTokenizer(paramEntry, ",");
            
            if (st.hasMoreTokens())
            {
            	
                   paramName = st.nextToken();
                   paramType = st.nextToken();
                   if (paramType.equals(("D")))
                	   add(paramName, Double.parseDouble(st.nextToken()));
                   else if (paramType.equals("I"))
                	   add(paramName, Integer.parseInt((st.nextToken())));
            }
		}
	}
	
	/*
	 * 
	 * NOTE: this should be changed to make a clone.
	 */
	public Map<String,Object> getList() {
		return new HashMap<String,Object>(list);
	}
	
}
