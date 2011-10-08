package massim.agents;

import massim.Message;
import massim.RowCol;

public class MAPAckMessage implements Message {


	String mainDelim = ",";
	String cmdDelim = ":";
	
	int sender;
	int receiver;	
	int benefit;
	int row;
	int col;
	
	static String protocol = "map";
	static String cmd = "ack";
	String msg; 
	
	public MAPAckMessage(int sender, int receiver) {
		this.sender = sender;		
		this.receiver = receiver;
	}
	
	public MAPAckMessage(String msg) {
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
			throw new Exception("Erro parsing the message");
			
		sender = Integer.parseInt(list[0]);
		receiver = Integer.parseInt(list[1]);
		protocol = list[2];
		cmd = list[3];
		
		String[] args = cmd.split(cmdDelim);
		
		
		
	}
	
	@Override
	public String toString() {
		pack();
		return msg;
	}

	
	private void pack() {
		cmd = "";
		cmd += cmd;
	
		
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
