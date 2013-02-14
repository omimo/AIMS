package frontends;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;



import org.apache.commons.cli.*;


import massim.Agent;
import massim.SEControl;
import massim.SimulationEngine;
import massim.Team;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.basicresourcemap.BasicResourceMAPTeam;
import massim.agents.empathic.EmpathicAgent;
import massim.agents.empathic.EmpathicTeam;
import massim.agents.nohelp.NoHelpTeam;


/**
 * This is a text-based front end. It uses files to lead the param list.
 * 
 *   
 * @author Omid Alemi
 *
 */

public class Experiment2 {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws ParseException {
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine();
		SEControl sec = se;
		
	// Load parameters
		CommandLineParser parser = new PosixParser();
		Options options = new Options();
//		options.addOption( OptionBuilder.withLongOpt( "exp-setup-file" ).w.withDescription( "experiment setup file" ).hasArg().withArgName("fname").create()); 
		options.addOption("e", "exp-setup-file", true, "experiment setup file" );
		options.addOption( "l", "log-file", false, "the output log file name" );
		options.addOption( "q", "quiet", false, "do not show output" );
		options.addOption( "v", "verbosity", true, "the verbosity level: 1, 2, 3" );
		
		 // parse the command line arguments
	    CommandLine line = parser.parse( options, args );
	    
	    String expSetFileName="";
	    if( line.hasOption( "exp-setup-file" ) ) {
	        // print the value of block-size
	    	expSetFileName= line.getOptionValue( "exp-setup-file" );
	    } else {
	    	System.err.print("Error: No experiment setup specified.");
	    	(new HelpFormatter()).printHelp("Experiment", options);
	    	System.exit(-1);
	    }
		
	    System.out.println(">>>>> "+expSetFileName);
	    (new Scanner(System.in)).nextLine();
		try {
			sec.loadFromFile(expSetFileName);
		
		} catch (IOException e1) {
			
			System.err.println("Error loading the experiment setup file: "+expSetFileName);
		}
		
		
		
		
	/**************************************/
	
		int numberOfRuns = 500;
		
	SimulationEngine.colorRange = 
		new int[] {0, 1, 2, 3, 4, 5};
	SimulationEngine.numOfColors =  
		SimulationEngine.colorRange.length;
	SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 70, 100, 300, 400, 450,  500};	
	SimulationEngine.numOfMatches = 5;
	
	/* Create the teams involved in the simulation */
		//Team.teamSize = 8;
		EmpathicTeam.useExp = true;
		AdvActionMapTeam.useExp = false;
		BasicActionMAPTeam.useExp = false;
		NoHelpTeam.useExp = false;
		
		Team[] teams = new Team[2];		
		teams[0] = new BasicActionMAPTeam();
		//teams[1] = new BasicResourceMAPTeam();
		teams[1] = new NoHelpTeam();
		
		sec.loadTeams(teams);
		
		//sec.addParam("env.disturbance", (Double)0.0);
		//sec.addParam("agent.helpoverhead", 5);
		
		System.out.println("DISTURBANCE,EMP,AAMAP,NO-HELP");
		
		/* The experiments loop */
		for (int exp=0;exp<11;exp++)
		{
			// percentage
			EmpathicAgent.nHelpActs = 0;
			EmpathicAgent.nHelpRequests =0;
			
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
			
			//Team.initResCoef = 200;
			//Team.unicastCost = 7;
			//Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
			sec.changeParam("Team.broadcastCost", sec.getParamI("Team.unicastCost")*(sec.getParamI("Team.teamSize")-1));
			
			Agent.calculationCost = 35;	
			Agent.cellReward = 100;
			Agent.achievementReward = 2000;

			AdvActionMAPAgent.requestThreshold = 299;
			AdvActionMAPAgent.WLL = 0.8;
			AdvActionMAPAgent.lowCostThreshold = 100;
			
			BasicActionMAPAgent.requestThreshold = 299;
	
			
			EmpathicAgent.WTH_Threshhold = 3.5;
		  	EmpathicAgent.emotState_W = 0.3;
		  	EmpathicAgent.salience_W = 1.5;
		  	EmpathicAgent.pastExp_W = 2.0;
		  	EmpathicAgent.requestThreshold = 299;
		  	
			/* vary the disturbance: */
			//SimulationEngine.disturbanceLevel = 0.1 * exp;
			sec.changeParam("env.disturbance", 0.0);//0.1 * exp);
			
			/* Initialize and run the experiment */
			sec.setupExeperiment(numberOfRuns);
			int[] teamScores = sec.startExperiment();


			/* Print the results */
			DecimalFormat df = new DecimalFormat("0.0");
			System.out.print(exp+","+
			//df.format(SimulationEngine.disturbanceLevel));
			df.format(sec.getParamD("env.disturbance")));
			for (int i=0;i<teams.length;i++)
			// int i = 1;
			System.out.printf(",%d", teamScores[i]);
			System.out.println("");
			// (new Scanner(System.in)).nextLine();
			//System.out.println(EmpathicAgent.nHelpActs + " from " + EmpathicAgent.nHelpRequests);

		}
	}

}
