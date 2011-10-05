package massim;

import java.util.PriorityQueue;

/**
 * The Multiagent Teamwork Simulator
 * The main class of the simulator
 * 
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 * 
 */
public class Simulator {

	private int simCounter; //change the name 
	private SimState simState;
	
	private Team[] teams;        
        
	public static enum SimStepCode {SIMOK, SIMEND, SIMERR}
	
    public static SimParams simParams;
    
	/**
	 * Constructor
	 * 	
	 * @param teams The array of team to be participated in the simulation
	 * @param simParams The class holding all the simulator parameters 
	 * @param initBoard The initial board settings	 
	 */
	public Simulator(Team[] teams, SimParams simParams) {
		
	}
	
	/**
	 * Initializes the simulator.
	 * Should be called before step() method
	 * @param initBoard The initial board, defined in the frontend
	 */
	public void init(Board initBoard) {
		// load the initial board state into the board
		// put the goals on the board
		// generate the cost vectors
		// initialize the teams
		// set the counter to zero
	}
	
	/**
	 * Performs one step of the simulation based on the current simulator's 
	 * counter.
	 * Should be called by the frontend software
	 * @param disturbanceLevel The level of desired disturbance on the board 
	 * @return The proper code from the SimStepCode enum
	 */
	public SimStepCode step(double disturbanceLevel) {		
		// increase the counter by 1		
		// refresh the board: only add the disturbance 
		// board.disturb(disturbanceLevel);
		// for each team in teams[]
		//      team.step(simState);
		// return the proper simulation step code
		
		return SimStepCode.SIMOK;
	}
	
	/**
	 * can be used by a frontend to run the simulator until the end
	 * without interruption
	 */
	public void autoplay() {
		// run the simulation from current step in a loop until the last step 
		// (determined by the return code) without user interaction
	}
	
	
	/*
	 * NOT SURE IF WE NEED THIS ANYMORE.
	 * EVERYTHING CAN BE DONE USING THE SIMSTATE and TEAMSTATE CLASSES.
	public void finish() {
		// sum the scores
		// ********
	}*/
	
	
	/**
	 * @return The current state of the simulation in a SimState object 
	 */
	public SimState getSimulationState() {
		
		return simState;		
	}
}
