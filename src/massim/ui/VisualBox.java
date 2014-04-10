package massim.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Shape;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class VisualBox extends JPanel {

	private XYSeries[] lstSeries;
	private JPanel chartContentPanel = new JPanel(new BorderLayout());
	ChartPanel chartPanel;
	
	public VisualBox(String strParams, String strXAxis, String strYAxis, String[] seriesNames) {
		this.setLayout(new BorderLayout(0, 0));
		lstSeries = new XYSeries[seriesNames.length];
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		for(int index = 0; index < lstSeries.length; index++) {
			lstSeries[index] = new XYSeries(seriesNames[index]);
			dataset.addSeries(lstSeries[index]);
		}
		
	    JFreeChart chart = createChart(dataset, strXAxis, strXAxis, strYAxis);
	    chartPanel = new ChartPanel(chart);
	    chartContentPanel.add(chartPanel);
	    chartPanel.setPreferredSize(new java.awt.Dimension(500, 370));
	    this.add(chartContentPanel, BorderLayout.CENTER);
	    
	    JTextArea txtParams = new JTextArea(strParams);
	    txtParams.setEditable(false);
	    StyleSet.setRegular(txtParams);
        StyleSet.setEmptyBorder(txtParams, 5);
	    this.add(txtParams, BorderLayout.EAST);
	}
	
	public void addData(double xValue, double[] data)
	{
		if(lstSeries.length != data.length) return;
		
		int index = 0;
		for(XYSeries series: lstSeries) {
			series.add(xValue, data[index]);
			index++;
		}
		chartPanel.validate();
	}
	
	 private  JFreeChart createChart(XYDataset dataset, String strTitle, String strXAxis, String strYAxis) {
	    	JFreeChart chart = ChartFactory.createXYLineChart(
	    			strTitle + " Variations",  // title
	    			strXAxis,             // x-axis label
	    			strYAxis,   // y-axis label
	    			dataset, 
	    			PlotOrientation.VERTICAL, 
	    			true, 
	    			true, 
	    			false);
	        
	        XYPlot plot = chart.getXYPlot();
	        ValueAxis axis = plot.getDomainAxis();
	        axis.setAutoRange(true);
	        axis = plot.getRangeAxis();
	        axis.setAutoRange(true); 
	        
	        XYLineAndShapeRenderer br = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
	        br.setBaseShapesVisible(true);
	        NumberFormat format = NumberFormat.getNumberInstance();
	        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator("{2}", format, format);
	        br.setBaseItemLabelGenerator(generator);
	        br.setBaseItemLabelsVisible(true);
	        for(int index = 0; index < lstSeries.length; index++) {
	        	br.setSeriesStroke(index, new BasicStroke(4.0f));
	        }
	        chart.setBackgroundPaint(this.getBackground());
	        return chart;
	}
}
