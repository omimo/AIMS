package massim.agents.basicactionmap;

import java.util.ArrayList;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.RowCol;
import massim.Team;

/**
 * Basic action MAP agent implementation.
 * 
 * @author Omid Alemi
 * @version 2.0		12/11/2011
 *
 */
public class BasicActionMAPAgent extends Agent {

	boolean dbgInf = false;
	boolean dbgErr = true;
	
	private enum BAMAPState {
		S_INIT, 
		S_SEEK_HELP, S_RESPOND_TO_REQ, 
		S_DECIDE_OWN_ACT, S_BLOCKED, S_RESPOND_BIDS, S_BIDDING,
		S_DECIDE_HELP_ACT, 
		R_IGNORE_HELP_REQ, R_GET_HELP_REQ,
		R_GET_BIDS, R_BIDDING, R_DO_OWN_ACT,
		R_BLOCKED, R_ACCEPT_HELP_ACT,R_GET_BID_CONF,
		R_DO_HELP_ACT
	}
	
	public static int requestThreshold;
	
	private final static int MAP_HELP_REQ_MSG = 1;
	private final static int MAP_BID_MSG = 2;
	private final static int MAP_HELP_CONF = 3;
	
	private BAMAPState state;
	
	private boolean bidding;
	private int agentToHelp;
	private RowCol helpeeNextCell;
	private String bidMsg;
	
	private int helperAgent;
	
	/**
	 * The constructor.
	 * 
	 * @param id					The agent's id; to be passed
	 * 								by the team.
	 * @param comMed				The instance of the team's 
	 * 								communication medium
	 */
	public BasicActionMAPAgent(int id, CommMedium comMed) {
		super(id, comMed);

	}
	
	/**
	 * Initializes the agent for a new run.
	 * 
	 * Called by Team.initializeRun()

	 * 
	 * @param initialPosition			The initial position of this agent
	 * @param goalPosition				The goal position for this agent
	 * @param actionCosts				The agent's action costs vector
	 * @param initResourcePoints		The initial resource points given
	 * 									to the agent by its team.
	 */
	@Override
	public void initializeRun(RowCol initialPosition, RowCol goalPosition,
			int[] actionCosts, int initResourcePoints) {
		
		super.initializeRun(initialPosition, goalPosition, 
				actionCosts,initResourcePoints);					
		
		logInf("Initialized for a new run.");
		logInf("My initial resource points = "+resourcePoints());		
		logInf("My initial position: "+ pos());
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
		
		state = BAMAPState.S_INIT;
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
			if (reachedGoal())
			{
				setState(BAMAPState.R_GET_HELP_REQ);
			}
			else 
			{
				RowCol nextCell = path().getNextPoint(pos());			
				int cost = getCellCost(nextCell);
				boolean needHelp = 
					(cost > BasicActionMAPAgent.requestThreshold);// ||
					//(cost > resourcePoints());
				if (needHelp)
				{
					logInf("Need help!");
					
					if (canCalc())
					{
						int teamBenefit = calcTeamBenefit(nextCell);
						
						if (canBCast())
						{
							logInf("Broadcasting help");
							logInf("Team benefit of help would be "+teamBenefit);
							String helpReqMsg = prepareHelpReqMsg(teamBenefit,nextCell);					
							broadcastMsg(helpReqMsg);
							this.numOfHelpReq++;
							setState(BAMAPState.R_IGNORE_HELP_REQ);
						}
						else
							setState(BAMAPState.R_BLOCKED);	
					}
					else
						setState(BAMAPState.R_BLOCKED);
				}
				else
				{
					setState(BAMAPState.R_GET_HELP_REQ);
				}
			}
			break;
		case S_RESPOND_TO_REQ:
			if(bidding && canSend())
			{
				logInf("Sending a bid to agent"+agentToHelp);
				sendMsg(agentToHelp, bidMsg);
				this.numOfBids++;
				setState(BAMAPState.R_BIDDING);
			}
			else
			{
				int cost = getCellCost(path().getNextPoint(pos()));
				if (cost <= resourcePoints())
					setState(BAMAPState.R_DO_OWN_ACT);
				else
					setState(BAMAPState.R_BLOCKED);
			}				
			break;
		case S_SEEK_HELP:
			setState(BAMAPState.R_GET_BIDS);
			break;
		case S_BIDDING:
			setState(BAMAPState.R_GET_BID_CONF);
			break;
		case S_DECIDE_OWN_ACT:
			int cost = getCellCost(path().getNextPoint(pos()));
			if (cost <= resourcePoints())
				setState(BAMAPState.R_DO_OWN_ACT);
			else
				setState(BAMAPState.R_BLOCKED);			
			break;
		case S_DECIDE_HELP_ACT:			
			setState(BAMAPState.R_DO_HELP_ACT);
			break;
		case S_RESPOND_BIDS:
			if (canSend())
			{
				logInf("Confirming the help offer of agent "+ helperAgent);
				String msg = prepareConfirmMsg(helperAgent);
				sendMsg(helperAgent, msg);
				setState(BAMAPState.R_ACCEPT_HELP_ACT); 
			}
			else
				setState(BAMAPState.R_BLOCKED); 
			/* should be checked if can not send ... */
			break;
		case S_BLOCKED:
			setState(BAMAPState.R_BLOCKED);
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
		case R_GET_HELP_REQ:
			ArrayList<Message> helpReqMsgs = new ArrayList<Message>();
			
			String msgStr = commMedium().receive(id());
			while(!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_HELP_REQ_MSG))
						helpReqMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			bidding = false;
			agentToHelp = -1;
			helpeeNextCell = null;
			
