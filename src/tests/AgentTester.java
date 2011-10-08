package tests;

import massim.Team.TeamStepCode;
import massim.agents.DummyAgent;
import massim.agents.DummyTeam;
import massim.*;

public class AgentTester {

	
	public static void main(String[] args) {
					
// Simulation-wide settings
		SimulationEngine.numOfTeams =1;
		Team.teamSize = 4;						
		Environment.numOfColors = 5;			
		Environment.colorRange = new int[] {10,11,12,13,14};
		Environment.actionCostRange = new int[] {10,15,20,30,50};
		
		
		DummyTeam dt = new DummyTeam();
				
// Experiment-wide settings		
		Board board = Board.randomBoard(5, 5);
		
		RowCol[] goals = new RowCol[Team.teamSize];  // can be assigned randomized, etc;			
		goals[0] = new RowCol(4,4);
		goals[1] = new RowCol(0,4);
		goals[2] = new RowCol(4,0);
		goals[3] = new RowCol(0,0);
		
		Environment.setBoard(board);
		Environment.setGoals(goals);
		
		int[][] actionCostsMatrix = {{20,10,10,15,50}, // can be assigned randomized, etc; 
									 {10,10,50,20,10},
									 {10,10,50,15,30},
									 {15,30,20,10,10}};   
			
		RowCol[] agentsPos = {new RowCol(0,0), // can be assigned randomized, etc;
							  new RowCol(4,0), 
							  new RowCol(0,4),
							  new RowCol(2,2)};
									
// Run-wide settings		
		
		dt.reset(agentsPos,actionCostsMatrix);
		
// Run
		
		System.out.println("The initial env: "+dt.env());		
		System.out.println("The initial team's resources = "+dt.teamResourcePoints());
		
		TeamStepCode tsc = TeamStepCode.OK;		
		while (tsc == TeamStepCode.OK)
		{
			System.out.println("--------------------");
			tsc = dt.step();			
		}
						
		System.out.println("The final team's resources = "+dt.teamResourcePoints());
	}

}
