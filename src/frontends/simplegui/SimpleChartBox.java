package frontends.simplegui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SimpleChartBox extends JPanel {

	private XYSeries series;
	
	public SimpleChartBox() {
		this.series = new XYSeries("NOHELP");
	        final XYSeriesCollection dataset = new XYSeriesCollection(this.series);
	        final JFreeChart chart = createChart(dataset);

	        final ChartPanel chartPanel = new ChartPanel(chart);
	        

	        final JPanel content = new JPanel(new BorderLayout());
	        content.add(chartPanel);

	        chartPanel.setPreferredSize(new java.awt.Dimension(500, 370));

	        this.add(content);
	        
	       
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
	       

	        return chart;

	    }
	 
	public void add(double x, double y) {
		 this.series.add(x,y);
	        
	}
	
}
