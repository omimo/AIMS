package massim;

import java.util.*;

public class Message {		
	private int sender;
	private int receiver;
	private int type;
	private HashMap<String,String> fields;
	
	//
	
	private String mainDelim = ",";
	private String fieldDelim = ":";
	
	public Message(int sender, int receiver, int type) {
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		fields = new HashMap<String,String>();
	}
	
	public Message(String inputMsg) {
		fields = new HashMap<String,String>();
		
		try {
			parse(inputMsg);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public void putTuple(String field, String value) {
		fields.put(field,value);
	}
	
	public void putTuple(String field, int value) {
		fields.put(field,Integer.toString(value));
	}
	
	public String getValue(String field) {
		//if (fields.containsKey(field))  /* commented for the moment */
			return fields.get(field);
		//else
		//	return "";
	}
	
	public int getIntValue(String field) {
		//if (fields.containsKey(field))
			return Integer.parseInt(fields.get(field));
		//else
		//	return -1;
	}
	
	///
	
	public String toString() {
		String msg = "";
		msg += Integer.toString(sender);
		msg += mainDelim;
		msg += Integer.toString(receiver);
		msg += mainDelim;		
		msg += Integer.toString(type);
				
		for (String field : fields.keySet())
		{		
			msg += mainDelim;			
			msg += field;
			msg += fieldDelim;
			msg += fields.get(field);			
		}		
		return msg;
	}
	
	public void parse(String msg) throws Exception {
		try {
			String[] list = msg.split(mainDelim);
			if (list.length < 3)
				throw new Exception ("Not enough arguments in the message.");
			
			sender = Integer.parseInt(list[0]);
			receiver = Integer.parseInt(list[1]);			
			type = Integer.parseInt(list[2]);			
			
			for (int i=3;i<list.length;i++)
			{
				String[] fieldTuple = list[i].split(fieldDelim);			
				fields.put(fieldTuple[0], fieldTuple[1]);
			}
		} 
		catch(Exception e) 
		{
			throw new Exception("Invalid message string.");
		}
	}
	
	public boolean isOfType(int t) {
		return (type == t);
	}
	
	public int sender() {
		return sender;
	}
	
	
}
