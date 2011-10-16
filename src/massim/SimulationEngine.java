package massim;

import java.util.PriorityQueue;

import massim.Team.TeamStepCode;

/**
 * The Multiagent Teamwork SimulationEngine
 * The main class of the simulator
 * 
 * @author Omid Alemi
 * @version 1.0 2011/10/01
 * 
 */
public class SimulationEngine {

	// Simulation params
	private int[] colorRange = {1,2,3,4,5,6};
	private int[] costsRange = {10,40,70,100,300,400,450,500};
	private int numOfColors = colorRange.length;
	private int numOfTeams;
	
	// Simulation objects
	private Team[] teams;   
	Board mainBoard;
	int[][] actionCostsMatrix;
	
	
	
	// Internal SimEng params:
	private int simCounter;
        
	public static enum SimRoundCode {SIMOK, SIMEND, SIMERR}
	    
    
	/**
	 * Constructor
	 * 	
	 * @param teams The array of team to be participated in the simulation
	 * @param simParams The class holding all the simulator parameters 
	 * @param initBoard The initial board settings	 
	 */
	public SimulationEngine(Team[] teams) {
		this.teams = teams;
		
	}
	
	/**
	 * Initializes the simulator.
	 * Should be called before step() method
	 * @param initBoard The initial board, defined in the frontend
	 */
	public void initializeSimulation() {			
		simCounter = 0;
		
	}
	
	/**
	 * Performs one step of the simulation based on the current simulator's 
	 * counter.
	 * Should be called by the frontend software
	 * @param disturbanceLevel The level of desired disturbance on the board 
	 * @return The proper code from the SimRoundCode enum
	 */
	public SimRoundCode round() {		
		// increase the counter by 1		
		// refresh the board: only add the disturbance 
		// board.disturb(disturbanceLevel);
		// for each team in teams[]
		//      team.step(simState);
		// return the proper simulation step code
		
		// increase the counter
		simCounter++;
		
		// Add disturbance to the board
		//simState.board().distrub(disturbanceLevel);
		
		// Execute each team
		boolean allDone = false;
		for (Team t : teams)
		{
			TeamStepCode tsc;
			
			tsc = t.step();
			
			if (tsc == TeamStepCode.OK)
				allDone = false;
			else 
				allDone = true;						
		}
		
		// Check the simulation's status at the step
		if (allDone)
			return SimRoundCode.SIMEND;
		else
			return SimRoundCode.SIMOK;		
	}
	
	/**
	 * can be used by a frontend to run the simulator until the end
	 * without interruption
	 * @return The final step's code, to be used to determine any error
	 */
	public SimRoundCode autoplay(double disturbanceLevel) {
		// run the simulation from current step in a loop until the last step 
		// (determined by the return code) without user interaction
		SimRoundCode ssc = SimRoundCode.SIMOK;
		while (ssc == SimRoundCode.SIMOK)		
			ssc = round();
		return ssc;		
	}
		
}
