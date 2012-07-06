package massim;

public interface SEControl {
	
	/* simulation control */
	
	public void setupExeperiment(int numberOfRuns);
	public int[] startExperiment();
	
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
}
