package massim.agents.nohelp;

import massim.Team;

/**
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public class NoHelpTeam extends Team {	
	
	/**
	 * New team - No Help
	 */
	public NoHelpTeam() {
		super();		
			
		NoHelpAgent2[] selfishAgents = new NoHelpAgent2[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			selfishAgents[i] = new NoHelpAgent2(i,commMedium());
		
		setAgents(selfishAgents);
	}	
}

