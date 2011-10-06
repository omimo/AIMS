package tests;

import massim.Message;

/**
 * 
 * "sender,receiver,protocol,cmd:arg1:arg2:..."
 * 
 * @author Omid Alemi
 *
 */
public class DummyHelpReqMessage implements Message {

	String mainDelim = ",";
	String cmdDelim = ":";
	
	int sender;
	int receiver; // -1 for broadcast	
	int amount = 0;
	String protocol = "dummy";
	String cmd = "";
	String msg; 
	
	public DummyHelpReqMessage(int sender, int receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
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
		
		amount = Integer.parseInt(args[1]);		
	}
	
	@Override
	public String toString() {
		return msg;
	}

	@Override
	public void pack() {
		cmd = "";
		cmd.concat("helpme");
		cmd.concat(cmdDelim);
		cmd.concat(Integer.toString(amount));
			
		msg = "";
		msg.concat(Integer.toString(sender));
		msg.concat(mainDelim);
		msg.concat(Integer.toString(receiver));
		msg.concat(mainDelim);
		msg.concat(protocol);
		msg.concat(mainDelim);
		msg.concat(cmd);
		
	}
	
	

}
