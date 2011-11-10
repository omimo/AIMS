package tests;

import massim.Path;
import massim.RowCol;

public class MAPFuncTester {

	static Path path;
	static RowCol pos;
	static int[] actionCost = {20,30,50,60,100,200};
	
	public static void main(String[] args) {
		
		path = new Path();
		path.addPathPoint(new RowCol(0,0));
		path.addPathPoint(new RowCol(0,1));
		path.addPathPoint(new RowCol(0,2));
		path.addPathPoint(new RowCol(0,3));
		path.addPathPoint(new RowCol(0,4));
		
		projectPoints(10,new RowCol(0,0));

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
}
