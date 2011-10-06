package massim;


public class Goal {

	private static int nextID=0;
	private RowCol pos;
	private int id;
	
	public Goal(RowCol pos) {
		id = nextID++;		
		this.pos = pos;
	}
	
	public Goal(int row, int col) {
		id = nextID++;		
		pos = new RowCol(row,col);			
	}
	
	public int id() {
		return id;
	}
	
	public RowCol pos() {
		return pos;
	}
	
	public void setPos(RowCol pos) {
		this.pos = pos;
	}
}
