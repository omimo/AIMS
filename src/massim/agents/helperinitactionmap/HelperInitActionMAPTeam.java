package massim.agents.helperinitactionmap;

import massim.Team;

public class HelperInitActionMAPTeam extends Team {
	
	/**
	 * The default constructor
	 */
	public HelperInitActionMAPTeam() {
		super();		
			
		HelperInitActionMAPAgent[] aaMAPAgents = new HelperInitActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new HelperInitActionMAPAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
	
}
