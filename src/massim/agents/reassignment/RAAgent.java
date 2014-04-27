package massim.agents.reassignment;

import java.util.ArrayList;
import java.util.Scanner;

import massim.Agent;
import massim.Board;
import massim.CommMedium;
import massim.HungarianAlgorithm;
import massim.Message;
import massim.Path;
import massim.RowCol;
import massim.Team;
import massim.TeamTask;


/**
 * The Reassignment Agent Implementation
 * 
 * @author Omid Alemi
 * @version 1.0  2012/01/19
 */
public class RAAgent extends Agent {
	
	private boolean dbgInf = false;
	private boolean dbgErr = true;
	
	private static int leaderAgent = 0;
	public static double EPSILON;

	public static double WREASSIGN;
	public static double WREASSIGNREQ;
	
	private enum RAAgentStates {
		S_RA_INIT, R_RA_GET_REQ,
		S_RA_CMD, R_RA_CMD, 
		S_RA_REPORT_ESTIMATE, R_RA_GATHER, 
		S_RA_ASSIGN, R_RA_ASSIGN,
		S_RA_CONT,R_RA_CONT,
		S_INIT, R_MOVE, R_BLOCKED, R_SKIP};
	
	private RAAgentStates state;
	
	private int[][] oldBoard;
	private double disturbanceLevel;
	
	private double[] agentsWellbeing;
	private double lastSentWellbeing;
	
	private final int RE_REASSIGN_CMD_MSG = 1;
	private final int RE_REPORT_CMD_MSG = 2;
	private final int RE_ASSIGN_MSG = 3;
	private final int RE_WELL_UPDATE_MSG = 4;
	private final int RE_ASSIGN_REQ_MSG = 5;
	
	private int[] estSubtaskCosts;
	int[] assignment; //to be used by the leader
	private boolean reassigning;
	/**
	 * The constructor
	 * 
	 * @param id			The given id of the agent
	 */
	public RAAgent(int id, CommMedium comMed) {
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
		logInf("My goal position: " + goalPos().toString());
		
		oldBoard = null;
		
		agentsWellbeing = new double[Team.teamSize];
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
	
		if (mySubtask() != -1)
		{
			logInf("My current position: " + pos().toString());
			if (path() == null)
			{		
				findPath();			
				logInf("Chose this path: "+ path().toString());
			}
		}
		
		state = RAAgentStates.S_RA_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
		disturbanceLevel = calcDistrubanceLevel();
		logInf("The estimated disturbance level on the board is " + disturbanceLevel);
		reassigning = false;
	}
	
