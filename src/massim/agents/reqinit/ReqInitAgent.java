package massim.agents.reqinit;

import java.util.ArrayList;

import massim.Agent;
import massim.Board;
import massim.EnvAgentInterface;
import massim.RowCol;
import massim.Team;

public class ReqInitAgent extends Agent {
	
	private boolean reachedThere = false;
	private boolean debuging = false;
	
	private enum RIState1 {NORMAL, SEND_REQ, WAIT_FOR_WILLINGS, SEND_ACK, DO_IT_MYSELF, DONE, FORFEIT};
	private enum RIState2 {ACCEPT_REQ, SEND_WILL, WAIT_FOR_ACK, DO_HELP, IGNORE};
	
	private RIState1 state1;
	private RIState2 state2;
	
	private int[][] actionCostsMatrix;
	private int waitForWillPass;
	
	
	
	private void initValues() {
		state1 = RIState1.NORMAL;
		state2 = RIState2.ACCEPT_REQ;
		waitForWillPass=2;
	}
	
	public ReqInitAgent(int id, EnvAgentInterface env) {
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
	public void perceive(Board board, int[][] actionCostsMatrix, RowCol[] goals, RowCol[] agentsPos) {
						
		super.perceive(board, actionCostsMatrix, goals, agentsPos);
						
		if (path() == null && goals[id()] != null)		
			findPath();
		
		if (pos().equals(goalPos()))
			reachedThere = true;
		
		for (int p = 0; p < actionCostsMatrix.length; p++)
			for (int q = 0; q < actionCostsMatrix[0].length; q++)
				this.actionCostsMatrix[p][q] = actionCostsMatrix[p][q];
	}
		
	
	@Override
	public AGCODE act() {
		AGCODE code = AGCODE.OK;
		
		RowCol nextCell = path().getNextPoint(pos());
		int cost = getCellCost(nextCell);
		
		if (state1 == RIState1.NORMAL)
		{
			if (cost > Team.costThreshold)
			{
				log("at "+ pos() + ", going to "+ nextCell +". Need help, should send the request next round");
				state1 = RIState1.SEND_REQ;
			}
			else if (cost < resourcePoints())
			{
				move();
			}
			else
			{
				log("No more hope! forfeiting...");
				state1 = RIState1.FORFEIT;
			}
		}
		
		return code;
	}
	
	@Override	
	public void doSend() {
		
		if (state1 == RIState1.SEND_REQ)
		{
			RowCol nextCell = path().getNextPoint(pos());
			
			int sendCount = 0;
			
			for (int i=0;i<Team.teamSize;i++)
			{
				int oc = getCellCost(nextCell,actionCostsMatrix[i]);
				if (oc < Team.costThreshold)
					{
						sendReq(i,nextCell);
						sendCount++;
					}
			}
			
			if (sendCount > 0)				
				state1 = RIState1.WAIT_FOR_WILLINGS;
			else
				state1 = RIState1.DO_IT_MYSELF;
		}
	}
		

	@Override
	public void doReceive() {	
		ArrayList<RIWillMessage> willMsgs = new ArrayList<RIWillMessage>();
		ArrayList<RIHelpReqMessage> helpReqMsgs = new ArrayList<RIHelpReqMessage>();
		
		String msg = env().communicationMedium().receive(id());		
		while (!msg.equals(""))
		{			
			if (RIWillMessage.isInstanceOf(msg))
				willMsgs.add(new RIWillMessage(msg));			
			msg = env().communicationMedium().receive(id());
		}
		
		if (state1 == RIState1.WAIT_FOR_WILLINGS)
		{
			
			if (willMsgs.size() > 0)
			{
				RowCol nextCell = path().getNextPoint(pos());
				int min_cost = Integer.MAX_VALUE;
				int min_agent = -1;
				for(int i=0;i<willMsgs.size();i++)				
					if (getCellCost(nextCell, actionCostsMatrix[willMsgs.get(i).sender]) < min_cost)
					{
						min_cost = getCellCost(nextCell, actionCostsMatrix[willMsgs.get(i).sender]);
						min_agent = i;
					}			
			}
			else if (waitForWillPass <= 0)
			{
				waitForWillPass = 2;
				state1 = RIState1.DO_IT_MYSELF;
			}
			else 
				waitForWillPass--;
		}
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
	
	private void sendReq(int agent, RowCol cell) {
		
		RIHelpReqMessage helpReqMsg = new RIHelpReqMessage(id(),agent,cell);
		env().communicationMedium().send(id(), agent, helpReqMsg.toString());
	}
	
	private void log(String s) {
		if (debuging )
		System.out.println("[HelperInitAgent "+id()+":] "+s);
	}
	
	/**
	 *JUST COPIED FROM OLD SIMULATIONS 
	 * @return
	 */
	public int pointsEarned() {
		
		int totalPoints = (path().getIndexOf(pos())+1) * Team.cellReward;
		if(reachedThere)
			totalPoints = Team.achievementReward + resourcePoints();
		return totalPoints;
	}
}