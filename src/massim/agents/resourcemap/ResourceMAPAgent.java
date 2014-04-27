package massim.agents.resourcemap;

import java.util.ArrayList;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.RowCol;
import massim.Team;
import massim.TeamTask;

/**
 * Resource MAP Agent Implementation
 * 
 * @author Omid Alemi
 * @version 1.0 2011/11/22
 */
public class ResourceMAPAgent extends Agent {

	private boolean dbgInf = true;
	private boolean dbgErr = true;
	
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
	
	/**
	 * The Constructor
	 * 
	 * @param id					The agent's id; to be passed
	 * 								by the team.
	 * @param comMed				The instance of the team's 
	 * 								communication medium
	 */
	public ResourceMAPAgent(int id, CommMedium comMed) {
		super(id, comMed);
	
	}
	
	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param tt						The team task setting
	 * @param subtaskAssignments		The subtask assignments for the team.
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	public void initializeRun(TeamTask tt, int[] subtaskAssignments ,
			RowCol[] currentPos,
			int[] actionCosts,int initResourcePoints, int[] actionCostsRange) {
		
		super.initializeRun(tt,subtaskAssignments,
				currentPos,actionCosts,initResourcePoints, actionCostsRange);		
		
		logInf("Initialized for a new run.");
		logInf("My initial resource points = "+resourcePoints());		
		logInf("My initial position: "+ pos());
		logInf("My goal position: " + goalPos().toString());	
		
		oldBoard = null;
	
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
		
		disturbanceLevel = calcDistrubanceLevel();
		logInf("The estimated disturbance level on the board is " + disturbanceLevel);
		
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
							int reqAmount = nextCost-resourcePoints();
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
			int l = 0; // TODO: number of helping agents
			if (canSend(l))
				setState(RMAPState.R_ACCEPT_HELP_ACT);
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
				int finalCoef = -1;
				
				for (Message msg : helpReqMsgs)
				{
					
					//TODO: Deliberation
					
					ArrayList<Integer> resOffers = new ArrayList<Integer>();
					//ArrayList<Integer> coefList = new ArrayList<Integer>();
					
					int reqAmount = msg.getIntValue("reqAmount");
					int teamBenefit = msg.getIntValue("teamBenefit");
					int qi = teamBenefit/reqAmount;
					int offer = reqAmount;
					int maxCoef = -1;
					while (offer > 1)
					{
						int teamLoss = calcTeamLoss(offer);
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
					
					int NTB = teamBenefit - resOffers.get(0);
					if (NTB > maxNetTeamBenefit)
					{
						maxNetTeamBenefit = NTB;
						finalCoef = maxCoef;
						finalResOffers = resOffers;
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
				//TODO:
				
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
				logInf("Received confirmation");
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
		
		int withHelpRewards = 
			projectRewardPoints(resourcePoints()-helpActCost, pos());
						
		int noHelpRewards =
			projectRewardPoints(resourcePoints(),pos());
						
		int withHelpRemPathLength = 
			path().getNumPoints() - 
			findFinalPos(resourcePoints()-helpActCost, pos()) -
			1;
					
		int noHelpRemPathLength = 
			path().getNumPoints() - 
			findFinalPos(resourcePoints(), pos()) -
			1;
				
		return  
			(noHelpRewards - withHelpRewards) *
			(1 + 
			(importance(noHelpRemPathLength)-importance(withHelpRemPathLength)) *
			(withHelpRemPathLength-noHelpRemPathLength)) +
			TeamTask.helpOverhead;
							
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
		
		int withHelpRewards = 
			projectRewardPoints(resourcePoints(), skipCell);  
			//Agent.cellReward; 
		/* double check cellReward
		projectRewardPoints() will include that */
		
		int noHelpRewards = 
			projectRewardPoints(resourcePoints(), pos());
		
		int withHelpRemPathLength = 
			path().getNumPoints() - 
			findFinalPos(resourcePoints(),skipCell) -
			1 ;
		
		int noHelpRemPathLength = 
			path().getNumPoints() - 
			findFinalPos(resourcePoints(),pos()) -
			1;
		
		return 
			(withHelpRewards-noHelpRewards) *
			(1+
			(importance(withHelpRemPathLength)-importance(noHelpRemPathLength)) *
			(noHelpRemPathLength-withHelpRemPathLength));
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
	 * The importance function.
	 * 
	 * Maps the remaining distance to the goal into 
	 * 
	 * Currently: imp(x) = 100/x
	 * 
	 * @param remainingLength
	 * @return
	 */
	private int importance(int remainingLength) {
		remainingLength ++; /* TODO: double check */
		if (remainingLength != 0)
			return 100/remainingLength;
		else
			return 0;
	}
	
	/**
	 * Calculates the disturbance level of the board.
	 * 
	 * This compares the current state of the board with the stored state
	 * from the previous round.
	 * 
	 * @return				The level of disturbance.
	 */
	private double calcDistrubanceLevel() {
		if (oldBoard == null)
			return 0.0;
		
		int changeCount = 0;		
		for (int i=0;i<theBoard().rows();i++)
			for (int j=0;j<theBoard().cols();j++)
				if (theBoard().getBoard()[i][j] != oldBoard[i][j])
					changeCount++;	
		
		return (double)changeCount / (theBoard().rows() * theBoard().cols());
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
	private String prepareBidMsg(int requester, ArrayList<Integer> resOffers, int coef) {
		String offers = Integer.toString(resOffers.get(0));
		for (int o : resOffers)
			offers += "," + o ;
		
		Message bidMsg = new Message(id(),requester,RMAP_BID_MSG);
		bidMsg.putTuple("amounts", offers);
		bidMsg.putTuple("coef", coef);
		return bidMsg.toString();
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
	protected void logInf(String msg) {
		if (dbgInf)
			System.out.println("[ResourceMAP Agent " + id() + "]: " + msg);
		//Denish, 2014/03/30
		super.logInf(msg);
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
