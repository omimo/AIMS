package massim.agents.reqinit;

import massim.Environment;
import massim.RowCol;
import massim.Team;

public class ReqInitTeam extends Team {
	
	public ReqInitTeam() {
		super();
		
		ReqInitAgent[] agents = new ReqInitAgent[teamSize];
		
		for (int i=0;i<teamSize;i++)
			agents[i] = new ReqInitAgent(i,env());
		
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
			sum += ((ReqInitAgent)agent(i)).pointsEarned();
		
		return sum;
	}
}