	/**
	 * The send cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		
		reSendCycle();
		
		logInf("Send Cycle");		
		
		if (mySubtask() == -1)
		{
			logInf("No Subtask to Do! Sleeping!");
			return returnCode;
		}
		
		switch (state) {
		case S_INIT:
			if (!reachedGoal())
			{
				RowCol nextCell = path().getNextPoint(pos());
				int cost = getCellCost(nextCell);
				if (cost  <= resourcePoints())
					setState(RAAgentStates.R_MOVE);
				else
					setState(RAAgentStates.R_BLOCKED);				
			}
			else
				setState(RAAgentStates.R_SKIP);
			
			returnCode = AgCommStatCode.NEEDING_TO_REC;
			break;	
		case R_RA_ASSIGN:
		case R_RA_GATHER:
		case R_RA_CONT:
		case R_RA_CMD:
		case R_RA_GET_REQ: 
			break;
		default:
			logErr("Undefined 1state: " + state.toString());
		}
		
		return returnCode;
	}
	
	/**
	 * The receive cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode receiveCycle() {
		AgCommStatCode returnCode = AgCommStatCode.NEEDING_TO_SEND;	
		
		if (!reReceiveCycle())
			return returnCode;
		
		if (mySubtask() == -1)
		{
			logInf("No Subtask to Do! Sleeping!");
			return AgCommStatCode.DONE;
		}
		
		logInf("Receive Cycle");		
		
		switch (state) {		
		case R_MOVE:	
			logInf("Setting current action to do my own move");
			setRoundAction(actionType.OWN);			
			returnCode = AgCommStatCode.DONE;
			break;	
		case R_BLOCKED:
			setRoundAction(actionType.FORFEIT);
			returnCode = AgCommStatCode.DONE;
			break;
		case R_SKIP:
			setRoundAction(actionType.SKIP);
			returnCode = AgCommStatCode.DONE;
			break;
		case S_RA_ASSIGN:
		case S_RA_CONT:
		case S_RA_CMD:
		case S_RA_REPORT_ESTIMATE:
			break;
		default:	
			logErr("Undefined state: " + state.toString());
		}
		
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
		
		
		
		if (mySubtask() == -1)
		{
			logInf("No Subtask to Do! Sleeping!");
			return AgGameStatCode.READY;
		}
		
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
     * Handles the reassignment related states
     * 
     */
	private void reSendCycle() {
    	
		logInf("Reassignment Send Cycle");		
		
		switch (state) {
		case S_RA_INIT:
			boolean needReassign = false;
			if (mySubtask() != -1)
			{
				double twb = teamWellbeing();
				double wellbeing = wellbeing();
				logInf("My wellbeing = " + wellbeing);
				logInf ("The team wellbeing = "+ twb);
				needReassign = (twb <= RAAgent.WREASSIGN ) ;//&&
				//wellbeing <= RAAgent.WREASSIGNREQ;
			}
			
			if (needReassign)
				logInf("Need to reassign.");
		
			if (needReassign && canBCast())				
			{
				logInf("Broadcasting the reassignment request.");
				broadcastMsg(buildRAReqMSG());
			}

			if (!needReassign && mySubtask() != -1) 
			{
				double wellbeing = wellbeing(); 
				logInf("My current wellbeing = " + wellbeing);
				if (Math.abs((wellbeing - lastSentWellbeing)/lastSentWellbeing) < EPSILON)
					if (canBCast()) {
						logInf("Broadcasting my wellbeing to the team");
						broadcastMsg(prepareWellBeingUpdateMsg(wellbeing));						
					}							
			}
			setState(RAAgentStates.R_RA_GET_REQ);
			break;
		case S_RA_CMD:
			if (leaderAgent == id() && reassigning)
			{
				logInf("Broadcasting reassignment command.");
				broadcastMsg(prepareREASSIGNMsg()); // the enough costs has been
				//checked.			
			}
			
			setState(RAAgentStates.R_RA_CMD);
			break;		
		case S_RA_REPORT_ESTIMATE:
			if (leaderAgent != id() && canSend())
			{
				sendMsg(leaderAgent, prepareECostReportMsg());	
			}
			setState(RAAgentStates.R_RA_GATHER);
			break;
		case S_RA_CONT:
			setState(RAAgentStates.R_RA_CONT);
			break;
		case S_RA_ASSIGN:
			if (leaderAgent == id())
			{
				//TODO: if can send

				for(int s=0;s<Team.teamSize;s++)
				{
					logInf("Assigning subtask "+s+ " to agent "+assignment[s]);
					if (assignment[s]==id())
						mySubtask(s);
					else if (assignment[s] != -1)
						if (canSend())
							sendMsg(assignment[s], buildAssignmentMSG(assignment[s],s));
						else
							logInf("No MORE RESOURCES TO SEND ASSIGNMENT SETTEING");
						//TODO: Change above
				}
			}
			setState(RAAgentStates.R_RA_ASSIGN);
			break;
		default:
			//logErr("Undefined re send state: " + state.toString());
		}
    }
		
