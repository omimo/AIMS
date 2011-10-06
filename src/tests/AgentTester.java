package tests;

import massim.agents.DummyAgent;
import massim.*;

public class AgentTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
			SimParams dummyParams = new SimParams();
			dummyParams.numOfAgentsPerTeam = 2;
			dummyParams.numOfTeams = 1;			
			dummyParams.numOfAbilitiesPerAgent = 5;			
			Simulator.simParams = dummyParams;
			
			Board board = Board.randomBoard(5, 5, 10, 5);
			
			int[][] costVectors = { {5,10,10,15,50}, 
									{10,10,50,5,10} };   
			
			RowCol[] agentsPos = {new RowCol(0,0), new RowCol(4,0)};
			
			Goal[] goals = new Goal[2];			
			goals[0] = new Goal(4,4);
			goals[1] = new Goal(0,4);
			
		
			Agent[] agents = new DummyAgent[2];					
			agents[0] = new DummyAgent(0);
			agents[1] = new DummyAgent(1);					
			
			Team team = new Team();			
			agents[0].init(team);
			agents[1].init(team);
			
			agents[0].incResources(100);
			agents[1].incResources(500);
			
			//--------------------------------------------------------
			
			System.out.println("Board:");
			System.out.println(board);
			System.out.println("agent 0 points: " +agents[0].points());
			System.out.println("agent 1 points: " +agents[1].points());
			
			
			//--------------------------------------------------------
			
			
			agents[0].perceive(board, costVectors, goals, agentsPos);
			agents[1].perceive(board, costVectors, goals, agentsPos);
			
			agents[0].doSend();
			agents[1].doSend();					
			
			agents[0].doReceive();
			agents[1].doReceive();
			
			agents[0].act();
			agents[1].act();
			
			/*
			Board board = Board.randomFill();
			
			TeamContext.setBoard(board);
			
			int[][] costVerctors = {values}
			Goal[] goals = {values}
						
			TeamContext.setGoals(goals)
			TeamContext.setCostVectors(costVectors)
			
			Agent[] mapAgents;
			Agent[] pahAgents;
			
			TeamContext tcMAP(mapAgents)
			TeamContext tcProActiveHelp(pahAgents)
			
			tcMAP.setAgentsPos({initial positions for agents})
			tcProActiveHelp.setAgentsPos({initial positions for agents})
			
			Team MAPTeam(tcMAP);
			Team ProActiveHelpTeam(tcProActiveHelp);
			
			Team[] teams;
			team[0] = MAPTeam;
			team[1] = ProTeam;
						
			simulator.init(teams)
			
			simulator.step(0.0);
			 												
			*/
	}

}
