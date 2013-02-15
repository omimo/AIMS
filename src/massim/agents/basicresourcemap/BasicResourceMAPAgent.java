package massim.agents.basicresourcemap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.manipulation.Sortable;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.RowCol;
import massim.SimulationEngine;
import massim.Team;

/**
 * Resource MAP Agent Implementation
 * 
 * @author Omid Alemi
 * @version 1.0 2011/11/22
 */
public class BasicResourceMAPAgent extends Agent {

	private boolean dbgInf = true;
	private boolean dbgErr = false;
	
	private final static int RMAP_HELP_REQ_MSG = 1;
	private final static int RMAP_BID_MSG = 2;
	private final static int RMAP_HELP_CONF = 3;
	
	private final static int RESOURCE_OFFER_INTERVAL = 10; 
	
	private enum RMAPState {S_INIT,
							S_RESPOND_TO_REQ, S_SEEKING_HELP, S_BLOCKED,
							S_BIDDING, S_DECIDE_OWN_ACT, S_RESPOND_BIDS,
							S_DECIDE_HELP_ACT,
							R_GET_REQUESTS, R_IGNORE_REQUESTS, R_BLOCKED,
							R_BIDDING, R_DO_OWN_ACT, R_GET_BIDS,
							R_GET_BID_CONF, R_ACCEPT_HELP_ACT,
							R_DO_HELP_ACT}
	
	
	private RMAPState state;
							
	private int[][] oldBoard;
	private double disturbanceLevel;
	
	private boolean bidding;
	private int agentToHelp;
	private String bidMsg;
	private int resAmountToGive;
	
	Set<Offer> chosenOffers;
	
	String offerDelim = "/";
	
	
	class Offer implements Comparable<Offer>{
		int amount;
		int coef;
		int agent;
		public Offer(int am,int co, int ag) {
			amount=am;
			coef=co;
			agent=ag;
		}
		
		@Override
		public int compareTo(Offer o) {
			return o.amount-amount;
		}
	}

	class Bid implements Comparable<Bid>{
		public Bid (int a, int c) {
			agent=a;coef=c;
		}
		void offers(String[] a) {
			offers = new Offer[a.length];
			int c=0;
			for (String s: a)
				offers[c++]=new Offer(Integer.parseInt(s),coef,agent);
			Arrays.sort(offers);
		}
		Offer[] offers;
		int agent;
		int coef;
		int chosenAmount;
		
		public int compareTo(Bid o) {
			return coef-o.coef;
		}
	}
	
	
	/**
	 * The Constructor
	 * 
	 * @param id					The agent's id; to be passed
	 * 								by the team.
	 * @param comMed				The instance of the team's 
	 * 								communication medium
	 */
	public BasicResourceMAPAgent(int id, CommMedium comMed) {
		super(id, comMed);
	
	}
	

	
	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param actionCosts				The agent's action costs vector
	 */
	@Override
	public void initializeRun(int[] actionCosts) {		
		super.initializeRun(actionCosts);		
		
		logInf("Initialized for a new run.");
	}
	
	/**
	 * Initializes the agent for a new match within current run
	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	@Override
	public void initializeMatch(RowCol initialPosition, RowCol goalPosition,
			 int initResourcePoints) {
		super.initializeMatch(initialPosition, goalPosition, initResourcePoints);
		
		logInf("Initializing for a new match");
		logInf("My initial resource points = "+resourcePoints());		
		logInf("My goal position: " + goalPos().toString());
				
	}
	
	/** 
	 * Initializes the agent for a new round of the game.
	 * 
	 * 
	 * @param board						The game board
	 * @param actionCostsMatrix			The matrix containing the action costs
	 * 									for all the agents in the team (depends
	 * 									on the level of mutual awareness in the
	 * 									team)
	 */
	@Override
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		super.initializeRound(board, actionCostsMatrix);				
		
		logInf("Starting a new round ...");
		
		if (path() == null)
		{		
			findPath();			
			logInf("Chose this path: "+ path().toString());
		}
		
