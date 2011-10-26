package tests;

import java.util.Random;

import massim.Board;
import massim.Path;
import massim.PolajnarPath;
import massim.PolajnarPath2;
import massim.RowCol;



public class PathTest {
	
	static int numOfColors = 6;
	public static int[] colorRange = {0, 1, 2, 3, 4, 5};
	static int[] actionCostsRange = {10, 40, 70, 100, 300, 400, 450,  500};
	
	/**
	 * Finds the lowest cost path among shortest paths of a rectangular board
	 * based on the Polajnar's algorithm.
	 * 
	 * NOTE: only the start and end points can only be at the diagonal corners
	 * of the board
	 * 
	 */
	public static Path findPath2(int[][] costs, int[] actionCosts, RowCol s, RowCol d) {
			                                  
		PolajnarPath2 pp = new PolajnarPath2();
		Path shortestPath = new Path(
				pp.findShortestPath(costs, s,d)
				);
		return shortestPath;
	}

	public static Path findPath1(int[][] costs, int[] actionCosts) {
        
		PolajnarPath pp = new PolajnarPath();
		Path shortestPath = new Path(
				pp.findShortestPath(costs, 10,	10));
		return shortestPath;
	}
	
	/**
	 * Calculates the costs associated to each square on the board.
	 * This method is used by the path finding algorithm.
	 * 
	 * @param board					The board setting
	 * @param actionCosts			The action costs set
	 * @return						The 2dim array; each entry represents 
	 * 								the cost associated with the square at
	 * 								the same position of the entry
	 */
	public static int[][] boardToCosts(int[][] board, int[] actionCosts) {
		int[][] costs = new int[board.length][board[0].length];

		for (int i = 0; i < costs.length; i++)
			for (int j = 0; j < costs[0].length; j++)
				costs[i][j] = actionCosts[board[i][j]];

		return costs;
	}
	
	public static void main(String[] args) {
	testAverageCost();
		
	}	
	
	public static void testAverageCost() {
		int cost =0;
		Random rnd = new Random();
		for (int run=0;run<1000;run++)
		{
			Board board = Board.randomBoard(10, 10,colorRange);			
			int[] actionCosts = new int[numOfColors];			
			
			
			for (int i=0;i<numOfColors;i++)
				actionCosts[i] = actionCostsRange[rnd.nextInt(actionCostsRange.length)];
			
			int[][] costs = boardToCosts(board.getBoard(), actionCosts);
			RowCol s = randomPos(10, 10);
			RowCol d = randomPos(10, 10);
			Path p = findPath2(costs, actionCosts,new RowCol(2,1),new RowCol(2,6));
						
			for (RowCol cell : p.pathPoints)
				cost+=costs[cell.row][cell.col];		
			System.out.println(s + " -> " + d);
			System.out.println(p);
		}
		
		System.out.println("average cost = "+cost/1000);
	}
	
	public static RowCol randomPos(int h, int w) {

		Random rnd = new Random();
		int row = rnd.nextInt(h);
		int col = rnd.nextInt(w);		
		return  new RowCol(row, col);
	}
	
}
