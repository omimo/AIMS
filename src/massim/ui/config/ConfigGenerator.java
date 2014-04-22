package massim.ui.config;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import massim.ui.config.ConfigProperty.DataType;
import massim.ui.config.ConfigProperty.InputType;
import massim.ui.config.ConfigProperty.ValueType;
import massim.ui.config.TeamConfiguration.TeamType;

import org.w3c.dom.Document;

public class ConfigGenerator {
	public static void main(String[] args)
	{
		run();
		//runValues();
	}
	private static void run()
	{
		boolean bExperiment = true;
		String strXSDName = "config/default/Config.xsd";
		String strConfigName = "config/default/Config.xml";
		Class<?> className = null;
		Configuration config = null;
		if(bExperiment)
		{
			className = ExperimentConfiguration.class;
			config = generateConfig1();
			ConfigLoader.saveConfiguration(strConfigName, (ExperimentConfiguration)config);
		}
		else {
			className = TeamConfiguration.class;
			config = generateConfig();
			ConfigLoader.saveConfiguration(strConfigName, (TeamConfiguration)config);
		}
		
		printConfiguration(strConfigName, className, config);
		createXSDFile(strXSDName, className);
	}
	
	private static void runValues()
	{
		boolean bExperiment = true;
		String strConfigName = "config/default/Configvalues.xml";
		ConfigurationValues config = null;
		if(bExperiment)
		{
			config = generateConfigValues1();
			ConfigLoader.saveConfiguration(strConfigName, config);
		}
		else {
			config = generateConfigValues();
			ConfigLoader.saveConfiguration(strConfigName, config);
		}
	}
	
