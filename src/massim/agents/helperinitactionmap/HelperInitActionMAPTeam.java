package massim.agents.helperinitactionmap;

import massim.Team;

/**
 * HIAMAP Team
 * 
 * @author Omid Alemi
 */
public class HelperInitActionMAPTeam extends Team {
	
	/**
	 * New team - HIAMAP
	 */
	public HelperInitActionMAPTeam() {
		super();		
			
		HelperInitActionMAPAgent[] aaMAPAgents = new HelperInitActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new HelperInitActionMAPAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
}
