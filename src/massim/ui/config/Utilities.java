package massim.ui.config;

import java.util.regex.Pattern;

public class Utilities {
	public static String trim(String strText, char character)
	{
		if(strText == null || strText.length() == 0)
			return strText;
		
		strText = strText.replaceAll("^[" + Pattern.quote(character + "") + "]+", "");
		strText = strText.replaceAll("[" + Pattern.quote(character + "") + "]+$", "");
		return strText;
	}
	
	public static Integer getInteger(String strText, Integer val)
	{
		if(strText == null || strText.length() == 0) return val;
		
		try {
			return Integer.parseInt(strText);
		} catch (NumberFormatException ex) { }
		return val;
	}
	
	public static Double getDouble(String strText, Double val)
	{
		if(strText == null || strText.length() == 0) return val;
		
		try {
			return Double.parseDouble(strText);
		} catch (NumberFormatException ex) { }
		return val;
	}
}
