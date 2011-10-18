package massim.agents.dummy;

import java.util.Random;

import massim.RowCol;
import massim.Team;

public class UselessTeam extends Team{
	
	private boolean debuggingInf = true;
	
	public UselessTeam() {
		super();				
	}	
	
	@Override
	public void initializeRun(RowCol[] initAgentsPos, RowCol[] goals, int[][]actionCostMatrix) {
		super.initializeRun(initAgentsPos, goals, actionCostMatrix);		
		testRunCounter = 7;		
	}
	
	@Override
	public int teamRewardPoints() 
	{
		Random rnd = new Random();
		return rnd.nextInt(10000);
	}
	
	private void logInf(String msg) {
		if (debuggingInf)
			System.out.println("[Useless Team]: " + msg);
	}
}
