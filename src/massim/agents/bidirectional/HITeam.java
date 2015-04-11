package massim.agents.bidirectional;

import massim.Team;

/**
 * HIAMAP Team
 * 
 * @author Mojtaba
 * @date 2014/07
 */
public class HITeam extends Team {
	
	public HITeam() {
		
		super();		
		HIAgent[] agents = new HIAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			agents[i] = new HIAgent(i,commMedium());
		
		setAgents(agents);
	}
	
	public void setSimHelp(boolean simHelp) {
		for(int i=0; i<Team.teamSize; i++)
			((HIAgent)agent(i)).simHelp = simHelp;
	}
	
	/*
	public void setWHH(double WHH) {
		for(int i=0; i<Team.teamSize; i++)
			((HIAgent)agent(i)).WHH = WHH;
	}
	*/

}

