package tests.comm;


import massim.CommMedium;
import massim.Team;
/**
 * Test class for the communication medium (CommMedium.java)
 * 
 * @author Omid Alemi
 *
 */
public class CommTester {
	
	public static void main(String[] args) {
		
		Team.teamSize = 4;
		CommMedium commMedium = new CommMedium();
		String msg1 = "Hello";
		String msg2 = "2,3,map,bid,234";
		
		if (commMedium.isEmpty())
			System.out.println("THE COMM MED IS EMPTY!");
		
		System.out.println("The channels for Agent 3:");
		
		System.out.println(commMedium);
		
		System.out.println("Agent 2 sends a message to Agent 3");
		commMedium.send(2, 3, msg2);
		
		System.out.println("The channels for Agent 3:");
		System.out.println(commMedium);
		
		//
		System.out.println("Agent 1 broadcast a message");
		commMedium.broadcast(1, msg1);
				
		
		System.out.println("The channels for Agent 0:");
		System.out.println(commMedium);
		
		System.out.println("The channels for Agent 1:");
		System.out.println(commMedium);
		
		System.out.println("The channels for Agent 2:");
		System.out.println(commMedium);
		
		System.out.println("The channels for Agent 3:");
		System.out.println(commMedium);
		
		if (commMedium.isEmpty())
			System.out.println("THE COMM MED IS EMPTY!");
	}

	public static void printBuffer(String[] buffer) {		
		for (int i=0;i<buffer.length;i++)
		{
			System.out.print("Agent "+ i+ " : ");
			System.out.println(buffer[i]);
		}
	}
}
