package massim;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import massim.Team.TeamRoundCode;

/**
 * The main class of the simulator. 
 * 
 * @author Omid Alemi
 * @version 3.0 2012/07/05
 * 
 */
public class SimulationEngine implements SEControl{
	private Logger logger = Logger.getLogger("all"); 
	
	public static ParamList pList;
	
	public static int[] colorRange;
	public static int[] actionCostsRange; 
									 
	public static int numOfColors;
	public static int numOfTeams;
	public static int numOfMatches;
	private int boardh = 10;
	private int boardw = 10;

	//public static double disturbanceLevel;	

	private Team[] teams;
	private Board mainBoard;
	private int[][] actionCostsMatrix;
	private RowCol[] goals;
	private RowCol[] initAgentsPos;

	private int roundCounter;
	private int[][] teamsScores;
	private int numOfRuns;

	private boolean debuggingInf = false;
	private boolean debuggingErr = true;

	
	//
	Random rnd = new Random();
	//
	/**
	 * SIMOK: The round executed without any problem and there is
	 *        at least one active team.
	 *        
	 * SIMEND: All the teams are done.
	 * 
	 * SIMERR: There was a problem in the current round.
	 */
	public static enum SimRoundCode {
		SIMOK, SIMEND, SIMERR
	}

	/**
	 * The constructor method
	 * 
	 * @param teams					The array of teams to be involved in 
	 * 								the simulations.
	 */
	public SimulationEngine() {
		
		pList = new ParamList();
		
		
	}

	public void loadTeams(Team[] teams) {
		logInf("SE created for " + teams.length + " teams.");
		this.teams = teams;
		SimulationEngine.numOfTeams = teams.length;
	}
	/**
	 * Initializes the simulation engine for a new experiment. 
	 * 
	 * Each experiment consists in a number of runs.
	 * The final score of each team for each run will be stored in an 
	 * array.
	 * 
	 * @param numOfRuns				Number of desired runs for current 
	 * 								experiment setting.
	 */
	public void initializeExperiment(int numOfRuns) {
		logInf("----- Experiment initialized for " + numOfRuns
				+ " number of runs -----");
		teamsScores = new int[numOfTeams][numOfRuns];
		this.numOfRuns = numOfRuns;
	}

	/**
	 * Prepares the simulation engine parameters for a new run. 
	 * 
	 * This includes a new board setting, new action costs matrix, and
	 * possibly new positions for initial agents' position and goals' position.
	 * 
	 * The method also invokes the Team.initializeRun() for all teams.
	 */
	public void initializeRun() {
		logInf("--- Initializing the run ---");

		mainBoard = Board.randomBoard
					(boardh, boardw,SimulationEngine.colorRange);
		
		logInf("The board setting for this run is:\n" + mainBoard.toString());
			
		//Random rnd = new Random();
		
		actionCostsMatrix = new int[paramI("Team.teamSize")][numOfColors];
		for (int i = 0; i < paramI("Team.teamSize"); i++)
		{
			for (int j = 0; j < numOfColors; j++)
				if (rnd.nextInt(2) % 2 == 1)
					actionCostsMatrix[i][j] = actionCostsRange[
					                    rnd.nextInt(actionCostsRange.length/2)];
				else
					actionCostsMatrix[i][j] = actionCostsRange[actionCostsRange.length/2+
					   					                    rnd.nextInt(actionCostsRange.length/2)];
		}

		for (int t = 0; t < numOfTeams; t++)
			teams[t].initializeRun(actionCostsMatrix);
	}

	/**
	 * Prepares the simulation parameters for a new match within the current
	 * run.
	 * 
	 */
	public void initializeMatch() {
		
		logInf("-| Initializing the match |-");
		
		roundCounter = 0;
		
		goals = new RowCol[paramI("Team.teamSize")];
		for (int i = 0; i < paramI("Team.teamSize"); i++)
			goals[i] = randomPos(boardh, boardw);

		initAgentsPos = new RowCol[paramI("Team.teamSize")];
		for (int i = 0; i < paramI("Team.teamSize"); i++)
			initAgentsPos[i] = randomPos(boardh, boardw);
		
		for (int t = 0; t < numOfTeams; t++)
			teams[t].initializeMatch(initAgentsPos, goals);
	}
	
	/**
	 * Executes one round of the simulation.
	 * 
	 * Each round of the simulation consist in updating the board; executing
	 * each team; and checking the current status of the simulation.
	 * 
	 * It is possible to implement error handling mechanisms for this method.
	 * 
	 * @return 				The proper simulation-round-code representing 
	 * 						the status of the round. 
	 */
	public SimRoundCode round() {
		roundCounter++;
		logInf("Round #" + roundCounter + " started ...");

		double disturbanceLevel = getParamD("env.disturbance");
		
		logInf("Changing the board setting based on the disturbance level of "+
				disturbanceLevel);
		mainBoard.disturb(disturbanceLevel);

		TeamRoundCode[] tsc = new TeamRoundCode[teams.length];
		for (int t = 0; t < SimulationEngine.numOfTeams; t++) {		
			tsc[t] = teams[t].round(mainBoard);
			
			logInf(teams[t].getClass().getSimpleName()
					+ " returned with the code: " + tsc[t].toString());
		}

		boolean allTeamsDone = true;
		for (int t = 0; t < teams.length; t++) {
			if (tsc[t] == TeamRoundCode.OK) {
				allTeamsDone = false;
				break;
			}
		}

		if (allTeamsDone)
			return SimRoundCode.SIMEND;
		else
			return SimRoundCode.SIMOK;
	}

