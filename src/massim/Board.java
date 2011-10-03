package massim;

/**
 * The class to hold the board settings
 * @author Omid Alemi
 *
 */
public class Board {

	private int[][] mainBoard;
		
	private final int rows;
	private final int cols;
	 
	
	/**
	 * Constructor 1: just with the size
	 * 
	 * @param r The number of rows of the board
	 * @param c The number of columns of the board 
	 */
	public Board(int r, int c) { 
		rows = r;
		cols = c;
	}
	
	/**
	 * Constructor 2: get the board setting
	 * @param board The 2dim array, representing the board's initial
	 *        setting
	 */
	public Board(int[][] board) {
		rows = board.length;
		cols = board[0].length;
		// board -> mainBoard
	}	
	
	/**
	 * Sets the board setting to the inputBoard
	 * @param initBoard The input board setting to be the main board setting
	 */
	public void setBoard(int[][] inputBoard) {
		
	}
	
	/**
	 * Returns the board setting
	 * @return 2 dim array of int representing the board's setting
	 */
	public int[][] getBoard(){
		return mainBoard;
	}
	
	
	/**
	 * Sets the value of one specific cell
	 * @param row
	 * @param col
	 * @param color
	 */
	public void  setCell(int row, int col, int color) {
		
	}
	
	/**
	 * Returns a board with randomly filled values (colors).
	 * @return A new instance of the Board class
	 */
	public static Board randomBoard() {
		return null;
	}
	
	/**
	 * Adds random values (disturbance) to the cells of the board. 
	 * Each cell on the board may be changed based on the probability
	 * defined by disturbanecLevel  
	 * @param disturbanceLevel The level of disturbance, between 0 and 1.0 
	 */
	public void distrub(double disturbanceLevel) {
		
	}
	
	/**
	 * The overridden clone() method.
	 * would be used to create a new copy of the current board's representation.
	 */
	@Override
	public Board clone() {
		// Creates a new instance of the Board class with the 
		// same internal representation 
		return null;
	}
}
