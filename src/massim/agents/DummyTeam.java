package massim.agents;

import massim.RowCol;
import massim.Team;

public class DummyTeam extends Team {
	
	
	public DummyTeam() {
		super();
		
		DummyAgent[] agents = new DummyAgent[teamSize];
		
		for (int i=0;i<teamSize;i++)
			agents[i] = new DummyAgent(i,env());
		
		setAgents(agents);
		
	}	
	
	public void reset(RowCol[] agentsPos, int[][] actionCostsMatrix) {
		super.reset(agentsPos, actionCostsMatrix);
		
		for(int i=0;i<teamSize;i++)
			agent(i).incResourcePoints(1000);
		
	}
}
