package massim.agents.classicmap;

import massim.Message;

/**
 * NOTE: These class should extend another class later
 * 
 * @author Omid Alemi
 *
 */
public class MAPBidMessage implements Message { 


	String mainDelim = ",";
	String cmdDelim = ":";
	
	int sender;
	int receiver; 
	int amount;	
	
	static String protocol = "map";
	static String cmd = "bid";
	String stringMsg; 
	
	public MAPBidMessage(int sender, int receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;				
	}
	
	public MAPBidMessage(String msg) {
		try {
			parse(msg);
		} catch (Exception e) {
			System.err.println("Error in parsing the message");
		}
	}
	
	public static boolean isInstanceOf(String s) {
		return s.contains(cmd);
	}
	
	@Override
	public void parse(String msg) throws Exception {
	
		this.stringMsg = msg;
		String[] list = msg.split(mainDelim);
		if (list.length != 4)
			throw new Exception("Error in parsing the message");
			
		sender = Integer.parseInt(list[0]);
		receiver = Integer.parseInt(list[1]);
		protocol = list[2];
		String cmdstr = list[3];
		
		String[] args = cmdstr.split(cmdDelim);				
		amount = Integer.parseInt(args[1]);		
	}
	
	@Override
	public String toString() {
		pack();
		return stringMsg;
	}

	
	private void pack() {
		String cmdstr = "";
		cmdstr += cmd;
		cmdstr += cmdDelim;
		cmdstr += Integer.toString(amount);
		
			
		stringMsg = "";
		stringMsg += Integer.toString(sender);
		stringMsg += mainDelim;
		stringMsg += Integer.toString(receiver);
		stringMsg += mainDelim;
		stringMsg += protocol;
		stringMsg += mainDelim;
		stringMsg += cmdstr;
		
	}
	
	

}
