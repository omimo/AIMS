package massim;

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
	
	/**
	 * The constructor method.
	 * @param simStep The simulator's counter at the this specific moment
	 * @param board The current board state
	 */
	public SimState(int simStep, Board board) {
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
	
}
