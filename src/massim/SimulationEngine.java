package massim;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import massim.ExperimentLogger.LogType;
import massim.Team.TeamRoundCode;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;

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
	
	//Denish, 2014/03/26. Changed to public static from private
	public static int boardh = 10;
	public static int boardw = 10;

	public static double disturbanceLevel;	
	public static double pulseLevel;
	public static double pulseProbability;
	public static int[] pulseOccurrence;
	public static int maximumNoOfPulses;
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
			teams[t].initializeRun(tt,actionCostsMatrix, mainBoard, actionCostsRange);
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

		boolean pulse = false;
		if(pulseOccurrence != null && pulseOccurrence.length > 0) {
			for(int round : pulseOccurrence) {
				if(roundCounter == round) {
					mainBoard.disturb(pulseLevel);
					logInf("Changing the board setting based on the pulse level of "+
							pulseLevel);
					pulse = true;
					break;
				}
			}
		} 
		if(!pulse) {
			mainBoard.disturb(disturbanceLevel);
			logInf("Changing the board setting based on the disturbance level of "+
					disturbanceLevel);
		}

		final TeamRoundCode[] tsc = new TeamRoundCode[teams.length];
		ArrayList<Thread> lstThread = new ArrayList<Thread>();
		for (int t = 0; t < SimulationEngine.numOfTeams; t++) {
			//System.out.println(t+" hellllo!");

			final int index = t;
			Thread thread = new Thread() {
				@Override
				public void run() {
					tsc[index] = teams[index].round(mainBoard);
					//System.out.println("round complete " + index);
				}
			};
			thread.run();
			lstThread.add(thread);
		}
		
		int index = 0;
		for(Thread thread : lstThread) {
			try {
				thread.join();
			} catch (InterruptedException e) { e.printStackTrace();}
			
			logInf(teams[index].getClass().getSimpleName()
					+ " returned with the code: " + tsc[index].toString());
			index++;
		}
		//System.out.println("round complete all");

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
		while (src == SimRoundCode.SIMOK) {
			src = round();
			
			//Denish, 2014/03/26
			if(roundCompListener != null)
				roundCompListener.actionPerformed(null);
		}
		logInf("-- The run ended --");
		//(new Scanner(System.in)).nextLine();
		
//		for (int t = 0; t < SimulationEngine.numOfTeams; t++) {
//			for (int a = 0; a < Team.teamSize; a++) {
//				Agent ag = teams[t].agent(a);
//				System.out.println(ag.id() + ", " + ag.mySubtask() + ", " + tt.startPos[ag.mySubtask()] 
//						+ ", " +  ag.pos() + ", " + ag.goalPos() + ", " + ag.resourcePoints() + ", " 
//						+ (ag.reachedGoal() ? "" : ((AdvActionMAPRepAgent)ag).estimatedCost(((AdvActionMAPRepAgent)ag).path()))
//						+ ", " + ag.reachedGoal());
//			}
//		}
		
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
			
			//Denish, 2014/03/26
			if(runInitListener != null)
				runInitListener.actionPerformed(null);
			
			run();

			for (int t = 0; t < numOfTeams; t++) {
				teamsScores[t][r] = teams[t].teamRewardPoints();
				logInf("Team " + teams[t].getClass().getSimpleName()
						+ " scored " + teamsScores[t][r]
						+ " for this run.");
			}
			(new Team()).logInfScore(-1, "");
			
			//Denish, 2014/03/26
			if(runCompleteListener != null)
				runCompleteListener.actionPerformed(null);
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
		if(logger != null)
			logger.logEvent(LogType.Engine, 0, "[SimulationEngine]: " + msg + "\n");
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

	//Denish, 2014/03/26
	private ActionListener roundCompListener;
	public void setRoundCompleteListener(ActionListener roundCompListener) {
		this.roundCompListener = roundCompListener;
	}
	private ActionListener runInitListener;
	public void setRunInitializedListener(ActionListener runInitListener) {
		this.runInitListener = runInitListener;
	}
	private ActionListener runCompleteListener;
	public void setRunCompleteListener(ActionListener runCompleteListener) {
		this.runCompleteListener = runCompleteListener;
	}
	
	public int[][] getBoard()
	{
		return mainBoard.getBoard();
	}
	ExperimentLogger logger;
	public void setLogger(ExperimentLogger logger) {
		this.logger = logger;
	}
}
