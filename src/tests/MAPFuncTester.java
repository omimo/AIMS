package tests;

import massim.Path;
import massim.RowCol;

public class MAPFuncTester {

	static Path path;
	static RowCol pos;
	static int[] actionCost = {20,30,50,60,100,200};
	static double disturbanceLevel = 0.0;
	static int resourcePoints = 250;
	
	public static void main(String[] args) {
		
		path = new Path();
		path.addPathPoint(new RowCol(0,0));
		path.addPathPoint(new RowCol(0,1));
		path.addPathPoint(new RowCol(0,2));
		path.addPathPoint(new RowCol(0,3));
		path.addPathPoint(new RowCol(0,4));
		
		projectPoints(10,new RowCol(0,0));
		
		pos = new RowCol(0,4);
		
		System.out.println(remainingPath(pos));
		System.out.println(wellbeing());
		
		

	}

	static int projectPoints(int remainingResourcePoints, RowCol startPos) {
		
		RowCol iCell = path.getNextPoint(startPos);		
		int iIndex = path.getIndexOf(iCell);
		
		while (iIndex < path.getNumPoints())
		{
			
			System.out.println("iIndex = " + iIndex+" , iCell = "+iCell);
			
			int cost = getCellCost(iCell);
			if (cost <= remainingResourcePoints)
			{
				System.out.println("Can afford it!");
				remainingResourcePoints-=cost;
				iCell=path.getNextPoint(iCell);
				iIndex++;
			}
			else
			{
				System.out.println("Can not afford it!");
				iCell = path.getNthPoint(iIndex-1);
				System.out.println("FINAL: iIndex = " + iIndex+" , iCell = "+iCell+"  , resources = "+remainingResourcePoints);
				break;
			}
		}		
		
		System.out.println("FINAL: iIndex = " + iIndex+" , iCell = "+iCell+"  , resources = "+remainingResourcePoints);
		return 0;
	}

	static int getCellCost(RowCol cell) {
		return 50;
}
	
	private static double estimatedCost(Path p) {		
		int l = p.getNumPoints();
		double sigma = 1 - disturbanceLevel;
		double eCost = 0.0;		
		if (Math.abs(sigma-1) < 0.000001)
		{
			for (int k=0;k<l;k++)
				eCost += getCellCost(p.getNthPoint(k));			
		}
		else
		{
			double m = getAverage(actionCost); /*TODO: check this! */				 
			eCost = (l - (1-Math.pow(sigma, l))/(1-sigma)) * m;		
			for (int k=0;k<l;k++)
				eCost += Math.pow(sigma, k) * getCellCost(p.getNthPoint(k));
		}
		return eCost;
	}
	
	private static double wellbeing () {
		//RowCol nextCell = path.getNextPoint(pos);
		double eCost = estimatedCost(remainingPath(pos));
		if (eCost == 0)
			return resourcePoints;
		else
			return resourcePoints/eCost;
	}
	
	/**
	 * Finds the remaining path from the given cell.
	 * 
	 * The path DOES NOT include the given cell and the starting cell 
	 * of the remaining path would be the next cell.
	 * 
	 * @param from					The cell the remaining path would be
	 * 								generated from.
	 * @return						The remaining path.
	 */
	private static Path remainingPath(RowCol from) {
		Path rp = new Path(path);
		
		while (!rp.getStartPoint().equals(from))
			rp = rp.tail();
		
		return rp.tail();
	}
	
	/**
	 * The importance function.
	 * 
	 * Maps the remaining distance to the goal into 
	 * 
	 * @param remainingLength
	 * @return
	 */
	private static int importance(int remainingLength) {
		if (remainingLength != 0)
			return 10/remainingLength;
		else
			return 0;
	}
	
	/**
	 * Calculates the average of the given integer array.
	 * 
	 * @return						The average.
	 */
	private static double getAverage(int[] array) {
		int sum = 0;
		for (int i=0;i<array.length;i++)
			sum+=array[i];
		return (double)sum/array.length;
	}
}
