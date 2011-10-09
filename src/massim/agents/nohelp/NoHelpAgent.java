package massim.agents.nohelp;

import massim.Agent;
import massim.EnvAgentInterface;
import massim.Board;
import massim.Path;
import massim.RowCol;
import massim.Team;



public class NoHelpAgent extends Agent {

	private Board theBoard;
	private Path path;
		
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
		
		theBoard = board;			
		if (path == null && goals[id()] != null)		
			findPath();
		
		if (pos().equals(goalPos()))
			reachedThere = true;
	}
	
	public int getCellCost(RowCol cell) {
		
		int [] colorRange = env().colorRange();		
		int index = 0;
		for (int i=0;i<colorRange.length;i++)
		{
			int color = theBoard.getBoard()[cell.row][cell.col];
			if (color == colorRange[i])
				index = i;			
		}
		
		return actionCosts()[index];			
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
		
		RowCol nextCell = path.getNextPoint(pos());
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
		
		// ignorance 
	}
	
	private void findPath() {
		log("Does not have a path, finding one ...");
		
		path = Path.getShortestPath2(pos(), goalPos(), theBoard.getBoard(), actionCosts());
	
		log("My path will be: " + path);
	}

	
	// ------------- MAP Specific Methods
	
	private boolean canCalc() {
		return (resourcePoints()-Team.calculationCost >= 0);
	}
	
	private boolean canSend() {
		return (resourcePoints()-Team.unicastCost >= 0);
	}
	
	private boolean canBroadcast() {
		return (resourcePoints()-Team.broadcastCost >= 0);
	}
	
	
	private boolean move() {
		RowCol nextPos = path.getNextPoint(pos());
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
						
		int totalPoints = (path.getIndexOf(pos())+1) * Team.cellReward;
		if(reachedThere)
			totalPoints = Team.achievementReward + resourcePoints();
		return totalPoints;
	}
	
}
