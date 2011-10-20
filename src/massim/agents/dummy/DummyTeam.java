package massim.agents.dummy;

import java.util.Random;

import massim.RowCol;
import massim.Team;

public class DummyTeam extends Team {	
	
	/**
	 * The default constructor
	 */
	public DummyTeam() {
		super();		
		
	}	
	
	/**
	 * The overridden Team.initializeRun() method.
	 * 
	 * This calls the same method of the superclass first.
	 * 
	 */
	@Override
	public void initializeRun(
			RowCol[] initAgentsPos, RowCol[] goals, int[][]actionCostMatrix) {
		
		super.initializeRun(initAgentsPos, goals, actionCostMatrix);
		testRunCounter = 10 + (new Random()).nextInt(5);
	}
	
	
	/**
	 * For debugging purposes only:
	 * 
	 * The overridden Team.teamRewardPoints() method to return a dummy amount 
	 * of reward points.
	 * 
	 * @return					The amount of reward points.
	 */
	@Override
	public int teamRewardPoints() 
	{
		Random rnd = new Random();
		return rnd.nextInt(10000);
	}
		
}
