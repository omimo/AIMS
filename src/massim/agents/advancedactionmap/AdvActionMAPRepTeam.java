package massim.agents.advancedactionmap;

import massim.Board;
import massim.Team;


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
	
	public TeamRoundCode round(Board board) {
		TeamRoundCode code = super.round(board);
		if(code == TeamRoundCode.DONE) {
			logInf("Replanning Count = " + getReplanCounts());
		}
		return code;
	}
	
	public int getReplanCounts() {
		int sum = 0;
		for(int i=0;i<Team.teamSize;i++)
			sum += ((AdvActionMAPRepAgent)agent(i)).replanCount;
		return sum;
	}
	
	//Denish, 2014/04/23
	public void setUseHelp2Character(boolean useHelp2Character) {
		for(int i=0;i<Team.teamSize;i++)
			((AdvActionMAPRepAgent)agent(i)).useHelp2Character = useHelp2Character;
	}
	
	public void setUseSwap(boolean useSwap) {
		for(int i=0;i<Team.teamSize;i++)
			((AdvActionMAPRepAgent)agent(i)).useSwapProtocol = useSwap;
	}
	
	public int getSwapCounts() {
		int sum = 0;
		for(int i=0;i<Team.teamSize;i++)
			sum += ((AdvActionMAPRepAgent)agent(i)).swapCount;
		return sum;
	}
}
