package massim.agents.empathic;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.io.*;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.Path;
import massim.RowCol;
import massim.SimulationEngine;
import massim.Team;


public class EmpathicAgent extends Agent {

	private boolean dbgInf = false;
	private boolean dbgErr = true;
	
	private enum EmpaticAgentState {
		S_INIT, 
		S_SEEK_HELP, S_RESPOND_TO_REQ, 
		S_DECIDE_OWN_ACT, S_BLOCKED, S_RESPOND_BIDS, S_BIDDING,
		S_DECIDE_HELP_ACT, 
		R_IGNORE_HELP_REQ, R_GET_HELP_REQ,
		R_GET_BIDS, R_BIDDING, R_DO_OWN_ACT,
		R_BLOCKED, R_ACCEPT_HELP_ACT,R_GET_BID_CONF,
		R_DO_HELP_ACT
	}
	
	private final static int EMP_HELP_REQ_MSG = 1;
	private final static int EMP_BID_MSG = 2;
	private final static int EMP_HELP_CONF = 3;
	
	private int[][] oldBoard;
	private double disturbanceLevel;
	
	private boolean bidding;
	private int agentToHelp;
	private RowCol helpeeNextCell;
	private String bidMsg;
	private int helperAgent;
	
	//private int[] experience;
	//private int[] originalActionCost;
	
	private EmpaticAgentState state;
	
	//private double ET; // The average emotional state of the team
	public static double WTH_Threshhold;
	
	// empathy parameters' weight TODO should come from the file
	public static double salience_W;
	public static double emotState_W;
	public static double pastExp_W;
	
	
	
	public EmpathicAgent(int id, CommMedium comMed) {
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
		
		logInf("My current position: " + pos().toString());
		if (path() == null)
		{		
			findPath();			
			logInf("Chose this path: "+ path().toString());
		}
		
		state =EmpaticAgentState.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
		disturbanceLevel = calcDistrubanceLevel();
	}
	
	/**
	 * The send cycle
	 * 
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Send Cycle");		
		
		/* was just a test... 
		logInf("&&&&"+Double.toString(this.WTH_Threshhold));
		logInf("&&&&"+Double.toString(this.emotState_W));
		logInf("&&&&"+Double.toString(this.salience_W));
		logInf("&&&&"+Double.toString(this.pastExp_W));
		*/
		
		switch (state) {
		case S_INIT:
			if (reachedGoal()){
				setState(EmpaticAgentState.R_GET_HELP_REQ);
			}
			else{
				RowCol nextCell = path().getNextPoint(pos());
				int cost = getCellCost(nextCell);
				//double emotionalState = emotionalState();
				//TODO change this, we shouldn't use ET
				boolean needHelp = (cost>resourcePoints() /* || emotionalState < ET*/);  
				if (needHelp){
					double salience = salience();
					if (canBCast()){
						String helpReqMsg = prepareHelpReqMsg(salience, nextCell);
						broadcastMsg(helpReqMsg);
						setState(EmpaticAgentState.R_IGNORE_HELP_REQ);
					}
					else
						setState(EmpaticAgentState.R_BLOCKED);
				}
				else{
					setState(EmpaticAgentState.R_GET_HELP_REQ);
				}
			}
			break;
			
		case S_RESPOND_TO_REQ:
			if (bidding && canSend()){
				sendMsg(agentToHelp, bidMsg);
				setState(EmpaticAgentState.R_BIDDING);
			}
			else
				setState(EmpaticAgentState.R_DO_OWN_ACT);
			break;
			
		case S_SEEK_HELP:
			setState(EmpaticAgentState.R_GET_BIDS);
			break;
		case S_BIDDING:
			setState(EmpaticAgentState.R_GET_BID_CONF);
			break;
		case S_DECIDE_OWN_ACT:
			setState(EmpaticAgentState.R_DO_OWN_ACT);
			break;
		case S_DECIDE_HELP_ACT:
			setState(EmpaticAgentState.R_DO_HELP_ACT);
			break;
			
		case S_RESPOND_BIDS:
			if (canSend())
			{
				String msg = prepareConfirmMsg(helperAgent);
				sendMsg(helperAgent, msg);
				setState(EmpaticAgentState.R_ACCEPT_HELP_ACT);
			}
			else
				setState(EmpaticAgentState.R_BLOCKED);
			break;		
		case S_BLOCKED:
			setState(EmpaticAgentState.R_BLOCKED);
			break;
		default:
			logErr("Undefined state: " + state.toString());
		}
		
