package massim.ui.config;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	
	@XmlElement(type = ConfigProperty.class)
	private List<ConfigProperty> configProperties;
	
	private HashMap<String, String> configValues;
	
	public Configuration()
	{
		configProperties = new ArrayList<ConfigProperty>();
		configValues = new HashMap<String, String>();
	}
	
	public void add(ConfigProperty property)
	{
		if(property == null) return;
		configProperties.add(property);
	}
	
	public void remove(ConfigProperty property)
	{
		configProperties.remove(property);
	}
	
	public void add(String propertyName, String value)
	{
		configValues.put(propertyName, value);
	}
	
	public void remove(String propertyName)
	{
		configValues.remove(propertyName);
	}
	
	public void removePropertyAt(int index)
	{
		if(configProperties.size() > index)
		{
			configProperties.remove(index);
		}
	}
	
	public List<ConfigProperty> getProperties() {
		return configProperties;
	}
	
	public String getPropertyValue(String strPropName) {
		for(String propName : configValues.keySet()) {
			if(propName != null && propName.equalsIgnoreCase(strPropName))
				return configValues.get(propName);
		}
		return null;
	}
	
	public void updateConfigForParams(LinkedList<Component> paraComponents)
	{
		Iterator<Component> iter = paraComponents.iterator();
		while(iter.hasNext())
		{
			Component comp = iter.next();
			if(comp instanceof JTextField) {
				JTextField text = (JTextField)comp;
				if(text.getName() != null) {
					if(text.getName().endsWith("-incr") || text.getName().endsWith("-to")) continue;
					String strName = text.getName();
					String strValue = (text.getText() != null ? text.getText().toString() : "");
					if(text.getName().endsWith("-from")) {
						strName = strName.replace("-from", "");
						if(iter.hasNext()) {
							text = ((JTextField) iter.next());
							strValue += "," + (text.getText() != null ? text.getText().toString() : "");
						}
						if(iter.hasNext()) {
							text = ((JTextField) iter.next());
							strValue += "," + (text.getText() != null ? text.getText().toString() : "");
						}
					}
					updateConfigParam(strName, strValue);
				}
			}
			else if(comp instanceof JComboBox) {
				JComboBox combo = (JComboBox)comp;
				if(combo.getName() != null) {
					if(combo.getName().endsWith("-incr") || combo.getName().endsWith("-to")) continue;
					
					String strName = combo.getName();
					String strValue = (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
					if(combo.getName().endsWith("-from")) {
						strName = strName.replace("-from", "");
						//System.out.println(combo.getName());
						if(iter.hasNext()) {
							combo = ((JComboBox) iter.next());
							//System.out.println(combo.getName());
							strValue += "," + (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
						}
						if(iter.hasNext()) {
							combo = ((JComboBox) iter.next());
							//System.out.println(combo.getName());
							strValue += "," + (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
						}
					}
					updateConfigParam(strName, strValue);
				}
			}
		}
		
		/*int iIndex = 0;
		for(int iLoop = 0; iLoop < paraComponents.size(); iLoop++)
		{
			Component comp = paraComponents.get(iLoop);
			if(comp instanceof JTextField) {
				JTextField text = (JTextField)comp;
				if(text.getName() != null) {
					if(text.getName().endsWith("-incr") || text.getName().endsWith("-to")) continue;
					
					String strValue = text.getText();
					if(text.getName().endsWith("-from")) {
						if(paraComponents.size() > (iIndex + 1) 
								&& paraComponents.get(iIndex + 1) instanceof JTextField)
							strValue += "," + ((JTextField)paraComponents.get(iIndex + 1)).getText();
						if(paraComponents.size() > (iIndex + 2) 
								&& paraComponents.get(iIndex + 2) instanceof JTextField)
							strValue += "," + ((JTextField)paraComponents.get(iIndex + 2)).getText();
					}
					updateConfigParam(text.getName(), strValue);
				}
			}
			else if(comp instanceof JComboBox) {
				JComboBox combo = (JComboBox)comp;
				if(combo.getName() != null) {
					if(combo.getName().endsWith("-incr") || combo.getName().endsWith("-to")) continue;
					
					String strValue = (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
					if(combo.getName().endsWith("-from")) {
						System.out.println(combo.getName());
						if(paraComponents.size() > (iIndex + 1) 
								&& paraComponents.get(iIndex + 1) instanceof JComboBox) {
							System.out.println(combo.getName());
							combo = ((JComboBox)paraComponents.get(iIndex + 1));
							strValue += "," + (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
							System.out.println(combo.getName());
						}
						if(paraComponents.size() > (iIndex + 2) 
								&& paraComponents.get(iIndex + 2) instanceof JComboBox){
							System.out.println(combo.getName());
							combo = ((JComboBox)paraComponents.get(iIndex + 2));
							System.out.println(combo.getName());
							strValue += "," + (combo.getSelectedItem() != null ? combo.getSelectedItem().toString() : "");
						}
					}
					updateConfigParam(combo.getName().replace("-from", ""), strValue);
				}
			}
			iIndex++;
		}*/
	}
	
	public void updateConfigParam(String strParamName, String strParamValue)
	{
		if(strParamName == null) return;
		configValues.put(strParamName, strParamValue);
	}
	
	public String toString()
	{
		String strText = "";
		for(String propName : configValues.keySet())
		{
			if(propName != null && propName.equalsIgnoreCase("windowcolor")) continue;
			String strValue = configValues.get(propName);
			if(strValue != null && strValue.split(",").length == 3)
			{
				String[] strParts = strValue.split(",");
				strValue = "[" + strParts[0] + " > " + (strParts[1].startsWith("-") ? "" : "+") + strParts[1] + " > " + strParts[2] + "]";
			}
			strValue = Utilities.trim(strValue, ',');
			strText += propName + " = " + strValue + "\n";
		}
		return strText;
	}

	public HashMap<String, String> getConfigValues() {
		return configValues;
	}

	public void setConfigValues(HashMap<String, String> configValues) {
		this.configValues = configValues;
	}
}