package massim.agents.nohelp;

import massim.RowCol;
import massim.Team;

/**
 * 
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public class NoHelpRepTeam extends Team {	
	
	/**
	 * The default constructor
	 */
	public NoHelpRepTeam() {
		super();		
			
		NoHelpRepAgent[] selfishAgents = new NoHelpRepAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			selfishAgents[i] = new NoHelpRepAgent(i,commMedium());
		
		setAgents(selfishAgents);
	}	
		
}

