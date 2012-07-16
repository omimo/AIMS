package frontends.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class VisualBox extends JPanel {

	private XYSeries series;
	private JPanel chartContentPanel = new JPanel(new BorderLayout());
	private JPanel boardPanel = new JPanel();
	SimpleSim ss;
	massim.microworlds.twctgrid.gui.Board b;
	
	public VisualBox(final Object parent, Class<?> gameGUI) {
		 ss = 	((SimpleSim)parent);
		
		this.setBorder(BorderFactory.createTitledBorder("Visual Inspector"));
		this.setLayout(new GridLayout(1,1));
		//this.setBackground(Color.white);
		//
		
		this.series = new XYSeries("NOHELP");
	    final XYSeriesCollection dataset = new XYSeriesCollection(this.series);
	    final JFreeChart chart = createChart(dataset);

	    final ChartPanel chartPanel = new ChartPanel(chart);
	        

	   // final JPanel content = new JPanel(new BorderLayout());
	    chartContentPanel.add(chartPanel);

	    chartPanel.setPreferredSize(new java.awt.Dimension(500, 370));
	    
	    //
	    
	    
	        
	    //
/*	    Constructor gameConstructor;
	    Object gg = null;
		try {
			gameConstructor = gameGUI.getConstructor(new Class[]{});
			  gg = gameConstructor.newInstance();
		} catch (Exception e) {
		}*/
	   
	    Random rnd = new Random();
		int[][] boardData = new int[10][10];
		for (int i=0;i<10;i++)
			for (int j=0;j<10;j++)
				boardData[i][j] = 4;//rnd.nextInt(4); 
		HashMap<Integer, java.awt.Color> colorMap = new HashMap<Integer, java.awt.Color>();

		colorMap.put(0, java.awt.Color.red);
		colorMap.put(1, java.awt.Color.blue);
		colorMap.put(2, java.awt.Color.green);
		colorMap.put(3, java.awt.Color.yellow);
		colorMap.put(4, java.awt.Color.DARK_GRAY);
		colorMap.put(5, java.awt.Color.ORANGE);
		
		b = new massim.microworlds.twctgrid.gui.Board(10,10,boardData,colorMap);
		
		boardPanel.add(b);
		
		//
	    JTabbedPane mainPane = new JTabbedPane();
		mainPane.add("Chart",chartContentPanel);
		mainPane.add("Game", boardPanel);
		this.add(mainPane);
	}
	
	public void setGameBoard(){
		b.setBoard(ss.sec.getBoardInstance());
		b.repaint();
	}

	 private  JFreeChart createChart(XYDataset dataset) {

	    	JFreeChart chart = ChartFactory.createXYLineChart(
	    	         "Disturbance Level Variations",  // title
	    	            "Experiment#",             // x-axis label
	    	            "Team Score",   // y-axis label
	    			dataset, 
	    			PlotOrientation.VERTICAL, 
	    			true, 
	    			true, 
	    			false);
	        

	        final XYPlot plot = chart.getXYPlot();
	        ValueAxis axis = plot.getDomainAxis();
	        axis.setAutoRange(true);
	        //axis.setFixedAutoRange(60000.0);  // 60 seconds
	        axis = plot.getRangeAxis();
	        axis.setRange(20000, 90000); 
	       
	     chart.setBackgroundPaint(this.getBackground());
	        
	        return chart;

	    }
	 
	public void add(double x, double y) {
		 this.series.add(x,y);
	        
	}
	
}
