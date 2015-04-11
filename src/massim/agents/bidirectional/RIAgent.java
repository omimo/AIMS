package massim.agents.bidirectional;

import java.io.*;
import java.util.ArrayList;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.Path;
import massim.PolajnarPath2;
import massim.RowCol;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;

/**
 * Refined requester-initiated agent which allows simultaneous providing and receiving help (RIAMAP*)
 * 
 * @author Mojtaba
 * @date 2014/06
 * 
 */
public class RIAgent extends Agent {

	boolean dbgInf = false;
	boolean dbgErr = true;
	boolean dbgInf2 = false;
	
	private enum RIStates {
		S_REQUEST, R_REQUEST1, R_REQUEST2, S_BID1, S_BID2, 
		R_BID1, R_BID2, R_AWAIT, S_CONFIRM1, S_CONFIRM2, 
		S_AWAIT1, S_AWAIT2, S_AWAIT3, R_CONFIRM1, R_CONFIRM2, 
		R_CONFIRM3, S_ACT1, S_ACT2, S_ACT3, S_ACT4, R_BLOCKED, 
		R_OWN_ACT, R_HELP_ACT, R_GET_HELP, R_HELP_GET_HELP,
	}
	
	//Parameters for RIAMAP 
	public static double WLL;
	public static double EPSILON;
	public static double requestThreshold;
	public static double lowCostThreshold;
	public static int importanceVersion = 1;
	public static int impFactor = 6;
	public static boolean useTeamWellbeing = false;
	
	//Private variables
	private final static int MAP_HELP_REQ_MSG = 1;
	private final static int MAP_BID_MSG = 2;
	private final static int MAP_HELP_CONF = 3;
	//private final static int MAP_WELL_UPDATE = 4;
	
	private RIStates state;
	
	private int[][] oldBoard;
	private double disturbanceLevel;
	
	private int helperAgent;
	private int agentToHelp;
	private RowCol helpeeNextCell;
	private String bidMsg;
	private ArrayList<Message> helpReqMsgs = new ArrayList<Message>();
	private int nxtCellCost;
	
	//private double[] agentsWellbeing;
	//private double lastSentWellbeing;
	
	public int noOfBroadcasts;
	
	public boolean simHelp;  //to enable/disable simultaneous help
	
	public boolean useHelp2Character;
	
	/**
	 * The Constructor
	 * 
	 * @param id					The agent's id; to be passed
	 * 								by the team.
	 * @param comMed				The instance of the team's 
	 * 								communication medium
	 */
	public RIAgent(int id, CommMedium comMed) {
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
		logInf("My initial resource points = " + resourcePoints());		
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
			logInf("Initial Planning: Chose this path: "+ path().toString());
		}
		
		
		logInf("My current position: " + pos().toString());		
		
		//if(useTeamWellbeing)
		///	state = RIStates.S_PRE_INIT;
		//else
		
		state = RIStates.S_REQUEST;
			
		logInf("Set the initial state to " + state.toString());
		
		setRoundAction(actionType.SKIP);
		
		disturbanceLevel = calcDistrubanceLevel();
		logInf("The estimated disturbance level on the board is " + disturbanceLevel);
		
		//agentsWellbeing = new double[Team.teamSize];
		//lastSentWellbeing = Double.MIN_VALUE;
		
		noOfBroadcasts = 0;
		
