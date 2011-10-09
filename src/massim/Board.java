package massim;

import java.util.ArrayList;
import java.util.Random;

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
		mainBoard = new int[rows][cols];
	}
	
	/**
	 * Constructor 2: get the board setting and creating an exact copy
	 * @param board The 2dim array, representing the board's initial
	 *        setting
	 */
	public Board(Board board) {
		rows = board.rows();
		cols = board.cols();
		mainBoard = new int[rows][cols];
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				this.mainBoard[i][j] = board.mainBoard[i][j];
	}	
	
	/**
	 * 
	 * @return The number of rows of the board
	 */
	public int rows() {
		return rows;
	}
	
	/**
	 * 
	 * @return The number of columns of the board
	 */
	public int cols() {
		return cols;
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
	//public static Board randomBoard(int rows, int cols, int startValue, int range ) {
	public static Board randomBoard(int rows, int cols ) {
		Board b = new Board(rows, cols);
		
		Random rnd = new Random();
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				b.mainBoard[i][j] = Environment.colorRange[rnd.nextInt(Environment.numOfColors)];
				//b.mainBoard[i][j] = startValue + rnd.nextInt(range);
		
		return b;
	}
	
	/**
	 * Adds random values (disturbance) to the cells of the board. 
	 * Each cell on the board may be changed based on the probability
	 * defined by disturbanecLevel  
	 * @param disturbanceLevel The level of disturbance, between 0 and 1.0 
	 */
	public void distrub(double disturbanceLevel) {
		
		Random rndColor = new Random();
		Random rndChange = new Random();
		
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
				if (rndChange.nextDouble() < disturbanceLevel)
					mainBoard[i][j] = Environment.colorRange[rndColor.nextInt(Environment.numOfColors)];
					//mainBoard[i][j] = Environment.colorRange[Environment.numOfColors/2+rndColor.nextInt(Environment.numOfColors/2)];
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
	
	@Override
	public String toString() {
		String out = "";
		
		for (int i=0;i<rows;i++)
		{
			for (int j=0;j<cols;j++)
				out += mainBoard[i][j]+ " ";
			out +="\n";
		}
				
		return out;
	}
	
	public String boardCostsToString(int actionCosts[]) {
		String out = "";
		int [] colorRange = Environment.colorRange;
		
		for (int i=0;i<rows;i++)
		{
			for (int j=0;j<cols;j++)
				{
				int index = 0;
				for (int k=0;k<colorRange.length;k++)
				{
					int color = mainBoard[i][j];
					if (color == colorRange[k])
						index = k;			
				}
				out += actionCosts[index]+ "\t";;	
				
				}
			out +="\n";
		}
								
		return out;
	}
		
}
