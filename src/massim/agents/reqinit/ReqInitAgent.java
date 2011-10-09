package massim.agents.reqinit;

import java.util.ArrayList;
import java.util.Scanner;

import massim.Agent;
import massim.Board;
import massim.EnvAgentInterface;
import massim.RowCol;
import massim.Team;

public class ReqInitAgent extends Agent {
	

	private boolean debuging = false;
	
	private enum RIState1 {NORMAL, SEND_REQ, WAIT_FOR_WILLINGS, SEND_ACK, DO_IT_MYSELF, WAIT_FOR_HELP, DONE, BLOCKED};
	private enum RIState2 {ACCEPT_REQ, SEND_WILL, WAIT_FOR_ACK, DO_HELP, IGNORE};
	
	private RIState1 state1;
	private RIState2 state2;
	
	private int[][] actionCostsMatrix;
	private int waitForWillPass;
	private int helperAgent;
	private int agentToHelp;
	private RowCol agentToHelpPos;
	private int waitForAckPass;
	private RowCol troublePos = null;
	private int waitForHelpPass;
	
	private void initValues() {
		state1 = RIState1.NORMAL;
		state2 = RIState2.ACCEPT_REQ;
		waitForWillPass=3;
		waitForAckPass=3;
		helperAgent=-1;
		agentToHelp=-1;
		agentToHelpPos=null;
		actionCostsMatrix=null;
		waitForHelpPass=3;
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
		{
			log("There!");
			state1 = RIState1.DONE;
		}
		
		if (state1  == RIState1.WAIT_FOR_HELP)		
		{
			waitForHelpPass--;
			if ((troublePos != null &&!pos().equals(troublePos)) 
					)
			{
				troublePos = null;
				state1 = RIState1.NORMAL;
				waitForHelpPass=3;
			}
			if (waitForHelpPass<=0)
			{
				troublePos = null;
				state1 = RIState1.NORMAL;
				waitForHelpPass=3;
				(new Scanner(System.in)).nextLine();
			}
		}
		this.actionCostsMatrix = new int[actionCostsMatrix.length][actionCostsMatrix[0].length];
		for (int p = 0; p < actionCostsMatrix.length; p++)
			for (int q = 0; q < actionCostsMatrix[0].length; q++)
				this.actionCostsMatrix[p][q] = actionCostsMatrix[p][q];
			
	}
		
	
	@Override
	public AGCODE act() {
		AGCODE code = AGCODE.OK;
		
		RowCol nextCell = path().getNextPoint(pos());
		int cost = getCellCost(nextCell);
		log(state1.toString());
		
		if (state2 == RIState2.DO_HELP)
		{
			log("helping "+agentToHelp);
			helpMove();
			state2=RIState2.ACCEPT_REQ;
		}
						
		if (state1 == RIState1.NORMAL)
		{
			if (cost > Team.costThreshold)
			{
				log("at "+ pos() + ", going to "+ nextCell +". Need help, should send the request next round");
				troublePos = new RowCol(pos());
				state1 = RIState1.SEND_REQ;
			}
			else if (cost < resourcePoints())
			{				
				move();
			}
			else
			{
				log("No more hope! forfeiting...");
				state1 = RIState1.BLOCKED;
			}
		}
		else if (state1 == RIState1.DO_IT_MYSELF)
		{
			if (cost < resourcePoints())
			{
				move();
			}
			else
			{
				log("No more hope! forfeiting...");
				state1 = RIState1.BLOCKED;
			}
		}		
				
		if (state1 == RIState1.DONE || state1 == RIState1.BLOCKED)
			code = AGCODE.DONE;
		
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
				int oc = getCellCost(nextCell,actionCostsMatrix[i])+Team.helpOverhead;
				if (oc < Team.costThreshold && i != id())
					{
						log("Sending help req to agent "+i);
						if (!canSend())
						{
							log("Can not communicate");
							state1= RIState1.DO_IT_MYSELF;
						}
						else
						{
							sendReq(i,nextCell);
							sendCount++;
						}
					}
			}
			
