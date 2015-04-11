package massim.agents.bidirectional;

import massim.Team;

/**
 * RIAMAP Team
 * 
 * @author Mojtaba
 * @date 2014/06
 */
public class RITeam extends Team {
	
	public RITeam() {
		
		super();		
		RIAgent[] agents = new RIAgent[Team.teamSize];
		
		for(int i=0; i<Team.teamSize; i++)
			agents[i] = new RIAgent(i,commMedium());
		
		setAgents(agents);
	}
	
	public void setUseHelp2Character(boolean useHelp2Character) {
		for(int i=0; i<Team.teamSize; i++)
			((RIAgent)agent(i)).useHelp2Character = useHelp2Character;
	}
	
	public void setSimHelp(boolean simHelp) {
		for(int i=0; i<Team.teamSize; i++)
			((RIAgent)agent(i)).simHelp = simHelp;
	}
	
	/*
	public void setWLL(double WLL) {
		for(int i=0; i<Team.teamSize; i++)
			((RIAgent)agent(i)).WLL = WLL;
	}
	*/
	
}

