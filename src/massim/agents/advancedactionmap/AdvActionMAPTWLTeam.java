package massim.agents.advancedactionmap;

import massim.RowCol;
import massim.Team;

public class AdvActionMAPTWLTeam extends Team {
	
	/**
	 * The default constructor
	 */
	public AdvActionMAPTWLTeam() {
		super();		
			
		AdvActionMAPTWLAgent[] aaMAPAgents = new AdvActionMAPTWLAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new AdvActionMAPTWLAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
	
}