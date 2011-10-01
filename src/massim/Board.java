package massim;

public class Board {

	private int[][] mainBoard;
	private int[][] shadowBoard;  
	// If the agents are not changing anything on the board but just their positions, do we still
	// need the shadow copy of the board?
	
	private int rows;
	private int cols;
	 
	
	public Board() {
		
	}
	
	public void init(int[][] initBoard) {
		
	}
	
	public int[][] getBoard(){
		return mainBoard;
	}
	
	public void updateBoard() {
		// mainBoard <- shadowBoard
	}
}
