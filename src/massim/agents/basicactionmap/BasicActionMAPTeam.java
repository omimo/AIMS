package massim.agents.basicactionmap;

import massim.RowCol;
import massim.Team;

public class BasicActionMAPTeam extends Team {
	
	/**
	 * The default constructor
	 */
	public BasicActionMAPTeam() {
		super();		
			
		BasicActionMAPAgent[] baMAPAgents = new BasicActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			baMAPAgents[i] = new BasicActionMAPAgent(i,commMedium());
		
		setAgents(baMAPAgents);
	}	
	
}
