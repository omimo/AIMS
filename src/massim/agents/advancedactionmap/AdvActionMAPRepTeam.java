package massim.agents.advancedactionmap;

import massim.RowCol;
import massim.Team;
import massim.TeamTask;

public class AdvActionMAPRepTeam extends Team {
	
	/**
	 * The default constructor
	 */
	public AdvActionMAPRepTeam() {
		super();		
			
		AdvActionMAPRepAgent[] aaMAPAgents = new AdvActionMAPRepAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new AdvActionMAPRepAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
	
}
