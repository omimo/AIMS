package massim.agents.bidirectional;

import massim.Team;

/**
 * BIAMAP Team
 * 
 * @author Mojtaba
 * @date 2014/07
 */
public class BITeam extends Team {
	
	public BITeam() {
		
		super();		
		BIAgent[] agents = new BIAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			agents[i] = new BIAgent(i,commMedium());
		
		setAgents(agents);
	}
	
	public void setUseHelp2Character(boolean useHelp2Character) {
		for(int i=0;i<Team.teamSize;i++)
			((BIAgent)agent(i)).useHelp2Character = useHelp2Character;
	}
}