		nxtCellCost = getCellCost(path().getNextPoint(pos()));
		helpReqMsgs.clear();
		helperAgent = -1;
		agentToHelp = -1;
		helpeeNextCell = null;
	}
	
	/**
	 * The agent's send states implementations.
	 * 
	 * @return					The current communication state.
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("In Send Cycle");	

		double wellbeing = wellbeing();
		logInf("My wellbeing = " + wellbeing);
		
		/*
		if (useTeamWellbeing && Math.abs((wellbeing - lastSentWellbeing)/lastSentWellbeing) >= EPSILON) {
			if (canBCast()) {
				String msg = prepareWellBeingUpdateMsg(wellbeing);
				broadcastMsg(msg);
			}
		}
		*/
		
		switch(state) {
		
		case S_REQUEST:	
			if (reachedGoal())			
				setState(RIStates.R_REQUEST1);							
			else 
			{
				RowCol nextCell = path().getNextPoint(pos());			
				boolean needHelp = checkNeedHelp(nxtCellCost, wellbeing);
				if (needHelp)
				{							
					logInf("I need help!");					
					if (canCalc())
					{
						int teamBenefit = calcTeamBenefit(nextCell);					
						if (canBCast())
						{
							logInf("I am requesting help");
							logInf("Team benefit of help would be "+teamBenefit);
							
							//for frugal agent
							String helpReqMsg = prepareHelpReqMsg(teamBenefit, nextCell, nxtCellCost);					
							broadcastMsg(helpReqMsg);
							this.numOfHelpReq++;
							setState(RIStates.R_REQUEST2);
						}
						else
							setState(RIStates.R_BLOCKED);
					}
					else
						setState(RIStates.R_BLOCKED);
				}
				else 
					setState(RIStates.R_REQUEST1);				
			}						
			break;
			
		case S_BID1:		
			if (helpReqMsgs.size() > 0 && canSend())
			{
				logInf("Received " + helpReqMsgs.size() + " help requests");
				
				int maxNetTeamBenefit = 0;				
				int maxSaving = 0;
				bidMsg = "";
				
				for (Message msg : helpReqMsgs)
				{
					RowCol reqNextCell = 
						new RowCol(msg.getIntValue("nextCellRow"), 
								   msg.getIntValue("nextCellCol"));
					
					int teamBenefit = msg.getIntValue("teamBenefit");
					int requesterAgent = msg.sender();
					int helpActCost = getCellCost(reqNextCell) + TeamTask.helpOverhead;
					int teamLoss = -1;
					int netTeamBenefit = -1;
					
					if (canCalc()) {
						teamLoss = calcTeamLoss(helpActCost);
						netTeamBenefit = teamBenefit - teamLoss;
					}
					
					logInf("For agent " + requesterAgent + ", team loss= " + teamLoss +
							", NTB= " + netTeamBenefit + ", cost= " + helpActCost);
					
					//for frugal agent
					int cost = msg.getIntValue("actionCost");
					int saving = cost - helpActCost;
					
					//Also considers unicastCost for sending a bid 
					if (netTeamBenefit > maxNetTeamBenefit &&
							helpActCost + Team.unicastCost < resourcePoints())
					{
						maxNetTeamBenefit = netTeamBenefit;
						agentToHelp = requesterAgent;
						helpeeNextCell = reqNextCell;
						
						//for frugal agent
						maxSaving = 0;
					}
					//for frugal agent
					else if(useHelp2Character && maxNetTeamBenefit == 0 && saving > maxSaving 
							&& helpActCost + Team.unicastCost < resourcePoints())
					{	
						maxSaving = saving;
						agentToHelp = requesterAgent;
						helpeeNextCell = reqNextCell;					
					}
				}
				
				if (agentToHelp != -1 && canSend())
				{					
					logInf("Decided to bid to help agent "+ agentToHelp);
					//for frugal agent
					bidMsg = prepareBidMsg(agentToHelp, maxNetTeamBenefit, maxSaving);
					logInf("Sending a bid to agent " + agentToHelp);
					sendMsg(agentToHelp, bidMsg);
					this.numOfBids++;
					setState(RIStates.R_AWAIT);
				}
				else if	(nxtCellCost <= resourcePoints() || reachedGoal())
					setState(RIStates.R_OWN_ACT);
				else
					setState(RIStates.R_BLOCKED);		
			}
			else if	(nxtCellCost <= resourcePoints() || reachedGoal())
				setState(RIStates.R_OWN_ACT);
			else
				setState(RIStates.R_BLOCKED);

			break;
		
		case S_BID2:		
			
			if (helpReqMsgs.size() > 0 && canSend() && simHelp)
			{
				logInf("Received " + helpReqMsgs.size() + " help requests");
				
				int maxNetTeamBenefit = 0;				
				int maxSaving = 0;
				bidMsg = "";
				
				for (Message msg : helpReqMsgs)
				{
					RowCol reqNextCell = 
						new RowCol(msg.getIntValue("nextCellRow"), 
								   msg.getIntValue("nextCellCol"));
					
					int teamBenefit = msg.getIntValue("teamBenefit");
					int requesterAgent = msg.sender();
					int helpActCost = getCellCost(reqNextCell) + TeamTask.helpOverhead;
					int teamLoss = -1;
					int netTeamBenefit = -1;
					
					if (canCalc()) {
						teamLoss = calcTeamLoss(helpActCost);
						netTeamBenefit = teamBenefit - teamLoss;
					}
					
					logInf("For agent " + requesterAgent + ", team loss= " + teamLoss +
							", NTB= " + netTeamBenefit + ", cost= " + helpActCost);
					
					//for frugal agent
					int cost = msg.getIntValue("actionCost");
					int saving = cost - helpActCost;
					
					//Considers unicastCosts for sending the bid and also possible responding to a bid
					if (netTeamBenefit > maxNetTeamBenefit &&
							helpActCost + 2 * Team.unicastCost < resourcePoints())
					{
						maxNetTeamBenefit = netTeamBenefit;
						agentToHelp = requesterAgent;
						helpeeNextCell = reqNextCell;
						
						//for frugal agent
						maxSaving = 0;
					}
					//for frugal agent
					else if(useHelp2Character && maxNetTeamBenefit == 0 && saving > maxSaving 
							&& helpActCost + 2 * Team.unicastCost < resourcePoints())
					{	
						maxSaving = saving;
						agentToHelp = requesterAgent;
						helpeeNextCell = reqNextCell;					
					}
				}
				
				if (agentToHelp != -1 && canSend())
				{					
					logInf("Decided to bid to help agent "+ agentToHelp);
					//for frugal agent
					bidMsg = prepareBidMsg(agentToHelp, maxNetTeamBenefit, maxSaving);
					logInf("Sending a bid to agent " + agentToHelp);
					sendMsg(agentToHelp, bidMsg);
					this.numOfBids++;
					setState(RIStates.R_BID2);
				}
				else
					setState(RIStates.R_BID1);		
			}
			else
				setState(RIStates.R_BID1);
			
			break;	
			
		case S_CONFIRM1:			
			if (canSend())
			{
				logInf("Responding to agent " + helperAgent);
				String msg = prepareConfirmMsg(helperAgent);
				sendMsg(helperAgent, msg);
				
				setState(RIStates.R_GET_HELP);	
			}
			else if	(nxtCellCost <= resourcePoints())
				setState(RIStates.R_OWN_ACT);
			else
				setState(RIStates.R_BLOCKED);
			break;
			
		case S_CONFIRM2:			
			if (canSend())
			{
				logInf("Responding to agent " + helperAgent);
				String msg = prepareConfirmMsg(helperAgent);
				sendMsg(helperAgent, msg);

				setState(RIStates.R_CONFIRM3);
			}
			else
				setState(RIStates.R_CONFIRM2);
			break;
			
		case S_AWAIT1:			
			setState(RIStates.R_CONFIRM1);
			break;
		
		case S_AWAIT2:			
			if	(nxtCellCost <= resourcePoints())
				setState(RIStates.R_OWN_ACT);
			else
				setState(RIStates.R_BLOCKED);
			break;
			
		case S_AWAIT3:			
			setState(RIStates.R_CONFIRM2);
			break;
		
		case S_ACT1:
			if	(nxtCellCost <= resourcePoints() || reachedGoal())
				setState(RIStates.R_OWN_ACT);
			else
				setState(RIStates.R_BLOCKED);
			break;
		
		case S_ACT2:
			setState(RIStates.R_HELP_ACT);
			break;
			
		case S_ACT3:
			setState(RIStates.R_GET_HELP);
			break;
			
		case S_ACT4:
			setState(RIStates.R_HELP_GET_HELP);
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
		logInf("In Receive Cycle");
		String msgStr;
		ArrayList<Message> bidMsgs = new ArrayList<Message>();
	
		switch (state) {
		
		case R_REQUEST1:			
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_HELP_REQ_MSG))
						helpReqMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			//saveWellBeingsOfAgents(helpReqMsgs);
			
			setState(RIStates.S_BID1);
			break;
			
		case R_REQUEST2:			
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_HELP_REQ_MSG))
						helpReqMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			//saveWellBeingsOfAgents(helpReqMsgs);
			
			setState(RIStates.S_BID2);
			break;
		
		case R_BID1:		
			msgStr = commMedium().receive(id());
			
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_BID_MSG))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}

			if (bidMsgs.size() == 0)
			{							
				logInf("Did not receive any bid for my request!");
				this.numOfUnSucHelpReq++;
				setState(RIStates.S_AWAIT2);
			}
			else
			{
				logInf("Received " + bidMsgs.size() + " bid(s) for my request.");

				int maxBid = 0;					
				//for frugal agent
				int maxS = 0;
				
				for (Message bid : bidMsgs)
				{
					int bidNTB = bid.getIntValue("NTB");
					//for frugal agent
					int s = bid.getIntValue("Saving");
					int offererAgent = bid.sender();
					
					if (bidNTB > maxBid)
					{
						maxBid = bidNTB;
						helperAgent = offererAgent;
						
						//for frugal agent
						maxS = 0;
					}
					//for frugal agent
					else if (useHelp2Character && maxBid == 0 && s > maxS) 
					{
						maxS = s;
						helperAgent = offererAgent;
					}
				}
				logInf("Agent " + helperAgent + " has the best bid to my request.");
				setState(RIStates.S_CONFIRM1);
			}		
			break;
			
		case R_BID2:		
			msgStr = commMedium().receive(id());
			
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(MAP_BID_MSG))
					bidMsgs.add(msg);
				 msgStr = commMedium().receive(id());
			}
			
			if (bidMsgs.size() == 0)
			{							
				logInf("Did not receive any bid for my request!");
				this.numOfUnSucHelpReq++;
				setState(RIStates.S_AWAIT3);
			}
			else
			{
				logInf("Received " + bidMsgs.size() + " bid(s) for my request.");

				int maxBid = 0;					
				//for frugal agent
				int maxS = 0;
				
				for (Message bid : bidMsgs)
				{
					int bidNTB = bid.getIntValue("NTB");
					//for frugal agent
					int s = bid.getIntValue("Saving");
					int offererAgent = bid.sender();
					
					if (bidNTB > maxBid)
					{
						maxBid = bidNTB;
						helperAgent = offererAgent;
						
						//for frugal agent
						maxS = 0;
					}
					//for frugal agent
					else if (useHelp2Character && maxBid == 0 && s > maxS) 
					{
						maxS = s;
						helperAgent = offererAgent;
					}
				}
				logInf("Agent " + helperAgent + " has the best bid to my request.");
				setState(RIStates.S_CONFIRM2);
			}		
			break;
		
		case R_AWAIT:
			setState(RIStates.S_AWAIT1);
			break;
			
		case R_CONFIRM1:
			msgStr = commMedium().receive(id());
			if (!msgStr.equals("") && 
					(new Message(msgStr)).isOfType(MAP_HELP_CONF)) 
			{
				logInf("Got confirmation for my bid to request!");
				this.numOfSucOffers++;
				setState(RIStates.S_ACT2);
			}
			else
			{
				logInf("Did not get confirmation for my bid to request");
				setState(RIStates.S_ACT1);
			}
			break;
			
		case R_CONFIRM2:
			msgStr = commMedium().receive(id());
			if (!msgStr.equals("") && 
					(new Message(msgStr)).isOfType(MAP_HELP_CONF) )				
			{
				logInf("Got confirmation for my bid to request!");
				this.numOfSucOffers++;
				setState(RIStates.S_ACT2);
			}
			else
			{
				logInf("Did not get confirmation for my bid to request");
				setState(RIStates.S_ACT1);
			}
			break;
			
		case R_CONFIRM3:
			msgStr = commMedium().receive(id());
			if (!msgStr.equals("") && 
					(new Message(msgStr)).isOfType(MAP_HELP_CONF) )				
			{
				logInf("Got confirmation for my bid to request!");
				this.numOfSucOffers++;
				setState(RIStates.S_ACT4);
			}
			else
			{
				logInf("Did not get confirmation for my bid to request");
				setState(RIStates.S_ACT3);
			}
			break;
		
		case R_BLOCKED:
			setRoundAction(actionType.FORFEIT);
			break;	
			
		case R_OWN_ACT:
			int cost = getCellCost(path().getNextPoint(pos()));			
			if (!reachedGoal() && cost <= resourcePoints())
			{
				logInf("OwnAct: Will do my own move.");
				setRoundAction(actionType.OWN);
			}
			else
			{
				//logInf("SKIP: Nothing to do at this round.");
				setRoundAction(actionType.SKIP);
				
				if(reachedGoal())
					logInf("SKIP R_OWN_ACT :: Reached goal");
				else	
				{
					logErr("SKIP R_OWN_ACT :: Could not pay the cost!!!");
					System.err.println("SKIP R_OWN_ACT :: Could not pay the cost!!!");
				}
			}
			break;
		
		case R_HELP_ACT:
			logInf("HelpAct: Will help another agent");
			setRoundAction(actionType.HELP_ANOTHER);
			break;
		
		case R_GET_HELP:
			logInf("GetHelp: Will receive help");
			setRoundAction(actionType.HAS_HELP);
			break;
			
		case R_HELP_GET_HELP:
			logInf("HelpGetHelp: Will receive help and help another agent");
			setRoundAction(actionType.HELP_GET_HELP);
			break;
		
		default:			
			logErr("Unimplemented receive state: " + state.toString());
		}
		
		//Read well being from unread messages
		//saveWellBeingsOfAgents(null);
		
		if (isInFinalState())
			returnCode = AgCommStatCode.DONE;
		
		return returnCode;
	}
	
	/**
	 * Save well being of other agents from received messages
	 * 
	 * @param wbMsgs					Received messages
	 */
	/*
	private void saveWellBeingsOfAgents(ArrayList<Message> wbMsgs) {
		
		if(useTeamWellbeing)
		{
			if(wbMsgs == null)
			{
				wbMsgs = new ArrayList<Message>();
				String msgStrWb = commMedium().receive(id());
				while (!msgStrWb.equals(""))
				{
					wbMsgs.add(new Message(msgStrWb));
					msgStrWb = commMedium().receive(id());
				}
			}
			for (Message msg : wbMsgs)
			{
				if(msg.getValue("wellbeing") != null)
				{
					double wbForAgent = msg.getDoubleValue("wellbeing");
					if(msg.sender() < agentsWellbeing.length)
					{
						agentsWellbeing[msg.sender()] = wbForAgent;
					}
				}
			}
		}
	}
	*/

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
		
		logInf("Finalizing the round : finalizeRound()");
		
		keepBoard();
		boolean succeed = act();
		
		if (reachedGoal())
		{
			logInf("Finalized the round : Reached the goal");
			return AgGameStatCode.REACHED_GOAL;
		}
		else
		{
			if (succeed) {
				logInf("Finalized the round : Not Reached the goal yet; Ready to go!");
				return AgGameStatCode.READY;
			}
			else  /*TODO: The logic here should be changed!*/
			{
				logInf("Finalized the round : Blocked!");
				return AgGameStatCode.BLOCKED;			
			}
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
		double change = (double)changeCount / (theBoard().rows() * theBoard().cols());
		//Mojtaba, 2014/04/20
		return change * (double)SimulationEngine.numOfColors/(SimulationEngine.numOfColors - 1);
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
	 * Calculates the estimated cost for the agent to move through path p.
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
	 * Calculates the agent's well being. Eq: (Res - Ecost) / ((RemLen + 1) * AvgCost)
	 * 
	 * @return						The agent's well being
	 */
	protected double wellbeing() {		
		
		Path pRemaining = remainingPath(pos());
		double eCost = estimatedCost(pRemaining);
		double avgCost = getAverage(actionCosts());
		double resPoints = resourcePoints(); 
		double resWB = (resPoints - eCost)/
				((pRemaining.getNumPoints() + 1) * avgCost);
		return resWB;
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
	 * The importance function. Eq: A / (RemLen + 1)
	 * 
	 * Maps the remaining distance to the goal into 
	 * 
	 * @param remainingLength
	 * @return
	 */
	private int importance(int remainingLength) {
		
		remainingLength ++;
		if (remainingLength != 0)
			return impFactor/remainingLength;
		else
			return 0;
	}
	
	/**
	 * Prepares a help request message and returns its String encoding.
	 * Appends well being if RIAMAP uses team well being feature.
	 * 
	 * @param teamBenefit			The team benefit to be included in
	 * 								the message.
	 * @return						The message encoded in String
	 */
	private String prepareHelpReqMsg(int teamBenefit, RowCol helpCell, int cost) {
		
		Message helpReq = new Message(id(),-1,MAP_HELP_REQ_MSG);
		helpReq.putTuple("teamBenefit", Integer.toString(teamBenefit));
		helpReq.putTuple("nextCellRow", helpCell.row);
		helpReq.putTuple("nextCellCol", helpCell.col);
		
		//Mojtaba, 2014/04/22, for frugal agent
		helpReq.putTuple("actionCost", cost); 
		
		/*
		if(useTeamWellbeing)
		{
			lastSentWellbeing = wellbeing();
			helpReq.putTuple("wellbeing", Double.toString(lastSentWellbeing));
		}
		*/
		return helpReq.toString();
	}
	
	/**
	 * Prepares a message to update other agent's belief of the agent's
	 * current well being.
	 * 
	 * @param wellbeing				The agent's current well being 								
	 * @return						The message encoded in String
	 */
	/*
	private String prepareWellBeingUpdateMsg(double wellbeing) {
		
		logInf("Broadcast wellbeing " + wellbeing);
		Message update = new Message(id(),-1, MAP_WELL_UPDATE);
		update.putTuple("wellbeing", Double.toString(wellbeing));
		noOfBroadcasts++;
		lastSentWellbeing = wellbeing;
		return update.toString();
	}
	*/
	
	/**
	 * Prepares a bid message and returns its String encoding.
	 * 
	 * @param requester				The help requester agent
	 * @param NTB					The net team benefit
	 * @return						The message encoded in String
	 */
	private String prepareBidMsg(int requester, int NTB, int costSaving) {
		Message bidMsg = new Message(id(),requester,MAP_BID_MSG);
		bidMsg.putTuple("NTB", NTB);
		
		//Mojtaba, 2014/04/22, for frugal agent
		bidMsg.putTuple("Saving", costSaving);
		
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
	 * Checks whether agent needs help using well being and thresholds.
	 * 
	 * @param cost					Cost of next action
	 * @param wellbeing				Well being of the agent
	 * @return
	 */
	private boolean checkNeedHelp(int cost, double wellbeing) {
		
		if (wellbeing < WLL) logInf2("Wellbeing = " + wellbeing);
		if ((wellbeing < WLL && cost > RIAgent.lowCostThreshold)) logInf2("Trig!");
		//TODO: logic for team wellbeing
		return (cost > resourcePoints()) ||
		   (wellbeing < WLL && cost > RIAgent.lowCostThreshold) ||
		   (cost > RIAgent.requestThreshold);
	}
	
	/**
	 * Calculates the team well being
	 * 
	 * @return			Team well being
	 */
	/*
	private double teamWellbeing() {
		
		double sum = 0;
		for (double w : agentsWellbeing)
			sum+=w;
		
		return sum/agentsWellbeing.length;
	}
	*/
	
	/**
	 * Calculates the standard deviation of the team's well being
	 * 
	 * @return 			Standard deviation of the team's well being
	 */
	/*
	private double teamWellbeingStdDev() { 
	
		double tw = teamWellbeing();
		double sum = 0;
		for (double w : agentsWellbeing)
		{
			sum+= (w-tw)*(w-tw);
		}
		
		return Math.sqrt(sum/agentsWellbeing.length);
	}
	*/
	
	/**
	 * Calculates the team loss considering spending the given amount 
	 * of resource points to help. 
	 * Checks which version of importance function to use.
	 * 
	 * @param helpActCost				The cost of help action
	 * @return							The team loss
	 */
	private int calcTeamLoss(int helpActCost) {
		
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
				
		if(importanceVersion == 1)
			return noHelpRewards - withHelpRewards;
		
		return  
			(noHelpRewards - withHelpRewards) *
			(1 + 
			(importance(noHelpRemPathLength)-importance(withHelpRemPathLength)));
		//	*(withHelpRemPathLength-noHelpRemPathLength));
	}
	
	/**
	 * Calculates the team benefit considering having another agent to the
	 * given action.
	 * Checks which version of importance function to use.
	 * 
	 * @param skipCell				The cell to skip.
	 * @return						The team benefit.
	 */
	private int calcTeamBenefit(RowCol skipCell) {
		
		decResourcePoints(Agent.calculationCost);
		int withHelpRewards = 
			projectRewardPoints(resourcePoints(), skipCell);  
			//Agent.cellReward; 
		
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
		
		if(importanceVersion == 1)
			return withHelpRewards - noHelpRewards;
		
		return 
			(withHelpRewards-noHelpRewards) *
			(1+
			(importance(withHelpRemPathLength)-importance(noHelpRemPathLength)));
	//	* (noHelpRemPathLength-withHelpRemPathLength)); 
	}
	
	/**
	 * Enables the agent to perform its own action. 
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
			logErr("Could not do my own move! :(");
			return false;
		}
	}
	
	/**
	 * Enables the agent to perform an action on behalf of another 
	 * agent (Help). 
	 * 
	 * @return						true if successful/false o.w.
	 */
	@Override
	protected boolean doHelpAnother() {
		
		boolean result;		
		int cost = getCellCost(helpeeNextCell);			
		logInf("Should help agent " + agentToHelp);
		
		if (resourcePoints() >= cost )
		{			
			logInf("Helped agent " + agentToHelp);
			decResourcePoints(cost);			
			result = true;
			//Denish, 2014/03/30
			setLastAction("Helped:" + (agentToHelp + 1));
		}
		else
		{
			logErr(""+resourcePoints());
			logErr(""+cost);
			logErr("Failed to help! :(");
			//Denish, 2014/03/30
			setLastAction("Failed Help:" + (agentToHelp + 1));
			result = false;
		}
		//helpeeNextCell = null;
		//agentToHelp = -1;
		return result;
	}
	
	/**
	 * Enables the agent to do any bookkeeping while receiving help.
	 * 
	 * @return						true if successful/false o.w.
	 */
	@Override
	protected boolean doGetHelpAction() {
		
		RowCol nextCell = path().getNextPoint(pos());
		logInf("Agent" + helperAgent + " helped me with this move!");
		//Denish, 2014/03/30
		setLastAction("Helped by:" + (helperAgent + 1));
		setPos(nextCell);
		//helperAgent = -1;
		return true;
	}
	
	/**
	 * Enables the agent to perform an action on behalf of another 
	 * agent (Help) and to do any bookkeeping while receiving help (GetHelp). 
	 * 
	 * @return						true
	 */
	@Override
	protected boolean doHelpGetHelp() {
		
		boolean result;
		int cost = getCellCost(helpeeNextCell);
		logInf("Should help agent " + agentToHelp + " and get help from agent " + helperAgent);
		
		if (resourcePoints() >= cost )
		{			
			logInf("Helped agent " + agentToHelp);
			decResourcePoints(cost);
			result = true;
			setLastAction("Helped:" + (agentToHelp + 1) + "And helped by:" + (helperAgent + 1));
		}
		else
		{
			logErr("" + resourcePoints());
			logErr("" + cost);
			logErr("Failed to help! :(");
			setLastAction("Failed Help:" + (agentToHelp + 1));
			result = false;
		}
		//helpeeNextCell = null;
		//agentToHelp = -1;
		
		RowCol nextCell = path().getNextPoint(pos());
		logInf("Agent" + helperAgent + " helped me with this move!");
		setPos(nextCell);
		//helperAgent = -1;
		
		return result;
	}
	
	/**
	 * Checks whether the agent is in a final state or not.
	 * 
	 * @return						true if it is in a final state /
	 * 								false otherwise	
	 */
	private boolean isInFinalState() {
		switch (state) {
			case R_GET_HELP:
			case R_HELP_ACT:
			case R_HELP_GET_HELP:	
			case R_OWN_ACT:
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
	private void setState(RIStates newState) {
		//logInf("In "+ state.toString() + " state");
		state = newState;
		logInf("Set the state to " + state.toString());
	}
	
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	protected void logInf(String msg) {
		if (dbgInf && id() == 3) {
			System.out.println("[RIAgent " + id() + "]: " + msg);

			//super.logInf(msg);
			
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("RILog" + ".txt", true)));
			    out.println("[RIAgent " + id() + "]: " + msg);
			    out.close();
			} catch (IOException e) {
				System.err.println("Error writing file.." + msg);
			}
		}
	}
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf2(String msg) {
		if (dbgInf2)
			System.err.println("[RIAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.out.println("[xxxxxxxxxxx][RIAgent " + id() + "]: " + msg);
		
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("RIErr" + ".txt", true)));
		    out.println("[RIAgent " + id() + "]: " + msg);
		    out.close();
		} catch (IOException e) {
			System.err.println("Error writing file.." + msg);
		}
	}
 
	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm V2.
	 * 
	 * The method uses the agents position as the starting point and the goal
	 * position as the ending point of the path.
	 * 
	 * @author Mojtaba
	 */
	
	@Override
	protected void findPath() {
		if (mySubtask() != -1)
		{
			PolajnarPath2 pp = new PolajnarPath2();
			Path shortestPath = new Path(pp.findShortestPath(
					estimBoardCosts(theBoard.getBoard()), 
					currentPositions[mySubtask()], goalPos()));
			path = new Path(shortestPath);
			
			decResourcePoints(planCost());
		}
		else 
			path = null;
	}
	
	/**
	 * Returns a two dimensional array representing the estimated cost
	 * of cells with i, j coordinates
	 * 
	 * @author Mojtaba
	 */
	private int[][] estimBoardCosts(int[][] board) {
		
		int[][] eCosts = new int[board.length][board[0].length];
		
		for (int i = 0; i < eCosts.length; i++)
			for (int j = 0; j < eCosts[0].length; j++) {
				
				eCosts[i][j] = estimCellCost(i ,j);
			}
						
		return eCosts;		
	}
	
	/**
	 * Returns estimated cost of a cell with k steps from current position
	 * 
	 * @param i				cell coordinate
	 * @param j				cell coordinate
	 * @author Mojtaba
	 */	
	private int estimCellCost(int i, int j) {
		double sigma = 1 - disturbanceLevel;
		double m = getAverage(actionCosts());
		int k = Math.abs((currentPositions[mySubtask()].row - i)) + Math.abs((currentPositions[mySubtask()].col - j));
		
		int eCost = (int) (Math.pow(sigma, k) * actionCosts[theBoard.getBoard()[i][j]]  + (1 - Math.pow(sigma, k)) * m);
		return eCost;		
	}
}