	/**
	 * Handles the reassignment related receive states
	 */
	private boolean reReceiveCycle() {
		
		boolean done = false;
		
		logInf("Reassignment Receive Cycle");		
		
		switch (state) {
		case R_RA_GET_REQ:
			String msgStr;
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(RE_WELL_UPDATE_MSG))
				{
					agentsWellbeing[msg.sender()] = msg.getDoubleValue("wellbeing");
					logInf("Received agent "+msg.sender()+ "'s wellbeing = " +
							agentsWellbeing[msg.sender()]);
				}
				else if (msg.isOfType(RE_ASSIGN_REQ_MSG))
				{
					logInf("Received reassignment request from agent "+msg.sender());
					agentsWellbeing[leaderAgent] = msg.getDoubleValue("wellbeing");
					logInf("Received leader's wellbeing = " + agentsWellbeing[leaderAgent]);
					
					if (leaderAgent == id() && 	canAssign())
						reassigning = true;
				}
				
				msgStr = commMedium().receive(id());	 
			}
			setState(RAAgentStates.S_RA_CMD);
			break;
		case R_RA_CONT:
			setState(RAAgentStates.S_INIT);
			break;
		case R_RA_CMD:	
			msgStr = commMedium().receive(id());
			while (!msgStr.equals(""))
			{
				logInf("Received a message: " + msgStr);
				Message msg = new Message(msgStr);				
				if (msg.isOfType(RE_WELL_UPDATE_MSG))
				{
					agentsWellbeing[msg.sender()] = msg.getDoubleValue("wellbeing");
					logInf("Received agent "+msg.sender()+ "'s wellbeing = " +
							agentsWellbeing[msg.sender()]);
				}
				else if (msg.isOfType(RE_REASSIGN_CMD_MSG))
				{
					reassigning = true;
					logInf("Received reassignment command.");
					agentsWellbeing[leaderAgent] = msg.getDoubleValue("wellbeing");
					logInf("Received leader's wellbeing = " + agentsWellbeing[leaderAgent]);
				}
				
				msgStr = commMedium().receive(id());	 
			}
			
			if (reassigning)
			{
				logInf("Now estimating the costs.");
				estSubtaskCosts = estimateSubtaskCosts();
				setState(RAAgentStates.S_RA_REPORT_ESTIMATE);	
			}
			else
				setState(RAAgentStates.S_RA_CONT);			
			break;
		case R_RA_GATHER:
			if (leaderAgent == id())
			{
				ArrayList<Message> reports = new ArrayList<Message>();
				
				msgStr = commMedium().receive(id());
				while (!msgStr.equals(""))
				{
					logInf("Received a message: " + msgStr);
					Message msg = new Message(msgStr);				
					if (msg.isOfType(RE_REPORT_CMD_MSG))
						reports.add(msg);
					 msgStr = commMedium().receive(id());
				}
				
				if (reports.size()>0)
				{	
					// get the number of incomplete subtasks
					int countIncomp = 0;
					for(int s=0;s<tt.goalPos.length;s++)
						if (!currentPositions[s].equals(tt.goalPos[s]))
							countIncomp++;
				
					int[] activeSubtasksMap = new int[countIncomp]; // a[i] = s; i:0..countIncomp, s:0..n#subtasks
					int[] activeAgentsMap = new int[reports.size()+1];
					
					// Fill the subtask / cost matrix
					double[][] subtaskCost = new double[reports.size()+1][countIncomp];
					
					int stCount = 0;
					for(int s=0;s<tt.goalPos.length;s++)
						if (!currentPositions[s].equals(tt.goalPos[s]))
							activeSubtasksMap[stCount++] = s;
					
					//Leader's
					for(int sm=0;sm<activeSubtasksMap.length;sm++)
					{
						subtaskCost[0][sm] = 
								estSubtaskCosts[activeSubtasksMap[sm]];
					}
					
					//Rest of the agents:
					int agCount = 1;
					for (Message m : reports)
					{
						activeAgentsMap[agCount] = m.sender();
						
						for(int sm=0;sm<activeSubtasksMap.length;sm++)
						{
							subtaskCost[agCount][sm] = 
									m.getIntValue(Integer.toString(activeSubtasksMap[sm]));
						}
						agCount++;
						
						agentsWellbeing[m.sender()] = m.getDoubleValue("wellbeing");
						logInf("Received agent "+m.sender()+ "'s wellbeing = " +
								agentsWellbeing[m.sender()]);
					}
					
					
					if (dbgInf)
					{
					System.out.println("Subtask/Cost Matrix:");
					for(int a=0;a<activeAgentsMap.length;a++)
						{
							System.out.print("Agent "+a);
							for(int sm=0;sm<activeSubtasksMap.length;sm++)
								System.out.print("\t"+subtaskCost[a][sm]);
							System.out.println("");
						}
					}
		
					decResourcePoints(TeamTask.assignmentOverhead);
					
					//Run the Hungarian Algorithm on this
					assignment = new int[currentPositions.length]; 
					//each item contains agent id
					for (int s=0;s<currentPositions.length;s++)
						assignment[s] = -1;
					
					boolean tr = false;
					if (subtaskCost.length > subtaskCost[0].length)
					{
						logInf("subtask/cost matrix transposed. (because rows>columns)");
						subtaskCost = HungarianAlgorithm.transpose(subtaskCost);
						tr = true;
					}
					
					int[][] aha = new int[activeSubtasksMap.length][2];
					aha = HungarianAlgorithm.hgAlgorithm(subtaskCost, "min");	
									
					for(int i=0;i<aha.length;i++)
					{
						int ag = tr ? 1:0;
						int st = tr ? 0:1;
						
						int a = aha[i][st];
						int subtask = activeSubtasksMap[a];
						int b = aha[i][ag];
					    assignment[subtask] = activeAgentsMap[b];
					}
					
					logInf("New assignment:");
					for (int s=0;s<currentPositions.length;s++)
						logInf("Subtask "+s +" -> Agent "+assignment[s]);
					
					if (dbgInf)
					{
					System.out.println("H.A Result:");
					int sum = 0;
					for (int i=0; i<aha.length; i++)
					{
						//<COMMENT> to avoid printing the elements that make up the assignment
						System.out.printf("subtaskCost(%d,%d) = %.2f\n", (aha[i][0]), (aha[i][1]),
								subtaskCost[aha[i][0]][aha[i][1]]);
						sum = sum + (int)subtaskCost[aha[i][0]][aha[i][1]];
						//</COMMENT>
					}
					}
				}
			}
			
