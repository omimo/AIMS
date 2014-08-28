package massim.ui.config;

public class ConfigProperty {
	protected String name;
	protected ValueType valueType;
	protected DataType dataType;
	protected InputType inputType;
	protected String[] dataItems;
	protected boolean isRange;
	protected String[] toDataItems;
	protected String[] incrDataItems;
	protected String description;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	public void setDataItems(String[] dataItems) {
		this.dataItems = dataItems;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}

	public void setToDataItems(String[] toDataItems) {
		this.toDataItems = toDataItems;
	}

	public void setIncrDataItems(String[] incrDataItems) {
		this.incrDataItems = incrDataItems;
	}

	public String getName() {
		return name;
	}

	public DataType getDataType() {
		return dataType;
	}
	
	public Class getDataTypeClass() {
		switch(dataType)
		{
			case Integer:
				return Integer.class;
			case Decimal:
				return Double.class;
			case Text:
				return String.class;
			case LargeNumber:
				return Long.class;
		}
		return String.class;
	}

	public InputType getInputType() {
		return inputType;
	}

	public String[] getDataItems() {
		return dataItems;
	}

	public String getDescription() {
		return description;
	}
	
	public ValueType getValueType() {
		return valueType;
	}
	
	public boolean isRange() {
		return isRange;
	}

	public String[] getToDataItems() {
		return toDataItems;
	}

	public String[] getIncrDataItems() {
		return incrDataItems;
	}

	public ConfigProperty()
	{
		this.name = "";
		this.valueType = ValueType.Single;
		this.dataType = DataType.Decimal;
		this.inputType = InputType.FreeText;
		this.dataItems = null;
		this.isRange = false;
		this.toDataItems = null;
		this.incrDataItems = null;
		this.description = "";
	}
	
	public ConfigProperty(String name, ValueType valueType, DataType dataType, InputType inputType, String[] dataItems, boolean isRange, String[] toDataItems, String[] incrDataItems, String mDescription) throws Exception
	{
		if(isRange && dataType == DataType.Text)
		{
			throw new Exception("Range does not apply to text dataType.");
		}
		else if(isRange && valueType == ValueType.Multiple)
		{
			throw new Exception("Range does not apply to multile valueType.");
		}
		else if(inputType == InputType.FixedChoices && (dataItems == null || dataItems.length == 0))
		{
			throw new Exception("In case of FixesChoices input, atleast one dataitem should exist.");
		}
		
		this.name = name;
		this.valueType = valueType;
		this.dataType = dataType;
		this.inputType = inputType;
		this.dataItems = dataItems;
		this.isRange = isRange;
		this.toDataItems = toDataItems;
		this.incrDataItems = incrDataItems;
		this.description = mDescription;
	}

	public String toString()
	{
		String strText = String.format("[type %s | name %s | data type %s | input type %s | description  %s |", getClass(), name, dataType, inputType, "", description);
		if(dataItems != null)
		{
			for(String s : dataItems)
			{
				strText += s + ",";
			}
		}
		strText += " range " + isRange + "]";
		return strText;
	}
	
	public enum ValueType
	{
		Single,
		Multiple
	}
	
	public enum DataType
	{
		Integer,
		Text,
		Decimal,
		LargeNumber
	}
	
	public enum InputType
	{
		FreeText,
		FixedChoices,
		OptionalDropDown
	}
}