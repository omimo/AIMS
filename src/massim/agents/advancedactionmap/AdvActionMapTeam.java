package massim.agents.advancedactionmap;

import massim.RowCol;
import massim.Team;

public class AdvActionMapTeam extends Team {
	
	/**
	 * The default constructor
	 */
	public AdvActionMapTeam() {
		super();		
			
		AdvActionMAPAgent[] aaMAPAgents = new AdvActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new AdvActionMAPAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
	
}
