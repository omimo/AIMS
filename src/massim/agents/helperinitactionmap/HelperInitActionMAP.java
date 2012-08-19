package massim.agents.helperinitactionmap;

import java.util.ArrayList;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.Path;
import massim.RowCol;
import massim.Team;
import massim.TeamTask;

/**
 * The Helper-Initiated Action MAP Implementation.
 * 
 * @author Omid Alemi
 * @version 1.0 2011/11/27
 */
public class HelperInitActionMAP extends Agent {

	boolean dbgInf = false;
	boolean dbgErr = true;
	
	public static double WHH;
	public static double EPSILON;
	
	private enum HIAMAPState 
			{ S_INIT,
			  S_SEEKING_BID, S_RESPOND_TO_OFFERS, S_BLOCKED, S_DECIDE_OWN_ACT,
			  S_RESPOND_TO_BIDS, S_BIDDING,
			  S_DECIDE_OFFERED_ACT, 
			  R_IGNORE_HELP_OFFERS, R_BLOCKED, R_GET_HELP_OFFERS,
			  R_GET_BIDS, R_DO_OWN_ACT, R_BIDDING,
			  R_DO_HELP_ACT, R_GET_BID_CONF,
			  R_WAIT_TO_BE_HELPED	
			}
	
	private HIAMAPState state;
	
	private final static int HIMAP_HELP_OFFER = 1;
	private final static int HIMAP_BID = 2;
	private final static int HIMAP_HELP_CONF = 3;
	private final static int HIMAP_WELL_UPDATE = 4;
	
	private int[][] oldBoard;
	private double disturbanceLevel;
	
	private double[] agentsWellbeing;
	private double lastSentWellbeing;
	
	private boolean bidding;
	private int helperAgent;
	private String bidMsg;
	
	private int agentToHelp; 
	
	/*
	 * The constructor
	 */
	public HelperInitActionMAP(int id, CommMedium comMed) {
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
			int[] actionCosts,int initResourcePoints) {
		
		super.initializeRun(tt,subtaskAssignments,
				currentPos,actionCosts,initResourcePoints);		
		
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
		
		state = HIAMAPState.S_INIT;
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
				//int nextCost = getCellCost(path().getNextPoint(pos()));
				double wellbeing = wellbeing();
				//TODO: update WHH
				boolean offerHelp = wellbeing > WHH;
				
				if (offerHelp && (!canCalc() || !canBCast()))
				{
					setState(HIAMAPState.R_BLOCKED);
				}
				else if (offerHelp && canCalc() && canBCast()) 
				{
					//TODO: Select action
				}
				else if (!offerHelp && !reachedGoal())
				{
					if (Math.abs((wellbeing - lastSentWellbeing)/lastSentWellbeing) < EPSILON)
						if (canBCast()) {
							String msg = prepareWellBeingUpdateMsg(wellbeing);
							broadcastMsg(msg);						
						}											
					setState(HIAMAPState.R_GET_HELP_OFFERS);						
				}
				else
					setState(HIAMAPState.R_DO_OWN_ACT);//TODO: check this
					
			break;
		case S_RESPOND_TO_OFFERS:
			if (bidding && canSend())
			{
				sendMsg(helperAgent, bidMsg);
				setState(HIAMAPState.R_BIDDING);
			}
			else if (bidding && !canSend() )			
				setState(HIAMAPState.R_BLOCKED);		
			else
				setState(HIAMAPState.R_DO_OWN_ACT);
			break;
		case S_SEEKING_BID:
			setState(HIAMAPState.R_GET_BIDS);
			break;
		case S_BIDDING:
			setState(HIAMAPState.R_GET_BID_CONF);
			break;
		case S_DECIDE_OWN_ACT:
			setState(HIAMAPState.R_DO_OWN_ACT);
			break;
		case S_DECIDE_OFFERED_ACT:
			setState(HIAMAPState.R_WAIT_TO_BE_HELPED);
			break;
		case S_RESPOND_TO_BIDS:
			if (canSend())
			{
				logInf("Confirmed to help agent "+agentToHelp);
				String msg = prepareConfirmMsg(agentToHelp);
				sendMsg(helperAgent, msg);
				setState(HIAMAPState.R_DO_HELP_ACT); 
			}
			else
				setState(HIAMAPState.R_BLOCKED); 
			break;
		case S_BLOCKED:
			setState(HIAMAPState.R_BLOCKED);
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
		case R_GET_HELP_OFFERS:
			ArrayList<Message> offerMsgs = new ArrayList<Message>();
			
			String msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(HIMAP_HELP_OFFER))
					offerMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			bidding = false;
			
