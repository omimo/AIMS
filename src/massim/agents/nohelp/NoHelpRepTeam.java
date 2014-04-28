package massim.agents.nohelp;

import massim.Team;

/**
 * 
 * @author Omid Alemi - Mojtaba
 * 
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

	//Denish, 2014/04/26, swap
	public void setUseSwap(boolean useSwap) {
		for(int i=0;i<Team.teamSize;i++)
			((NoHelpRepAgent)agent(i)).useSwapProtocol = useSwap;
	}
}

