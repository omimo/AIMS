package massim.agents;

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

	
	private Board theBoard;
	private Path path;
		
	private boolean forfeit = false;
	private boolean reachedThere = false;
	private boolean debuging = true;
	
	private enum MAPState1 {NORMAL, SHOULD_REQ, WAIT_FOR_BIDS, SHOULD_ACK, DO_IT_MYSELF };
	private enum MAPState2 {ACCEPT_REQS, RECEIVED_REQ, SHOULD_BID, WAIT_FOR_ACK, SHOULD_DO_HELP};
	
	MAPState1 state1 = MAPState1.NORMAL;
	MAPState2 state2 = MAPState2.ACCEPT_REQS;
	
	private int helperAgent = -1;
	
	private int agentToHelp = -1;
	private int teamCostIfHelp = 0;
	private RowCol agentToHelpPos = null;
	
	private int waitForBidsPass = 2;
	
	public MAPAgent(int id, EnvAgentInterface env) {
		super(id,env);
		System.out.println("Hello from MAPAgent " + id());
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
		
		//--------
		
		if (state2 == MAPState2.SHOULD_DO_HELP)
		{
			helpMove();
			state2 = MAPState2.ACCEPT_REQS;
		}
		
		if (pos().equals(path.getEndPoint()))
			reachedThere = true;
		
		if (reachedThere)
			return AGCODE.OFF;
		
		RowCol nextCell = path.getNextPoint(pos());
		int cost = getCellCost(nextCell);
		
		
		if (state1 == MAPState1.NORMAL)
		{
			if(cost == MAPTeam.colorPenalty) // or resourcePoints() < cost
			{				
				log("at "+ pos() + ", going to "+ nextCell +". Need help, should send the request next round");
				state1 = MAPState1.SHOULD_REQ;								
			} 
			else if (resourcePoints() >= cost) 
			{
				move();
				state1 = MAPState1.NORMAL;
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
		
		/*if(cost == MAPTeam.colorPenalty && state1 == MAPState1.NORMAL) // or resourcePoints() < cost
		{
			
			log("at "+ pos() + ", going to "+ nextCell +". Need help, should send the request next round");
			state1 = MAPState1.SHOULD_REQ;					
		
		} 
		else if (state1 == MAPState1.DO_IT_MYSELF)
		{
			if (resourcePoints() >= cost) 
			{
				log("Doint it by myself");
				move();
				state1 = MAPState1.NORMAL;
			}
			else
				forfeit = true;
		} 
		else if (resourcePoints() >= cost) 
		{
			move();
			state1 = MAPState1.NORMAL;
		} 			
		else 
		{
			forfeit = true;
		}*/
		

		return code;
	}
	
	
	@Override	
	public void doSend() {
		
		if (state1==MAPState1.SHOULD_REQ)
		{			
		
			RowCol nextCell = path.getNextPoint(pos());
			
			int withHelp = calculateTeamBenefitWithHelp();			
			int noHelp = projectPoints(pos(),resourcePoints());
			int benefit = withHelp - noHelp;
			log("with= "+ withHelp+"   nohelp="+noHelp);
			
			requestHelp(benefit, nextCell);
			
			state1=MAPState1.WAIT_FOR_BIDS;
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
			bid(agentToHelp, teamCostIfHelp);
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
			log(":::::::::::"+msg);
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
			
			if (waitForBidsPass < 2)
			{
				log("any bids?"+ bidMsgs.size());
				java.util.Scanner s = new Scanner(System.in);
				//s.nextLine();
				
			}
			
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
				waitForBidsPass = 2;
				state1 = MAPState1.DO_IT_MYSELF;
			}
			
			waitForBidsPass--;
		}
		
		if (state2 == MAPState2.ACCEPT_REQS)  // or could be anything
		{
		
			for (int i=0;i<requestMsgs.size();i++) // here it helps just the last one!
			{
				agentToHelpPos = new RowCol(requestMsgs.get(i).row,requestMsgs.get(i).col);
				teamCostIfHelp = calculateTeamCost(requestMsgs.get(i).benefit, agentToHelpPos);
				agentToHelp = requestMsgs.get(i).sender;
				state2 = MAPState2.SHOULD_BID;
				log("some one needs help!");				
			}
		}
		else if (state2 == MAPState2.WAIT_FOR_ACK)		
			if (ackMsg != null)
				state2 = MAPState2.SHOULD_DO_HELP;
		

	}
	
	private void findPath() {
		System.out.println("Agent " + id() +": Does not have a path, finding one ...");
		
		path = Path.getShortestPaths(pos(), goalPos(), theBoard.getBoard(), actionCosts(), 1).get(0);
		
		System.out.println("Agent " + id() +": My path will be: " + path);
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
		RowCol nextCell = path.getNextPoint(pos());
		int benefit = projectPoints(nextCell,resourcePoints());
		
		return benefit+Team.cellReward;
	}
	
	private int projectPoints(RowCol start, int points)
	{		
		RowCol i = new RowCol(start.row,start.col);
		int benefit = 0;
		
		while(!i.equals(path.getEndPoint())) 
		{
			int cost = getCellCost(i); 				
			
			if(points >= cost) 
			{
				points -= cost;
				i = new RowCol(path.getNextPoint(i).row,path.getNextPoint(i).col);
				benefit += Team.cellReward;				
			} 
			else 
			{
				break;
			}
		}
					
		
		if(i.equals(path.getEndPoint()))
			benefit += points;

		return benefit;
	}
	
	private void requestHelp(int benefit, RowCol nextCell)
	{
		
		if(benefit <= 0)
			return; // Don't ask for help
		if (!canBroadcast())
			{
				log("Don't have enough resources to broadcast :(");	
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
	
	private boolean move() {
		RowCol nextPos = path.getNextPoint(pos());
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
	
	private int calculateTeamCost(int benefit, RowCol cell)
	{				
		if(!canCalc()) 
		{			
			forfeit = true;
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
	
	/**
	 *JUST COPIED FROM OLD SIMULATIONS 
	 * @return
	 */
	public int pointsEarned() {
		
				
		int totalPoints = path.getIndexOf(pos()) * Team.cellReward;
		if(reachedThere)
			totalPoints = Team.achievementReward + resourcePoints();
		return totalPoints;
	}
}