		logInf("My current position: " + pos().toString());		
		
		state = RMAPState.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);

		
	}
	
	/**
	 * The agent's send states implementations.
	 * 
	 * @return					The current communication state.
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Send Cycle");	
		
		switch(state) {
		case S_INIT:
				if (reachedGoal()) {
					logInf("Has reached the goal, nothing to check for myself.");
					setState(RMAPState.R_GET_REQUESTS);
				}
				else {
					RowCol nextCell = path().getNextPoint(pos());			
					int nextCost = getCellCost(nextCell);
					boolean needHelp = nextCost > resourcePoints();
					
					if (needHelp)
					{							
						logInf("Need help!");
						
						if (canCalc())
						{
							int teamBenefit = calcTeamBenefit(nextCell);
							int reqAmount = nextCost-resourcePoints()+Team.broadcastCost;
							if (canBCast())
							{
								logInf("Broadcasting help");
								logInf("Team benefit of help would be "+teamBenefit);
								logInf("The requested amount is: "+(reqAmount));
								String helpReqMsg = prepareHelpReqMsg(teamBenefit,reqAmount);					
								broadcastMsg(helpReqMsg);
								setState(RMAPState.R_IGNORE_REQUESTS);
							}
							else
								setState(RMAPState.R_BLOCKED);								
						}
						else
							setState(RMAPState.R_BLOCKED);
					}
					else
					{
						setState(RMAPState.R_GET_REQUESTS);
					}
				}
			break;
		case S_RESPOND_TO_REQ:
			RowCol nextCell = path().getNextPoint(pos());			
			int nextCost = getCellCost(nextCell);
			
			if (bidding && canSend())
			{
				sendMsg(agentToHelp, bidMsg);
				setState(RMAPState.R_BIDDING);
			}
			else if (!bidding && nextCost < resourcePoints())
			{
				setState(RMAPState.R_DO_OWN_ACT);
			}
			else
			{
				setState(RMAPState.R_BLOCKED);
			}
			break;
		case S_SEEKING_HELP:
			setState(RMAPState.R_GET_BIDS);
			break;
		case S_BIDDING:
			setState(RMAPState.R_GET_BID_CONF);
			break;
		case S_DECIDE_OWN_ACT:
			setState(RMAPState.R_DO_OWN_ACT);
			break;
		case S_DECIDE_HELP_ACT:
			setState(RMAPState.R_DO_HELP_ACT);
			break;
		case S_RESPOND_BIDS:
			if (canSend(chosenOffers.size()))
			{
				for (Offer o : chosenOffers)
				{
					logInf("Confirming the help offer of agent "+ o.agent);
					String msg = prepareConfirmMsg(o.agent,o.amount);
					sendMsg(o.agent, msg);
				}
				
				setState(RMAPState.R_ACCEPT_HELP_ACT);
			}
			else
				setState(RMAPState.R_BLOCKED);				
			break;
		case S_BLOCKED:
			setState(RMAPState.R_BLOCKED);
			break;	
		default:
			logErr("Unimplemented send state: " + state.toString());
		}
		
		return returnCode;
	}
	
	/**
	 * The agent's receive states implementations.
	 * 
	 * @return					The current communication state;
	 * 							'done' when the state is final. 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.NEEDING_TO_SEND;		
		logInf("Receive Cycle");		
	
		switch (state) {
		case R_GET_REQUESTS:
			ArrayList<Message> helpReqMsgs = new ArrayList<Message>();
			
			String msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(RMAP_HELP_REQ_MSG))
						helpReqMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			bidding = false;
			agentToHelp = -1;
			bidMsg = "";
			
			if (helpReqMsgs.size() > 0)
			{
				logInf("Received "+helpReqMsgs.size()+" help requests");
				
				int maxNetTeamBenefit = Integer.MIN_VALUE;				
				ArrayList<Integer> finalResOffers = new ArrayList<Integer>();
				//ArrayList<Integer> finalCoefs = new ArrayList<Integer>();
				int finalCoef=0;
				
				
				for (Message msg : helpReqMsgs)
				{
					ArrayList<Integer> resOffers = new ArrayList<Integer>();
					//ArrayList<Integer> coefList = new ArrayList<Integer>();
					
					int reqAmount = msg.getIntValue("reqAmount");
					int teamBenefit = msg.getIntValue("teamBenefit");
					int qi = teamBenefit/reqAmount;
					int offer = reqAmount;
					int maxCoef = -1;
					while (offer > 1 && canCalc())
					{
						int teamLoss = calcTeamLoss(offer+SimulationEngine.pList.paramI("agent.reshelpoverhead"));
						int coef = teamLoss/offer;
						if (coef < qi)
						{
							resOffers.add(offer);
							//coefList.add(coef);
							if (coef > maxCoef)
								maxCoef = coef;
							
							offer -= RESOURCE_OFFER_INTERVAL;
						}
					}
					
					if (resOffers.size()>0)
					{
						int NTB = teamBenefit - resOffers.get(0);
						if (NTB > maxNetTeamBenefit)
						{
							maxNetTeamBenefit = NTB;
							finalCoef = maxCoef;
							finalResOffers = resOffers;
							agentToHelp = msg.sender();
						}
					}					
				}
			
				if (agentToHelp != -1)
				{					
					logInf("Prepared to bid to help agent "+ agentToHelp);
					bidMsg = prepareBidMsg(agentToHelp, finalResOffers, finalCoef);					
					bidding = true;					
				}	
			}
			setState(RMAPState.S_RESPOND_TO_REQ);
			break;
		case R_IGNORE_REQUESTS:
			setState(RMAPState.S_SEEKING_HELP);
			break;
		case R_BIDDING:
			setState(RMAPState.S_BIDDING);
			break;
		case R_GET_BIDS:
			ArrayList<Message> bidMsgs = new ArrayList<Message>();
			
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(RMAP_BID_MSG))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			if (bidMsgs.size() == 0)
			{
				RowCol nextCell = path().getNextPoint(pos());			
				int nextCost = getCellCost(nextCell);
				if (nextCost <= resourcePoints())
					setState(RMAPState.S_DECIDE_OWN_ACT);
				else
					setState(RMAPState.S_BLOCKED);
			}
			else
			{		
				logInf("Received "+bidMsgs.size()+" bids");
						
				Bid[] bids = new Bid[bidMsgs.size()];
				int of=0;
				
				for (Message msg : bidMsgs)
				{
					bids[of] = new Bid(msg.sender(),msg.getIntValue("coef"));
					String[] amounts = msg.getValue("amounts").split(offerDelim);
					bids[of].offers(amounts); 				
					of++;
				}
				
				Arrays.sort(bids);
				
				RowCol nextCell = path().getNextPoint(pos());			
				int nextCost = getCellCost(nextCell);
				int reqAmount = nextCost-resourcePoints();
				
				chosenOffers = new HashSet<Offer>();
				int sum=0;
				for (int b=0;b<bids.length;b++)
				{
					int c=0;
					chosenOffers.add(bids[b].offers[c]);  // choose the next offer with smallest coef and max amount
					
					sum = 0;	// calc the sum of the chosen set of offers
					for (Offer o: chosenOffers)
						sum+=o.amount;
					
					if (sum >= reqAmount)
						break;	
				}
				logInf("Chosen offers:"+chosenOffers.size());
			
				if (sum < reqAmount) 					
					if (nextCost <= resourcePoints())
						setState(RMAPState.S_DECIDE_OWN_ACT);
					else
						setState(RMAPState.S_BLOCKED);
				
				
				setState(RMAPState.S_RESPOND_BIDS);
			}
			break;
		case R_BLOCKED:
			//TODO: ? skip the action
			// or forfeit
			setRoundAction(actionType.FORFEIT);
			break;
		case R_GET_BID_CONF:
			msgStr = commMedium().receive(id());
			
			if (!msgStr.equals("") && 
					(new Message(msgStr)).isOfType(RMAP_HELP_CONF) )				
			{
				resAmountToGive = (new Message(msgStr)).getIntValue("amount");
				logInf("Received confirmation - wil give away "+resAmountToGive);
				setState(RMAPState.S_DECIDE_HELP_ACT);
			}
			else
			{
				logInf("Didn't received confirmation");				
				RowCol nextCell = path().getNextPoint(pos());			
				int nextCost = getCellCost(nextCell);
				if (nextCost <= resourcePoints())
					setState(RMAPState.S_DECIDE_OWN_ACT);
				else
					setState(RMAPState.S_BLOCKED);							
			}
			break;
		case R_DO_OWN_ACT:
			if (!reachedGoal())
			{
				logInf("Will do my own move.");
				setRoundAction(actionType.OWN);
			}
			else
			{
				logInf("Nothing to do at this round.");
				setRoundAction(actionType.SKIP);
			}
			break;
		case R_DO_HELP_ACT:
			logInf("Will help another agent");
			setRoundAction(actionType.HELP_ANOTHER);
			break;
		case R_ACCEPT_HELP_ACT:
			logInf("Will receive help");
			setRoundAction(actionType.HAS_HELP);
			break;	
		default:			
			logErr("Unimplemented receive state: " + state.toString());
		}
		
		if (isInFinalState())
			returnCode = AgCommStatCode.DONE;
		
		return returnCode;		
	}
	
	
	
	/**
	 * Calculates the team loss considering spending the given amount 
	 * of resource points to help. 
	 * 
	 * @param helpActCost				The cost of help action
	 * @return							The team loss
	 */
	private int calcTeamLoss(int helpActCost)
	{
		decResourcePoints(Agent.calculationCost);
		
		int withHelpRewardPoints = 
			projectRewardPoints(resourcePoints()-helpActCost, pos());
		
		int noHelpRewardPoints = 
			projectRewardPoints(resourcePoints(), pos());
		
		return noHelpRewardPoints - withHelpRewardPoints;
							
	}
	
	/**
	 * Calculates the team benefit considering having another agent to the
	 * given action.
	 * 
	 * @param skipCell				The cell to skip.
	 * @return						The team benefit.
	 */
	private int calcTeamBenefit(RowCol skipCell) {
		
		decResourcePoints(Agent.calculationCost);
		
		int withHelpRewardPoints = 
			projectRewardPoints(resourcePoints(), skipCell);
		
		int noHelpRewardPoints = 
			projectRewardPoints(resourcePoints(), pos());
		
		return withHelpRewardPoints - noHelpRewardPoints;
	}
	
	/**
	 * Finds the final position of the agent assuming using the
	 * given resource points.
	 * 
	 * @param remainingResourcePoints			The amount of resource points
	 * 											the agent can use.
	 * @return									The index of the agent's position
	 * 											on the path, consumed all the resources;
	 * 											staring from 0.
	 */
	private int findFinalPos(int remainingResourcePoints, RowCol startPos) {
		
		if (path().getEndPoint().equals(startPos))
			return path().getIndexOf(startPos);
			
		RowCol iCell = path().getNextPoint(startPos);		
		int iIndex = path().getIndexOf(iCell);
		
		while (iIndex < path().getNumPoints())
		{
			int cost = getCellCost(iCell);
			if (cost <= remainingResourcePoints)
			{
				remainingResourcePoints-=cost;
				iCell=path().getNextPoint(iCell);
				iIndex++;
			}
			else
			{
				iCell = path().getNthPoint(iIndex-1);		
				break;
			}
		}		
		
		return path().getIndexOf(iCell);
	}
	
	/**
	 * Estimates the agent's reward points at the end of the game.
	 * 
	 * Estimates the agent's reward points at the end of the game assuming having
	 * the given resources points left and being the the specified position.
	 * 
	 * @param remainingResourcePoints			The assumed remaining resource
	 * 											points 
	 * @param startPos							The position which the agent
	 * 											starts to move along the path.	
	 * @return									The estimated reward points
	 */
	private int projectRewardPoints(int remainingResourcePoints, RowCol startPos) {
		
		if (path().getEndPoint().equals(startPos))
			return calcRewardPoints(remainingResourcePoints, startPos);
		
		RowCol iCell = path().getNextPoint(startPos);		
		int iIndex = path().getIndexOf(iCell);
		
		while (iIndex < path().getNumPoints())
		{							
			int cost = getCellCost(iCell);
			if (cost <= remainingResourcePoints)
			{
				remainingResourcePoints-=cost;
				iCell=path().getNextPoint(iCell);
				iIndex++;
			}
			else
			{
				iCell = path().getNthPoint(iIndex-1);		
				break;
			}
		}		
		
		return calcRewardPoints(remainingResourcePoints, iCell);
	}
	
	
	
	/**
	 * Prepares a help request message and returns its String encoding.
	 * 
	 * @param teamBenefit			The team benefit to be included in
	 * 								the message.
	 * @return						The message encoded in String
	 */
	private String prepareHelpReqMsg(int teamBenefit, int reqAmount) {				
		Message helpReq = new Message(id(),-1,RMAP_HELP_REQ_MSG);
		helpReq.putTuple("reqAmount", Integer.toString(reqAmount));
		helpReq.putTuple("teamBenefit", Integer.toString(teamBenefit));
		return helpReq.toString();
	}
	
	/**
	 * Checks whether the agent is in a final state or not.
	 * 
	 * @return						true if is in a final state /
	 * 								false otherwise	
	 */
	private boolean isInFinalState() {
		switch (state) {
			case R_ACCEPT_HELP_ACT:
			case R_DO_HELP_ACT:
			case R_DO_OWN_ACT:
			case R_BLOCKED:
				return true;				
			default:
				return false;
		}
	}	
	
	/**
	 * Prepares a bid message and returns its String encoding.
	 * 
	 * @param requester				The help requester agent
	 * @param NTB					The net team benefit
	 * @return						The message encoded in String
	 */
	private String prepareBidMsg(int requester, ArrayList<Integer> resOffers, int  coef) {
		
		String offers = Integer.toString(resOffers.get(0));
		for (int i=1;i<resOffers.size();i++)
			offers += offerDelim + resOffers.get(i);
		
		/*String coefs = Integer.toString(coefsList.get(0));
		for (int o : coefsList)
			offers += "," + o ;*/
		
		Message bidMsg = new Message(id(),requester,RMAP_BID_MSG);
		bidMsg.putTuple("amounts", offers);
		bidMsg.putTuple("coef", coef);
		return bidMsg.toString();
	}
	
	/**
	 * Prepares a help confirmation message returns its String 
	 * encoding.
	 * 
	 * @param helper				The helper agent
	 * @return						The message encoded in String
	 */
	private String prepareConfirmMsg(int helper, int amount) {
		Message confMsg = new Message(id(),helper,RMAP_HELP_CONF);
		confMsg.putTuple("amount", amount);
		return confMsg.toString();
	}
	
	/**
	 * Finalizes the round by moving the agent.
	 * 
	 * Also determines the current state of the agent which can be
	 * reached the goal, blocked, or ready for next round.  
	 * 
	 * @return 						Returns the current state 
	 */
	@Override
	protected AgGameStatCode finalizeRound() {			
		logInf("Finalizing the round ...");				

		boolean succeed = act();
		
		if (reachedGoal())
		{
			logInf("Reached the goal");
			return AgGameStatCode.REACHED_GOAL;
		}
		else
		{
			if (succeed) 
				return AgGameStatCode.READY;
			else  /*TODO: The logic here should be changed!*/
			{
				logInf("Blocked!");
				return AgGameStatCode.BLOCKED;			
			}
		}					
	}
	
	/**
	 * Enables the agent to perform its own action. 
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	@Override
	protected boolean doOwnAction() {
		RowCol nextCell = path().getNextPoint(pos());
		int cost = getCellCost(nextCell);
		logInf("Should do my own move!");
		if (resourcePoints() >= cost )
		{			
			decResourcePoints(cost);
			setPos(nextCell);
			logInf("Moved to " + pos().toString());
			return true;
		}
		else
		{
			logErr("Could not do my own move :(");
			return false;
		}
	}
	
	/**
	 * Enables the agent to perform an action on behalf of another 
	 * agent (Help). 
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	@Override
	protected boolean doHelpAnother() {
		boolean result;		
		int cost = resAmountToGive+SimulationEngine.pList.paramI("agent.reshelpoverhead");	
		logInf("Should help agent "+agentToHelp);
		if (resourcePoints() >= cost )
		{			
			logInf("Helped agent " + agentToHelp);
			decResourcePoints(cost);			
			result = true;
		}
		else
		{
			logErr("Failed to help :(");
			result = false;
		}
		resAmountToGive = 0;
		agentToHelp = -1;
		
		// Do own act - but check for res
		RowCol nextCell = path().getNextPoint(pos());
		cost = getCellCost(nextCell);
		logInf("Should do my own move!");
		if (resourcePoints() >= cost )
		{			
			decResourcePoints(cost);
			setPos(nextCell);
			logInf("Moved to " + pos().toString());
			return true;
		}
		else
		{
			logErr("Could not do my own move :(");
			return false;
		}
}
	
	/**
	 * Enables the agent do any bookkeeping while receiving help.
	 * 
	 * To be overriden by the agent if necessary.
	 * 
	 * @return						true if successful/false o.w.
	 */
	@Override
	protected boolean doGetHelpAction() {
		RowCol nextCell = path().getNextPoint(pos());
		logInf("Yaay! I have help for this move!");
		setPos(nextCell);
		
		return true;
	}
	
	/*************************************************************/
	
	/**
	 * Tells whether the agent has enough resources to send a unicast
	 * message or not
	 * 
	 * @return 					true if there are enough resources /
	 * 							false if there aren't enough resources	
	 */
	private boolean canSend() {
		return (resourcePoints() >= Team.unicastCost);	
	}
	
	/**
	 * Tells whether the agent has enough resources to send n unicast
	 * messages or not
	 * 
	 * @return 					true if there are enough resources /
	 * 							false if there aren't enough resources	
	 */
	private boolean canSend(int n) {
		return (resourcePoints() >= Team.unicastCost * n);	
	}
	
	/**
	 * Tells whether the agent has enough resources to send a broadcast
	 * message or not
	 * 
	 * @return 					true if there are enough resources /
	 * 							false if there aren't enough resources	
	 */
	private boolean canBCast() {
		return (resourcePoints() >= Team.broadcastCost);	
	}
	
	/**
	 * Indicates whether the agent has enough resources to do calculations.
	 * 
	 * @return					true if there are enough resources /
	 * 							false if there aren't enough resources
	 */
	private boolean canCalc() {
		return (resourcePoints() >= Agent.calculationCost);
	}
	
	/**
	 * Broadcast the given String encoded message.
	 * 
	 * @param msg				The String encoded message 
	 */
	private void broadcastMsg(String msg) {
		decResourcePoints(Team.broadcastCost);
		commMedium().broadcast(id(), msg);
	}
	
	/**
	 * Sends the given String encoded message to the specified
	 * receiver through the communication medium.
	 * 
	 * @param receiver			The receiver's id
	 * @param msg				The String encoded message
	 */
	private void sendMsg(int receiver, String msg) {
		decResourcePoints(Team.unicastCost);
		commMedium().send(id(), receiver, msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(RMAPState newState) {
		logInf("In "+ state.toString() +" state");
		state = newState;
		logInf("Set the state to +"+state.toString());
	}
	
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (dbgInf)
			System.out.println("[ResourceMAP Agent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.out.println("[xxxxxxxxxxx][ResourceMAP Agent " + id() + 
							   "]: " + msg);
	}

}
