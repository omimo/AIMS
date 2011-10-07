package massim;

import java.util.HashMap;

/**
 * Team.java
 * 
 *
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 */
public class Team {

	private Agent[] agents;	
	private Environment env;
	
	public static enum TeamStepCode {OK, DONE, ERR}
	
	/**
	 * Default constructor
	 */
	public Team(int teamSize) {
		
	}
	
	
	
	/**
	 * Called by the simulator in each step of simulation
	 * @return ENDSIM code if the simulation is over
	 */
	public TeamStepCode step(SimState simState) {
		
		// 0. Update Agents Percepts		
		// for each agent a_i in agents[]
		//     a_i.perceive(simSt.board(), simSt.costVerctor(i), simSt.goal(i), ts.agPositions());
		
		
		
		// 1. Communication Phase
		// noMsgPass = 5  
		// do
		//     for each agent a in agents[]
		//         a.doSend();
		//     for each agent a in agents[]
		//         a.doReceive();

		// ?? WEHRE SHOULD WE GET THE STATUS CODE FROM THE AGENT? 
		// (in doReceive or in a separate method just for that?) 
		
		// 	   if (there was no communication)
		//         noMsgPass--;
		//     else 
		//         noMsgPass = 5;
		// while (there was at least one communication && noMsgPass != 0)
		
		
		// 1. Action Phase
		// for each agent a in agents[]
		//     a.act()
		
		return TeamStepCode.OK;
	}
	

}
