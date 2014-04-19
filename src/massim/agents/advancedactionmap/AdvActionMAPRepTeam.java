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
	
	
}
