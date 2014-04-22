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
}
