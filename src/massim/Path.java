package massim;

import java.util.*;

/**
 * The class to represent a path
 * @author Omid Alemi
 * @version 1.0
 */
public class Path implements Comparable {

	private ArrayList<RowCol> pathPoints = new ArrayList<RowCol>();
	
	public Path() {
		
	}
	
	public Path(Vector<RowCol> path){
		pathPoints.addAll(path);
	}
	
	public Path(Path p) {
		LinkedList<RowCol> points = p.getPoints();
		
		for (RowCol l:points)
			pathPoints.add(new RowCol(l.row,l.col));			
	}
	
	public void addPathPoint(RowCol newPoint) {
		try {
			if (!pathPoints.isEmpty() && !RowCol.areNeighbors(newPoint, pathPoints.get(pathPoints.size()-1))) {
				throw new Exception("MalformedPathException");
			}
			pathPoints.add(newPoint);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getNumPoints() {
		return pathPoints.size();
	}
	
	public RowCol getPoint(int n) {
		return pathPoints.get(n);
	}
	
	public RowCol getStartPoint() {
		return pathPoints.get(0);
	}
	
	public RowCol getEndPoint() {
		return pathPoints.get(pathPoints.size()-1);
	}
	
	public RowCol getNthPoint(int n) {
		return pathPoints.get(n);
	}

	public RowCol getNextPoint(RowCol currentPoint) {
		
		if (currentPoint.equals(getEndPoint()))
			return getEndPoint();
		
		for (int i=0;i<pathPoints.size();i++)
			if (pathPoints.get(i).equals(currentPoint))
				return pathPoints.get(i+1);
		
		return currentPoint;
	}
	
	public int getIndexOf(RowCol point) {
		
		for (int i=0;i<pathPoints.size();i++)
			if (pathPoints.get(i).equals(point))
				return i;
		return -1;
	}
	
	public LinkedList<RowCol> getPoints() {
		LinkedList<RowCol> list = new LinkedList<RowCol>();
		
		for (RowCol l : pathPoints) 
			list.add(l);
		
		return list;
	}
	
	public Path tail() {		
		Path tail = new Path(this);
		tail.pathPoints.remove(0);		
		return tail;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("Path:");
		
		for (int i=0;i<pathPoints.size();i++) 
		{
			RowCol l = pathPoints.get(i);
			sb.append(" ("+l.row+","+l.col+")");
		}
		
		sb.append("\n");
		
		return sb.toString();
		
	}
	
	public boolean equals(Path o) {
		Path path = o;
		
		if (path.getNumPoints() != getNumPoints())
			return false;
		
		for (int i=0;i<path.getNumPoints();i++) 
		{
			if ((path.getPoint(i).row != getPoint(i).row) || (path.getPoint(i).col != getPoint(i).col))
				return false;
		}
		
		return true;
	}
	
	public int hashCode() {
    	return toString().hashCode();
    }
	
	public int compareTo(Object o) {
		Path p = (Path)o;
		
		int weight1 = getNumPoints();
		int weight2 = p.getNumPoints();

//		int weight1 = totalPathCost(board, actionCosts);
//		int weight2 = p.getNumPoints();

		if (weight1 > weight2) 
			return 1;
		if (weight1 < weight2) 
			return -1;
		else 
			return 0;
				
	}
	
	public int totalPathCost(int[][] board, Map<Integer,Integer> actionCosts) {
		int cost = 0;
		for (RowCol p : pathPoints)
			cost += actionCosts.get(board[p.row][p.col]);
		
		return cost;
	}
	
	public boolean contains (RowCol loc)
	{
		for (RowCol l : pathPoints) 
		{
			if (l.row == loc.row && l.col == loc.col)
				return true;
		}
		return false;
	}

	public static Path getShortestPath2(RowCol start, RowCol end, int[][] board, int[] actionCosts) {
		Path path = new Path();
		
		int r1 = start.row;
		int c1 = start.col;
		
		int r2 = end.row;
		int c2 = end.col;
		
		int ri = r1;
		int ci = c1;
		
		for (ri=r1;ri<r2;ri++)		
			path.addPathPoint(new RowCol(ri,c1));
		
		for (ci=c1;ci<=c2;ci++)		
			path.addPathPoint(new RowCol(r2,ci));				
		
		return path;
	}
	
	
	public static ArrayList<Path> getShortestPaths(RowCol start, RowCol end, int[][] board, int[] actionCosts, int maxPathNum) {
		ArrayList<Path> paths = new ArrayList<Path>();
		PriorityQueue<Path> queue = new PriorityQueue<Path>();
		
		Path path = new Path();
		path.addPathPoint(start);
		queue.offer(path);
		Path p;
		LinkedHashSet<RowCol> neighbors;
		int counter = 0;
		
		while (!queue.isEmpty() && counter < maxPathNum)
		{
			p = queue.poll();
			
			if (p.getEndPoint().equals(end)) 
			{
				paths.add(p);
				counter++;
			}
			else 
			{
				neighbors = (LinkedHashSet<RowCol>) get4Neighbors(p.getEndPoint(),board);
				
				for (RowCol l : neighbors)
				{
					if (!p.contains(l))
						{
							Path newPath = new Path(p);
							newPath.addPathPoint(l);
							queue.offer(newPath);
						}
				}
			}
		}
		
		return paths;
	}
	
	
	public static Set<RowCol> get4Neighbors(RowCol l,int [][] board) {
		Set<RowCol> neighbors = new LinkedHashSet<RowCol>();
		
		int x = l.row;
		int y = l.col;
		
		
		if (y>0) neighbors.add(new RowCol(x,y-1));			
		if (x>0) neighbors.add(new RowCol(x-1,y));
		if (x<board.length-1) neighbors.add(new RowCol(x+1, y));		
		if (y<board[0].length-1) neighbors.add(new RowCol(x,y+1));
		
		
		return neighbors;
	}	
}	
