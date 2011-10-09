package massim.agents.nohelp;

import java.util.ArrayList;
import java.util.Scanner;

import massim.Agent;
import massim.Board;
import massim.EnvAgentInterface;
import massim.Path;
import massim.RowCol;
import massim.Team;



public class NoHelpAgent extends Agent {
		
	private boolean forfeit = false;
	private boolean reachedThere = false;
	private boolean debuging = false;
	
	
	public NoHelpAgent(int id, EnvAgentInterface env) {
		super(id,env);
		log("Hello");
	}
	
	@Override 
	public void reset(int[] actionCosts) {
		super.reset(actionCosts);
		
		forfeit = false;
		reachedThere = false;						
	}
	
	@Override
	public void perceive(Board board, int[][] costVectors, RowCol[] goals, RowCol[] agentsPos) {
						
		super.perceive(board, costVectors, goals, agentsPos);
		
			
		if (path() == null && goals[id()] != null)		
			findPath();
		
		if (pos().equals(goalPos()))
			reachedThere = true;
	}
	
	@Override
	public AGCODE act() {
		AGCODE code = AGCODE.OK;;
						
		if (reachedThere)
		{
			log("GOT THERE!!!!");
			return AGCODE.DONE;
		}
		
		if (forfeit)
		{
			log("Forfeited :(");
			return AGCODE.DONE;
		}
		
		RowCol nextCell = path().getNextPoint(pos());
		int cost = getCellCost(nextCell);
							
		if (resourcePoints() >= cost) 
			move();				 			
		else 
			forfeit = true;

		return code;
	}
	
	
	@Override	
	public void doSend() {
		
			// quiet!
		
	}
	
	@Override
	public void doReceive() {		
		
		// ignore 
	}		
	
	private boolean move() {
		RowCol nextPos = path().getNextPoint(pos());
		boolean suc = env().move(id(), nextPos);
		
		if (suc)
			{
				log("at "+ pos() +", moving to " + nextPos );				
				decResourcePoints(getCellCost(nextPos));
			}
		else 
			log("at "+ pos()+", failed to move to " + nextPos );
		
		return suc;
	}
	
	
	
	private void log(String s) {
		if (debuging )
		System.out.println("[NoHelpAgent "+id()+":] "+s);
	}
	
	
	
	/**
	 *JUST COPIED FROM OLD SIMULATIONS 
	 * @return
	 */
	public int pointsEarned() {
						
		int totalPoints = (path().getIndexOf(pos())+1) * Team.cellReward;
		if(reachedThere)
			totalPoints += Team.achievementReward + resourcePoints();
		return totalPoints;
	}
	
}
