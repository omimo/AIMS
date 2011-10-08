package tests;

import java.util.Random;

import massim.Team.TeamStepCode;
import massim.agents.MAPTeam;
import massim.*;

public class MAPTester {

	
	public static void main(String[] args) {
					
// Simulation-wide settings		
		SimulationEngine.numOfTeams =1;
		Team.teamSize = 8;							
		Environment.numOfColors = 5;			
		Environment.colorRange = new int[] {10,11,12,13,14};
		Environment.actionCostRange = new int[] {10,50,200,300,500,500,500};

		Team.unicastCost = 10;
		Team.calculationCost = 10;
		Team.achievementReward = 2000;
		Team.cellReward = 100;
		Team.broadcastCost = Team.unicastCost*Team.teamSize;			
		MAPTeam.colorPenalty = 500;
		
		
		int boardw = 6;
		int boardh = 6;
		
		MAPTeam mt = new MAPTeam();
				
// Experiment-wide settings		
		Board board = Board.randomBoard(boardh, boardw);
		
		RowCol[] goals = new RowCol[Team.teamSize];			
		
		for (int i=0;i<Team.teamSize;i++)
			goals[i] = randomPos(boardh, boardw);
			
		
		Environment.setBoard(board);
		Environment.setGoals(goals);
		
		Random rnd = new Random();
		int[][] actionCostsMatrix = new int[Team.teamSize][Environment.numOfColors];
		
		
		for (int i=0;i<Team.teamSize;i++)
			for (int j=0;j<Environment.numOfColors;j++)
				actionCostsMatrix[i][j]= Environment.actionCostRange[rnd.nextInt(Environment.numOfColors)];
		
/*		int[][] actionCostsMatrix = {{20,10,10,15,50}, // can be assigned randomized, etc; 
									 {10,10,50,20,10},
									 {10,10,50,15,30},
									 {15,30,20,10,10},
									 {20,10,10,15,50},  
									 {10,10,50,20,10},
									 {10,10,50,15,30},
									 {15,30,20,10,10}};   */
			
		RowCol[] agentsPos = new RowCol[Team.teamSize];					
		for (int i=0;i<Team.teamSize;i++)
			agentsPos[i] = randomPos(boardh, boardw);
									
// Run-wide settings		
		
		mt.reset(agentsPos,actionCostsMatrix);
		
// Run
		
		System.out.println("The initial env: "+mt.env());		
		System.out.println("The initial team's resources = "+mt.teamResourcePoints());
		
		TeamStepCode tsc = TeamStepCode.OK;		
		while (tsc == TeamStepCode.OK)
		{
			System.out.println("--------------------");
			tsc = mt.step();			
		}
						
		System.out.println("The final team's resources = "+mt.teamResourcePoints());
		
		
		
		
	}

	
	public static RowCol randomPos(int h, int w) {
		
		Random rnd = new Random();
		int row = rnd.nextInt(h);
		int col = rnd.nextInt(w);
		RowCol pos = new RowCol(row,col);
		return pos; 
	}
	
	
}
