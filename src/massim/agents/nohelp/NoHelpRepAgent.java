package massim.agents.nohelp;

import java.util.ArrayList;

import massim.Board;
import massim.CommMedium;
import massim.Message;
import massim.Path;
import massim.RowCol;
import massim.Team;
import massim.TeamTask;

public class NoHelpRepAgent extends NoHelpAgent {

	boolean dbgInf = false;
	boolean dbgInf2 = false;
	boolean dbgErr = true;
	
	public static double WREP; 
	
	
	//Denish, 2014/04/26, swap
	public static int swapRequestThreshold;
	public static int swapDeliberationThreshold;
	public static int swapBidThreshold;
	public static int swapResourceThreshold = 50;
	public boolean useSwapProtocol;
	
	private final static int MAP_SWAP_REQ_MSG = 5;
	private final static int MAP_SWAP_BID_MSG = 6;
	private final static int MAP_SWAP_COMMIT_MSG = 7;
	private final static int MAP_SWAP_ABORT_MSG = 8;
	private boolean swapPriority;
	private boolean swapRequest;
	private int topRequester;
	private boolean swapCommit;
	private String swapMsg;
	private int swapAgent;
	private int swapAgentRes;
	private boolean replanned;
	private double tauFitness;
	private boolean bidding;
	private String bidMsg;
	private int roundCount = 0, repRound = 0;
	
	public NoHelpRepAgent(int id, CommMedium comMed) {
		
		super(id, comMed);
	}
	
	@Override
	public void initializeRun(TeamTask tt, int[] subtaskAssignments,
			RowCol[] currentPos, int[] actionCosts, int initResourcePoints, int[] actionCostsRange) {
		super.initializeRun(tt, subtaskAssignments, currentPos, actionCosts,
				initResourcePoints, actionCostsRange);
		
		//Denish, 2014/04/26, swap
		replanned = false;
		roundCount = 0;
		repRound = 0;
	}
	
	@Override
	protected void initializeRound(Board board, int[][] actionCostsMatrix) {
		super.initializeRound(board, actionCostsMatrix);
		
		//Denish, 2014/04/26, swap
		swapPriority = useSwapProtocol;
		roundCount++;
	}
	
