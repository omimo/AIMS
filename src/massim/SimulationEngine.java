package massim;

import java.util.Calendar;
import java.util.Random;

import massim.Team.TeamRoundCode;

/**
 * The main class of the simulator. 
 * 
 * @author Omid Alemi
 * @version 1.2 2011/10/16
 * 
 */
public class SimulationEngine {

	public static int[] colorRange = {0, 1, 2, 3, 4, 5};
	public static int[] actionCostsRange = 
									 {10, 40, 70, 100, 300, 400, 450,  500};
	public static int numOfColors = colorRange.length;
	public static int numOfTeams;
	private int boardh = 10;
	private int boardw = 10;

	public static double disturbanceLevel;	

	private Team[] teams;
	Board mainBoard;
	int[][] actionCostsMatrix;
	RowCol[] goals;
	RowCol[] initAgentsPos;

	private int roundCounter;
	private int[][] teamsScores;
	private int numOfRuns;

	private boolean debuggingInf = true;
	private boolean debuggingErr = true;

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
	public SimulationEngine(Team[] teams) {
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
		logInf("--- The run initialized ---");
		roundCounter = 0;
		mainBoard = Board.randomBoard(boardh, boardw,SimulationEngine.colorRange);
		logInf("The board setting for this run is:\n" + mainBoard.toString());

		goals = new RowCol[Team.teamSize];
		for (int i = 0; i < Team.teamSize; i++)
			goals[i] = new RowCol(boardh - 1, boardw - 1);

		initAgentsPos = new RowCol[Team.teamSize];
		for (int i = 0; i < Team.teamSize; i++)
			initAgentsPos[i] = new RowCol(0, 0);

		Random rnd = new Random(Calendar.getInstance().getTimeInMillis());
		actionCostsMatrix = new int[Team.teamSize][numOfColors];
		for (int i = 0; i < Team.teamSize; i++)
			for (int j = 0; j < numOfColors; j++)
				actionCostsMatrix[i][j] = actionCostsRange[rnd
						.nextInt(actionCostsRange.length)];

		for (int t = 0; t < numOfTeams; t++)
			teams[t].initializeRun(initAgentsPos, goals, actionCostsMatrix);
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

		logInf("Chaning the board setting based on the disturbance level of "+
				disturbanceLevel);
		mainBoard.disturb(disturbanceLevel);

		TeamRoundCode[] tsc = new TeamRoundCode[teams.length];
		for (int t = 0; t < teams.length; t++) {
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
	public SimRoundCode run() {
		logInf("-- The run started --");
		SimRoundCode src = SimRoundCode.SIMOK;
		while (src == SimRoundCode.SIMOK)
			src = round();
		logInf("-- The run ended --");
		return src;
	}

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
			run();
			for (int t = 0; t < numOfTeams; t++) {
				teamsScores[t][r] = teams[t].teamRewardPoints();
				logInf("Team " + teams[t].getClass().getSimpleName()
						+ " scored " + teams[t].teamRewardPoints()
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
			System.out.println("[SimulationEngine]: " + msg);
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
}
