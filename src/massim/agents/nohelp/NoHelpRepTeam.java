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

	public int getReplanCounts() {
		int sum = 0;
		for(int i=0;i<Team.teamSize;i++)
			sum += ((NoHelpRepAgent)agent(i)).replanCount;
		return sum;
	}

	//Denish, 2014/04/26, swap
	public void setUseSwap(boolean useSwap) {
		for(int i=0;i<Team.teamSize;i++)
			((NoHelpRepAgent)agent(i)).useSwapProtocol = useSwap;
	}
	
	public int getSwapCounts() {
		int sum = 0;
		for(int i=0;i<Team.teamSize;i++)
			sum += ((NoHelpRepAgent)agent(i)).swapCount;
		return sum;
	}
}

