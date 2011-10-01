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
	private CommMedium communicationMeduim;
	
	public Team() {
		
	}
	
	public Team(Agent[] agents) {
		init(agents);		
	}
	
	public void init(Agent[] agents) {
		// communicationMeduim = new CommMedium(this);
		// this.agents = agents
		
		// for each agent a
		//     a.setTeam(this)
	}
	
	public int cycle() {
		
		// 0. Update Agents Percepts
		// for each agent a in agents[]
		//     a.perceive()
		
		
		// 1. Communication Phase
		// noMsgPass = 1 
		// do
		//     for each agent a in agents[]
		//         a.doSend();
		//     for each agent a in agents[]
		//         a.doReceive();
		//
		// 	   if (there was no communication)
		//         noMsgPass--;
		// while (there was at least one communication && noMsgPass != 0)
		
		
		// 1. Action Phase
		// for each agent a in agents[]
		//     a.act()
		
		return 0;
	}
	
	public Agent[] getAgents() {
		return agents;
	}
	
	public void send(Agent sender, Agent receiver, String msg) {
		communicationMeduim.send(sender, receiver, msg);
	}
	
	public HashMap<Agent,String> receive(Agent receiver) {
		
		return communicationMeduim.receive(receiver);		
	}
	
}
