package massim.agents;

import tests.DummyMessage;
import massim.Agent;
import massim.EnvAgentInterface;
import massim.Board;
import massim.Environment;
import massim.Goal;
import massim.Path;
import massim.RowCol;

public class DummyAgent extends Agent {

	private Board theBoard;
	private Path path;
	
	boolean sentHelpReq = false;
	boolean recHelpReq = false;
	boolean shouldAck = false;
	boolean reachedThere = false;
	
	public DummyAgent(int id, EnvAgentInterface env) {
		super(id,env);
		System.out.println("Hello from DummyAgent " + id());
	}
	
	
	@Override
	public void perceive(Board board, int[][] costVectors, RowCol[] goals, RowCol[] agentsPos) {
						
		super.perceive(board, costVectors, goals, agentsPos);
		
		theBoard = board;
		System.out.println("Agent " + id() +" New Percepts:");
		System.out.println("Agent " + id() +": resourcePoints = "+ resourcePoints());
		System.out.println("Agent " + id() +": my pos = "+ pos() 
				+": my goal's pos = "+ goalPos());
		
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
		
		if (!reachedThere)
		{
			RowCol nextPos = path.getNextPoint(pos());
			if (env().move(id(), nextPos))
				{
					System.out.println("Agent " + id() +": moving to " + nextPos );
					
					decResourcePoints(getCellCost(nextPos));
				}
			else 
				System.out.println("Agent " + id() +": failed to move to " + nextPos );
		}
		else
		{
			code = AGCODE.OFF;
		}
			
		
		return code;
	}
	
	
	@Override	
	public void doSend() {
		
		
	}
	
	@Override
	public void doReceive() {		
		
		
	}
	
	private void findPath() {
		System.out.println("Agent " + id() +": Does not have a path, finding one ...");
		
		path = Path.getShortestPaths(pos(), goalPos(), theBoard.getBoard(), actionCosts(), 1).get(0);
		
		System.out.println("Agent " + id() +": My path will be: " + path);
	}
	
}
