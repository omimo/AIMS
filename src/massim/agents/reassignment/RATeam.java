package massim.agents.reassignment;

import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;

public class RATeam extends Team{
	/**
	 * The default constructor
	 */
	public RATeam() {
		super();		
			
		RAAgent[] aaMAPAgents = new RAAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			aaMAPAgents[i] = new RAAgent(i,commMedium());
		
		setAgents(aaMAPAgents);
	}	
}
