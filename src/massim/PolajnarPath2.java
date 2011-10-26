package massim;

import massim.PolajnarPath.BPCell;


public class PolajnarPath2 {

	
	class BPCell {
		int x;
		int y;
		int p;
	}

	enum DIRECTION {NW,NE,SW,SE};
	
	BPCell[][] BP;

	BPCell[] path;

	int[][] C;

	void fillBP_SE(int sr, int sc, int dr, int dc) {
				
		int n = Math.abs(dr-sr) + 1;
		int m = Math.abs(dc-sc) + 1;			
		
		for (int d = 0; d < n + m - 1; d++) {
			for (int i = sr; i <= sr+d; i++) {
				int j = sc + d - (i-sr);
				if (i <= dr && j <= dc) {
					
					if (i == sr) {
						if (j == sc) {
							BP[i][j].x = -1; // undefined
							BP[i][j].y = -1; // undefined
							BP[i][j].p = 0;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j - 1;
							BP[i][j].p = C[i][j] + BP[i][j - 1].p;
						}
					} else if (j == sc) {
						BP[i][j].x = i - 1;
						BP[i][j].y = j;
						BP[i][j].p = C[i][j] + BP[i - 1][j].p;
					} else {
						int ph = BP[i - 1][j].p; // best path cost of the horizontal neighbor
						int pv = BP[i][j - 1].p; // best path cost of the vertical neighbor

						if (ph <= pv) {
							BP[i][j].x = i - 1;
							BP[i][j].y = j;
							BP[i][j].p = C[i][j] + BP[i - 1][j].p;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j - 1;
							BP[i][j].p = C[i][j] + BP[i][j - 1].p;
						}
					}
				}
			}
		}
	}

	void fillBP_SW(int sr, int sc, int dr, int dc) {
		
		int n = Math.abs(dr-sr) + 1;
		int m = Math.abs(dc-sc) + 1;			
		
		for (int d = 0; d < n + m - 1; d++) {
			for (int i = sr; i <= sr+d; i++) {
				int j = sc - d + (i-sr);
				if (i <= dr && j >= dc) {					
					if (i == sr) {
						if (j == sc) {
							BP[i][j].x = -1; // undefined
							BP[i][j].y = -1; // undefined
							BP[i][j].p = 0;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j + 1;
							BP[i][j].p = C[i][j] + BP[i][j + 1].p;
						}
					} else if (j == sc) {
						BP[i][j].x = i - 1;
						BP[i][j].y = j;
						BP[i][j].p = C[i][j] + BP[i - 1][j].p;
					} else {
						int ph = BP[i - 1][j].p; // best path cost of the horizontal neighbor
						int pv = BP[i][j + 1].p; // best path cost of the vertical neighbor

						if (ph <= pv) {
							BP[i][j].x = i - 1;
							BP[i][j].y = j;
							BP[i][j].p = C[i][j] + BP[i - 1][j].p;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j + 1;
							BP[i][j].p = C[i][j] + BP[i][j + 1].p;
						}
					}
				}
			}
		}
	}
	
	void fillBP_NW(int sr, int sc, int dr, int dc) {
		
		int n = Math.abs(dr-sr) + 1;
		int m = Math.abs(dc-sc) + 1;			
		
		for (int d = 0; d < n + m - 1; d++) {
			for (int i = sr; i >= sr-d; i--) {
				int j = sc - d - (i-sr);
				
				if (i >= dr && j >= dc) {
					
					if (i == sr) {
						if (j == sc) {
							BP[i][j].x = -1; // undefined
							BP[i][j].y = -1; // undefined
							BP[i][j].p = 0;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j + 1;
							BP[i][j].p = C[i][j] + BP[i][j + 1].p;
						}
					} else if (j == sc) {
						BP[i][j].x = i + 1;
						BP[i][j].y = j;
						BP[i][j].p = C[i][j] + BP[i + 1][j].p;
					} else {
						int ph = BP[i + 1][j].p; // best path cost of the horizontal neighbor
						int pv = BP[i][j + 1].p; // best path cost of the vertical neighbor

						if (ph <= pv) {
							BP[i][j].x = i + 1;
							BP[i][j].y = j;
							BP[i][j].p = C[i][j] + BP[i + 1][j].p;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j + 1;
							BP[i][j].p = C[i][j] + BP[i][j + 1].p;
						}
					}
				}
			}
		}
	}
	