			if (offerMsgs.size() > 0)
			{
				logInf("Received "+offerMsgs.size()+" help offers");
				
				int maxNetTeamBenefit = Integer.MIN_VALUE;				
				helperAgent = -1;
				bidMsg = "";
				
				for (Message msg : offerMsgs)
				{ 
					if (canCalc())
					{
						//TODO: check if the offered actions coincides with the agent's
						//next act
						int nextCost = getCellCost(path().getNextPoint(pos()));
						int teamBenefit = calcTeamBenefit(path().getNextPoint(pos()));
						int NTB=0; //TODO
					
						if (NTB > maxNetTeamBenefit)
						{
							maxNetTeamBenefit = NTB;
							helperAgent = msg.sender();
						}	
					}
				}
				
				if (helperAgent != -1)
				{
					logInf("Prepared to bid to agent "+ helperAgent);
					bidMsg = prepareBidMsg(helperAgent,path().getNextPoint(pos()), maxNetTeamBenefit);					
					bidding = true;
					setState(HIAMAPState.S_RESPOND_TO_OFFERS);
				}
				else
					setState(HIAMAPState.S_BLOCKED);
			}
			else
			{
				int nextCost = getCellCost(path().getNextPoint(pos()));
				if(resourcePoints()>=nextCost)
					setState(HIAMAPState.S_DECIDE_OWN_ACT);
				else
					setState(HIAMAPState.S_BLOCKED);
			}
			break;
		case R_IGNORE_HELP_OFFERS:
			setState(HIAMAPState.S_SEEKING_BID);
			break;
		case R_BIDDING:
			setState(HIAMAPState.S_BIDDING);
			break;
		case R_GET_BIDS:
			ArrayList<Message> bidMsgs = new ArrayList<Message>();
					
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(HIMAP_BID))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			agentToHelp = -1;
			
			if (bidMsgs.size() == 0)
			{
				int cost = getCellCost(path().getNextPoint(pos()));
				if (cost <= resourcePoints())
					setState(HIAMAPState.S_DECIDE_OWN_ACT);
				else
					setState(HIAMAPState.S_BLOCKED);	
			}
			else
			{
				logInf("Received "+bidMsgs.size()+" bids.");
				int maxBid = Integer.MIN_VALUE;					
				for (Message bid : bidMsgs)
				{
					int bidNTB = bid.getIntValue("NTB");
					int helpeeAgent = bid.sender();
					
					if (bidNTB > maxBid)
					{
						maxBid = bidNTB;
						agentToHelp = helpeeAgent;
					}
				}
				logInf("Agent "+ agentToHelp+" won the bidding.");
				setState(HIAMAPState.S_RESPOND_TO_BIDS);
			}
			break;			
		case R_GET_BID_CONF:
			msgStr = commMedium().receive(id());
			
			if (!msgStr.equals("") && 
					(new Message(msgStr)).isOfType(HIMAP_HELP_CONF) )				
			{
				logInf("Received confirmation");
				setState(HIAMAPState.S_DECIDE_OFFERED_ACT);
			}
			else
			{ 
				logInf("Didn't received confirmation");				
				RowCol nextCell = path().getNextPoint(pos());			
				int nextCost = getCellCost(nextCell);
				if (nextCost <= resourcePoints())
					setState(HIAMAPState.S_DECIDE_OWN_ACT);
				else
					setState(HIAMAPState.S_BLOCKED);								
			}
			break;
		case R_BLOCKED:
			//TODO: ? skip the action
			// or forfeit
			setRoundAction(actionType.FORFEIT);
			break;
		case R_DO_OWN_ACT:
			//TODO: Check this
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
		case R_WAIT_TO_BE_HELPED:
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
				
		keepBoard();
		
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
	 * Checks whether the agent is in a final state or not.
	 * 
	 * @return						true if is in a final state /
	 * 								false otherwise	
	 */
	private boolean isInFinalState() {
		switch (state) { //TODO: check this for helper-init map
			case R_WAIT_TO_BE_HELPED:
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
	private String prepareBidMsg(int requester, RowCol nextCell, int NTB) {
		Message bidMsg = new Message(id(),requester,HIMAP_BID);
		bidMsg.putTuple("NTB", NTB);
		bidMsg.putTuple("nextActionRow", nextCell.row);
		bidMsg.putTuple("nextActionCol", nextCell.col);
		return bidMsg.toString();
	}
	
	/**
	 * Prepares a help confirmation message returns its String 
	 * encoding.
	 * 
	 * @param helper				The helper agent
	 * @return						The message encoded in String
	 */
	private String prepareConfirmMsg(int helpee) {
		Message confMsg = new Message(id(),helpee,HIMAP_HELP_CONF);
		return confMsg.toString();
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
	 * Keeps the current state of the board for calculating the disturbance
	 * in the next round of the game.
	 * 
	 * This copied theBoard into oldBoard. 
	 */
	private void keepBoard() {
		
		int rows = theBoard().rows();
		int cols = theBoard().cols();
		
		if (oldBoard == null) /* first round */
			oldBoard = new int[rows][cols];
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				oldBoard[i][j] = theBoard().getBoard()[i][j];	
	}
	
	/**
	 * Calculates the agent's wellbeing.
	 * 
	 * @return						The agent's wellbeing
	 */
	private double wellbeing () {		
		double eCost = estimatedCost(remainingPath(pos()));
		if (eCost == 0)
			return resourcePoints();
		else
			return (double)resourcePoints()/eCost;
	}
	
	/**
	 * Finds the remaining path from the given cell.
	 * 
	 * The path DOES NOT include the given cell and the starting cell 
	 * of the remaining path would be the next cell.
	 * 
	 * @param from					The cell the remaining path would be
	 * 								generated from.
	 * @return						The remaining path.
	 */
	private Path remainingPath(RowCol from) {
		Path rp = new Path(path());
		
		while (!rp.getStartPoint().equals(from))
			rp = rp.tail();
		
		return rp.tail();
	}
	
	/**
	 * Calculated the estimated cost for the agent to move through path p.
	 * 
	 * @param p						The agent's path
	 * @return						The estimated cost
	 */
	private double estimatedCost(Path p) {		
		int l = p.getNumPoints();
		double sigma = 1 - disturbanceLevel;
		double eCost = 0.0;		
		if (Math.abs(sigma-1) < 0.000001)
		{
			for (int k=0;k<l;k++)
				eCost += getCellCost(p.getNthPoint(k));			
		}
		else
		{
			double m = getAverage(actionCosts()); /*TODO: check this! */				 
			eCost = (l - ((1-Math.pow(sigma, l))/(1-sigma))) * m;		
			for (int k=0;k<l;k++)
				eCost += Math.pow(sigma, k) * getCellCost(p.getNthPoint(k));
		}
		return eCost;
	}
	
	/**
	 * Calculates the team well being
	 * 
	 * @return			Team well being
	 */
	private double teamWellbeing()
	{
		double sum = 0;
		for (double w : agentsWellbeing)
			sum+=w;
		
		return sum/agentsWellbeing.length;
	}
	
	/**
	 * Calculates the standard deviation of the team's well being
	 * 
	 * @return 			Standard deviation of the team's well being
	 */
	private double teamWellbeingStdDev() { 
	
		double tw = teamWellbeing();
		
		double sum = 0;
		for (double w : agentsWellbeing)
		{
			sum+= (w-tw)*(w-tw);
		}
		
		return Math.sqrt(sum/agentsWellbeing.length);
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
	 * Prepares a message to update other agent's belief of the agent's
	 * current well being.
	 * 
	 * @param wellbeing				The agent's current well being 								
	 * @return						The message encoded in String
	 */
	private String prepareWellBeingUpdateMsg(double wellbeing) {
		
		Message update = new Message(id(),-1,HIMAP_WELL_UPDATE);
		update.putTuple("wellbeing", Double.toString(wellbeing));
		
		return update.toString();
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
	 * Calculates the average of the given integer array.
	 * 
	 * @return						The average.
	 */
	private double getAverage(int[] array) {
		int sum = 0;
		for (int i=0;i<array.length;i++)
			sum+=array[i];
		return (double)sum/array.length;
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(HIAMAPState newState) {
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
			System.out.println("[Helper-Init ActionMAP Agent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.out.println("[xxxxxxxxxxx][Helper-Init ActionMAP Agent " + id() + 
							   "]: " + msg);
	}

}
