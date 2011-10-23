package tests;

import java.util.Scanner;

import massim.SimulationEngine;
import massim.Team;
import massim.agents.dummy.DummyTeam;
import massim.agents.dummy.UselessTeam;

/**
 * Simulation Engine/Teams interaction test.
 * 
 * @author Omid Alemi
 * @version 2011/10/17
 */
public class SimEngTeamTester {

	public static void main(String[] args)
	{
		singleExperiment(); 
		multipleExperiments();
	}
	
	/** 
	 * To demonstrate the mechanism of performing a single experiment.
	 */
	public static void singleExperiment()
	{
		int numberOfRuns = 4;
		
		/* Create the teams involved in the simulation */
		Team[] teams = new Team[2];
		teams[0] = new DummyTeam();
		teams[1] = new UselessTeam();
		
		/* Set the teams-wide parameters */ 
		Team.initResCoef = 200;
		
		/* Create and initialize the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);		
		se.initializeExperiment(numberOfRuns);
		
		/* Run the experiment */
		int[] teamScores = se.runExperiment();
		
		/* Print the results */
		for (int i=0;i<teams.length;i++)
			System.out.println(teams[i].getClass().getSimpleName()+
					" average score = "+teamScores[i]);
	}
	
	/**
	 * To demonstrate how to use the SimulatinEngine to perform
	 * multiple experiments by changing some parameters.
	 */
	public static void multipleExperiments()
	{
		int numberOfRuns = 4;
		/* Create the teams involved in the simulation */
		Team[] teams = new Team[2];
		teams[0] = new DummyTeam();
		teams[1] = new UselessTeam();
		
				
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			Team.initResCoef = 200;
			
			/* vary the disturbance: */
			SimulationEngine.disturbanceLevel = 0.1 * exp;  
			
			/* Initialize and run the experiment */
			se.initializeExperiment(numberOfRuns);			
			int[] teamScores = se.runExperiment();
			
			/* Print the results */
			for (int i=0;i<teams.length;i++)
				System.out.println("Exp"+exp+": disturbance level="+ 
						SimulationEngine.disturbanceLevel+"; "+ 
						teams[i].getClass().getSimpleName()+
						" average score = "+teamScores[i]);
			(new Scanner(System.in)).nextLine();
			
		}
		
	}
}
