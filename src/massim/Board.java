package massim;

import java.util.Random;

/**
 * The class to hold the board settings
 * 
 * @author Omid Alemi
 * @version 1.1
 */
public class Board {
	private static Random rndBoardGen = new Random();

	private int[][] mainBoard;

	private final int rows;
	private final int cols;

	/**
	 * Constructor
	 * 
	 * @param r				The number of rows of the board
	 * @param c        		The number of columns of the board
	 */
	public Board(int r, int c) {
		rows = r;
		cols = c;
		mainBoard = new int[rows][cols];
	}

	
	/**
	 * Returns the number of rows of the board
	 * 
	 * @return 				The number of rows of the board in int
	 */
	public int rows() {
		return rows;
	}

	/**
	 * Returns the number of columns of the board
	 * 
	 * @return 				The number of columns of the board in int
	 */
	public int cols() {
		return cols;
	}

	/**
	 * Sets the board setting to the giving setting
	 * 
	 * @param initBoard		The input board setting to be the main board's 
	 * 						setting
	 */
	public void setBoard(int[][] inputBoard) {

	}

	/**
	 * Returns the board setting
	 * 
	 * @return 				2 dim array of int representing the board's 
	 * 						setting
	 */
	public int[][] getBoard() {
		return mainBoard;
	}

	/**
	 * Sets the value of one specific cell
	 * 
	 * @param row			The row# of the desired cell
	 * @param col       	The column# of the desired cell
	 * @param color     	The new color for the desired cell
	 */
	public void setCell(int row, int col, int color) {

	}

	/**
	 * Creates a board with randomly filled values (colors).
	 * 
	 * Static method; 
	 * 
	 * @return 				The instance of the newly randomly generated board
	 */
	public static Board randomBoard(int rows, int cols,int[] colorRange) {
		Board b = new Board(rows, cols);

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				b.mainBoard[i][j] = colorRange[rndBoardGen.nextInt(colorRange.length)];
		return b;
	}

	/**
	 * Adds random values (disturbance) to the cells of the board. 
	 * 
	 * Each cell on the board may be changed based on the probability defined by
	 * disturbanecLevel.
	 * 
	 * @param disturbanceLevel		The level of disturbance, between 0 and 1.0
	 */
	public void disturb(double disturbanceLevel) {

		Random rndColor = new Random();
		Random rndChange = new Random();

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				if (rndChange.nextDouble() < disturbanceLevel)
					mainBoard[i][j] = SimulationEngine.colorRange[rndColor
							.nextInt(SimulationEngine.numOfColors)];
	}

	/**
	 * Converts the current setting of the board into a string.
	 * 
	 * For debugging purposes
	 * 
	 * @return 			The string representing the current setting of the board
	 */
	@Override
	public String toString() {
		String out = "";

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++)
				out += mainBoard[i][j] + " ";
			out += "\n";
		}

		return out;
	}

	/**
	 * Prints the costs associated with each square of the board based on the
	 * given action costs set into a string.
	 * 
	 * Used for debugging purposes.
	 * 
	 * @param actionCosts		The action costs set of an agent
	 * @return					The string representation of the board; 
	 * 							displaying the costs of each cell
	 */
	public String boardCostsToString(int actionCosts[]) {
		String out = "";
		int[] colorRange = SimulationEngine.colorRange;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int index = 0;
				for (int k = 0; k < colorRange.length; k++) {
					int color = mainBoard[i][j];
					if (color == colorRange[k])
						index = k;
				}
				out += actionCosts[index] + "\t";
				;

			}
			out += "\n";
		}

		return out;
	}

}
