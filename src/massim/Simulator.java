package massim;

import java.util.PriorityQueue;

import massim.Team.TeamStepCode;

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
		System.arraycopy(teams, 0, this.teams, 0, teams.length);
		Simulator.simParams = simParams;
	}
	
	/**
	 * Initializes the simulator.
	 * Should be called before step() method
	 * @param initBoard The initial board, defined in the frontend
	 */
	public void init(SimState ss) {		
		// set the simulation state to the input one
		// initialize the teams
		// set the counter to zero
				
		simState = ss;
		
		/*for (Team t : teams)
			t.init();*/
		
		simCounter = 0;
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
		
		// increase the counter
		simCounter++;
		
		// Add disturbance to the board
		simState.board().distrub(disturbanceLevel);
		
		// Execute each team
		boolean allDone = false;
		for (Team t : teams)
		{
			TeamStepCode tsc;
			
			tsc = t.step(simState);
			
			if (tsc == TeamStepCode.OK)
				allDone = false;
			else 
				allDone = true;						
		}
		
		// Check the simulation's status at the step
		if (allDone)
			return SimStepCode.SIMEND;
		else
			return SimStepCode.SIMOK;		
	}
	
	/**
	 * can be used by a frontend to run the simulator until the end
	 * without interruption
	 * @return The final step's code, to be used to determine any error
	 */
	public SimStepCode autoplay(double disturbanceLevel) {
		// run the simulation from current step in a loop until the last step 
		// (determined by the return code) without user interaction
		SimStepCode ssc = SimStepCode.SIMOK;
		while (ssc == SimStepCode.SIMOK)		
			ssc = step(disturbanceLevel);
		return ssc;		
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