	/**
	 * Executes the simulator for one whole run. 
	 * 
	 * A run consists in invoking the round() until it indicates that it is 
	 * either done or there was a problem during the execution.
	 * 
	 * @return 				The return code of the last round method
	 * 						invocation,	representing the return code 
	 * 						of the run.
	 */
	public void run(int r) {
		logInf("-- The run started --");
				
		for (int m=1;m<=SimulationEngine.numOfMatches;m++)
		{
			initializeMatch();
			
			SimRoundCode src = SimRoundCode.SIMOK;
			while (src == SimRoundCode.SIMOK) {
				src = round();
				//Thread.currentThread().suspend();
			}
			
			for (int t=0;t<numOfTeams;t++)
			{
				teamsScores[t][r] += teams[t].teamRewardPoints();
				logInf("Team "+ t+"'s scores for this match("+m+") ="+teams[t].teamRewardPoints());
				logInf("Team "+ t+"'s total score = "+teamsScores[t][r]);
			}				
		}
		
		logInf("-- The run ended --");				
	}

	
	/*
	 public initializeMatch(int m) {
	 
	 	agInitPos[] = ...
	 	agGoalPos[] = ...
	 	for each team t 
	 		t.initializeMatch(agInitPos, agGoalPos)
	 
	 
	 }
	 */
	
	/**
	 * Executes the simulation for one whole experiment. 
	 * 
	 * A experiment consists in multiple runs using the identical set 
	 * of parameters, but with a new board and costs settings.
	 * 
	 * @return 				The score of each team averaged over multiple
	 * 						runs.
	 */
	public int[] runExperiment() {
		logInf("---- The experiment started ----");
		for (int r = 0; r < numOfRuns; r++) {
			initializeRun();
			run(r);
			for (int t = 0; t < numOfTeams; t++) {			
				logInf("Team " + teams[t].getClass().getSimpleName()
						+ " scored " + teamsScores[t][r]
						+ " for this run.");
			}
		}
		logInf("---- The experiment ended ----");

		int[] averageTeamScores = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++)
			averageTeamScores[t] = average(teamsScores[t]);

		return averageTeamScores;
	}

	/**
	 * Calculates the average of the given integer array.
	 * 
	 * Note: it calculates the average using a double division then
	 * rounding the result to the nearest integer.
	 * 
	 * @param numbers	    The array of integer numbers
	 * @return 				The average of the input array
	 */
	private int average(int[] numbers) {
		int sum = 0;
		for (int i = 0; i < numbers.length; i++)
			sum += numbers[i];
		return (int) Math.round((double)sum / numbers.length);
	}

	/**
	 * Prints the log message into the output if the information debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg			 The desired message to be printed
	 */
	private void logInf(String msg) {
		if (debuggingInf)
		{
		    logger.info("[SimulationEngine]: " +msg);
			//System.out.println("[SimulationEngine]: " + msg);
			
		}
	}

	/**
	 * Prints the log message into the output if the error debugging level is
	 * turned on (debuggingErr).
	 * 
	 * @param msg			  The desired message to be printed
	 */
	private void logErr(String msg) {
		if (debuggingErr)
			System.err.println("[SimulationEngine]: " + msg);
	}
	
	/**
	 * Generates a random position within the specified range
	 * 
	 * @param h					The height of the board
	 * @param w					The width of the board
	 * @return					The generated position
	 */
	private RowCol randomPos(int h, int w) {
		//Random rnd = new Random();
		
		return new RowCol(rnd.nextInt(h),rnd.nextInt(w));
	}
	
	public void addParam(String p, int v) {
		pList.add(p, v);
	}
	
	public void addParam(String p, double v) {
		pList.add(p, v);
	}
	
	public int getParamI(String p) {
		return pList.paramI(p);
	}
	
	public double getParamD(String p) {
		return pList.paramD(p);
	}
	
	public void changeParam(String p, int nv) {
		pList.change(p, nv);
	}
	
	public void changeParam(String p, double nv) {
		pList.change(p, nv);
	}

	@Override
	public int[] startExperiment() {
		
		return runExperiment();
		
	}

	@Override
	public void setupExeperiment(int numberOfRuns) {
		initializeExperiment(numberOfRuns);
		
	}

	@Override
	public int[][] getBoardInstance() {
		int[][] b = new int[boardh][boardw];
		
		for (int i=0;i<boardh;i++)
			for (int j=0;j<boardw;j++)
				b[i][j]=mainBoard.getBoard()[i][j];
		
		return b;
	}

	@Override
	public void loadFromFile(String filename) throws IOException {
		pList.loadFromFile(filename);
		
	}

	@Override
	public Map<String, Object> getList() {
		return pList.getList();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	/**
	 * 
	 * return: 
	 *    0 : normal
	 *    1 : done     
	 */
	@Override
	public int stepExp() {
		
		
		
		logInf("---- The experiment ended ----");
		return 0;
	}

	@Override
	public void setupDebugExp() {
		teamsScores = new int[numOfTeams][numOfRuns];
		
	}

	@Override
	public int[] getDebugResults() {
		

		int[] averageTeamScores = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++)
			averageTeamScores[t] = average(teamsScores[t]);

		return averageTeamScores;
	}
	
	/*
	 * Returns the integer parameter from the parameters list
	 */
	protected int paramI(String p) {
		return SimulationEngine.pList.paramI(p);
	}
	
	/*
	 * Returns the double parameter from the parameters list
	 */
	protected double paramD(String p) {
		return SimulationEngine.pList.paramD(p);
	}
}


