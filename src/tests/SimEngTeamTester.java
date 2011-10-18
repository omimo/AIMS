package tests;

import massim.SimulationEngine;
import massim.Team;
import massim.agents.dummy.DummyTeam;
import massim.agents.dummy.UselessTeam;

public class SimEngTeamTester {

	public static void main(String[] args)
	{
		Team[] teams = new Team[2];
		teams[0] = new DummyTeam();
		teams[1] = new UselessTeam();
		
		SimulationEngine se = new SimulationEngine(teams);
		
		Team.initResCoef = 200;
		
		se.initializeExperiment(2);
		int[] teamScores = se.runExperiment();
		
		for (int i=0;i<teams.length;i++)
			System.out.println(teams[i].getClass().getSimpleName()+" average score = "+teamScores[i]);
	}
}
