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
		Environment.numOfColors = 6;			
		Environment.colorRange = new int[] {10,11,12,13,14,15};
		Environment.actionCostRange = new int[] {10,50,200,250,300,500};

		Team.unicastCost = 10;
		Team.calculationCost = 10;
		Team.achievementReward = 2000;
		Team.cellReward = 100;
		Team.broadcastCost = Team.unicastCost*(Team.teamSize-1);			
		//MAPTeam.colorPenalty = 500;
		MAPTeam.costThreshold = 299;
		
		
		int boardw = 10;
		int boardh = 10;
		
		MAPTeam mt = new MAPTeam();
				
// Experiment-wide settings		
		Board board = Board.randomBoard(boardh, boardw);
		
		RowCol[] goals = new RowCol[Team.teamSize];			
		
		for (int i=0;i<Team.teamSize;i++)
			//goals[i] = randomPos(boardh, boardw);
			goals[i] = new RowCol(9,9);
			
		
		Environment.setBoard(board);
		Environment.setGoals(goals);
		
		Random rnd = new Random();
		int[][] actionCostsMatrix = new int[Team.teamSize][Environment.numOfColors];
		
		
		for (int i=0;i<Team.teamSize;i++)
			for (int j=0;j<Environment.numOfColors;j++)
				actionCostsMatrix[i][j]= Environment.actionCostRange[rnd.nextInt(Environment.actionCostRange.length)];
		
			
		RowCol[] agentsPos = new RowCol[Team.teamSize];					
		for (int i=0;i<Team.teamSize;i++)
			//agentsPos[i] = randomPos(boardh, boardw);
			agentsPos[i] = new RowCol(0,0);
					
		int numOfRuns = 1000;
		int[] res = new int[numOfRuns];
		for (int r=0;r<numOfRuns;r++)
		{
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
		System.out.println("The final team's earned points = "+mt.pointsEarned());
		res[r]=mt.pointsEarned();
		}
		
		int sum = 0;
		for (int r=0;r<numOfRuns;r++)
			sum+=res[r];
		
		System.out.println("The average of runs = "+sum/numOfRuns);
	}

	
	public static RowCol randomPos(int h, int w) {
		
		Random rnd = new Random();
		int row = rnd.nextInt(h);
		int col = rnd.nextInt(w);
		RowCol pos = new RowCol(row,col);
		return pos; 
	}
	
	
}
