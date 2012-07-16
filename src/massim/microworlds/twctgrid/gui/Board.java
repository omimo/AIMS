package massim.microworlds.twctgrid.gui;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Board extends JPanel {

	public static final int GOAL = 10;
	public static final int PLAYER = 20;
	
	Cell[][] cells;
    final int PAD = 10;
    int ROWS = 10;
    int COLS = 10;

    ArrayList<Obj> objects = new ArrayList<Obj>();
    
    int[][] board;
    HashMap<Integer,Color> colorMap;
    
    static Color gridLinesColor = Color.black;
    
	public Board(int rows, int cols, int[][] board, HashMap<Integer,Color> colorMap) {
		ROWS = rows;
		COLS = cols;
		this.colorMap = colorMap;
		setBoard(board);
		this.setPreferredSize(new Dimension(ROWS*PAD*3, COLS*PAD*3));
	}
    
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        if(cells == null) {
            initCells();
        }

        
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLS; j++) {
                cells[i][j].draw(g2);
            }
            
       for (Obj o : objects) {
        	drawObject(g2,o.row,o.col,o.obj,o.id);
        }
        
        }
        
        
    }
   
	public void setBoard(int[][] b) {
		this.board = new int[b.length][b[0].length];
		for (int i=0; i<board.length;i++)
			for (int j=0;j<board[0].length;j++)
				this.board[i][j] = b[i][j];
		cells = null;
	}
	
	public void addObject(int row, int col, int obj, int id) {
		objects.add(new Obj(row,col,obj,id));
	}
	
	public void drawObject(Graphics2D g2, int row, int col, int obj, int id) {		
		switch (obj)
		{
		case PLAYER : drawPlayer(g2, row, col, obj, id);break;
		}
		
		
	}
	
	public void drawPlayer(Graphics2D g2, int row, int col, int obj, int id) {
		
		int w = getWidth();
        int h = getHeight();
        double xInc = (double)(w - 2*PAD)/COLS;
        double yInc = (double)(h - 2*PAD)/ROWS;
        
        double x = PAD + row * xInc ;
        double y = PAD + col * yInc ;
        
        Ellipse2D.Double el =
            new Ellipse2D.Double(x+4, y+4, xInc-8, yInc-8);
        
        g2.setPaint(Color.black);
		g2.fill(el);
		g2.setPaint(Color.white);
		g2.draw(el);
		
		g2.drawString(Integer.toString(id),(int)(x+xInc/2) , (int)(y+yInc/2));		
	}
	
	private void initCells() {
        cells = new Cell[ROWS][COLS];
        int w = getWidth();
        int h = getHeight();
        double xInc = (double)(w - 2*PAD)/COLS;
        double yInc = (double)(h - 2*PAD)/ROWS;
        for(int i = 0; i < ROWS; i++) {
            double y = PAD + i*yInc;
            for(int j = 0; j < COLS; j++) {
                double x = PAD + j*xInc;
                Rectangle2D.Double r =
                    new Rectangle2D.Double(x, y, xInc, yInc);
                Color bg = colorMap.get(board[i][j]);
                cells[i][j] = new Cell(i, j, r, bg);
            }
        }
    }    
}



class Cell {
	private final int row;
	private final int col;
	Rectangle2D.Double rect;
	Color bgColor;
	Color borderColor;
	
	public Cell(int r, int c, Rectangle2D.Double rect, Color bg) {
		row = r;
		col = c;
		this.rect = rect;
		bgColor = bg;
		borderColor = Board.gridLinesColor;  
	}
	
	public void draw(Graphics2D g2) {
		g2.setPaint(bgColor);
		g2.fill(rect);
		g2.setPaint(borderColor);
		g2.draw(rect);
		
		
	}
	
}

class Obj {
	int row;
	int col;
	int obj;
	int id;
	
	public Obj(int r, int c, int o, int i) {
		row = r;
		col = c;
		obj = o;
		id = i;
	}
}