	/**
	 * The send cycle method.
	 * 
	 */
	@Override
	protected AgCommStatCode sendCycle() {
		
		AgCommStatCode returnCode = AgCommStatCode.DONE;
		logInf("Replan Send Cycle");	
		double wellbeing = wellbeing();
		logInf("My wellbeing = " + wellbeing);
		
		if (wellbeing < WREP && canReplan() 
				&& (roundCount > (repRound + 1) || (roundCount != repRound && disturbanceLevel >= 0.5))) {
			//Denish, 2014/04/26, for swap, created replanning method
			replan();
			
			wellbeing = wellbeing();
			logInf("My wellbeing = " + wellbeing);
		}
		
		boolean subState;
		do {
			subState = false;
			switch(state) {
			case S_INIT:
				if(useSwapProtocol) {
					if(swapPriority) {
						setState(NoHelpAgentStates.S_INIT_SW);
						logInf("Moving to subState: " + state);
						subState = true;
					} else {
						setState(NoHelpAgentStates.S_INIT);
					}
					swapRequest = false;
					swapMsg = null; 
				} else {
					setState(NoHelpAgentStates.S_INIT);
				}
				break;
			case S_INIT_SW:
				double estimatedCost = 0;
				if(reachedGoal()) {
					tauFitness = 0;
				} else {
					estimatedCost = estimatedCost(remainingPath(pos()));
					tauFitness = estimatedCost - getAverage(actionCostsRange()) * (path().getNumPoints() - 1);
				}
				if(tauFitness >= swapRequestThreshold && resourcePoints() >= swapResourceThreshold && canReplan()) {
					if(!reachedGoal()) {
						// replan();
						estimatedCost = estimatedCost(remainingPath(pos()));
						tauFitness = estimatedCost - getAverage(actionCostsRange()) * (path().getNumPoints() - 1);
					}
					if(tauFitness >= swapRequestThreshold) {
						if (canBCast()) {
							logInf2("Broadcasting swap request");
							logInf2("Tau Value for request is " + tauFitness + ", estimatedcost = " + estimatedCost + ", length = " + (path().getNumPoints() - 1));
							
							swapRequest = true;
							swapMsg = prepareSwapReqMsg(mySubtask(), estimatedCost, tauFitness);
							broadcastMsg(swapMsg);
							numOfSwapReq++;
							if(swapPriority) {
								setState(NoHelpAgentStates.SW_R_GET_REQ);
								break;
							}
						}
					} else {
						logInf2("Did not send swap request. Tau Value = " + tauFitness + ", Resources = " + resourcePoints() + ", length = " + (path.getNumPoints() - 1));
					}
				}
				setState(NoHelpAgentStates.R_INIT);
				break;
			case SW_S_AWAIT_RESPONSE:
				setState(NoHelpAgentStates.SW_R_GET_BIDS);
				break;
			case SW_S_RESPOND_TO_REQ:
				if(bidding && canSend()) {
					sendMsg(topRequester, bidMsg);
					this.numOfSwapBid++;
				}
				setState(NoHelpAgentStates.SW_R_AWAIT_OUTCOME);
				break;
			case SW_S_AWAIT_OUTCOME:
				setState(NoHelpAgentStates.SW_R_COMPLETE_SWAP);
				break;
			case SW_S_ANNOUNCE:
				if(canBCast()) {
					if(swapMsg != null) {
						broadcastMsg(swapMsg);
					}
					setState(NoHelpAgentStates.SW_R_COMPLETE_SWAP);
				} else {
					setState(NoHelpAgentStates.S_INIT);
				}
				break;
			}
		} while(subState);
		
		if(state == NoHelpAgentStates.S_INIT) {
			returnCode = super.sendCycle();
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
		logInf("Replan Receive Cycle");
		ArrayList<Message> swapReqMsgs = new ArrayList<Message>();
		
		boolean subState;
		do {
			subState = false;
			switch (state) {
			//Denish, 2014/04/26, swap
			case R_INIT:
				String msgStr = commMedium().receive(id());
				while (!msgStr.equals(""))
				{
					logInf("Received a message: " + msgStr);
					Message msg = new Message(msgStr);				
					if(msg.isOfType(MAP_SWAP_REQ_MSG)) {
						swapReqMsgs.add(msg);
					}
					 msgStr = commMedium().receive(id());
				}
				if(swapReqMsgs.size() > 0) {
					setState(NoHelpAgentStates.SW_R_GET_REQ);
					subState = true;
				} else {
					swapPriority = false;
					setState(NoHelpAgentStates.S_INIT);
				}
				break;
			case SW_R_GET_REQ:
				topRequester = -1;
				int reqSubTask = -1;
				double reqSubTaskECostForReq = 0;
				double maxTauValue = Double.MIN_VALUE;
				bidding = false;

				if(swapReqMsgs.size() == 0) {
					msgStr = commMedium().receive(id());
					while (!msgStr.equals("")) {
						logInf("Received a message: " + msgStr);
						Message msg = new Message(msgStr);				
						if(msg.isOfType(MAP_SWAP_REQ_MSG)) {
							swapReqMsgs.add(msg);
						}
						 msgStr = commMedium().receive(id());
					}
				}
				if(swapRequest && swapMsg != null) {
					swapReqMsgs.add(new Message(swapMsg));
				}
				for (Message msg : swapReqMsgs) {
					if(msg.getDoubleValue("tauValue") > maxTauValue ||
							(msg.getDoubleValue("tauValue") == maxTauValue && msg.sender() < topRequester)) {
						maxTauValue = msg.getDoubleValue("tauValue");
						reqSubTask = msg.getIntValue("subTask");
						reqSubTaskECostForReq = msg.getDoubleValue("eCost");
						topRequester = msg.sender();
					}
				}
				logInf2("Top requester is = " + topRequester + " with subtask = " + reqSubTask + " and subTaskCost = " + reqSubTaskECostForReq);
				if(topRequester == id()) {
					setState(NoHelpAgentStates.SW_S_AWAIT_RESPONSE);
				} else {
					double estimatedCost = 0;
					if(reachedGoal()) {
						tauFitness = 0;
					} else {
						estimatedCost = estimatedCost(remainingPath(pos())); 
						tauFitness = estimatedCost - getAverage(actionCostsRange()) * (path().getNumPoints() - 1);
					}
					if(reqSubTask > -1 && tauFitness >= swapDeliberationThreshold && resourcePoints() >= swapResourceThreshold) {
						if(!replanned && !reachedGoal() && canReplan()) {
							logInf2("Replanning before bidding");
							replan();
							estimatedCost = estimatedCost(remainingPath(pos()));
							tauFitness = estimatedCost - getAverage(actionCostsRange()) * (path().getNumPoints() - 1);
						}
						logInf2("Tau after replanning = " + tauFitness);
						if(canSwap() && tauFitness >= swapDeliberationThreshold) {
							Path reqSubtaskPathAg = findPath(currentPositions[reqSubTask], tt.goalPos[reqSubTask]);
							double reqSubTaskECostForAg = estimatedCost(reqSubtaskPathAg.tail());
							double delta = reqSubTaskECostForReq - reqSubTaskECostForAg;
							if(delta >= swapBidThreshold) {
								logInf2("Bidding for request with delta = " + delta + " to agent " + topRequester + ", eCostForReqST = " + reqSubTaskECostForAg + ", bidSubTask = " + mySubtask() + ", eCostBidSTask = " + estimatedCost);
								bidMsg = prepareSwapBidMsg(topRequester, reqSubTaskECostForAg, mySubtask(), estimatedCost, resourcePoints());
								bidding = true;
							}
						}
					}
					setState(NoHelpAgentStates.SW_S_RESPOND_TO_REQ);
				}
				break;
			case SW_R_GET_BIDS:
				ArrayList<Message> swapBidMsgs = new ArrayList<Message>();
				swapCommit = false;
				msgStr = commMedium().receive(id());
				while (!msgStr.equals(""))
				{
					logInf("Received a message: " + msgStr);
					Message msg = new Message(msgStr);				
					if (msg.isOfType(MAP_SWAP_BID_MSG))
						swapBidMsgs.add(msg);
					 msgStr = commMedium().receive(id());
				}
				ArrayList<Message> lstBidsConsidered = new ArrayList<Message>();
				if(swapBidMsgs.size() > 0 && canSwap()) {
					Message lowestBidMsg; 
					do {
						lowestBidMsg = null;
						double minECost = Double.MAX_VALUE;
						for (int index = 0; index < swapBidMsgs.size(); index++)
						{
							Message msg = swapBidMsgs.get(index);
							if(lstBidsConsidered.contains(msg)) continue;
							
							double eCost = msg.getDoubleValue("eCostReqSubTask");
							if(eCost < minECost) {
								lowestBidMsg = msg;
								minECost = msg.getDoubleValue("eCostReqSubTask");
							}
						}
						if(lowestBidMsg != null) {
							int bidSubTask = lowestBidMsg.getIntValue("bidSubTask");
							Path bidSubtaskPathAg = findPath(currentPositions[bidSubTask], tt.goalPos[bidSubTask]);
							double bidSubTaskECostForAg = estimatedCost(bidSubtaskPathAg.tail());
							logInf2("Lowest bid with eCost = " + minECost + ", bidTask = " + bidSubTask + ", bidTaskCostForAg = " + bidSubTaskECostForAg);
							if(bidSubTaskECostForAg <= lowestBidMsg.getDoubleValue("eCostBidSubTask")) {
								swapCommit = true;
								swapAgent = lowestBidMsg.sender();
								swapMsg = prepareSwapCommitMsg(id(), swapAgent, resourcePoints());
								swapAgentRes = lowestBidMsg.getIntValue("bidderRes");
								logInf2("Commiting swap with agent = " + swapAgent);
								break;
							}
							lstBidsConsidered.add(lowestBidMsg);
						}
					} while(lowestBidMsg != null && lstBidsConsidered.size() < swapBidMsgs.size());
				}
				if(!swapCommit) {
					numOfSwapAbort++;
					swapMsg = prepareSwapAbortMsg();
					logInf2("Aborting swap.");
				}
				setState(NoHelpAgentStates.SW_S_ANNOUNCE);
				break;
			case SW_R_AWAIT_OUTCOME:
				setState(NoHelpAgentStates.SW_S_AWAIT_OUTCOME);
				break;
			case SW_R_COMPLETE_SWAP:
				if(topRequester != id()) {
					swapCommit = false;
					ArrayList<Message> swapCommitMsgs = new ArrayList<Message>();
					msgStr = commMedium().receive(id());
					while (!msgStr.equals(""))
					{
						logInf2("Received a message: " + msgStr);
						Message msg = new Message(msgStr);				
						if (msg.isOfType(MAP_SWAP_COMMIT_MSG))
							swapCommitMsgs.add(msg);
						 msgStr = commMedium().receive(id());
					}
					for(Message msg : swapCommitMsgs) {
						if(msg.getIntValue("bidder") == id()) {
							swapCommit = true;
							swapAgent = id();
							swapAgentRes = msg.getIntValue("reqRes");
						} else {
							swapCommit = false;
							int bidder = msg.getIntValue("bidder");
							int requester = msg.getIntValue("requester");
							swapSubTaskAssignment(requester, bidder);
							logInf2("Updating subtask assignments of agents : " + requester + "," + bidder);
						}
					}
				}
				if(swapCommit) {
					if(topRequester == id()) {
						//logInf2("Old path = " + (path.getNumPoints() > 0 ? remainingPath(pos()) : pos()));
						
						swapSubTaskAssignment(swapAgent);
						logInf2("Swapping with agent = " + swapAgent + ", ResPoints = " + swapAgentRes + ", OldRes = " + resourcePoints());
						resourcePoints = swapAgentRes - Team.unicastCost;
						decResourcePoints(TeamTask.swapOverhead);
						numOfSwapSuccess++;
						if(canReplan())
							replan();
						//logInf2("New path = " + (path.getNumPoints() > 0 ? remainingPath(pos()) : pos()));
					} 
					else if(swapAgent == id()) {
						//logInf2("Old path = " + (path.getNumPoints() > 0 ? remainingPath(pos()) : pos()));
						
						swapSubTaskAssignment(topRequester);
						logInf2("Swapping with agent = " + topRequester + ", ResPoints = " + swapAgentRes + ", OldRes = " + resourcePoints());
						resourcePoints = swapAgentRes - Team.broadcastCost;
						decResourcePoints(TeamTask.swapOverhead);
						if(canReplan())
							replan();
						//logInf2("New path = " + (path.getNumPoints() > 1 ? remainingPath(pos()) : pos()));
					}
				}
				swapPriority = false;
				setState(NoHelpAgentStates.S_INIT);
				break;
			}
		} while(subState);
		
		if(state == NoHelpAgentStates.R_MOVE || state == NoHelpAgentStates.R_BLOCKED || state == NoHelpAgentStates.R_SKIP) {
			returnCode = super.receiveCycle();
		}
		return returnCode;
	}

	private void replan() {
		findPath();
		repRound = roundCount;
		logInf2("Replanning: Chose this path: " + path().toString());
		numOfReplans++;	
		replanned = true;
	}

	/**
	 * Calculates the estimated cost of a path p.
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
	 * Checks whether the agent has enough resources in order to replan
	 * 
	 * @author Mojtaba
	 */
	private boolean canReplan() {
		return (resourcePoints() >= planCost());
	}
	
	/**
	 * Prepares a swap request message and returns its String encoding.
	 * 
	 * @param tauValue				The tauValue to be included in
	 * 								the message.
	 * @return						The message encoded in String
	 */
	private String prepareSwapReqMsg(int subTask, double estimatedCost, double tauValue) {
		
		Message swapReq = new Message(id(),-1,MAP_SWAP_REQ_MSG);
		swapReq.putTuple("subTask", Integer.toString(subTask));
		swapReq.putTuple("eCost", Double.toString(estimatedCost));
		swapReq.putTuple("tauValue", Double.toString(tauValue));
		return swapReq.toString();
	}
	
	/**
	 * Prepares a swap bid message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
private String prepareSwapBidMsg(int reqAgent, double eCostReqSubTask, int bidSubTask, double eCost, int resources) {
		
		Message swapReq = new Message(id(),reqAgent,MAP_SWAP_BID_MSG);
		swapReq.putTuple("eCostReqSubTask", Double.toString(eCostReqSubTask));
		swapReq.putTuple("bidSubTask", Integer.toString(bidSubTask));
		swapReq.putTuple("eCostBidSubTask", Double.toString(eCost));
		swapReq.putTuple("bidderRes", resourcePoints());
		return swapReq.toString();
	}
	
	/**
	 * Prepares a swap commit message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String prepareSwapCommitMsg(int requesterAgent, int bidderAgent, int resources) {
		
		Message swapReq = new Message(id(),-1,MAP_SWAP_COMMIT_MSG);
		swapReq.putTuple("requester", Integer.toString(requesterAgent));
		swapReq.putTuple("bidder", Integer.toString(bidderAgent));
		swapReq.putTuple("reqRes", Integer.toString(resources));
		return swapReq.toString();
	}
	
	/**
	 * Prepares a swap commit message and returns its String encoding.
	 * 
	 * @return						The message encoded in String
	 */
	private String prepareSwapAbortMsg() {
		
		Message swapReq = new Message(id(),-1,MAP_SWAP_ABORT_MSG);
		return swapReq.toString();
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
	
	//*******************************************************************
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	protected void logInf(String msg) {
		if (dbgInf)
			System.out.println("[NoHelpRepAgent " + id() + "]: " + msg);
	}
	
	/**
	 * Prints the log message into the output if the information debugging 
	 * level is turned on (debuggingInf).
	 * 
	 * @param msg					The desired message to be printed
	 */
	private void logInf2(String msg) {
		if (dbgInf2)
			System.err.println("[NoHelpRepAgent " + id() + "]: " + msg);
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
			System.err.println("[xx][NoHelpRepAgent " + id() + 
							   "]: " + msg);
	}

}

