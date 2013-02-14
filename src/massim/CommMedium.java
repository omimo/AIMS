package massim;

/**
 * CommMedium.java
 * Responsible for all the communications within a team of 
 * agents
 *
 * @author Omid Alemi
 * @version 1.1 2011/10/07
 */
public class CommMedium {

	
	String[][] channels;
	int numOfChannels;
	/**
	 * The default constructor
	 * 
	 */
	public CommMedium(int n) {

		numOfChannels = n;
		// Initializing all the channels
		channels = new String[numOfChannels][numOfChannels];
		clear();
	}
	
	/**
	 * Sends a message.
	 * 
	 * Puts the msg into the unidirectional channel between the sender
	 * and the receiver.
	 * 
	 * @param sender 				The sender agent's id
	 * @param receiver 				The receiver agent's id
	 * @param msg 					The message
	 */
	public void send(int sender, int receiver, String msg) {	

		if (receiver != sender)
			channels[sender][receiver] = msg;
	}
	
	/**
	 * Broadcasts a message.
	 * 
	 * Puts the msg into all the unidirectional channels starting from 
	 * the sender.
	 * 
	 * @param sender 				The sender agent's id
	 * @param msg 					The message
	 */
	public void broadcast(int sender, String msg) {
		
		for (int i=0;i<numOfChannels;i++)		
			if (i!=sender)
				channels[sender][i] = msg;		
	}

	
	/**
	 * Receives the next message.
	 *  
	 * Returns the next available message in the unidirectional channels
	 * ending to the receiver.
	 * 
	 * Returns an empty message if there is no message left on the 
	 * channels.
	 * 
	 * @param receiver				The id of the receiver agent
	 * @return						The message/empty string 
	 */
	public String receive(int receiver) {
		
		String out="";
		
		for(int i=0;i<channels.length;i++)
		{			
			if (!channels[i][receiver].isEmpty())
			{
				out = channels[i][receiver];
				channels[i][receiver] = "";
				return out;
			}
		}
		return out;
	}
	
	/**
	 * Checks if all the unidirectional channels are empty.
	 * 
	 * @return 					true if all the channels are empty.
	 * 							/ false otherwise.
	 */
	public boolean allChannelsEmpty() {
		
		for (int i=0;i<numOfChannels;i++)
			for (int j=0;j<paramI("Team.teamSize");j++)
				if (!channels[i][j].isEmpty())
					return false;
		return true;
	}
	
	/**
	 * Clears all the channels
	 */
	public void clear() {
		for (int i=0;i<numOfChannels;i++)
			for (int j=0;j<numOfChannels;j++)
				channels[i][j]="";
	}
	
	/**
	 * Generates a string representation of all the communication channels
	 * and their values.
	 * 
	 * Used for the debugging purposes
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i=0;i<channels[0].length;i++)
		{
			s += "[Agent "+ i+ "'s incoming channels: ]\n";
						
			for (int j=0;j<channels.length;j++)
			{
				if (i!=j)
				{
					s += "Agent "+ j+ " : ";
					s += channels[j][i];
					s += "\n";
				}
			}
		}				
		return s;
	}

	/*
	 * Returns the integer parameter from the parameters list
	 */
	protected int paramI(String p) {
		return SimulationEngine.pList.paramI(p);
	}
	
	/*
	 * Returns the double parameter from the parameters list
	 */
	protected double paramD(String p) {
		return SimulationEngine.pList.paramD(p);
	}
}
