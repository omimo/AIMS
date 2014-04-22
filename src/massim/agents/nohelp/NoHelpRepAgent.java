package massim.agents.nohelp;

import massim.CommMedium;
import massim.Path;
import massim.RowCol;

public class NoHelpRepAgent extends NoHelpAgent {

	boolean dbgInf = false;
	boolean dbgErr = true;
	
	public static double WREP; 
	int replanCount;
	
	public NoHelpRepAgent(int id, CommMedium comMed) {
		
		super(id, comMed);
		replanCount=0;
	}
	
	/**
	 * The send cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		
		logInf("Replan Send Cycle");	
		double wellbeing = wellbeing();
		logInf("My wellbeing = " + wellbeing);
		
		if (wellbeing < NoHelpRepAgent.WREP && canReplan()) {
			findPath();
			logInf("Replanning: Chose this path: " + path().toString());
			replanCount++;
			wellbeing = wellbeing();
			logInf("My wellbeing = " + wellbeing);
		}
		
		return super.sendCycle();
	}

	/**
	 * Calculates the estimated cost of a path p.
	 * 
	 * @param p						The agent's path
	 * @return						The estimated cost
	 */
	private double estimatedCost(Path p) {
		
		int l = p.getNumPoints();
		double sigma = 1 - disturbanceLevel;
		double eCost = 0.0;		
		if (Math.abs(sigma-1) < 0.000001)
		{
			for (int k=0;k<l;k++)
				eCost += getCellCost(p.getNthPoint(k));			
		}
		else
		{
			double m = getAverage(actionCosts()); /*TODO: check this! */				 
			eCost = (l - ((1-Math.pow(sigma, l))/(1-sigma))) * m;		
			for (int k=0;k<l;k++)
				eCost += Math.pow(sigma, k) * getCellCost(p.getNthPoint(k));
		}
		return eCost;
	}
	
	/**
	 * Calculates the agent's well being. Eq: (Res - Ecost) / ((RemLen + 1) * AvgCost)
	 * 
	 * @return						The agent's well being
	 */
	protected double wellbeing() {		
		
		Path pRemaining = remainingPath(pos());
		double eCost = estimatedCost(pRemaining);
		double avgCost = getAverage(actionCosts());
		double resPoints = resourcePoints(); 
		double resWB = (resPoints - eCost)/
				((pRemaining.getNumPoints() + 1) * avgCost);
		return resWB;
	}
	
	/**
	 * Finds the remaining path from the given cell.
	 * 
	 * The path DOES NOT include the given cell and the starting cell 
	 * of the remaining path would be the next cell.
	 * 
	 * @param from					The cell the remaining path would be
	 * 								generated from.
	 * @return						The remaining path.
	 */
	private Path remainingPath(RowCol from) {
		Path rp = new Path(path());
		
		while (!rp.getStartPoint().equals(from))
			rp = rp.tail();
		
		return rp.tail();
	}
	
	/**
	 * Checks whether the agent has enough resources in order to replan
	 * 
	 * @author Mojtaba
	 */
	private boolean canReplan() {
		return (resourcePoints() >= planCost());
	}
	
	
	//*******************************************************************
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	protected void logInf(String msg) {
		if (dbgInf)
			System.out.println("[NoHelpRepAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][NoHelpRepAgent " + id() + 
							   "]: " + msg);
	}

}

