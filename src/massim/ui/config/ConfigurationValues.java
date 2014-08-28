package massim.ui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConfigurationValues {
	
	private String key;
	
	@XmlElement(type = ConfigurationValue.class)
	private List<ConfigurationValue> configValues;
	
	@XmlElement(type = ConfigurationValues.class)
	private List<ConfigurationValues> configChildren;
	
	public ConfigurationValues()
	{
		configChildren = new ArrayList<ConfigurationValues>();
		configValues = new ArrayList<ConfigurationValue>();
	}
	
	public ConfigurationValues(String key)
	{
		this.key = key;
	}
	
	public void add(String childKey, ConfigurationValues child)
	{
		child.setKey(childKey);
		configChildren.add(child);
	}
	
	public void add(String propertyName, String value)
	{
		configValues.add(new ConfigurationValue(propertyName, value));
	}
	
	public List<ConfigurationValues> getConfigChildren() {
        return configChildren;
    }
	
	public List<ConfigurationValue> getConfigValues() {
		return configValues;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}