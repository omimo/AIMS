package massim;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public interface SEControl {
	
	/* simulation control */
	
	public void setupExeperiment(int numberOfRuns);
	public int[] startExperiment();
	
	public void setupDebugExp();
	public int stepExp();
	public int[] getDebugResults();
	
	/* misc */
	public Logger getLogger();
	
	/* Game */
	public int[][] getBoardInstance();
	
	/*
	 * set teams
	 * 
	 * 
	 */
	
	/* Parameters */
	public void addParam(String p, int v);
	public void addParam(String p, double v);
	public int getParamI(String p);	
	public double getParamD(String p);
	public void changeParam(String p, int nv);
	public void changeParam(String p, double nv);
	public void loadFromFile(String filename) throws IOException;
	public Map<String,Object> getList();
}
