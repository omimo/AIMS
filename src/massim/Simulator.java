package massim;

import java.util.PriorityQueue;

public class Simulator {

	private int counter;
	
	private Board board;
	private Team[] teams;
    private PriorityQueue<Event> events;     
    
    public static Scoring scores;
    
	public Simulator() {
		
	}
	
	public Simulator(Team[] teams, Scoring scores, int[][] initBoard, Event[] initEvents) {
		
	}
	
	public void init() {
		// load the events into the queue
		// load the initial board state into the board
		// initialize the teams
		// set the counter to zero
	}
	
	public int step() {		
		// increase the counter by 1
		// apply the changes in the current time step events into the board
		// refresh the board
		// for each team in teams[]
		//      team.cycle()
		// update the board (shadow -> main)
		// check if the simulation is over, return END signal
		
		return 0;
	}
	
	public void autoplay() {
		// run the simulation from current step in a loop until the last step (return code) without user interaction
	}
	
	public void finalize() {
		// sum the scores
		// ********
	}
}