	void fillBP_NE(int sr, int sc, int dr, int dc) {
		
		int n = Math.abs(dr-sr) + 1;
		int m = Math.abs(dc-sc) + 1;			
		
		for (int d = 0; d < n + m - 1; d++) {
			for (int i = sr; i >= sr-d; i--) {
				int j = sc + d + (i-sr);				
				if (i >= dr && j <= dc) {					
					if (i == sr) {
						if (j == sc) {
							BP[i][j].x = -1; // undefined
							BP[i][j].y = -1; // undefined
							BP[i][j].p = 0;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j - 1;
							BP[i][j].p = C[i][j] + BP[i][j - 1].p;
						}
					} else if (j == sc) {
						BP[i][j].x = i + 1;
						BP[i][j].y = j;
						BP[i][j].p = C[i][j] + BP[i + 1][j].p;
					} else {
						int ph = BP[i + 1][j].p; // best path cost of the horizontal neighbor
						int pv = BP[i][j - 1].p; // best path cost of the vertical neighbor

						if (ph <= pv) {
							BP[i][j].x = i + 1;
							BP[i][j].y = j;
							BP[i][j].p = C[i][j] + BP[i + 1][j].p;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j - 1;
							BP[i][j].p = C[i][j] + BP[i][j - 1].p;
						}
					}
				}
			}
		}
	}

	void fillPath(int sr, int sc, int dr, int dc) {
		/* set the local n and m variables */
		int n = Math.abs(dr-sr) + 1;
		int m = Math.abs(dc-sc) + 1;
		path = new BPCell[n + m - 1];
		for (int i = 0; i < n + m - 1; i++)
			path[i] = new BPCell();
		
		/* put the end point */
		path[n + m - 2].x = dr;
		path[n + m - 2].y = dc;
		path[n + m - 2].p = BP[dr][dc].p;

		int previ = BP[dr][dc].x;
		int prevj = BP[dr][dc].y;

		for (int d = n + m - 3; d >= 0; d--) {
			path[d].x = previ;
			path[d].y = prevj;
			path[d].p = BP[previ][prevj].p;
			previ = BP[path[d].x][path[d].y].x;
			prevj = BP[path[d].x][path[d].y].y;
		}

	}

	public RowCol[] findShortestPath(int[][] costs, RowCol source, RowCol destination) {
		/* set the global (board) n and m variables */
		int n = costs.length;
		int m = costs[0].length;
		
		BP = new BPCell[n][m];				
		C = new int[n][m];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				BP[i][j] = new BPCell();
		
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				C[i][j] = costs[i][j];

		switch(findDirection(source, destination))
		{
			case SE:fillBP_SE(source.row,source.col,destination.row,destination.col); break;
			case NW:fillBP_NW(source.row,source.col,destination.row,destination.col); break;
			case SW:fillBP_SW(source.row,source.col,destination.row,destination.col); break;
			case NE:fillBP_NE(source.row,source.col,destination.row,destination.col); break;
		}
		
		
		
		fillPath(source.row,source.col,destination.row,destination.col);

		RowCol[] shortestPath = new RowCol[path.length];
		for (int d = 0; d < path.length; d++)
			shortestPath[d] = new RowCol(path[d].x, path[d].y);

		return shortestPath;
	}
	
	private DIRECTION findDirection(RowCol source, RowCol destination)
	{
		DIRECTION dir = DIRECTION.SE;
		int sr = source.row;
		int sc = source.col;
		int dr = destination.row;
		int dc = destination.col;
		
		if (dc - sc >= 0 && dr - sr >= 0)
			dir = DIRECTION.SE;
		else if (dc-sc < 0 && dr-sr < 0 )
			dir = DIRECTION.NW;
		else if (dc-sc <= 0 && dr-sr >= 0)
			dir = DIRECTION.SW;
		else if (dc-sc >= 0 && dr-sr <= 0)
			dir = DIRECTION.NE;
		return dir;		
	}

}

