package massim;

/**
 * The message interface. 
 * All the protocol specific messages should implement this interface.
 * @author Omid Alemi
 * @version 1.0
 */
public interface Message {
	
	public String toString();
	
	public void parse(String msg) throws Exception;
	
	public void pack();
}
