package massim;

import java.util.ArrayList;

import massim.ExperimentLogger.LogType;

public class TeamInitAssign {
	private Board board;
	private RowCol[] startPos;
	private RowCol[] goalPos;
	private int[][] actionCostsMatrix;
	public boolean dbgScore = false;

	public int[] getOptimumAssign(Agent[] agents, Board board, TeamTask tt,
			int[] subtaskAssignments, int[][] actionCostsMatrix) {
		this.board = board;
		if (subtaskAssignments.length != Team.teamSize
				|| actionCostsMatrix.length != Team.teamSize)
			return subtaskAssignments;

		this.startPos = tt.startPos;
		this.goalPos = tt.goalPos;
		this.actionCostsMatrix = actionCostsMatrix;

		// get the number of incomplete subtasks
		int countIncomp = 0;
		for (int s = 0; s < tt.goalPos.length; s++)
			if (!startPos[s].equals(tt.goalPos[s]))
				countIncomp++;

		int[] activeSubtasksMap = new int[countIncomp]; // a[i] = s;
														// i:0..countIncomp,
														// s:0..n#subtasks
		int[] inActiveSubtasksMap = new int[tt.goalPos.length - countIncomp];
		ArrayList<String> lstAllAgents = new ArrayList<String>();

		// Fill the subtask / cost matrix
		double[][] subtaskCost = new double[Team.teamSize][countIncomp];

		int stCount = 0, inactCount = 0;
		for (int s = 0; s < tt.goalPos.length; s++) {
			if (!startPos[s].equals(tt.goalPos[s])) {
				activeSubtasksMap[stCount++] = s;
			} else {
				inActiveSubtasksMap[inactCount++] = s;
			}
		}

		double totalCostBefore = 0;
		for (int agent = 0; agent < Team.teamSize; agent++) {
			lstAllAgents.add(agent + "");
			double[] estCosts = estimateSubtaskCosts(agent);
			for (int sm = 0; sm < activeSubtasksMap.length; sm++) {
				subtaskCost[agent][sm] = estCosts[activeSubtasksMap[sm]];
			}
			totalCostBefore += estCosts[agent];
		}

		boolean tr = false;
		if (subtaskCost.length > subtaskCost[0].length) {
			subtaskCost = HungarianAlgorithm.transpose(subtaskCost);
			tr = true;
		}

		int[][] aha = new int[activeSubtasksMap.length][2];
		aha = HungarianAlgorithm.hgAlgorithm(subtaskCost, "min");

		for (int i = 0; i < aha.length; i++) {
			int ag = tr ? 1 : 0;
			int st = tr ? 0 : 1;

			int a = aha[i][st];
			int subtask = activeSubtasksMap[a];
			int b = aha[i][ag];
			// assignment[subtask] = b;
			subtaskAssignments[subtask] = b;
			lstAllAgents.remove(b + "");
		}
		for (int i = 0; i < inActiveSubtasksMap.length; i++) {
			int subtask = inActiveSubtasksMap[i];
			if (lstAllAgents.size() > 0) {
				subtaskAssignments[subtask] = Integer.parseInt(lstAllAgents
						.get(0));
				lstAllAgents.remove(0);
			}
		}

		double totalCostAfter = 0;
		for (int i = 0; i < subtaskAssignments.length; i++) {
			if (tr) {
				if (subtaskCost.length > i
						&& subtaskCost[i].length > subtaskAssignments[i])
					totalCostAfter += subtaskCost[i][subtaskAssignments[i]];
			} else {
				if (subtaskCost.length > subtaskAssignments[i]
						&& subtaskCost[subtaskAssignments[i]].length > i)
					totalCostAfter += subtaskCost[subtaskAssignments[i]][i];
			}
		}
		logInf("Total cost before optimization = " + totalCostBefore);
		logInf("Total cost after optimization = " + totalCostAfter);
		String strLog = "";
		strLog += "Subtask/Cost Matrix:\n";
		double[][] subtaskCostLog = subtaskCost;
		if (tr) {
			subtaskCostLog = HungarianAlgorithm.transpose(subtaskCost);
		}
		for (int sm = 0; sm < (subtaskCostLog.length > 0 ? subtaskCostLog[0].length : 0); sm++) {
			strLog += "\tSubtask " + activeSubtasksMap[sm];
		}
		strLog += "\n";
		for (int a = 0; a < subtaskCostLog.length; a++) {
			strLog += "Agent " + a;
			for (int sm = 0; sm < subtaskCostLog[a].length; sm++) {
				strLog += "\t" + subtaskCostLog[a][sm];
			}
			strLog += "\n";
		}
		logInf(strLog);

		strLog = "Optimal subtask assignment:";
		for (int a = 0; a < subtaskAssignments.length; a++) {
			strLog += subtaskAssignments[a] + ",";
		}
		logInf(strLog);
		return subtaskAssignments;
	}

