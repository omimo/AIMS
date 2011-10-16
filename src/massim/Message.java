package massim;

/**
 * The message interface. 
 * All the protocol specific messages should implement this interface.
 * @author Omid Alemi
 * @version 1.0
 */
public interface Message {
	
	/**
	 * Encodes the contents of the message into a string.
	 * This string then can be sent to the communication channels
	 * 
	 * @return			The string representation of the message
	 */
	public String toString();
	
	/**
	 * Parses the given string into proper message object
	 * Throws exception if the message is not welformed.
	 * 
	 * @param msg				The message in string
	 * @throws Exception		If the given string does not have 
	 * 							the desired format, method throws
	 * 							an Exception	
	 */
	public void parse(String msg) throws Exception;
		
}
