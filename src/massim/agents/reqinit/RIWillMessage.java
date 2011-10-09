package massim.agents.reqinit;

import massim.Message;
import massim.RowCol;

public class RIWillMessage implements Message {


	String mainDelim = ",";
	String cmdDelim = ":";
	
	int sender;
	int receiver;	
	int benefit;
	int row;
	int col;
	
	static final String protocol = "helpreq";
	static final String cmd = "will";
	String stringMsg; 
	
	public RIWillMessage(int sender, int receiver) {
		this.sender = sender;		
		this.receiver = receiver;
	}
	
	public RIWillMessage(String msg) {
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
	
		stringMsg = msg;
		String[] list = msg.split(mainDelim);
		if (list.length != 4)
			throw new Exception("Erro parsing the message");
			
		sender = Integer.parseInt(list[0]);
		receiver = Integer.parseInt(list[1]);
				
	}
	
	@Override
	public String toString() {
		pack();
		return stringMsg;
	}

	
	private void pack() {
		String cmdstr = "";
		cmdstr += cmd;
	
		
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