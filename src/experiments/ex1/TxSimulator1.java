package experiments.ex1;

import massim.*;
import massim.agents.*;

public class TxSimulator1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Agent[] MAPAgents;
		Team[] teams;
		Team MAPTeam;
		Board initBoard;
		SimParams simParams = new Ex1Params();
		SimulationEngine sim;
		
		// set the parameters values as desired, possibly load from a file -> simParams
		// load the initial board setting from a text file into an array -> initBoard
		
		// for each Agent a in MAPAgents 
		//     a = new MAPAgent();
		
		// DummyTeam = new Team (MAPAgents);
		
		// teams.add(DummyTeam);
		
		// initBoard = Board.randomBoard(); 
		
		// sim = new SimulationEngine (teams, simParams);
		
				
		// 1.
		// sim.init(initBoard);
		// sim.autoplay();		
		// simState = sim.getSimulationState();		
		// results = simState.teamStates();
		// display the results
		
		// OR
		
		// 2.
		// sim.init(initBoard);
		// code = sim.step();
		// simState = sim.getSimulationState();		
		// results = simState.teamStates();
		// display the results
		// if code != SIMEND then repeat
	}

}
