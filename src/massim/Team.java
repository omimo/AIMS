package massim;

import java.util.Random;

/**
 * Team.java
 * 
 *
 * @author Omid Alemi
 * @version 1.2 2011/10/17
 */
public class Team {

	private static int nextID=1;
	private int id;
	
	public static int teamSize;
	public static int initResCoef;

	private CommMedium commMedium;
	private int[][] actionCostsMatrix;
		
	
	private static Random rnd1 = new Random();
	public static enum TeamStepCode {OK, DONE, ERR}
	private boolean debuggingInf = true;

	public int testRunCounter;
	/**
	 * Default constructor
	 */
	public Team() {				
		id = nextID++;
		commMedium = new CommMedium();	
	}
	

	public void initializeRun(RowCol[] initAgentsPos, RowCol[] goals, int[][]actionCostMatrix) {
		logInf("initilizing for a new run.");
		commMedium.clear();
		
		for (int i=0;i<teamSize;i++)
			for (int j=0;j<SimulationEngine.numOfColors;j++)
				this.actionCostsMatrix[i][j] = actionCostMatrix[i][j];					
	}
	

	public TeamStepCode round(Board board) {		
		logInf("starting a new round."+testRunCounter);
		for (int i=0;i<Team.teamSize;i++)
		{
			
			int[][] probActionCostMatrix = new int[Team.teamSize][SimulationEngine.numOfColors];				
			for (int p = 0; p < Team.teamSize; p++)
				for (int q = 0; q < SimulationEngine.numOfColors; q++)						
					if (rnd1.nextDouble() < SimulationEngine.mutualAwareness || p==i)						
						probActionCostMatrix[p][q] = actionCostsMatrix[p][q];						
					else							
						probActionCostMatrix[p][q] = SimulationEngine.actionCostsRange[rnd1.nextInt(SimulationEngine.actionCostsRange.length)];
					                     
		}
		
		
		if (testRunCounter >0)
		{
			testRunCounter--;
			return TeamStepCode.OK;
		}
		else
			{
				logInf(" is done!");	
				return TeamStepCode.DONE;				
			}
	}

	
	/**
	 * To get the collective reward points for the team
	 * 
	 * @return				The amount of reward points that all the team's agents own
	 */
	public int teamRewardPoints() {
		int sum = 0;
	//	for (Agent a: agents)
	//		sum += a.rewardPoints();
		return sum;
	}
	
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[Team "+id+"]: " + msg);
	}
}
