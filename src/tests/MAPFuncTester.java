package tests;

import massim.Agent;
import massim.Path;
import massim.RowCol;

public class MAPFuncTester {

	static Path path;
	static RowCol pos;
	static int[] actionCost = {20,30,50,60,100,200};
	static double disturbanceLevel = 0.0;
	static int resourcePoints = 400;
	
	public static void main(String[] args) {
		Agent.achievementReward = 1001;
		Agent.cellReward = 50;
		Agent.helpOverhead = 102;
		
		path = new Path();
		path.addPathPoint(new RowCol(0,0));
		path.addPathPoint(new RowCol(0,1));
		path.addPathPoint(new RowCol(0,2));
		path.addPathPoint(new RowCol(0,3));
		path.addPathPoint(new RowCol(0,4));
	
		pos = new RowCol(0,0);

		
		System.out.println("tl= "+calcTeamLoss(100));
		
		//	projectPoints(10,new RowCol(0,0));		
//		System.out.println(remainingPath(pos));
		//System.out.println(wellbeing());
		
		

	}

	private static int calcTeamLoss(int helpActCost)
	{
		//decResourcePoints(Agent.calculationCost);
		
		int withHelpRewards = 
			projectPoints(resourcePoints-helpActCost, pos);
						
		int noHelpRewards =
			projectPoints(resourcePoints,pos);
						
		int withHelpRemPathLength = 
			path.getNumPoints() - 
			findFinalPos(resourcePoints-helpActCost, pos) -
			1;
		System.out.println("with help rem path l= " +withHelpRemPathLength+" imp= "+importance(withHelpRemPathLength));	
					
		int noHelpRemPathLength = 
			path.getNumPoints() - 
			findFinalPos(resourcePoints, pos) -
			1;
		System.out.println("no help rem path l= " +noHelpRemPathLength+" imp= "+importance(noHelpRemPathLength));
		
		return  
			(noHelpRewards - withHelpRewards) *
			(1 + 
			(importance(noHelpRemPathLength)-importance(withHelpRemPathLength)) *
			(withHelpRemPathLength-noHelpRemPathLength)) +
			Agent.helpOverhead;
			
							
	}
	private static int calcTeamBenefit(RowCol skipCell) {
		
		//decResourcePoints(Agent.calculationCost);
		
		int withHelpRewards = 
			projectPoints(resourcePoints, skipCell) + 
			Agent.cellReward; /* double check cellReward */
		System.out.println("with help rewards " +withHelpRewards);
		
		int noHelpRewards = 
			projectPoints(resourcePoints, pos);
		System.out.println("no help rewards" +noHelpRewards);
		
		int withHelpRemPathLength = 
			path.getNumPoints() - 
			findFinalPos(resourcePoints,skipCell) -
			1 ;
		System.out.println("with help rem path l= " +withHelpRemPathLength+" imp= "+importance(withHelpRemPathLength));
		
		int noHelpRemPathLength = 
			path.getNumPoints() - 
			findFinalPos(resourcePoints,pos) -
			1;
		System.out.println("no help rem path l= " +noHelpRemPathLength+" imp= "+importance(noHelpRemPathLength));
		
		return 
			(withHelpRewards-noHelpRewards) *
			(1+
			(importance(withHelpRemPathLength)-importance(noHelpRemPathLength)) *
			(noHelpRemPathLength-withHelpRemPathLength));
	}
	private static int projectPoints(int remainingResourcePoints, RowCol startPos) {
		
		if (path.getEndPoint().equals(startPos))
		{
			System.out.println("first if!");
			return calcRewardPoints(remainingResourcePoints, startPos);
		}
		RowCol iCell = path.getNextPoint(startPos);		
		int iIndex = path.getIndexOf(iCell);
		
		while (iIndex < path.getNumPoints())
		{							
			int cost = getCellCost(iCell);
			if (cost <= remainingResourcePoints)
			{
				System.out.println("if");
				remainingResourcePoints-=cost;
				iCell=path.getNextPoint(iCell);
				iIndex++;
			}
			else
			{
				System.out.println("else!");
				iCell = path.getNthPoint(iIndex-1);		
				break;
			}
		}		
		System.out.println(iCell);
		System.out.println(remainingResourcePoints);
		return calcRewardPoints(remainingResourcePoints, iCell);
	}

	private static  int calcRewardPoints(int resources, RowCol position) {
		int r = 0;
		
		if (position.equals(path.getEndPoint()))
			r= Agent.achievementReward + resources;
		else
			r=(path.getIndexOf(position)) * Agent.cellReward;
			/* uses the index of position, starting from 0;
		     * as if the agent has not moved at all, there should
		     * be no reward points 
		     */
			return r;
	}
	
	static int projectPoints2(int remainingResourcePoints, RowCol startPos) {
		
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
		remainingLength ++; /* TODO: double check */
		if (remainingLength != 0)
			return 20/remainingLength;
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
	
	
	
	private static int findFinalPos(int remainingResourcePoints, RowCol startPos) {
		
		if (path.getEndPoint().equals(startPos))
			return path.getIndexOf(startPos);
			
		RowCol iCell = path.getNextPoint(startPos);		
		int iIndex = path.getIndexOf(iCell);
		
		while (iIndex < path.getNumPoints())
		{
			int cost = getCellCost(iCell);
			if (cost <= remainingResourcePoints)
			{
				System.out.println("R: if");
				remainingResourcePoints-=cost;
				iCell=path.getNextPoint(iCell);
				iIndex++;
			}
			else
			{
				System.out.println("R: else");
				iCell = path.getNthPoint(iIndex-1);		
				break;
			}
		}		
		
		return path.getIndexOf(iCell);
	}
	
}
