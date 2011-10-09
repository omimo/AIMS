package massim.agents.nohelp;

import massim.Environment;
import massim.RowCol;
import massim.Team;


public class NoHelpTeam extends Team {


	public NoHelpTeam() {
		super();
		
		NoHelpAgent[] agents = new NoHelpAgent[teamSize];
		
		for (int i=0;i<teamSize;i++)
			agents[i] = new NoHelpAgent(i,env());
		
		setAgents(agents);
	}
	
	public void reset(RowCol[] agentsPos, int[][] actionCostsMatrix) {
		super.reset(agentsPos, actionCostsMatrix);
		
		for(int i=0;i<teamSize;i++)
			agent(i).incResourcePoints(initResCoef*(Environment.board().rows()+Environment.board().cols()));	
	}
	
	public int pointsEarned(){
		int sum = 0;
		
		for (int i=0;i<teamSize;i++)
			sum += ((NoHelpAgent)agent(i)).pointsEarned();
		
		return sum;
	}
}