	/**
	 * Calculates the distance between two points in a board.
	 * 
	 * @param start
	 *            The position of the starting point
	 * @param end
	 *            The position of the ending point
	 * @return The distance
	 */
	protected int calcDistance(RowCol start, RowCol end) {
		return Math.abs(end.row - start.row) + Math.abs(end.col - start.col)
				+ 1;
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
	protected Path findPath(int agent, int dotask) {
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(pp.findShortestPath(
				estimBoardCosts(agent, board.getBoard(), dotask),
				startPos[dotask], goalPos[dotask]));
		return shortestPath;
	}

	/**
	 * Returns a two dimensional array representing the estimated cost of cells
	 * with i, j coordinates
	 * 
	 * @author Mojtaba
	 */
	private int[][] estimBoardCosts(int agent, int[][] board, int doTask) {

		int[][] eCosts = new int[board.length][board[0].length];

		for (int i = 0; i < eCosts.length; i++)
			for (int j = 0; j < eCosts[0].length; j++) {

				eCosts[i][j] = estimCellCost(agent, i, j, doTask);
			}

		return eCosts;
	}

	/**
	 * Returns estimated cost of a cell with k steps from current position
	 * 
	 * @param i
	 *            cell coordinate
	 * @param j
	 *            cell coordinate
	 * @author Mojtaba
	 */
	private int estimCellCost(int agent, int i, int j, int doTask) {
		double sigma = 1;
		double m = getAverage(actionCostsMatrix[agent]);
		int k = Math.abs((startPos[doTask].row - i))
				+ Math.abs((startPos[doTask].col - j));

		int eCost = (int) (Math.pow(sigma, k)
				* actionCostsMatrix[agent][board.getBoard()[i][j]] + (1 - Math
				.pow(sigma, k)) * m);
		return eCost;
	}

	/**
	 * Calculates the average of the given integer array.
	 * 
	 * @return The average.
	 */
	private double getAverage(int[] array) {
		int sum = 0;
		for (int i = 0; i < array.length; i++)
			sum += array[i];
		return (double) sum / array.length;
	}

	private double[] estimateSubtaskCosts(int agent) {
		double[] estimates = new double[startPos.length];

		for (int s = 0; s < startPos.length; s++) {
			Path subtaskPath = findPath(agent, s);
			estimates[s] = estimatedCost(agent, subtaskPath);
		}

		return estimates;
	}

	/**
	 * Calculated the estimated cost for the agent to move through path p.
	 * 
	 * @param p
	 *            The agent's path
	 * @return The estimated cost
	 */
	private double estimatedCost(int agent, Path p) {
		int l = p.getNumPoints();
		double sigma = 1;// - SimulationEngine.disturbanceLevel;
		double eCost = 0.0;
		if (Math.abs(sigma - 1) < 0.000001) {
			for (int k = 0; k < l; k++)
				eCost += getCellCost(agent, p.getNthPoint(k));
		}
		return eCost;
	}

	protected int getCellCost(int agent, RowCol cell) {
		int color = board.getBoard()[cell.row][cell.col];
		return actionCostsMatrix[agent][color];
	}

	protected void logInf(String msg) {
		if (dbgScore) {
			System.out.println("[Team " + teamIndex + "]: " + msg);
		}
		if (logger != null && bLoggerOn)
			logger.logEvent(LogType.Team, teamIndex, "[Team#" + teamIndex
					+ "]:" + msg + "\n");
	}

	ExperimentLogger logger;
	int teamIndex;
	boolean bLoggerOn = false;

	public void setLogger(ExperimentLogger logger, int teamIndex,
			boolean bLoggerOn) {
		this.logger = logger;
		this.teamIndex = teamIndex;
		this.bLoggerOn = bLoggerOn;
	}
}
