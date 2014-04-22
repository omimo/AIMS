package massim.agents.reassignment;

import massim.Team;

/**
 * 
 */
public class NoHelpAssignTeam extends Team {	
	
	public NoHelpAssignTeam() {
		super();		
			
		NoHelpAssignAgent[] selfishAgents = new NoHelpAssignAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			selfishAgents[i] = new NoHelpAssignAgent(i,commMedium());
		
		setAgents(selfishAgents);
	}	
}