		return returnCode;
	}

	/**
	 * The receive cycle
	 * 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.NEEDING_TO_SEND;
		// TODO Why is it DONE in here different from map?
		
		logInf("Receive Cycle");		
		
		switch (state) {
		case R_GET_HELP_REQ:
			ArrayList<Message> helpReqMsgs = new ArrayList<Message>();
			String msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				Message msg = new Message(msgStr);				
				if (msg.isOfType(EMP_HELP_REQ_MSG))
						helpReqMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			bidding = false;
			agentToHelp = -1;
			
			if(helpReqMsgs.size()>0){
				
				 double maxWTH = Double.MIN_VALUE;
				 for (Message msg : helpReqMsgs){
					 
					 double ObjectSalience = msg.getDoubleValue("salience");
					 RowCol reqNextCell = new RowCol(msg.getIntValue("nextCellRow"), msg.getIntValue("nextCellCol"));
					 double wth = -1;
					 
					 int colorIndex = theBoard().getBoard()[reqNextCell.row][reqNextCell.col];
					 // TODO does wth have calculation cost?
					 wth = willingnessToHelp(ObjectSalience,colorIndex);
					 logInf("** WTH IS: "+Double.toString(wth));
					 // TODO complete the function and arguments
					 int requesterAgent = msg.sender();
					 int helpActCost = getCellCost(reqNextCell) + Agent.helpOverhead;
					 
					 //TODO replace the number with threshhold
					 if (wth>WTH_Threshhold && helpActCost<resourcePoints()){
						 logInf("## Helping!");
						 maxWTH = wth;
						 agentToHelp = requesterAgent;
						 helpeeNextCell = reqNextCell;
					 }
				 }
				 
				 if (agentToHelp!=-1){
					 bidMsg = prepareBidMsg(agentToHelp, maxWTH);
					 bidding=true;
				 }
			}
			setState(EmpaticAgentState.S_RESPOND_TO_REQ);
			break;
			
		case R_IGNORE_HELP_REQ:
			setState(EmpaticAgentState.S_SEEK_HELP);
			break;
		case R_BIDDING:
			setState(EmpaticAgentState.S_BIDDING);
			break;
		case R_GET_BIDS:
			ArrayList<Message> bidMsgs = new ArrayList<Message>();
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(EMP_BID_MSG))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			helperAgent = -1;
			
			if (bidMsgs.size() == 0)
			{							
				/* TODO: this may not be necessary as it will be checked
				 * in the R_DO_OWN_ACT
				 */
				int cost = getCellCost(path().getNextPoint(pos()));
				if (cost <= resourcePoints())
					setState(EmpaticAgentState.S_DECIDE_OWN_ACT);
				else
					setState(EmpaticAgentState.S_BLOCKED);
			}
			else{
				double maxBid = Double.MIN_VALUE;					
				for (Message bid : bidMsgs)
				{
					double bidWTH = bid.getDoubleValue("WTH");
					int offererAgent = bid.sender();
					
					if (bidWTH > maxBid)
					{
						maxBid = bidWTH;
						helperAgent = offererAgent;
					}
				}
				setState(EmpaticAgentState.S_RESPOND_BIDS);
			}
			break;
			
		case R_BLOCKED:
			//TODO: ? skip the action
			// or forfeit
			setRoundAction(actionType.FORFEIT);
			break;
		case R_GET_BID_CONF:
			msgStr = commMedium().receive(id());
			if(!msgStr.equals("") && (new Message(msgStr)).isOfType(EMP_HELP_CONF)){
				logInf("Received confirmation");
				setState(EmpaticAgentState.S_DECIDE_HELP_ACT);
			}
			else{
				logInf("Didn't received confirmation");				
				setState(EmpaticAgentState.S_DECIDE_OWN_ACT);	
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
			logErr("Undefined state: " + state.toString());
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
		
		if (pos().equals(goalPos()))
		{
			logInf("Reached the goal");
			return AgGameStatCode.REACHED_GOAL;
		}
		else
		{
			if (act())
				return AgGameStatCode.READY;
			else  /*TODO: The logic here should be changed!*/
				{
					logInf("Blocked!");
					return AgGameStatCode.BLOCKED;			
				}
		}					
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
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (dbgInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf(String msg) {
		if (dbgInf)
			System.out.println("[EmpathicAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (dbgErr).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.err.println("[xx][EmpathicAgent " + id() + 
							   "]: " + msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(EmpaticAgentState newState) {
		logInf("In "+ state.toString() +" state");
		state = newState;
		logInf("Set the state to +"+state.toString());
	}

	/**
	 * The agent performs its own action (move) here.
	 * 
	 * @return					True if succeeded
	 */
	@Override
	protected boolean doOwnAction() {
		RowCol nextCell = path().getNextPoint(pos());
		int cost = getCellCost(nextCell);
		logInf("Should do my own move!");
		if (resourcePoints() >= cost )
		{			
			decResourcePoints(cost);
			incExperience(theBoard().getBoard()[nextCell.row][nextCell.col]);
			setPos(nextCell);
			logInf("Moved to " + pos().toString());
			
			// TODO: Must decrease the cost
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
			logErr(""+resourcePoints());
			logErr(""+cost);
			logErr("Failed to help :(");
			result = false;
		}
		helpeeNextCell = null;
		agentToHelp = -1;
		return result;
	}
	
	
	/*
	 *  Calculating agent's emotional state
	 */
	private double emotionalState() {
		// TODO refine the method
		double eCost = estimatedCost(remainingPath(pos()));
		if (eCost == 0)
			return resourcePoints();
		else
			return (double)resourcePoints()/eCost;
		
	}
	
	private double salience(){
		// TODO refine the method
		return 1/emotionalState();
	}
	
	private double willingnessToHelp(double salience, int colorIndex){
		return ((salience*salience_W) + (emotionalState()*emotState_W)) + (pastExperience(colorIndex)*pastExp_W) / (salience_W + emotState_W + pastExp_W);
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
	 * Prepares a help request message and returns its String encoding.
	 * 
	 * @param teamBenefit			The team benefit to be included in
	 * 								the message.
	 * @return						The message encoded in String
	 */
	private String prepareHelpReqMsg(double salience, RowCol helpCell) {
		Message helpReq = new Message(id(),-1,EMP_HELP_REQ_MSG);
		helpReq.putTuple("salience", Double.toString(salience));
		helpReq.putTuple("nextCellRow", helpCell.row);
		helpReq.putTuple("nextCellCol", helpCell.col);
		return helpReq.toString();
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
	 * Prepares a help confirmation message returns its String 
	 * encoding.
	 * 
	 * @param helper				The helper agent
	 * @return						The message encoded in String
	 */
	private String prepareConfirmMsg(int helper) {
		Message confMsg = new Message(id(),helper,EMP_HELP_CONF);
		return confMsg.toString();
	}
	
	/**
	 * Prepares a bid message and returns its String encoding.
	 * 
	 * @param requester				The help requester agent
	 * @param NTB					The net team benefit
	 * @return						The message encoded in String
	 */
	private String prepareBidMsg(int requester, double WTH) {
		Message bidMsg = new Message(id(),requester,EMP_BID_MSG);
		bidMsg.putTuple("WTH", WTH);
		return bidMsg.toString();
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
	* Finds the remaining path from the given cell.
	*
	* The path DOES NOT include the given cell and the starting cell
	* of the remaining path would be the next cell.
	*
	* @param from The cell the remaining path would be
	* generated from.
	* @return The remaining path.
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
	* @param p The agent's path
	* @return The estimated cost
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
	* Calculates the average of the given integer array.
	*
	* @return The average.
	*/
	private double getAverage(int[] array) {
		int sum = 0;
		for (int i=0;i<array.length;i++)
		sum+=array[i];
		return (double)sum/array.length;
	}
	
	
}



