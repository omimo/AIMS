package massim.agents;

import massim.Agent;
import massim.AgentEnvInterface;

public class MAPAgent extends Agent {

	
	public MAPAgent(int id,AgentEnvInterface env) {
		super(id, env);
	}
	
	/**
	 * The MAP agents perform their 'move' action here  
	 */
	@Override
	public int act() {
		
		return 0;
	}
	
	@Override
	public void doSend() {
		// Do the MAP Protocol
		
		// team.send(this, agents[2], "1,2,bid:400")
		// or team.communicationMedium.send()
	}
	
	@Override
	public void doReceive() {
		// Do the MAP Protocol
		
		// HashMap<Agent,String> incoming = team.receive(this);
	}
	
}
