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

	private static int nextID = 1; // for debugging purposes only
	private int id;

	public static int teamSize;
	public static int initResCoef;

	private CommMedium commMedium;
	private int[][] actionCostsMatrix;

	private static Random rnd1 = new Random();

	public static enum TeamStepCode {
		OK, DONE, ERR
	}

	private boolean debuggingInf = true;
	public int testRunCounter;

	/**
	 * Default constructor
	 */
	public Team() {
		id = nextID++;
		commMedium = new CommMedium();
	}

	/**
	 * Called by the simulation engine (SimulationEngine.initializeRun())
	 * to initialize the team and agents for a new run. 
	 * 
	 * It should reset necessary variables values.
	 * 
	 * @param initAgentsPos					Array of initial agents	position
	 * @param goals							Array of initial goals position
	 * @param actionCostMatrix				Matrix of action costs
	 */
	public void initializeRun(RowCol[] initAgentsPos, RowCol[] goals,
			int[][] actionCostMatrix) {
		logInf("initilizing for a new run.");
		commMedium.clear();

		for (int i = 0; i < teamSize; i++)
			for (int j = 0; j < SimulationEngine.numOfColors; j++)
				this.actionCostsMatrix[i][j] = actionCostMatrix[i][j];
	}

	/**
	 * Called by the simulation engine (SimulationEngine.round()) to start 
	 * a new round of the simulation for this specific team.
	 *  
	 * @param board							The current board representation
	 * @return								The proper TeamStepCode based on
	 * 										the team's current state after at 
	 * 										the end of the round.
	 */
	public TeamStepCode round(Board board) {
		logInf("starting a new round");
		for (int i = 0; i < Team.teamSize; i++) {

			int[][] probActionCostMatrix = 
				new int[Team.teamSize][SimulationEngine.numOfColors];
			
			for (int p = 0; p < Team.teamSize; p++)
				for (int q = 0; q < SimulationEngine.numOfColors; q++)
					if (rnd1.nextDouble() < SimulationEngine.mutualAwareness
							|| p == i)
						probActionCostMatrix[p][q] = 
							actionCostsMatrix[p][q];
					else
						probActionCostMatrix[p][q] = 
							SimulationEngine.actionCostsRange[
							 rnd1.nextInt(
									 SimulationEngine.actionCostsRange.length)];

		}

		if (testRunCounter > 0) {  // For debugging purposes only; 
			testRunCounter--;	   // indicates when the team should be done
			return TeamStepCode.OK;
		} else {
			logInf(" is done!");
			return TeamStepCode.DONE;
		}
	}

	
	/**
	 * To get the collective reward points of the team members
	 * 
	 * @return 						The amount of reward points that all the 
	 * 								team's agents own
	 */
	public int teamRewardPoints() {
		int sum = 0;
		// for (Agent a: agents)
		// sum += a.rewardPoints();
		return sum;
	}

	/**
	 * Prints the log message into the output if the information debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[Team " + id + "]: " + msg);
	}
}
