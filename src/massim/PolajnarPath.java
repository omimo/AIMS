package massim;


public class PolajnarPath {

	int m;
	int n;

	class BPCell {
		int x;
		int y;
		int p;
	}

	BPCell[][] BP;

	BPCell[] path;

	int[][] C;

	void fillBP() {
		/*for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < m; j++)
				System.out.print(C[i][j]+"\t");
			System.out.println();
		}*/
		
		for (int d = 0; d < n + m - 1; d++) {
			for (int i = 0; i <= d; i++) {
				int j = d - i;
				if (i < n && j < m) {
					if (i == 0) {
						if (j == 0) {
							BP[i][j].x = -1; // undefined
							BP[i][j].y = -1; // undefined
							BP[i][j].p = 0;
						} else {
							BP[i][j].x = i;
							BP[i][j].y = j - 1;
							BP[i][j].p = C[i][j] + BP[i][j - 1].p;
						}
					} else if (j == 0) {
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

	void fillPath() {
		/* put the end point */
		path[n + m - 2].x = n - 1;
		path[n + m - 2].y = m - 1;
		path[n + m - 2].p = BP[n - 1][m - 1].p;

		int previ = BP[n - 1][m - 1].x;
		int prevj = BP[n - 1][m - 1].y;

		for (int d = n + m - 3; d >= 0; d--) {
			path[d].x = previ;
			path[d].y = prevj;
			path[d].p = BP[previ][prevj].p;
			previ = BP[path[d].x][path[d].y].x;
			prevj = BP[path[d].x][path[d].y].y;
		}

	}

	public RowCol[] findShortestPath(int[][] costs, int rows, int cols) {
		n = rows;
		m = cols;
		BP = new BPCell[n][m];
		path = new BPCell[n + m - 1];
		C = new int[n][m];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				BP[i][j] = new BPCell();

		for (int i = 0; i < n + m - 1; i++)
			path[i] = new BPCell();

		RowCol[] shortestPath = new RowCol[path.length];

		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				C[i][j] = costs[i][j];

		fillBP();
		fillPath();

		for (int d = 0; d < path.length; d++)
			shortestPath[d] = new RowCol(path[d].x, path[d].y);

		return shortestPath;
	}

}

