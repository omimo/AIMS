package experiments.ex1;

import java.util.Random;
import java.util.Scanner;

import massim.Board;
import massim.Environment;
import massim.RowCol;
import massim.SimulationEngine;
import massim.Team;
import massim.Team.TeamStepCode;
import massim.agents.classicmap.MAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.reqinit.ReqInitTeam;

public class exp1 {
	public static void main(String[] args) {

		// Simulation-wide settings
		int numOfRuns = 100;
		int numOfExp = 10;

		SimulationEngine.numOfTeams = 3;
		Team.teamSize = 8;
		
		Environment.numOfColors = 6;
		Environment.colorRange = new int[] { 10, 11, 12, 13, 14, 15 };
		Environment.actionCostRange = new int[] { 10, 20, 30, 40, 50, 100, 300, 350, 400, 450, 500 };
		

		Team[] teams = new Team[SimulationEngine.numOfTeams];
		teams[0] = new MAPTeam();
		teams[1] = new NoHelpTeam();
		teams[2] = new ReqInitTeam();

		for (int exp = 0; exp < numOfExp; exp++) {
			// Experiment-wide settings
			Team.initResCoef = 100;
			Team.unicastCost = 5;
			Team.calculationCost = 5;
			Team.achievementReward = 2000;
			Team.cellReward = 100;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);		
			Team.costThreshold = 299;
			Team.helpOverhead = 20;
			
			Environment.mutualAwareness = 0.1  *exp;
			Environment.disturbanceLevel = 0.3;
			
			int boardw = 5;
			int boardh = 5;

			Board board;

			RowCol[] goals;

			int[][] actionCostsMatrix;
			RowCol[] agentsPos;

			int[][] res = new int[SimulationEngine.numOfTeams][numOfRuns];
			for (int r = 0; r < numOfRuns; r++) {
				// Run-wide settings

				board = Board.randomBoard(boardh, boardw);

				goals = new RowCol[Team.teamSize];

				for (int i = 0; i < Team.teamSize; i++)				
					goals[i] = new RowCol(boardh-1, boardw-1);

				Environment.setBoard(board);
				Environment.setGoals(goals);

				Random rnd = new Random();
				actionCostsMatrix = new int[Team.teamSize][Environment.numOfColors];
				for (int i = 0; i < Team.teamSize; i++)
					for (int j = 0; j < Environment.numOfColors; j++)						
						actionCostsMatrix[i][j] = Environment.actionCostRange[rnd.nextInt(Environment.actionCostRange.length)];
						
				agentsPos = new RowCol[Team.teamSize];
				for (int i = 0; i < Team.teamSize; i++)
					agentsPos[i] = new RowCol(0, 0);

				
				for (Team t : teams)
					t.reset(agentsPos, actionCostsMatrix);

				// Run

				boolean allDone = false;

				while (!allDone) {
					allDone = true;
					// Step					
					for (Team t : teams) {
						TeamStepCode tsc;
						tsc = t.step();
						if (tsc == TeamStepCode.OK)
							allDone = false;
					}
					Environment.board().distrub(Environment.disturbanceLevel);
					//
				}

				
			//System.out.println(r+" ===========================================");(new Scanner(System.in)).nextLine();

				res[0][r] = ((MAPTeam) teams[0]).pointsEarned();
				res[1][r] = ((NoHelpTeam) teams[1]).pointsEarned();
				res[2][r] = ((ReqInitTeam) teams[2]).pointsEarned();

			}

			System.out.printf("%1.1f,%1.1f", Environment.mutualAwareness,Environment.disturbanceLevel);
			for (int t = 0; t < SimulationEngine.numOfTeams; t++) {
				int sum = 0;
				for (int r = 0; r < numOfRuns; r++)
					sum += res[t][r];
				System.out.printf(",%d", sum / numOfRuns);
				// System.out.println("The average earned points of team "+ t
				// +" = " + sum / numOfRuns);
			}
			System.out.printf("\n");
		}
	}

	public static RowCol randomPos(int h, int w) {

		Random rnd = new Random();
		int row = rnd.nextInt(h);
		int col = rnd.nextInt(w);
		RowCol pos = new RowCol(row, col);
		return pos;
	}
}
