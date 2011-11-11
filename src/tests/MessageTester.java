package tests;

import massim.Message;

public class MessageTester {

	final static int MAPHelpReqMsg = 1;
	final static int MAPBidMsg = 2;
	final static int MAPAckMsg = 3;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Message msg1 = new Message(1,4,MAPHelpReqMsg);
		msg1.putTuple("teamBenefit", 240);
		msg1.putTuple("cellRow", 12);
		msg1.putTuple("cellCol", 5);
		
		String comm = msg1.toString();
		System.out.println(comm);
		
		Message msg2 = new Message(comm);
		
		System.out.println(msg2.getIntValue("teamBenefit"));
	}

}