			if (sendCount > 0)				
				state1 = RIState1.WAIT_FOR_WILLINGS;
			else
				state1 = RIState1.DO_IT_MYSELF;
		}
		else if (state1 == RIState1.SEND_ACK)
		{
			if (!canSend())
			{
				log("Can not communicate");
				helperAgent=-1;
				state1 = RIState1.DO_IT_MYSELF;
			}
			else
			{
				sendAck(helperAgent);
				helperAgent=-1;
				state1 = RIState1.WAIT_FOR_HELP;
			}
		}
		
		// STATE2
		
		if (state2 == RIState2.SEND_WILL)
		{
			if (!canSend())
			{
				log("Can not communicate");
				agentToHelp=-1;
				state2 = RIState2.ACCEPT_REQ;
			}
			else
			{
				sendWill(agentToHelp);
				state2 = RIState2.WAIT_FOR_ACK;
			}
		}
	}
		

	@Override
	public void doReceive() {	
		ArrayList<RIWillMessage> willMsgs = new ArrayList<RIWillMessage>();
		ArrayList<RIHelpReqMessage> helpReqMsgs = new ArrayList<RIHelpReqMessage>();
		RIAckMessage ackMsg= null;
		
		String msg = env().communicationMedium().receive(id());		
		while (!msg.equals(""))
		{			
			if (RIWillMessage.isInstanceOf(msg))
				willMsgs.add(new RIWillMessage(msg));		
			else if (RIHelpReqMessage.isInstanceOf(msg))
				helpReqMsgs.add(new RIHelpReqMessage(msg));
			else if (RIAckMessage.isInstanceOf(msg))
				ackMsg = new RIAckMessage(msg);
			
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
				{
					int costForHelper =getCellCost(nextCell, actionCostsMatrix[willMsgs.get(i).sender])+Team.helpOverhead; 					
					if ( costForHelper < min_cost)
					{
						min_cost = costForHelper;
						min_agent = willMsgs.get(i).sender;
					}					
					log("Agent "+ willMsgs.get(i).sender+" is willing to help me. I think it would cost her "+costForHelper);
				}
				if (min_agent != -1)
				{
					state1 = RIState1.SEND_ACK;
					helperAgent  = min_agent;
					log("Chose agent "+ helperAgent+" to help me.");
				}
			}					
			else if (waitForWillPass <= 0)
			{
				waitForWillPass = 3;
				state1 = RIState1.DO_IT_MYSELF;
			}
			else 
				waitForWillPass--;
		}

		// STATE2
		
		if (state2 == RIState2.ACCEPT_REQ)
		{
			if (helpReqMsgs.size() > 0)
			{
				int min_cost = Integer.MAX_VALUE;
				int min_agent = -1;
				RowCol min_agent_pos = null;
				for (int i=0;i<helpReqMsgs.size();i++)
				{
					int cost = getCellCost(new RowCol (helpReqMsgs.get(i).row,helpReqMsgs.get(i).col))+Team.helpOverhead;					
					if (cost < min_cost && cost < Team.costThreshold)
					{
						min_cost = cost;
						min_agent = helpReqMsgs.get(i).sender;
						min_agent_pos = new RowCol (helpReqMsgs.get(i).row,helpReqMsgs.get(i).col);
					}
					log("Got help req from agent "+helpReqMsgs.get(i).sender+" it can cost me "+ cost);
					
				}
				if (min_agent != -1)
				{
					state2 = RIState2.SEND_WILL;
					agentToHelp = min_agent;
					agentToHelpPos = new RowCol(min_agent_pos);
					log("Chose to help agent "+agentToHelp);
				}
			}
		}
		else if (state2 == RIState2.WAIT_FOR_ACK)
		{
			if (ackMsg != null)
			{
				log("Received ack from agent "+ agentToHelp);
				state2 = RIState2.DO_HELP;
				waitForAckPass=3;
			}			
			else if(waitForAckPass <= 0)
			{
				state2 = RIState2.ACCEPT_REQ;
				agentToHelp = -1;
				agentToHelpPos = null;
				waitForAckPass=3;
			}
			else 
				waitForAckPass--;
		}
	}
	
	private boolean move() {
		
		troublePos = null;
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
		
		boolean suc = env().move(agentToHelp, agentToHelpPos);				
		if (suc)
			{
				log("Helping Agent " + agentToHelp +": to move to " + agentToHelpPos );				
				decResourcePoints(getCellCost(agentToHelpPos)+Team.helpOverhead);
			}
		else 
			log("Failed to help Agent " + agentToHelp +": move to " + agentToHelpPos );
				
		agentToHelp = -1;
		agentToHelpPos = null;	
		return suc;
		
	}
	
	private boolean canCalc() {
		return (resourcePoints()-Team.calculationCost >= 0);
	}
	
	private boolean canSend() {
		return (resourcePoints()-Team.unicastCost >= 0);
	}	
	
	private void sendReq(int agent, RowCol cell) {
		
		decResourcePoints(Team.unicastCost);
		
		RIHelpReqMessage helpReqMsg = new RIHelpReqMessage(id(),agent,cell);
		env().communicationMedium().send(id(), agent, helpReqMsg.toString());
	}
	
	private void sendAck(int agent) {		
		decResourcePoints(Team.unicastCost);
		
		RIAckMessage ackMsg = new RIAckMessage(id(), agent);
		env().communicationMedium().send(id(), agent, ackMsg.toString());
	}
	
	private void sendWill(int agent) {
		
		decResourcePoints(Team.unicastCost);
		RIWillMessage willMsg = new RIWillMessage(id(), agent);
		env().communicationMedium().send(id(), agent, willMsg.toString());
	}
	
	private void log(String s) {
		if (debuging)
			System.out.println("[HelperInitAgent "+id()+":] "+s);
	}
	
	/**
	 *JUST COPIED FROM OLD SIMULATIONS 
	 * @return
	 */
	public int pointsEarned() {
		
		int totalPoints;
		if(state1 == RIState1.DONE)
			totalPoints = Team.achievementReward + resourcePoints();
		else 
			totalPoints = (path().getIndexOf(pos())+1) * Team.cellReward;
		return totalPoints;
	}
}