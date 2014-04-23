package massim.agents.advancedactionmap;

import massim.Team;

/**
 * RIAMAP Team
 * 
 * @author Omid Alemi
 */
public class AdvActionMAPTeam extends Team {
	
	/**
	 * New team - RIAMAP
	 */
	public AdvActionMAPTeam() {
		
		super();		
		AdvActionMAPAgent[] aaMAPAgents = new AdvActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new AdvActionMAPAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}
	
	//Denish, 2014/04/23
	public void setUseHelp2Character(boolean useHelp2Character) {
		for(int i=0;i<Team.teamSize;i++)
			((AdvActionMAPAgent)agent(i)).useHelp2Character = useHelp2Character;
	}
}
