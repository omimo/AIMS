package massim.ui.config;

public class ConfigurationValue
{
	private String propertyName;
	private String value;
	
	public ConfigurationValue()
	{
		this.propertyName = "";
		this.value = "";
	}
	public ConfigurationValue(String propertyName, String value)
	{
		this.propertyName = propertyName;
		this.value = value;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
