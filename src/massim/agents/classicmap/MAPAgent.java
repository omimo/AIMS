package massim.agents.classicmap;

import java.util.ArrayList;
import java.util.Scanner;

import massim.Agent;
import massim.Board;
import massim.EnvAgentInterface;
import massim.Path;
import massim.RowCol;
import massim.Agent.AGCODE;
import massim.Team;

public class MAPAgent extends Agent {
			
		
	private boolean forfeit = false;
	private boolean reachedThere = false;
	private boolean debuging = false;
	
	private enum MAPState1 {NORMAL, SHOULD_REQ, WAIT_FOR_BIDS, SHOULD_ACK, DO_IT_MYSELF };
	private enum MAPState2 {ACCEPT_REQS, RECEIVED_REQ, SHOULD_BID, WAIT_FOR_ACK, SHOULD_DO_HELP, IGNORE};
	
	MAPState1 state1;
	MAPState2 state2;
	
	private int helperAgent;
	
	private int agentToHelp;
	private int teamCostIfHelp;
	private RowCol agentToHelpPos;
	
	private int waitForBidsPass;
	private int waitForAckPass;
	
	private void initValues() {
		forfeit = false;
		reachedThere = false;
		
		state1 = MAPState1.NORMAL;
		state2 = MAPState2.ACCEPT_REQS;
		
		helperAgent = -1;
		agentToHelp = -1;
		teamCostIfHelp = 0;
		agentToHelpPos = null;
		
		waitForBidsPass = 3;
		waitForAckPass = 3;
			
	}
	
	public MAPAgent(int id, EnvAgentInterface env) {
		super(id,env);
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
		
		//--------
		
		if (state2 == MAPState2.SHOULD_DO_HELP)
		{
			helpMove();
			state2 = MAPState2.ACCEPT_REQS;
		}
		
				
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
		
		
		if (state1 == MAPState1.NORMAL)
		{
			if(cost > MAPTeam.costThreshold ) // or resourcePoints() < cost
			{				
				log("at "+ pos() + ", going to "+ nextCell +". Need help, should send the request next round");
				state1 = MAPState1.SHOULD_REQ;								
			} 
			else if (resourcePoints() >= cost) 
			{
				move();				
			} 			
			else 
			{
				forfeit = true;
			}			
		}		
		else if (state1 == MAPState1.DO_IT_MYSELF)
		{
			if (resourcePoints() >= cost) 
			{
				move();
				state1 = MAPState1.NORMAL;
			} 			
			else 
			{
				forfeit = true;
			}			
		}
		
		
		

		return code;
	}
	
	
	@Override	
	public void doSend() {
		
		if (state1==MAPState1.SHOULD_REQ)
		{			
		
			RowCol nextCell = path().getNextPoint(pos());
			
			int withHelp = calculateTeamBenefitWithHelp();			
			int noHelp = projectPoints(pos(),resourcePoints());
			int benefit = withHelp - noHelp;
			log("with= "+ withHelp+"   nohelp="+noHelp);
			
			state1 = MAPState1.WAIT_FOR_BIDS;
			waitForBidsPass = 3;
			
			requestHelp(benefit, nextCell);
			
			
		}
		else if (state1 == MAPState1.SHOULD_ACK)
		{
			log("Sending ack to the winner: Agent "+helperAgent);
			ackHelp(helperAgent);
			helperAgent = -1;
			state1 = MAPState1.NORMAL;
		}
		
		if (state2 == MAPState2.SHOULD_BID)
		{						
			log("Bidding to help agent " +agentToHelp + " with the value: "+ teamCostIfHelp);
			bid(agentToHelp, teamCostIfHelp);
			waitForAckPass = 3;
			state2=MAPState2.WAIT_FOR_ACK;
		}
		
	}
	
