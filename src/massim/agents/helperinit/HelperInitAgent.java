package massim.agents.helperinit;

import massim.Agent;
import massim.Board;
import massim.EnvAgentInterface;
import massim.RowCol;
import massim.Team;
import massim.Agent.AGCODE;

public class HelperInitAgent extends Agent {

	private boolean forfeit = false;
	private boolean reachedThere = false;
	private boolean debuging = false;
	
	private void initValues() {
		
	}
	
	public HelperInitAgent(int id, EnvAgentInterface env) {
		super(id, env);
		log("Hello");
		initValues();
	}

	@Override 
	public void reset(int[] actionCosts) {
		super.reset(actionCosts);
		initValues();
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
		AGCODE code = AGCODE.OK;
		
		return code;
	}
	
	@Override	
	public void doSend() {
		
	}
	
	@Override
	public void doReceive() {	
		
	}
	
	private boolean move() {
		RowCol nextPos = path().getNextPoint(pos());
		boolean suc = env().move(id(), nextPos);
		
		if (suc)
			{
				log("Agent " + id() +": at "+ pos() +", moving to " + nextPos );				
				decResourcePoints(getCellCost(nextPos));
			}
		else 
			log("Agent " + id() +": at "+ pos()+", failed to move to " + nextPos );
		
		return suc;
	}
	
	private boolean helpMove() {
		
		boolean suc = true;
		
		return suc;
	}
	
	
	private void log(String s) {
		if (debuging )
		System.out.println("[HelperInitAgent "+id()+":] "+s);
	}
}