			if (helpReqMsgs.size() > 0)
			{
				logInf("Received "+helpReqMsgs.size()+" help requests");				
				
				int maxNetTeamBenefit = Integer.MIN_VALUE;		
				
				for (Message msg : helpReqMsgs)
				{
					RowCol reqHelpCell = 
						new RowCol(msg.getIntValue("nextCellRow"), 
								   msg.getIntValue("nextCellCol"));
					
					int teamBenefit = msg.getIntValue("teamBenefit");
					int requesterAgent = msg.sender();
					int helpActCost = getCellCost(reqHelpCell) + Agent.helpOverhead;
					int teamLoss = -1;
					int netTeamBenefit = -1;
					
					if (canCalc())
					{
						teamLoss = calcTeamLoss(helpActCost);
						netTeamBenefit = teamBenefit - teamLoss;
					}					
					
					logInf("For agent "+ requesterAgent+", team loss= "+teamLoss+
							", NTB= "+netTeamBenefit);
					
					if (netTeamBenefit > 0 && 
							netTeamBenefit > maxNetTeamBenefit &&
							helpActCost < resourcePoints())
					{
						maxNetTeamBenefit = netTeamBenefit;
						agentToHelp = requesterAgent;
						helpeeNextCell = reqHelpCell;
					}
				}
				
				if (agentToHelp != -1)
				{					
					logInf("Prepared to bid to help agent "+ agentToHelp);
					bidMsg = prepareBidMsg(agentToHelp, maxNetTeamBenefit);					
					bidding = true;					
				}	
			}
			setState(BAMAPState.S_RESPOND_TO_REQ);
			break;
		case R_IGNORE_HELP_REQ:
			setState(BAMAPState.S_SEEK_HELP);
			break;
		case R_BIDDING:
			setState(BAMAPState.S_BIDDING);
			break;
		case R_GET_BIDS:
			ArrayList<Message> bidMsgs = new ArrayList<Message>();
			
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_BID_MSG))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			helperAgent = -1;
			
			if (bidMsgs.size() == 0)
			{							
				this.numOfUnSucHelpReq++;
				int cost = getCellCost(path().getNextPoint(pos()));
				if (cost <= resourcePoints())
					setState(BAMAPState.S_DECIDE_OWN_ACT);
				else
					setState(BAMAPState.S_BLOCKED);
			}
			else
			{
				logInf("Received "+bidMsgs.size()+" bids.");
				int maxBid = Integer.MIN_VALUE;					
				for (Message bid : bidMsgs)
				{
					int bidNTB = bid.getIntValue("NTB");
					int offererAgent = bid.sender();
					
					if (bidNTB > maxBid)
					{
						maxBid = bidNTB;
						helperAgent = offererAgent;
					}
				}
				logInf("Agent "+ helperAgent+" won the bidding.");
				setState(BAMAPState.S_RESPOND_BIDS);
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
					(new Message(msgStr)).isOfType(MAP_HELP_CONF) )				
			{
				logInf("Received confirmation");
				this.numOfSucOffers++;
				setState(BAMAPState.S_DECIDE_HELP_ACT);
			}
			else
			{
				logInf("Didn't received confirmation");				
				RowCol nextCell = path().getNextPoint(pos());			
				int nextCost = getCellCost(nextCell);
				if (nextCost <= resourcePoints())
					setState(BAMAPState.S_DECIDE_OWN_ACT);
				else
					setState(BAMAPState.S_BLOCKED);								
			}
			break;
		case R_DO_OWN_ACT:
			int cost = getCellCost(path().getNextPoint(pos()));			
			if (!reachedGoal() && cost <= resourcePoints())
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
	 * Calculates the team benefit by skipping the given cell (having help).
	 * 
	 * @param skipCell				The cell to be skipped
	 * @return						The team benefit
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
	private String prepareHelpReqMsg(int teamBenefit, RowCol helpCell) {
		
		Message helpReq = new Message(id(),-1,MAP_HELP_REQ_MSG);
		helpReq.putTuple("teamBenefit", Integer.toString(teamBenefit));
		helpReq.putTuple("nextCellRow", helpCell.row);
		helpReq.putTuple("nextCellCol", helpCell.col);
		return helpReq.toString();
	}
	
	/**
	 * Prepares a bid message and returns its String encoding.
	 * 
	 * @param requester				The help requester agent
	 * @param NTB					The net team benefit
	 * @return						The message encoded in String
	 */
	private String prepareBidMsg(int requester, int NTB) {
		Message bidMsg = new Message(id(),requester,MAP_BID_MSG);
		bidMsg.putTuple("NTB", NTB);
		return bidMsg.toString();
	}
	
	/**
	 * Prepares a help confirmation message returns its String 
	 * encoding.
	 * 
	 * @param helper				The helper agent
	 * @return						The message encoded in String
	 */
	private String prepareConfirmMsg(int helper) {
		Message confMsg = new Message(id(),helper,MAP_HELP_CONF);
		return confMsg.toString();
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
		int cost = getCellCost(helpeeNextCell);			
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
		helpeeNextCell = null;
		agentToHelp = -1;
		
		return result;
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
		logInf("Yaay! Agent"+ helperAgent+" is helping me with this move!");
		setPos(nextCell);
		
		helperAgent = -1;
		return true;
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
	
	/*******************************************************************/
	
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
	 * Broadcasts the given String encoded message through the communication 
	 * medium.
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
	private void setState(BAMAPState newState) {
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
			System.out.println("[BasicActionMAP Agent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.out.println("[xxxxxxxxxxx][BasicActionMAP Agent " + id() + 
							   "]: " + msg);
	}
}