	@Override
	public void doReceive() {		
		
		ArrayList<MAPHelpReqMessage> requestMsgs = new ArrayList<MAPHelpReqMessage>();
		ArrayList<MAPBidMessage> bidMsgs = new ArrayList<MAPBidMessage>();
		MAPAckMessage ackMsg = null;
		
		String msg = env().communicationMedium().receive(id());
		
		while (!msg.equals(""))
		{			
			if (MAPHelpReqMessage.isInstanceOf(msg)) // msg is a help request			
				requestMsgs.add(new MAPHelpReqMessage(msg));
			else if (MAPBidMessage.isInstanceOf(msg)) // msg is a bid		
				bidMsgs.add(new MAPBidMessage(msg));	
			else if (MAPAckMessage.isInstanceOf(msg))
				ackMsg = new MAPAckMessage(msg);
			
			msg = env().communicationMedium().receive(id());
		}
		
				
		
		if (state1 == MAPState1.WAIT_FOR_BIDS)
		{													
						
			int min_bid = Integer.MAX_VALUE;
			int min_agent = -1;
			
			log(waitForBidsPass+ " )any bids?"+ bidMsgs.size());
				//(new Scanner(System.in)).nextLine();
			
			
			for (int i=0;i<bidMsgs.size();i++)		
				if (bidMsgs.get(i).amount> 0 && bidMsgs.get(i).amount < min_bid)
				{
					min_bid = bidMsgs.get(i).amount;
					min_agent = bidMsgs.get(i).sender;
				}				
			
			if (min_agent != -1)
			{
				state1 = MAPState1.SHOULD_ACK;
				helperAgent = min_agent;
			}
			else if (waitForBidsPass <= 0 )
			{
				waitForBidsPass = 4;
				state1 = MAPState1.DO_IT_MYSELF;
			}
			
			waitForBidsPass--;
		}
		
		if (state2 == MAPState2.ACCEPT_REQS)  // or could be anything
		{
		
			int max_req = Integer.MIN_VALUE;
			int max_agent = -1;
			RowCol max_agent_pos = null; 
			
			if (requestMsgs.size() > 0) {
				for (int i = 0; i < requestMsgs.size(); i++)
					// here it helps just the max one!
					if (requestMsgs.get(i).benefit > max_req) {
						max_req = requestMsgs.get(i).benefit;
						max_agent = requestMsgs.get(i).sender;
						max_agent_pos = new RowCol(requestMsgs.get(i).row,
								requestMsgs.get(i).col);
					}

				agentToHelpPos = new RowCol(max_agent_pos.row,	max_agent_pos.col);
				teamCostIfHelp = calculateTeamCost(max_req, agentToHelpPos);								
				agentToHelp = max_agent;
				if (teamCostIfHelp>0)
				{
					state2 = MAPState2.SHOULD_BID;
					log("some one needs help!");
				}
			}
		}
		else if (state2 == MAPState2.WAIT_FOR_ACK)		
			if (ackMsg != null)
				state2 = MAPState2.SHOULD_DO_HELP;
			else 
			{
				if (waitForAckPass<=0)
				{
					state2 = MAPState2.ACCEPT_REQS;
					waitForAckPass = 3;
				}
				waitForAckPass--;
			}

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
	
	private int calculateTeamBenefitWithHelp()
	{
		if (!canCalc())
			return -1;	
		decResourcePoints(Team.calculationCost);
		
		RowCol nextCell = path().getNextPoint(pos());
		int benefit = projectPoints(nextCell,resourcePoints());
		
		return benefit+Team.cellReward;
	}
	
	private int projectPoints(RowCol start, int resPoints)
	{		
		RowCol i = new RowCol(start.row,start.col);
		int benefit = 0;
		
		while(!i.equals(path().getEndPoint())) 
		{
			int cost = getCellCost(i); 				
			
			if(resPoints >= cost) 
			{
				resPoints -= cost;
				i = new RowCol(path().getNextPoint(i).row,path().getNextPoint(i).col);
				benefit += Team.cellReward;				
			} 
			else 
			{
				break;
			}
		}					
		
		if(i.equals(path().getEndPoint()))
			benefit += resPoints;

		return benefit;
	}
	
	private void requestHelp(int benefit, RowCol nextCell)
	{
		
		if(benefit <= 0)
		{
			state1 = MAPState1.DO_IT_MYSELF;
			return; // Don't ask for help
		}
		
		if (!canBroadcast())
		{
			log("Don't have enough resources to broadcast :(");
			state1 = MAPState1.DO_IT_MYSELF;
			return;
		}
		
		log("Broadcasting help...!");
		MAPHelpReqMessage req = new MAPHelpReqMessage(id(), benefit, nextCell);
		env().communicationMedium().broadcast(id(), req.toString());
		
	}
	

	private void ackHelp(int receiver)
	{
		if (!canSend())
			return;
		decResourcePoints(Team.unicastCost);
		
		MAPAckMessage ackMsg = new MAPAckMessage(id(), receiver);
		env().communicationMedium().send(id(), receiver, ackMsg.toString());
	}	
	
	private int calculateTeamCost(int benefit, RowCol cell)
	{				
		if(!canCalc()) 
		{			
//			forfeit = true;
			return -1; // Not enough points to calculate
		}
		
		decResourcePoints(Team.calculationCost);
			
		int cost = getCellCost(cell) + Team.helpOverhead;					
		int remaining = resourcePoints() - cost;	
		if(remaining < 0)
			return -1; // Refuse to do work
		
		int yes = projectPoints(pos(), remaining);
		int no = projectPoints(pos(), resourcePoints());
		
		if (no-yes > benefit)
			return -1;
		else						
			return no - yes;
		
	}
	
	private void bid(int agent, int teamCost) {
		if (!canSend())
			return;
		
		decResourcePoints(Team.unicastCost);
		
		MAPBidMessage bid_msg = new MAPBidMessage(id(), agent, teamCost);
		env().communicationMedium().send(id(), agent, bid_msg.toString());
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
	
	
	private void log(String s) {
		if (debuging )
		System.out.println("[MAPAgent "+id()+":] "+s);
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