	private static void printConfiguration(String strConfigFileName, Class<?> className, Configuration config)
	{
		JAXBContext jc;
		File fileOut = new File(strConfigFileName);
		try {
			jc = JAXBContext.newInstance(className);
		
			Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
			config = (Configuration) jaxbUnmarshaller.unmarshal(fileOut);
			for(ConfigProperty prop : config.getProperties())
			{
				System.out.println(prop.toString());
			}
			if(config instanceof ExperimentConfiguration) {
				for(TeamConfiguration team : ((ExperimentConfiguration)config).getTeams())
				{
					System.out.println(team.getTeamType().getFullName());
					for(ConfigProperty prop : team.getProperties())
					{
						System.out.println(prop.toString());
					}
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}
	
	public static void createXSDFile(String strXSDName, Class<?> className)
	{
		// grab the context
	    JAXBContext context = null;
		try {
			context = JAXBContext.newInstance(className);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	    final java.util.List<DOMResult> results = new java.util.ArrayList<DOMResult>();

	    // generate the schema
	    try {
			context.generateSchema(
			        // need to define a SchemaOutputResolver to store to
			        new SchemaOutputResolver()
			        {
			            @Override
			            public Result createOutput( String ns, String file )
			                    throws IOException
			            {
			            	System.out.println(file);
			                // save the schema to the list
			                DOMResult result = new DOMResult();
			                result.setSystemId(file);
			                results.add(result);
			                return result;
			            }
			        } );
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    DOMResult res = results.get(0);
	    org.w3c.dom.Node node = res.getNode();
	    System.out.println(node.getNodeName());
	    System.out.println(node.getChildNodes().item(0).getNodeName());
	    System.out.println(node.getChildNodes().item(0).getChildNodes().getLength());
	    
	    // output schema via System.out
	    DOMResult domResult = results.get( 0 );
	    Document doc = (Document) domResult.getNode();
	    writeXmlFile(doc, strXSDName);
	}
	
	public static void writeXmlFile(Document doc, String filename) {
	    try {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource(doc);
	 
	        // Prepare the output file
	        File file = new File(filename);
	        Result result = new StreamResult(file);
	 
	        // Write the DOM document to the file
	        Transformer xformer = TransformerFactory.newInstance().newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    } catch (TransformerException e) {
	    }
	 
	}
	
	public static TeamConfiguration generateConfig()
	{
		String[] strValues = new String[] { "1", "2", "3"};
		String[] strValues1 = new String[] { "", "Advanced Action MAP", "Advanced Action MAP Replaning",  "Basic Action MAP", "HIAMAP", "No Help"};
		
		TeamConfiguration teamConfig = null;
		
		try {
			teamConfig = new TeamConfiguration(TeamType.AdvActionMAPRep);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WLL", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value to compare wellbeing for requesting help."));
			teamConfig.add(new ConfigProperty("WREP", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value to compare wellbeing for replaning."));
			teamConfig.add(new ConfigProperty("Request Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is greater than Request Threshold."));
			teamConfig.add(new ConfigProperty("Lowcost Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is lower than Lowcost threshold"));
			teamConfig.add(new ConfigProperty("Importance Version", ValueType.Single, DataType.Integer, InputType.FixedChoices, new String[] {"1", "2"}, false, null, null, "Which version of importance function to use."));
			teamConfig.add(new ConfigProperty("Proximity Bias", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Factor to calculate importance of hel action."));
			//teamConfig.add(new ConfigProperty("Epsilon", ValueType.Single, DataType.Decimal, InputType.FreeText, null, "0", false, null, null, ""));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return teamConfig;
	}
	
	public static ConfigurationValues generateConfigValues()
	{
		ConfigurationValues childConfig = null;		
		try {
			childConfig = new ConfigurationValues();
			childConfig.add("Agent Type", "No Help");
			childConfig.add("Team Name", "No Help");
			childConfig.add("WindowColor", "#E9E9F9");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return childConfig;
	}

	public static ExperimentConfiguration generateConfig1()
	{
		String[] strValues = new String[] { "1", "2", "3"};
		String[] strValues1 = new String[] {""};
		
		ExperimentConfiguration config = new ExperimentConfiguration();
		
		try {
			config.add(new ConfigProperty("Number of Runs", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Numer of times to run the simulation."));
			config.add(new ConfigProperty("Number of Colors", ValueType.Single, DataType.Integer, InputType.FixedChoices, new String[]{"8", "7", "6", "5", "4"}, false, null, null, "Number of distinct colors on board."));
			config.add(new ConfigProperty("Action Costs", ValueType.Multiple, DataType.Integer, InputType.FreeText, null, false, null, null, "Possible action costs to choose from for each color for each agent."));
			config.add(new ConfigProperty("Team Size", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Numer of agents in the team"));
			config.add(new ConfigProperty("Board Size", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Size of the square board. NxN"));
			config.add(new ConfigProperty("Unicast Cost", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Cost to send a message to one agent."));
			config.add(new ConfigProperty("Calc Cost", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Cost to do calculation to offer/ask for help. "));
			config.add(new ConfigProperty("Disturbance", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Probability of change of colors on the board. Put any value between 0 to 1.0"));
			config.add(new ConfigProperty("Help Overhead", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Overhead in case agent offers help"));
			config.add(new ConfigProperty("Assignment Overhead", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Overhead in case agent performs action for other."));
			config.add(new ConfigProperty("Cell Reward", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Reward points for one step moved."));
			config.add(new ConfigProperty("Achievement Reward", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Reward points for reaching the goal."));
			config.add(new ConfigProperty("Initial Resource Coefficient", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Coefficient to calculate initial resource assigned by multiplication with number of steps to reach goal."));
			config.add(new ConfigProperty("Additional Leader Resource", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Number of extra initial resource points for the leader (first) agent."));
			config.add(new ConfigProperty("Plan Resource Coefficient", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Coefficient to calculate replanning cost."));
			config.add(new ConfigProperty("Use Remaining Resources", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to add remaining resources in the final reward points."));
			config.add("Number of Runs", "5");
			config.add("Number of Colors", "6");
			config.add("Action Costs", "10,40,100,150,250,300,350,500");
			config.add("Team Size", "8");
			config.add("Board Size", "10");
			config.add("Unicast Cost", "1,1,5");
			config.add("Calc Cost", "1");
			config.add("Disturbance", "0.1");
			config.add("Help Overhead", "20");
			config.add("Assignment Overhead", "10");
			config.add("Cell Reward", "100");
			config.add("Achievement Reward", "2000");
			config.add("Initial Resource Coefficient", "160");
			config.add("Plan Resource Coefficient", "0.03");
			config.add("Additional Leader Resource", "0");
			config.add("Use Remaining Resources", "No");
			
			TeamConfiguration teamConfig = new TeamConfiguration(TeamType.AdvActionMAP);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WLL", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value to compare wellbeing for requesting help."));
			teamConfig.add(new ConfigProperty("Request Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is greater than Request Threshold."));
			teamConfig.add(new ConfigProperty("Lowcost Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is lower than Lowcost threshold"));
			teamConfig.add(new ConfigProperty("Importance Version", ValueType.Single, DataType.Integer, InputType.FixedChoices, new String[] {"1", "2"}, false, null, null, "Which version of importance function to use."));
			teamConfig.add(new ConfigProperty("Proximity Bias", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Factor to calculate importance of hel action."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			//teamConfig.add(new ConfigProperty("Epsilon", ValueType.Single, DataType.Decimal, InputType.FreeText, null, "0", false, null, null, ""));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "Advanced Action MAP");
			teamConfig.add("Team Name", "Advanced Action MAP");
			teamConfig.add("WLL", "-0.1");
			teamConfig.add("Request Threshold", "351");
			teamConfig.add("Lowcost Threshold", "50");
			teamConfig.add("Importance Version", "2");
			teamConfig.add("Proximity Bias", "6");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			//teamConfig.add("Epsilon", "0");
			teamConfig.add("WindowColor", "#F9F9F9");
			config.add(teamConfig);
			
			teamConfig = new TeamConfiguration(TeamType.AdvActionMAPRep);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WLL", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value to compare wellbeing for requesting help."));
			teamConfig.add(new ConfigProperty("WREP", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value to compare wellbeing for replaning."));
			teamConfig.add(new ConfigProperty("Request Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is greater than Request Threshold."));
			teamConfig.add(new ConfigProperty("Lowcost Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Request help in case cost is lower than Lowcost threshold"));
			teamConfig.add(new ConfigProperty("Importance Version", ValueType.Single, DataType.Integer, InputType.FixedChoices, new String[] {"1", "2"}, false, null, null, "Which version of importance function to use."));
			teamConfig.add(new ConfigProperty("Proximity Bias", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Factor to calculate importance of hel action."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			//teamConfig.add(new ConfigProperty("Epsilon", ValueType.Single, DataType.Decimal, InputType.FreeText, null, "0", false, null, null, ""));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "Advanced Action MAP Replaning");
			teamConfig.add("Team Name", "Advanced Action MAP Replaning");
			teamConfig.add("WLL", "-0.1");
			teamConfig.add("WREP", "-0.3");
			teamConfig.add("Request Threshold", "351");
			teamConfig.add("Lowcost Threshold", "50");
			teamConfig.add("Importance Version", "2");
			teamConfig.add("Proximity Bias", "6");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			//teamConfig.add("Epsilon", "0");
			teamConfig.add("WindowColor", "#F9D9D9");
			config.add(teamConfig);
			
			teamConfig = new TeamConfiguration(TeamType.BasicActionMAP);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("Request Threshold", ValueType.Single, DataType.Integer, InputType.FreeText, null, false, null, null, "Request help in case cost is greater than Request Threshold."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "Basic Action MAP");
			teamConfig.add("Team Name", "Basic Action MAP");
			teamConfig.add("Request Threshold", "351");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			teamConfig.add("WindowColor", "#E9F9E9");
			config.add(teamConfig);
			
			teamConfig = new TeamConfiguration(TeamType.HIAMAP);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WHH", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Offer help if wellbeing is higher than Higher value watermark."));
			teamConfig.add(new ConfigProperty("Request Threshold", ValueType.Single, DataType.Decimal, InputType.FreeText, null, false, null, null, "Do not offer help for action for which the cost is greater than Request Threshold."));
			teamConfig.add(new ConfigProperty("Importance Version", ValueType.Single, DataType.Integer, InputType.FixedChoices, new String[] {"1", "2"}, false, null, null, "Which version of importance function to use."));
			teamConfig.add(new ConfigProperty("Proximity Bias", ValueType.Single, DataType.Integer, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Factor to calculate importance of hel action."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			//teamConfig.add(new ConfigProperty("Epsilon", ValueType.Single, DataType.Decimal, InputType.FreeText, null, "0", false, null, null));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "HIAMAP");
			teamConfig.add("Team Name", "Helper Initiated Action MAP");
			teamConfig.add("WHH", "0.5");
			teamConfig.add("Request Threshold", "351");
			teamConfig.add("Importance Version", "2");
			teamConfig.add("Proximity Bias", "6");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			//teamConfig.add("Epsilon", "0");
			teamConfig.add("WindowColor", "#F9E9E9");
			config.add(teamConfig);
			
			teamConfig = new TeamConfiguration(TeamType.NoHelp);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "No Help");
			teamConfig.add("Team Name", "No Help");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			teamConfig.add("WindowColor", "#E9E9F9");
			config.add(teamConfig);
			
			teamConfig = new TeamConfiguration(TeamType.NoHelpRep);
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, strValues1, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WREP", ValueType.Single, DataType.Decimal, InputType.OptionalDropDown, strValues, true, strValues, strValues, "Low watermark value for replaning."));
			teamConfig.add(new ConfigProperty("Use Initial Optimum Assignment", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {"Yes", "No"}, false, null, null, "Whether to use initial optimum assignment."));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "No Help Replaning");
			teamConfig.add("Team Name", "No Help Replaning");
			teamConfig.add("WREP", "-0.3");
			teamConfig.add("Use Initial Optimum Assignment", "Yes");
			teamConfig.add("WindowColor", "#E9E9F9");
			config.add(teamConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
	
	public static ConfigurationValues generateConfigValues1()
	{
		ConfigurationValues config = new ConfigurationValues();
		
		try {
			config.add("Number of Runs", "1000");
			config.add("Number of Colors", "6");
			config.add("Action Costs", "10,40,150,200,300,350");
			config.add("Team Size", "8");
			config.add("Board Size", "6");
			config.add("Unicast Cost", "1,2,3");
			config.add("Calc Cost", "3,2,1");
			config.add("Disturbance", "1,1,1");
			config.add("Help Overhead", "20");
			config.add("Assignment Overhead", "20");
			config.add("Cell Reward", "100");
			config.add("Achievement Reward", "2000");
			config.add("Initial Resource Coefficient", "2000");
			config.add("Use Remaining Resources", "Yes");
			
			ConfigurationValues childConfig = new ConfigurationValues();
			
			childConfig.add("Agent Type", "Advanced Action MAP");
			childConfig.add("Team Name", "Advanced Action MAP");
			childConfig.add("WLL", "-0.5,-0.1,0.1");
			childConfig.add("Request Threshold", "299");
			childConfig.add("Lowcost Threshold", "50");
			childConfig.add("Importance Version", "2");
			childConfig.add("Proximity Bias", "10,100,5");
			//childConfig.add("Epsilon", "0");
			childConfig.add("WindowColor", "#F9F9F9");
			config.add("Advanced Action MAP", childConfig);
			
			childConfig = new ConfigurationValues();
			childConfig.add("Agent Type", "Basic Action MAP");
			childConfig.add("Team Name", "Basic Action MAP");
			childConfig.add("Request Threshold", "299");
			childConfig.add("WindowColor", "#E9F9E9");
			config.add("Basic Action MAP", childConfig);
			
			childConfig = new ConfigurationValues();
			childConfig.add("Agent Type", "HIAMAP");
			childConfig.add("Team Name", "Helper Initiated Action MAP");
			childConfig.add("WHH", "0.1,0.3,0.5");
			childConfig.add("Request Threshold", "299");
			childConfig.add("Importance Version", "2");
			childConfig.add("Proximity Bias", "10,100,5");
			//teamConfig.add("Epsilon", "0");
			childConfig.add("WindowColor", "#F9E9E9");
			config.add("HIAMAP", childConfig);
			
			childConfig = new ConfigurationValues();
			childConfig.add("Agent Type", "No Help");
			childConfig.add("Team Name", "No Help");
			childConfig.add("WindowColor", "#E9E9F9");
			config.add("No Help", childConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}
}
