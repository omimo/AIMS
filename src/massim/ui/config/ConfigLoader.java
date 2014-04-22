package massim.ui.config;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import massim.ui.config.ConfigProperty;
import massim.ui.config.ConfigProperty.DataType;
import massim.ui.config.ConfigProperty.InputType;
import massim.ui.config.ConfigProperty.ValueType;
import massim.ui.config.TeamConfiguration.TeamType;

public class ConfigLoader {

	public static void saveConfiguration(String strConfigFileName, Object config)
	{
		JAXBContext jc;
		File fileOut = new File(strConfigFileName);
		
		try {
			jc = JAXBContext.newInstance(config.getClass());
		
	        Marshaller marshaller = jc.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.marshal(config, fileOut);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static ExperimentConfiguration readConfig(String strKey, String valueFile)
	{
		if(strKey == null) strKey = "config";
		String strFileName = strKey;
		if(!strKey.endsWith(".xml"))
			strFileName = getFileName(strKey, 0);
		File fileOut = new File(strFileName);
		ExperimentConfiguration config = readConfig(fileOut);
		if(valueFile != null) {
			File fileValue = new File(valueFile);
			ConfigurationValues configValues = readConfigValues(fileValue);
			if(config != null && configValues != null) {
				for(ConfigurationValue value : configValues.getConfigValues()) {
					if(value == null) continue;
					config.add(value.getPropertyName(), value.getValue());
				}
				List<TeamConfiguration> newTeams = new ArrayList<TeamConfiguration>();
				Iterator<TeamConfiguration> iterator  = config.getTeams().iterator();
				while(iterator.hasNext()) {
					TeamConfiguration teamConfig = iterator.next();
					if(teamConfig == null) continue;
					boolean bTeamFound = false;
					for(ConfigurationValues child : configValues.getConfigChildren()) {
						if(teamConfig.getPropertyValue("Agent Type") != null 
								&& teamConfig.getPropertyValue("Agent Type").equalsIgnoreCase(child.getKey())) {
							if(bTeamFound) {
								TeamConfiguration newTeamConfig = new TeamConfiguration(teamConfig.getTeamType());
								newTeams.add(newTeamConfig);
								for(ConfigProperty prop : teamConfig.getProperties()) {
									try {
										newTeamConfig.add(new ConfigProperty(prop.getName(), prop.getValueType(), 
												prop.getDataType(), prop.getInputType(), 
												prop.getDataItems(), prop.isRange(), prop.getToDataItems(), 
												prop.getIncrDataItems(), prop.getDescription()));
									} catch (Exception e) { e.printStackTrace(); }
									for(ConfigurationValue value : child.getConfigValues()) {
										if(value == null) continue;
										newTeamConfig.add(value.getPropertyName(), value.getValue());
									}
								}
							} else {
								bTeamFound = true;
								for(ConfigurationValue value : child.getConfigValues()) {
									if(value == null) continue;
									teamConfig.add(value.getPropertyName(), value.getValue());
								}
							}
						}
					}
					if(!bTeamFound) {
						iterator.remove();
					}
				}
				for(TeamConfiguration newTeamConfig : newTeams) {
					config.getTeams().add(newTeamConfig);
				}
			}
		}
		return config;
	}
	
	private static ExperimentConfiguration readConfig(File fileOut)
	{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(ExperimentConfiguration.class);
		
			Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
			ExperimentConfiguration config = (ExperimentConfiguration) jaxbUnmarshaller.unmarshal(fileOut);
			
			File fileSchema = new File(getFileName("config", 1));
			if(!validateConfigFile(jc, config, fileSchema))
			{
				System.out.println("Configuration file could not validate.");
				return null;
			}
			return config;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static ConfigurationValues readConfigValues(File fileIn)
	{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(ConfigurationValues.class);
		
			Unmarshaller jaxbUnmarshaller = jc.createUnmarshaller();
			ConfigurationValues config = (ConfigurationValues) jaxbUnmarshaller.unmarshal(fileIn);
			return config;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TeamConfiguration readConfigOfTeam(String strKey, String strValueFile)
	{
		if(strKey == null && strValueFile == null) return null;
		
		ConfigurationValues configValues = null;
		if(strValueFile != null) {
			File fileValue = new File(strValueFile);
			configValues = readConfigValues(fileValue);
			if(strKey == null) {
				for(ConfigurationValue value : configValues.getConfigValues()) {
					if(value == null) continue;
					if(value.getPropertyName() != null && value.getPropertyName().equalsIgnoreCase("Agent Type"))
						strKey = value.getValue();
				}
			}
		}
		
		TeamConfiguration teamConfig = null;
		ExperimentConfiguration config = readConfig("config", null);
		if(config != null) {
			Iterator<TeamConfiguration> iterator  = config.getTeams().iterator();
			while(iterator.hasNext()) {
				TeamConfiguration teamConfigComp = iterator.next();
				if(teamConfigComp == null) continue;
				if(teamConfigComp.getPropertyValue("Agent Type") != null &&
						teamConfigComp.getPropertyValue("Agent Type").equalsIgnoreCase(strKey)) {
					teamConfig = teamConfigComp;
				}
			}
		}
		
		if(teamConfig == null) {
			teamConfig = getNewTeamConfiguration();
		}
		
		if(teamConfig != null && configValues != null) {
			for(ConfigurationValue value : configValues.getConfigValues()) {
				if(value == null) continue;
				teamConfig.add(value.getPropertyName(), value.getValue());
			}
		}
		return teamConfig;
	}
	
	private static TeamConfiguration getNewTeamConfiguration() {
		TeamConfiguration teamConfig = new TeamConfiguration(TeamType.New);
		try {
			teamConfig.add(new ConfigProperty("Agent Type", ValueType.Single, DataType.Text, InputType.FixedChoices, new String[] {""}, false, null, null, "Type of agent and team."));
			teamConfig.add(new ConfigProperty("Team Name", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, "Name of the team for simulation."));
			teamConfig.add(new ConfigProperty("WindowColor", ValueType.Single, DataType.Text, InputType.FreeText, null, false, null, null, ""));
			teamConfig.add("Agent Type", "");
			teamConfig.add("Team Name", "New");
			teamConfig.add("WindowColor", "#E9E9F9");
		} catch (Exception ex) { }
		return teamConfig;
	}
	
	public static LinkedList<Component> loadPropertiesInPanel(Configuration config, JPanel pnlContainer, int pnlWidth, ActionListener listener)
	{
		LinkedList<Component> components = new LinkedList<Component>();
		for(ConfigProperty prop : config.getProperties())
		{
			if(prop.getName() != null && prop.getName().equalsIgnoreCase("windowcolor")) continue;
			
			if(prop.getInputType() == InputType.FreeText)
				components.addAll(ConfigLoader.addTextBox(pnlContainer, prop.getName(), config.getPropertyValue(prop.getName()), prop.isRange(), prop.getDescription() ));
			else if(prop.getInputType() == InputType.FixedChoices)
				components.addAll(ConfigLoader.addComboBox(pnlContainer, prop.getName(), config.getPropertyValue(prop.getName()), prop.getDataItems(), prop.isRange(), prop.getToDataItems(), prop.getIncrDataItems(), false, prop.getDescription(), listener));
			else if(prop.getInputType() == InputType.OptionalDropDown)
				components.addAll(ConfigLoader.addComboBox(pnlContainer, prop.getName(), config.getPropertyValue(prop.getName()), prop.getDataItems(), prop.isRange(), prop.getToDataItems(), prop.getIncrDataItems(), true, prop.getDescription(), listener));
		}
		return components;
	}
	
	public static boolean validateConfigFile(JAXBContext jc, Configuration config, File fileSchema)
	{
		JAXBSource source = null;
		try {
			source = new JAXBSource(jc, config);
		} catch (JAXBException e) {
			e.printStackTrace();
		}			
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Schema schema = null;
		try {
			schema = sf.newSchema(fileSchema);
		} catch (SAXException e) {
			e.printStackTrace();
		} 
		
		Validator validator = schema.newValidator();
		try {
			validator.validate(source);
			return true;
		} catch (Exception e) { }
		return false;
	}
	
	public static LinkedList<Component> addTextBox(JPanel pnlContainer, String name, String currentValue, boolean isRange, String description)
	{
		LinkedList<Component> components = new LinkedList<Component>();
		JPanel pnlBoxLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		pnlBoxLabel.setOpaque(false);
		pnlContainer.add(pnlBoxLabel);
		
		JLabel lblText = new JLabel(name);
		pnlBoxLabel.add(lblText);
		
		JPanel pnlBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		pnlBox.setOpaque(false);
		pnlContainer.add(pnlBox);
		
		if(isRange)
		{		
			String[] parts = (currentValue + "").split(",");
			
			JTextField textFrom = new JTextField(parts.length > 0 ? parts[0] : "");
			textFrom.setPreferredSize(new Dimension(50, 20));
			pnlBox.add(textFrom);
			components.add(textFrom);
			textFrom.setName(name + "-from");
			textFrom.setToolTipText("From Value:\n " + description);
			
			JTextField textIncr = new JTextField(parts.length > 1 ? parts[1] : "");
			textIncr.setPreferredSize(new Dimension(50, 20));
			pnlBox.add(textIncr);
			components.add(textIncr);
			textIncr.setName(name + "-incr");
			textIncr.setToolTipText("Increment Value (Leave blank if value does not change for the simulation)");
			
			JTextField textTo = new JTextField(parts.length > 2 ? parts[2] : "");
			textTo.setPreferredSize(new Dimension(50, 20));
			components.add(textTo);
			pnlBox.add(textTo);
			textTo.setName(name + "-to");
			textTo.setToolTipText("To Value (Leave blank if value does not change for the simulation)");
		}
		else
		{
			JTextField text = new JTextField(currentValue);
			text.setName(name);
			components.add(text);
			text.setPreferredSize(new Dimension(150, 20));
			pnlBox.add(text);
			text.setToolTipText(description);
		}
		return components;
	}
	
	public static LinkedList<Component> addComboBox(JPanel pnlContainer, String name, String currentValue, String[] values, boolean isRange, String[] strTo, String[] strIncr, boolean bEditable, String description, ActionListener listener)
	{
		LinkedList<Component> components = new LinkedList<Component>();
		//System.out.println(name);
		JPanel pnlBoxLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		pnlBoxLabel.setOpaque(false);
		pnlContainer.add(pnlBoxLabel);
		
		JLabel lblText = new JLabel(name);
		pnlBoxLabel.add(lblText);
		
		JPanel pnlBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		pnlBox.setOpaque(false);
		pnlContainer.add(pnlBox);
		
		if(name != null && name.equalsIgnoreCase("Agent Type")) {
			values = new String[TeamType.values().length];
			int index = 0;
			for(TeamType type: TeamType.values()) {
				values[index] = type.getFullName();
				index++;
			}
		}
		
		if(isRange)
		{
			String[] parts = (currentValue + "").split(",");
			
			JComboBox<String> comboFrom = new JComboBox<String>();
			comboFrom.setEditable(bEditable);
			comboFrom.setPreferredSize(new Dimension(50, 20));
			if(values != null)
			{
				for(String valueLoop : values)
				{
					comboFrom.addItem(valueLoop);
				}
			}
			if(parts.length > 0)
				comboFrom.setSelectedItem(parts[0]);
			else
				comboFrom.setSelectedItem("");
			comboFrom.setName(name + "-from");
			comboFrom.addActionListener(listener);
			pnlBox.add(comboFrom);
			components.add(comboFrom);
			comboFrom.setToolTipText("From Value:\n " + description);
			
			JComboBox<String> comboIncr = new JComboBox<String>();
			comboIncr.setEditable(bEditable);
			comboIncr.setPreferredSize(new Dimension(50, 20));
			if(strIncr != null)
			{
				for(String valueLoop : strIncr)
				{
					comboIncr.addItem(valueLoop);
				}
			}
			if(parts.length > 1)
				comboIncr.setSelectedItem(parts[1]);
			else
				comboIncr.setSelectedItem("");
			comboIncr.setName(name + "-incr");
			comboIncr.addActionListener(listener);
			pnlBox.add(comboIncr);
			components.add(comboIncr);
			comboIncr.setToolTipText("Increment Value (Leave blank if value does not change for the simulation)");
			
			JComboBox<String> comboTo = new JComboBox<String>();
			comboTo.setEditable(bEditable);
			comboTo.setPreferredSize(new Dimension(50, 20));
			if(strTo != null)
			{
				for(String valueLoop : strTo)
				{
					comboTo.addItem(valueLoop);
				}
			}
			if(parts.length > 2)
				comboTo.setSelectedItem(parts[2]);
			else
				comboTo.setSelectedItem("");
			comboTo.setName(name + "-to");
			comboTo.addActionListener(listener);
			pnlBox.add(comboTo);
			components.add(comboTo);
			comboTo.setToolTipText("To Value (Leave blank if value does not change for the simulation)");
		}
		else
		{
			JComboBox<String> combo = new JComboBox<String>();
			combo.setEditable(bEditable);
			combo.setName(name);
			combo.setPreferredSize(new Dimension(150, 20));
			if(values != null)
			{
				for(String valueLoop : values)
				{
					combo.addItem(valueLoop);
					if(valueLoop.equals(currentValue))
						combo.setSelectedItem(valueLoop);
				}
			}
			combo.addActionListener(listener);
			pnlBox.add(combo);
			components.add(combo);
			combo.setToolTipText(description);
		}
		return components;
	}
	
	private static String getFileName(String strFileKey, int type) {
		String strExtension = ".xml";
		if(type == 1) strExtension = ".xsd";
		else if(type == 2) strExtension = "values.xml";
		return "config/default/Config" + strExtension;
	}
}
