package massim.agents.classicmap;

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
	
	static final String protocol = "map";
	static final String cmd = "helpreq";
	String stringMsg; 
	
	public MAPHelpReqMessage(int sender, int benefit, RowCol cell) {
		this.sender = sender;		
		this.benefit = benefit;
		this.row = cell.row;
		this.col = cell.col;
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
			
		String[] list = msg.split(mainDelim);
		if (list.length != 4)
			throw new Exception("Erro parsing the message");
			
		sender = Integer.parseInt(list[0]);
		receiver = Integer.parseInt(list[1]);
		//protocol = list[2];
		String cmdstr = list[3];
		
		String[] args = cmdstr.split(cmdDelim);
		
		
		benefit = Integer.parseInt(args[1]);
		row = Integer.parseInt(args[2]);
		col = Integer.parseInt(args[3]);
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
		cmdstr += Integer.toString(benefit);
		cmdstr += cmdDelim;
		cmdstr += Integer.toString(row);
		cmdstr += cmdDelim;
		cmdstr += Integer.toString(col);
		
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
