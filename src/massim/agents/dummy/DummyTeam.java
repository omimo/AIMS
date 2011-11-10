package massim.agents.dummy;

import massim.RowCol;
import massim.Team;

/**
 * 
 * 
 * @author Omid Alemi
 * @version 2.0 2011/10/31
 */
public class DummyTeam extends Team {	
	
	/**
	 * The default constructor
	 */
	public DummyTeam() {
		super();		
			
		DummyAgent[] dummyAgents = new DummyAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			dummyAgents[i] = new DummyAgent(i);
		
		setAgents(dummyAgents);
	}	
	
	/**
	 * The overridden Team.initializeRun() method.
	 * 
	 * This calls the same method of the superclass first.
	 * 
	 * Initialized the agents, giving them their initial position, goal
	 * position, action costs, and their initial resources based on their
	 * path length.
	 */
	@Override
	public void initializeRun(
		RowCol[] initAgentsPos, RowCol[] goals, int[][]actionCostsMatrix) {
		
		super.initializeRun(initAgentsPos, goals, actionCostsMatrix);
						
		for(int i=0;i<Team.teamSize;i++)
		{
			int pathLength = calcDistance(initAgentsPos[i], goals[i]);
			
			agent(i).initializeRun(initAgentsPos[i], goals[i], 
					actionCostsMatrix[i], pathLength * initResCoef);
		}
	}
	
	/**
	 * Calculates the distance between two points in a board.
	 * 
	 * @param start					The position of the starting point
	 * @param end					The position of the ending point
	 * @return						The distance
	 */
	private int calcDistance(RowCol start, RowCol end) {
		return  Math.abs(end.row-start.row) + Math.abs(end.col-start.col) + 1;
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
		int sum = 0;
		
		for(int i=0;i<Team.teamSize;i++)
			sum += agent(i).rewardPoints();
		
		return sum;
	}
		
}
