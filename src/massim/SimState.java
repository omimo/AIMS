package massim;

import java.util.ArrayList;
import java.util.List;

/**
 * The class representing the current state of the 
 * simulator: the simulator counter, all the team states,
 * and the board status.
 * 
 * @author Omid Alemi
 * @version 1.0 2011/10/02
 *
 */
public class SimState {

	TeamState[] teamsState;
	int simStep;
	Board board;
	
	private Goal[] goals;
	int costVerctors[][]; //cost vectors for each agent
	
	
	/**
	 * The constructor method.
	 * @param simStep The simulator's counter at the this specific moment
	 * @param board The current board state
	 */
	public SimState(int simStep, Board board, Goal[] goals, int costVectors[][]) {
		//The board should be copied internally, not referenced.
	}
	
	/**
	 * Adds a team state to the simulation's state
	 * @param ts The team state
	 */
	public void addTeamState(TeamState ts) {
		
	}
	
	/**
	 * Returns the simulator's counter
	 * @return Simulator's counter
	 */
	public int simStep() {
		return simStep;
	}
	
	/**
	 * Returns the board representation of the simulator
	 * stored at the time step simStep
	 * @return The board object
	 */
	public Board board() {
		return board;
	}
	
	/**
	 * 
	 * @return The list of teamState object for all the teams 
	 * in the simulator.
	 */
	public TeamState[] teamsState() {
		return teamsState;
	}
	
	
	/**
	 * returns the ith team state
	 * @param i
	 * @return
	 */
	public TeamState teamState(int i) {
		return teamsState[i];
	}

	/**
	 * Returns the cost vector for the specified agent 
	 * @param agent the id of the agent
	 * @return
	 */
	public int[] costVector(int agent) {
		return costVerctors[agent];
	}
	
	/**
	 * Returns the ith goal
	 * @param i
	 * @return
	 */
	public Goal goal(int i) {
		return goals[i];
	}
}

