package massim.agents.advancedactionmap;

import massim.Team;

/**
 * RIAMAP Team
 * 
 * @author Omid Alemi
 */
public class AdvActionMapTeam extends Team {
	
	/**
	 * New team - RIAMAP
	 */
	public AdvActionMapTeam() {
		
		super();		
		AdvActionMAPAgent[] aaMAPAgents = new AdvActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new AdvActionMAPAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
}
