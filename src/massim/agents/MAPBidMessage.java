package massim.agents;

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
	String msg; 
	
	public MAPBidMessage(int sender, int receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;		
		System.out.println(")))))))))))ag"+ sender+"created a bid msg");
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
	
		this.msg = msg;
		String[] list = msg.split(mainDelim);
		if (list.length != 4)
			throw new Exception("Error in parsing the message");
			
		sender = Integer.parseInt(list[0]);
		receiver = Integer.parseInt(list[1]);
		protocol = list[2];
		cmd = list[3];
		
		String[] args = cmd.split(cmdDelim);				
		amount = Integer.parseInt(args[1]);		
	}
	
	@Override
	public String toString() {
		pack();
		return msg;
	}

	
	private void pack() {
		cmd = "";
		cmd += cmd;
		cmd += cmdDelim;
		cmd += Integer.toString(amount);
		
			
		msg = "";
		msg += Integer.toString(sender);
		msg += mainDelim;
		msg += Integer.toString(receiver);
		msg += mainDelim;
		msg += protocol;
		msg += mainDelim;
		msg += cmd;
		
	}
	
	

}
