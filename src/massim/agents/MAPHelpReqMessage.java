package massim.agents;

import massim.Message;
import massim.RowCol;

public class MAPHelpReqMessage implements Message {


	String mainDelim = ",";
	String cmdDelim = ":";
	
	int sender;
	int receiver = -1; // -1 for broadcast	
	int benefit;
	int row;
	int col;
	
	static String protocol = "map";
	static String cmd = "helpreq";
	String msg; 
	
	public MAPHelpReqMessage(int sender, int benefit, RowCol cell) {
		this.sender = sender;		
		this.benefit = benefit;
		this.row = cell.row;
		this.col = cell.col;
		System.out.println(")))))))))))ag"+ sender+"created a req msg");
	}
	
	public MAPHelpReqMessage(String msg) {
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
		
		
		benefit = Integer.parseInt(args[1]);
		row = Integer.parseInt(args[2]);
		col = Integer.parseInt(args[3]);
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
		cmd += Integer.toString(benefit);
		cmd += cmdDelim;
		cmd += Integer.toString(row);
		cmd += cmdDelim;
		cmd += Integer.toString(col);
		
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
