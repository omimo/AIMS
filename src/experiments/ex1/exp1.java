package experiments.ex1;

import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import massim.Board;
import massim.Environment;
import massim.RowCol;
import massim.SimulationEngine;
import massim.Team;
import massim.Team.TeamStepCode;
import massim.agents.classicmap.MAPAgent;
import massim.agents.classicmap.MAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.reqinit.ReqInitTeam;

public class exp1 {
	
	public static void main(String[] args) {
		boolean debugging = false;
		
		// Simulation-wide settings
		int numOfRuns = 1000;
		int numOfExp = 11;
	
		Environment.numOfColors = 6;
		Environment.colorRange = new int[] { 10, 11, 12, 13, 14, 15 };		
		Environment.actionCostRange = new int[] {10,40,70,100,300,400,450,500};

		SimulationEngine.numOfTeams = 3;
		Team.teamSize = 8;			
		Team[] teams = new Team[SimulationEngine.numOfTeams];
		teams[0] = new MAPTeam();
		teams[1] = new NoHelpTeam();
		teams[2] = new ReqInitTeam();
		
		Random rnd = new Random();	
		
		/*
		 * Experiments loop 
		 */
		for (int exp = 0; exp < numOfExp; exp++) {    
			// Experiment-wide settings
			Team.initResCoef = 200;
			Team.unicastCost = 3;
			Team.calculationCost = 3;
			Team.achievementReward = 2000;
			Team.cellReward = 100;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);		
			Team.costThreshold = 299;
			Team.helpOverhead = 50;			
			Environment.mutualAwareness = 0.8;
			Environment.disturbanceLevel = 0.1 * exp;
			
			int boardw = 10;
			int boardh = 10;
			Board board;
			RowCol[] goals;
			int[][] actionCostsMatrix;
			RowCol[] agentsPos;
							
			int[][] teamsScores = new int[SimulationEngine.numOfTeams][numOfRuns];
			
			
			/*
			 * Loop for each run
			 */
			for (int r = 0; r < numOfRuns; r++) {

				// Run-wide settings

				board = Board.randomBoard(boardh, boardw);
				Environment.setBoard(board);
				
				goals = new RowCol[Team.teamSize];
				for (int i = 0; i < Team.teamSize; i++)				
					goals[i] = new RowCol(boardh-1, boardw-1);						
				Environment.setGoals(goals);
				
				actionCostsMatrix = new int[Team.teamSize][Environment.numOfColors];
				for (int i = 0; i < Team.teamSize; i++)
					for (int j = 0; j < Environment.numOfColors; j++)						
						actionCostsMatrix[i][j] = Environment.actionCostRange[rnd.nextInt(Environment.actionCostRange.length)];
						
				agentsPos = new RowCol[Team.teamSize];
				for (int i = 0; i < Team.teamSize; i++)
					agentsPos[i] = new RowCol(0, 0);
				
				for (Team t : teams)
					t.reset(agentsPos, actionCostsMatrix);

				/*
				 * Main loop of the run
				 * Each iteration represents one simulation round
				 * Executes all the teams until they are all done
				 * 
				 */
				boolean allDone = false;				
				while (!allDone) {
					Environment.board().distrub(Environment.disturbanceLevel);
					
					allDone = true;							
					for (Team t : teams) {
						TeamStepCode tsc;
						tsc = t.step();
						if (tsc == TeamStepCode.OK)
							allDone = false;
					}															
				}
				
				// Collecting team scores for the current run of the exprtiment 
				teamsScores[0][r] = ((MAPTeam) teams[0]).pointsEarned();
				teamsScores[1][r] = ((NoHelpTeam) teams[1]).pointsEarned();
				teamsScores[2][r] = ((ReqInitTeam) teams[2]).pointsEarned();

				
				// Pause at each run, to check the agent's debugging output
				if (debugging) {
					System.out.println("Run: "+r+" ===========================================");
					(new Scanner(System.in)).nextLine();
					}

			}
			
			// Printing the average score of current experiment over multiple runs						
			System.out.printf("%1.1f,%1.1f", Environment.mutualAwareness,Environment.disturbanceLevel);
			for (int t = 0; t < SimulationEngine.numOfTeams; t++) {
				int sum = 0;
				for (int r = 0; r < numOfRuns; r++)
					sum += teamsScores[t][r];
				System.out.printf(",%d", sum / numOfRuns);
			}
			System.out.printf("\n");
		}
	}

	/**
	 * Returns a random position within a board with specified dimensions
	 *  
	 * @param h 				The height of the board
	 * @param w 				The width of the board
	 * @return					The position in RowCol
	 */
	public static RowCol randomPos(int h, int w) {

		Random rnd = new Random();
		int row = rnd.nextInt(h);
		int col = rnd.nextInt(w);		
		return  new RowCol(row, col);
	}
}
