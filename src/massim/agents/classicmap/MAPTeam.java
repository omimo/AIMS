package massim.agents.classicmap;

import massim.Environment;
import massim.RowCol;
import massim.Team;

public class MAPTeam extends Team {

	
	//public static int colorPenalty; // as in the old simulations
	public static int costThreshold;
	
	public MAPTeam() {
		super();
		
		MAPAgent[] agents = new MAPAgent[teamSize];
		
		for (int i=0;i<teamSize;i++)
			agents[i] = new MAPAgent(i,env());
		
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
			sum += ((MAPAgent)agent(i)).pointsEarned();
		
		return sum;
	}
}

