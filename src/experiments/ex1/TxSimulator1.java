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
		Scoring scores = new Ex1Scoring();
		Simulator sim;
		
		// set the score values as desired, possibly load from a file -> score
		// load the initial board setting from a text file into an array -> initBoard
		// lead the script file into a list of events -> scriptEvents
		
		// for each Agent a in MAPAgents 
		//     a = new MAPAgent();
		
		// MAPTeam = new Team (MAPAgents);
		
		// teams.add(MAPTeam);
		
		// sim = new Simulator (teams, scores,initBoard, scriptEvents);
		
		// 1.
		// sim.autoplay();		
		// simState = sim.getSimState();		
		// results = simState.getTeamResult();
		// display the results
		
		// OR
		
		// 2.
		// 
		// code = sim.step();
		// simState = sim.getSimState();		
		// results = simState.getTeamResult();
		// display the results
		// if code != SIMEND then repeat
	}

}