			setState(RAAgentStates.S_RA_ASSIGN);
			break;
		case R_RA_ASSIGN:		
			if (leaderAgent != id())
			{
				msgStr = commMedium().receive(id());
			
				if (!msgStr.equals(""))
				{
					Message am = new Message(msgStr);
				
					if (am.isOfType(RE_ASSIGN_MSG))
					{
						int newSubtask = am.getIntValue("subtask");
						logInf("Received new subtask assignment: I will work on subtaks " + 
								newSubtask);
						mySubtask(newSubtask);
					}
				}
				else // The leader didn't send a new assignment
				{  //TODO: if had a subtask before, should it work on it?! or not
					mySubtask(-1);
				}
			}
			else
			{
				mySubtask(-1);
				for(int s=0;s<Team.teamSize;s++)
				{
					if (assignment[s]==id())
						mySubtask(s);
				}
			}
			findPath();
			if (mySubtask() != -1)
			{			
				logInf("Chose this path: "+ path().toString());
			}
			setState(RAAgentStates.S_INIT);
			break;
		default:	
			done = true;
		//	logErr("Undefined re receive state: " + state.toString());
		}
		
		return done;
	}

	
	/**
	 * Prepares a reassignment command message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String prepareREASSIGNMsg() {
		
		Message cmd = new Message(id(),-1,RE_REASSIGN_CMD_MSG);
		
		Double w = wellbeing();
		cmd.putTuple("wellbeing", Double.toString(w));
		lastSentWellbeing = w;
		
		return cmd.toString();
	}
	
	/**
	 * Prepares a reassignment command message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String buildRAReqMSG() {
		
		Message req = new Message(id(),-1,RE_ASSIGN_REQ_MSG);
		
		Double w = wellbeing();
		req.putTuple("wellbeing", Double.toString(w));
		lastSentWellbeing = w;
		
		return req.toString();
	}
	
	/**
	 * Prepares a report message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String prepareECostReportMsg() {
		
		Message report = new Message(id(),-1,RE_REPORT_CMD_MSG);

		for(int s=0;s<Team.teamSize;s++)
		{
			if (!currentPositions[s].equals(tt.goalPos[s])) //subtask is not complete
				report.putTuple(Integer.toString(s), estSubtaskCosts[s]);
		}
		
		Double w = wellbeing();
		report.putTuple("wellbeing", Double.toString(w));
		lastSentWellbeing = w;
		
		return report.toString();
	}
	
	/**
	 * Prepares an assignment command message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String buildAssignmentMSG(int ag,int subtaskAssignment) {
		
		Message report = new Message(id(),ag, RE_ASSIGN_MSG);

		report.putTuple("subtask", subtaskAssignment);
	
		return report.toString();
	}
	
	/**
	 * Prepares a message to update other agent's belief of the agent's
	 * current well being.
	 * 
	 * @param wellbeing				The agent's current well being 								
	 * @return						The message encoded in String
	 */
	private String prepareWellBeingUpdateMsg(double wellbeing) {
		
		Message update = new Message(id(),-1,RE_WELL_UPDATE_MSG);
		
		Double w = wellbeing();
		update.putTuple("wellbeing", Double.toString(w));
		lastSentWellbeing = w;
		
		return update.toString();
	}
	
	private int[] estimateSubtaskCosts() {
		int[] estimates = new int[Team.teamSize];
		
		for(int s=0;s<Team.teamSize;s++)
		{
			Path subtaskPath = findPath(currentPositions[s], tt.goalPos[s]);
			estimates[s] = (int)estimatedCost(subtaskPath);
		}
		
		return estimates;
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
		
		try {
		while (!rp.getStartPoint().equals(from))
			rp = rp.tail();
		
		}
		catch (Exception e) 
		{ 
			System.err.println("my sub task >>>>>>>>>> " + mySubtask());
			e.printStackTrace();
		}
		return rp.tail();
	}
	
	/**
	 * Calculates the team well being
	 * 
	 * @return			Team well being
	 */
	private double teamWellbeing()
	{
		double sum = 0;
		agentsWellbeing[id()] = wellbeing();
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
	
/*******************************************************************/
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
	 * Tells whether the agent has enough resources to send a unicast
	 * message or not
	 * 
	 * @return 					true if there are enough resources /
	 * 							false if there aren't enough resources	
	 */
	private boolean canSend(int times) {
		return (resourcePoints() >= Team.unicastCost * times);	
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
	 * Indicates whether the agent has enough resources to do assignment.
	 * 
	 * @return					true if there are enough resources /
	 * 							false if there aren't enough resources
	 */
	private boolean canAssign() {
		return (resourcePoints() >= TeamTask.assignmentOverhead
				+Team.broadcastCost+
				(Team.unicastCost*(Team.teamSize-1)));
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
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	protected void logInf(String msg) {
		if (dbgInf)
			System.out.println("[RAAgent " + id() + "]: " + msg);
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
			System.err.println("[xx][RAAgent " + id() + 
							   "]: " + msg);
	}
	
	/**
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(RAAgentStates newState) {
		logInf("In "+ state.toString() +" state");
		state = newState;
		logInf("Set the state to +"+state.toString());
	}

	/**
	 * Agent's move action.
	 * 
	 * Moves the agent to the next position if possible
	 * 
	 * TODO: Needs to be extended to perform help.
	 * 
	 * @return
	 */
	private boolean move() {
		
		RowCol nextCell = path().getNextPoint(pos());
		if (pos().equals(nextCell))
		{
			logErr("Can not move from "+pos() +" to itself!"); 			
			return false;
		}
		else
		{
			logInf("Moved from "+pos() +" to "+ nextCell);
			
			int cost = getCellCost(nextCell);
		
			decResourcePoints(cost);
			setPos(nextCell);	
			
			if (reachedGoal())
			{
			//	mySubtask(-1);
			//	logInf("Just reached the goal. Setting my subtask to -1");
				logInf("Just reached the goal.");
			}
			return true;
		}
	}
	
	/**
	 * The agent performs its own action (move) here.
	 * 
	 * @return					The same as what move() returns.
	 */
	@Override
	protected boolean doOwnAction() {
		return move();		
	}
	
}
