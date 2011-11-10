package massim.agent.advancedactionmap;

import massim.Agent;
import massim.Board;
import massim.Path;
import massim.RowCol;
import massim.SimulationEngine;

/**
 * Advanced Action MAP Implementation.
 * 
 * @author Omid Alemi
 * @version 1.0 2011/11/07
 * 
 */
public class AdvActionMAP extends Agent {

	boolean dbgInf = true;
	boolean dbgErr = true;
	
	private enum AAMAPState {
		S_INIT, 
		S_SEEK_HELP, S_RESPOND_TO_REQ, 
		S_DECIDE_OWN_ACT, S_BLOCKED, S_RESPOND_BIDS, S_BIDDING,
		S_DECIDE_HELP_ACT, 
		R_IGNORE_HELP_REQ, R_GET_HELP_REQ,
		R_GET_BIDS, R_BIDDING, R_DO_OWN_ACT,
		R_BLOCKED, R_ACCEPT_HELP_ACT,R_GET_BID_CONF,
		R_DO_HELP_ACT
	}
	
	public static double WLL;
	public static double requestThreshold;
	
	private AAMAPState state;
	
	private int[][] oldBoard;
	private double disturbanceLevel;
	private boolean bidding;
	/**
	 * The Constructor
	 * 
	 * @param id					The agent's id; to be passed
	 * 								by the team.
	 */
	public AdvActionMAP(int id) {
		super(id);

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
		findPath();			
		logInf("Chose this path: "+ path().toString());		
		
		
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
		
		state = AAMAPState.S_INIT;
		logInf("Set the inital state to +"+state.toString());
		
		setRoundAction(actionType.SKIP);
		
		disturbanceLevel = calcDistrubanceLevel();
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
			RowCol nextCell = path().getNextPoint(pos());			
			int cost = getCellCost(nextCell);
			double wellbeing = wellbeing();
			boolean needHelp = (cost > resourcePoints()) ||
							   (wellbeing < WLL) ||
							   (cost > requestThreshold);
			
			if (needHelp)
			{
				int withHelpRewards = projectPoints(resourcePoints(), nextCell);
				int noHelpRewards = projectPoints(resourcePoints(), pos());
				int withHelpRemPathLength = path().getNumPoints() - findFinalPos(resourcePoints(),nextCell) + 1 ;
				int noHelpRemPathLength = path().getNumPoints() - findFinalPos(resourcePoints(),nextCell) + 1;
				double teamBenefit = (withHelpRewards-noHelpRewards) *
									(1+
										(importance(withHelpRemPathLength)-importance(noHelpRemPathLength)) *
										(noHelpRemPathLength-withHelpRemPathLength));
				/*
				 * calc team benefit f(5)
				 * subt(computation cost)
				 * broadcast help (cost)
				 */
				setState(AAMAPState.R_IGNORE_HELP_REQ);
			}
			else
			{
				setState(AAMAPState.R_GET_HELP_REQ);
			}			
			break;
		case S_RESPOND_TO_REQ:
			if(bidding)
			{
				/* send the bid (cost)
				 * 
				 */
				setState(AAMAPState.R_BIDDING);
			}
			else
				setState(AAMAPState.R_DO_OWN_ACT);
			break;
		case S_SEEK_HELP:
			setState(AAMAPState.R_GET_BIDS);
			break;
		case S_BIDDING:
			setState(AAMAPState.R_GET_BID_CONF);
			break;
		case S_DECIDE_OWN_ACT:
			setState(AAMAPState.R_DO_OWN_ACT);
			break;
		case S_DECIDE_HELP_ACT:
			setState(AAMAPState.R_DO_HELP_ACT);
			break;
		case S_RESPOND_BIDS:
			/*
			 * send a conf msg to the selected agent Aj (cost)
			 */
			setState(AAMAPState.R_ACCEPT_HELP_ACT);
			break;
		case S_BLOCKED:
			setState(AAMAPState.R_BLOCKED);
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
		AgCommStatCode returnCode = AgCommStatCode.DONE;		
		logInf("Receive Cycle");		
	
		switch (state) {
		case R_GET_HELP_REQ:
			int k = 0;
			/*
			 * receive k requests 
			 */
			bidding = false;
			
			if (k>0)
			{
				/*
				 * calc net team benefit for each using F(7)
				 * sub(computation cost)
				 * if (any of the net team benefits are positive)
				 * 		select the highest NTB
				 * 		prepare a bid
				 * 		bidding = true;
				 */			
			}
			setState(AAMAPState.S_RESPOND_TO_REQ);
			break;
		case R_IGNORE_HELP_REQ:
			setState(AAMAPState.S_SEEK_HELP);
			break;
		case R_BIDDING:
			setState(AAMAPState.S_BIDDING);
			break;
		case R_GET_BIDS:
			/*receive q bids
			 * 
			 * if (q==0)
			 *   if has resources
			 *     setState(AAMAPState.S_DECIDE_OWN_ACT);
			 *   else
			 *     setState(AAMAPState.S_BLOCKED);
			 * else
			 *   select the bid with hight NTB;
			 *   setState(AAMAPState.S_RESPOND_TO_BIDS);
			 */
			break;
		case R_BLOCKED:
			// skip the action
			break;
		case R_GET_BID_CONF:
			// if receved conf
					setState(AAMAPState.S_DECIDE_HELP_ACT);
			// else
					setState(AAMAPState.S_DECIDE_OWN_ACT);
			break;
		case R_DO_OWN_ACT:
			setRoundAction(actionType.OWN);
			break;
		case R_DO_HELP_ACT:
			setRoundAction(actionType.HELP_ANOTHER);
			break;
		case R_ACCEPT_HELP_ACT:
			setRoundAction(actionType.HAS_HELP);
			break;
		default:			
			logErr("Unimplemented receive state: " + state.toString());
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
		AgGameStatCode returnCode = AgGameStatCode.READY;
		keepBoard();
		
	
		return returnCode;
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
		if (theBoard() == null)
			return 0.0;
		
		int changeCount = 0;		
		for (int i=0;i<theBoard().rows();i++)
			for (int j=0;j<theBoard().cols();j++)
				if (theBoard().getBoard()[i][j] != oldBoard[i][j])
					changeCount++;
		
		return (double)changeCount / theBoard().rows() * theBoard().cols();
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
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				oldBoard[i][j] = theBoard().getBoard()[i][j];	
	}
	
	private double estimatedCost(Path p) {
		int l = p.getNumPoints();
		double sigma = 1 - disturbanceLevel;		
		double m = getAverage(actionCosts()); /*TODO: check this! */		
		double eCost; 
		eCost = (l - (1-Math.pow(sigma, l))/(1-sigma)) * m;		
		for (int k=0;k<l;k++)
			eCost += Math.pow(sigma, k) * getCellCost(p.getNthPoint(k));
		
		return eCost;
	}
	
	private double wellbeing () {
		RowCol nextCell = path().getNextPoint(pos());
		double eCost = estimatedCost(remainingPath(nextCell));
		if (eCost == 0)
			return resourcePoints();
		else
			return resourcePoints()/eCost;
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
	
	private int projectPoints(int remainingResourcePoints, RowCol startPos) {
		
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
	 * @param remainingLength
	 * @return
	 */
	private int importance(int remainingLength) {
		if (remainingLength != 0)
			return 10/remainingLength;
		else
			return 0;
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
	 * Changes the current state of the agents state machine.
	 * 
	 * @param newState				The new state
	 */
	private void setState(AAMAPState newState) {
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
			System.out.println("[AdvActionMAP Agent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the  debugging level
	 * is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logErr(String msg) {
		if (dbgErr)
			System.out.println("[xxxxxxxxxxx][AdvActionMAP Agent " + id() + 
							   "]: " + msg);
	}
}
