package experiments.wellbeing;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Properties;
/*
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
*/
import massim.Agent;
import massim.SimulationEngine;
import massim.Team;
import massim.TeamTask;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.helperinitactionmap.HelperInitActionMAPAgent;
import massim.agents.helperinitactionmap.HelperInitActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.agents.reassignment.RAAgent;


/**
 * This is an experiment for testing wellbeing expression of agents
 * over Resource Multiplier, Disturbance, No of Runs & Expression versions
 * 
 *   
 * @author Denish M
 *
 */
public class CMDSimulator {

	static String strMsg = "";
	public static void main(String[] args) {
			try {
				//sendEmail();
				//runSimulation(10000, 2, 1);
				//runSimulation(10000, 2, 2);
				//runSimulation(5000, 2, 3);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/*
	 * There are two versions of wellbeing.
	 */
	public static void runSimulation(int numberOfRuns, int impVersion, int experiment) throws Exception {
	//	if(wbVersion < 1 || wbVersion > 2)
		//	throw new Exception("Wellbeing value is invalid!");
		if(numberOfRuns < 1)
			throw new Exception("numberOfRuns is invalid!");
		
		SimulationEngine.colorRange = 
			new int[] {0, 1, 2, 3, 4, 5};
		SimulationEngine.numOfColors =  
			SimulationEngine.colorRange.length;
		SimulationEngine.actionCostsRange = 
		new int[] {10, 40, 150, 200, 250, 300, 350, 500};	
	
		/* Create the teams involved in the simulation */
		Team.teamSize = 8;
		Team[] teams = new Team[3];		
		teams[0] = new AdvActionMAPTeam();
		teams[1] = new HelperInitActionMAPTeam();
		teams[2] = new BasicActionMAPTeam();
		
		/* Create the SimulationEngine */
		SimulationEngine se = new SimulationEngine(teams);
		//System.out.println("DISTURBANCE\tAD-ACTION-MAP\tHELPER-INIT\tBASIC-ACTION");
		/* The experiments loop */
		
			/* Set the experiment-wide parameters: */
			/* teams-wide, SimulationEngine, etc params */			
		
		for (int expx=1;expx<2;expx++)
		{
			/* vary the disturbance as the x-axis: */  
			for (int expy=-10;expy<1;expy++)
			{	
				if(experiment == 1)
				{
					Team.unicastCost = 1;
					Agent.calculationCost = 1;			
					SimulationEngine.disturbanceLevel = 0.1;
					TeamTask.initResCoef = 120;
					AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 24;
					AdvActionMAPAgent.WLL = expy * 0.05;
					HelperInitActionMAPAgent.WHH = 0.1;
				}
				else if(experiment == 2)
				{
					Team.unicastCost = 3;
					Agent.calculationCost = 2;			
					SimulationEngine.disturbanceLevel = 0.1;
					TeamTask.initResCoef = 160;
					AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 24;
					AdvActionMAPAgent.WLL = expy * 0.05;
					HelperInitActionMAPAgent.WHH = 0.1;
				}
				else
				{
					Team.unicastCost = 5;
					Agent.calculationCost = 2;			
					SimulationEngine.disturbanceLevel = 0.2;
					TeamTask.initResCoef = 200;
					AdvActionMAPAgent.impFactor = HelperInitActionMAPAgent.impFactor = 24;
					AdvActionMAPAgent.WLL = expy * 0.05;
					HelperInitActionMAPAgent.WHH = 0.1;
				}
				//HelperInitActionMAPAgent.WHL = expx * 0.05;
				
				/*if(HelperInitActionMAPAgent.WHL >= HelperInitActionMAPAgent.WHH)
				{
					System.out.print(HelperInitActionMAPAgent.WHL);
					System.out.print(","+HelperInitActionMAPAgent.WHH);
					System.out.println(",15000,10000,15000");
					continue;
				}*/
				Team.broadcastCost = Team.unicastCost * (Team.teamSize-1);
				TeamTask.helpOverhead = 20;			
				TeamTask.cellReward = 100;
				TeamTask.achievementReward = 2000;
				TeamTask.assignmentOverhead = 0;
				
				AdvActionMAPAgent.requestThreshold = 489;
				AdvActionMAPAgent.lowCostThreshold = 50;
				AdvActionMAPAgent.importanceVersion = HelperInitActionMAPAgent.importanceVersion = impVersion;
				
				HelperInitActionMAPAgent.requestThreshold = 289;
				HelperInitActionMAPAgent.EPSILON = 0.1;
				
				BasicActionMAPAgent.requestThreshold = 489;
				
				RAAgent.EPSILON = 0.2;
				RAAgent.WREASSIGN = 0.3;
				RAAgent.WREASSIGNREQ = 1.0;

				/* Initialize and run the experiment */
				se.initializeExperiment(numberOfRuns);			
				int[] teamScores = se.runExperiment();


				/* Print the results */
				DecimalFormat df = new DecimalFormat("0.00");
				//System.out.print(HelperInitActionMAPAgent.WHH);
				strMsg = AdvActionMAPAgent.WLL + "";
				//System.out.print(","+HelperInitActionMAPAgent.WHL);
				for (int i=0;i<teams.length;i++)
					strMsg += "," + teamScores[i];
				//System.out.printf(",%d", teamScores[i]);
				//System.out.println("");
				//strMsg += "\n";
				
				try {
				    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("outfilename.txt", true)));
				    out.println(strMsg);
				    out.close();
				} catch (IOException e) {
				    //oh noes!
					System.err.println("Error writing file.." + strMsg);
				}
			}
		}
	}

/*
	public static void sendEmail()
	{
		try
		{
			String host = "smtp.gmail.com";
		    String from = "denish.smith77@gmail.com";
		    String pass = "smith1234";
		    Properties props = System.getProperties();
		    props.put("mail.smtp.starttls.enable", "true"); // added this line
		    props.put("mail.smtp.host", host);
		    props.put("mail.smtp.user", from);
		    props.put("mail.smtp.password", pass);
		    props.put("mail.smtp.port", "587");
		    props.put("mail.smtp.auth", "true");
	
		    String[] to = {"mumbaiw@unbc.ca"}; // added this line
	
		    Session session = Session.getDefaultInstance(props, null);
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(from));
	
		    InternetAddress[] toAddress = new InternetAddress[to.length];
	
		    // To get the array of addresses
		    for( int i=0; i < to.length; i++ ) { // changed from a while loop
		        toAddress[i] = new InternetAddress(to[i]);
		    }
		    System.out.println(Message.RecipientType.TO);
	
		    for( int i=0; i < toAddress.length; i++) { // changed from a while loop
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    message.setSubject("Test email");
		    message.setText("Welcome to JavaMail");
		    Transport transport = session.getTransport("smtp");
		    transport.connect(host, from, pass);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		}
		catch (Exception ex)
		{
			System.err.println("Error : " + ex.getMessage());
		}
	}
	*/
}