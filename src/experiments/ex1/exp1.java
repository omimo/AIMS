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

public class exp1 {
	public static void main(String[] args) {

		// Simulation-wide settings
		int numOfRuns = 200;
		int numOfExp = 10;

		SimulationEngine.numOfTeams = 2;
		Team.teamSize = 8;
		
		Environment.numOfColors = 6;
		Environment.colorRange = new int[] { 10, 11, 12, 13, 14, 15 };
		Environment.actionCostRange = new int[] { 10, 20, 30, 40, 50, 100, 300, 350, 400, 450, 500 };

		Team[] teams = new Team[SimulationEngine.numOfTeams];
		teams[0] = new MAPTeam();
		teams[1] = new NoHelpTeam();

		for (int exp = 0; exp < numOfExp; exp++) {
			// Experiment-wide settings
			Team.initResCoef = 100;
			Team.unicastCost = 5;
			Team.calculationCost = 5;
			Team.achievementReward = 2000;
			Team.cellReward = 100;
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);		
			MAPTeam.costThreshold = 299;
			Team.helpOverhead = 20;
			
			Environment.disturbanceLevel = exp * 0.1;
			
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
					// goals[i] = randomPos(boardh, boardw);
					goals[i] = new RowCol(boardh-1, boardw-1);

				Environment.setBoard(board);
				Environment.setGoals(goals);

				Random rnd = new Random();
				actionCostsMatrix = new int[Team.teamSize][Environment.numOfColors];

				for (int i = 0; i < Team.teamSize; i++)
					for (int j = 0; j < Environment.numOfColors; j++)						
						actionCostsMatrix[i][j] = 
							Environment.actionCostRange[rnd.nextInt(Environment.actionCostRange.length)];
						/*if (j<Environment.numOfColors/2)
							actionCostsMatrix[i][j] = 
								Environment.actionCostRange[rnd.nextInt(Environment.actionCostRange.length/2)];
						else
							actionCostsMatrix[i][j] = 
								Environment.actionCostRange[Environment.actionCostRange.length/2+rnd.nextInt(Environment.actionCostRange.length/2)];*/

				agentsPos = new RowCol[Team.teamSize];
				for (int i = 0; i < Team.teamSize; i++)
					// agentsPos[i] = randomPos(boardh, boardw);
					agentsPos[i] = new RowCol(0, 0);

				for (Team t : teams)
					t.reset(agentsPos, actionCostsMatrix);

				// Run

				boolean allDone = false;

				while (!allDone) {
					allDone = true;
					// Step
					Environment.board().distrub(Environment.disturbanceLevel);
					for (Team t : teams) {
						TeamStepCode tsc;
						tsc = t.step();
						if (tsc == TeamStepCode.OK)
							allDone = false;
					}
					//
				}

				
//				 System.out.println(r+" ==============================================================="); 
//				 System.out.println("Team 0 = "+((MAPTeam)
//				 teams[0]).pointsEarned());
//				 System.out.println("Team 1 = "+((NoHelpTeam)
//				 teams[1]).pointsEarned());
				 

//				 (new Scanner(System.in)).nextLine();

				res[0][r] = ((MAPTeam) teams[0]).pointsEarned();
				res[1][r] = ((NoHelpTeam) teams[1]).pointsEarned();

			}

			System.out.printf("%1.1f", Environment.disturbanceLevel);
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
