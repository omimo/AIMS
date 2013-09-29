package massim;

import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import massim.Team.TeamRoundCode;

/**
 * The main class of the simulator. 
 * 
 * @author Omid Alemi
 * @version 1.3 2012/01/19
 * 
 */
public class SimulationEngine {

	public static int[] colorRange;
	public static int[] actionCostsRange; 
									 
	public static int numOfColors;
	public static int numOfTeams;
	private int boardh = 10;
	private int boardw = 10;

	public static double disturbanceLevel;	
	public static double pulseLevel;
	public static double pulseProbability;
	public static double stepProbability;

	private Team[] teams;
	private Board mainBoard;

	private int[][] actionCostsMatrix;
	
	private TeamTask tt;

	private int roundCounter;
	private int[][] teamsScores;
	private int numOfRuns;

	private boolean debuggingInf = false;
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
		
		tt = new TeamTask();
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
		mainBoard = Board.randomBoard
					(boardh, boardw,SimulationEngine.colorRange);
		
		logInf("The board setting for this run is:\n" + mainBoard.toString());
		
		tt.goalPos = new RowCol[Team.teamSize];
		for (int i = 0; i < Team.teamSize; i++)
			tt.goalPos[i] = //new RowCol(boardh-1, boardw-1);
				randomPos(boardh, boardw);

		tt.startPos = new RowCol[Team.teamSize];
		for (int i = 0; i < Team.teamSize; i++)
			tt.startPos[i] = //new RowCol(0, 0); 
				randomPos(boardh, boardw);

		Random rnd = new Random();
		
		actionCostsMatrix = new int[Team.teamSize][numOfColors];
		
		for (int i = 0; i < Team.teamSize; i++)
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
			teams[t].initializeRun(tt,actionCostsMatrix);
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

		logInf("Changing the board setting based on the disturbance level of "+
				disturbanceLevel);
		mainBoard.disturb(disturbanceLevel);

		TeamRoundCode[] tsc = new TeamRoundCode[teams.length];
		for (int t = 0; t < SimulationEngine.numOfTeams; t++) {
			//System.out.println(t+" hellllo!");
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
		//(new Scanner(System.in)).nextLine();
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
						+ " scored " + teamsScores[t][r]
						+ " for this run.");
			}
			(new Team()).logInfScore(-1, "");
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
	
	/**
	 * Generates a random position within the specified range
	 * 
	 * @param h					The height of the board
	 * @param w					The width of the board
	 * @return					The generated position
	 */
	private RowCol randomPos(int h, int w) {
		Random rnd = new Random();
		
		return new RowCol(rnd.nextInt(h),rnd.nextInt(w));
	}

	public int[] getHelpReqCounts() {
		int[] r = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++) {
			r[t] = teams[t].getHelpReqCounts()/numOfRuns;
		}
		return r;
	}

	public int[] getBidsCounts() {
		int[] r = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++) {
			r[t] = teams[t].getBidsCounts()/numOfRuns;
		}
		return r;
	}

	public int[] getSucOffersCounts() {
		int[] r = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++) {
			r[t] = teams[t].getSucOffersCounts()/numOfRuns;
		}
		return r;
	}

	public int[] getUnSucHelpReqCounts() {
		int[] r = new int[numOfTeams];
		for (int t = 0; t < numOfTeams; t++) {
			r[t] = teams[t].getUnSucHelpReqCounts()/numOfRuns;
		}
		return r;
	}
}
