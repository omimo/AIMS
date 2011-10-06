package massim.agents;

import tests.DummyHelpReqMessage;
import massim.Agent;
import massim.Board;
import massim.Goal;
import massim.RowCol;

public class DummyAgent extends Agent {

	private Board theBoard;
	
	public DummyAgent(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void perceive(Board board, int[][] costVectors, Goal[] goals, RowCol[] agentsPos) {
		super.perceive(board, costVectors, goals, agentsPos);
		theBoard = board;
	}
	
	@Override
	public int act() {
		System.out.println("Agent " + id() +": resources = "+ resources());
		System.out.println("Agent " + id() +": my pos = "+ pos().row + " , " + pos().col);
		
		
		
		return 0;
	}
	
	
	@Override	
	public void doSend() {
		if (resources() < 200)
		{
			DummyHelpReqMessage dhrm = new DummyHelpReqMessage(this.id(),-1,resources());
			dhrm.pack();
			team().broadcast(this.id(), dhrm.toString());
		}
		
	}
	
	@Override
	public void doReceive() {
		String[] incoming = team().receive(this.id());
		System.out.println("Agent " + id() +": buffers: ");
		printBuffer(incoming);
		
		for(String msg : incoming)
		{
			if (msg=="")
				continue;
			
			if (msg.startsWith("helpme"))
				System.out.println("Agent " + id() +": received a helpreq");
				//remember to help;			
		}
	}

	public static void printBuffer(String[] buffer) {		
		for (int i=0;i<buffer.length;i++)
		{
			System.out.print("Agent "+ i+ " : ");
			System.out.println(buffer[i]);
		}
	}
}
