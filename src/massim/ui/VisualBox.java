package massim.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;


public class VisualBox extends JPanel {

	private XYSeries[] lstSeries;
	//private JPanel chartContentPanel = new JPanel(new BorderLayout());
	JLayeredPane chartContentPanel = new JLayeredPane();
	JScrollPane spTextParam;
	ChartPanel chartPanel;
	ArrayList<SeriesValues> lstValues;
	String[] seriesNames;
	
	public VisualBox(String strParams, String strXAxis, String strYAxis, String[] seriesNames) {
		this.setLayout(new BorderLayout(0, 0));
		lstSeries = new XYSeries[seriesNames.length];
		this.seriesNames = seriesNames;
		XYSeriesCollection dataset = new XYSeriesCollection();
		for(int index = 0; index < lstSeries.length; index++) {
			lstSeries[index] = new XYSeries(seriesNames[index]);
			dataset.addSeries(lstSeries[index]);
		}
		
		chartContentPanel.setBackground(Color.WHITE);
		chartContentPanel.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) { }
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				if(chartPanel == null) return;
			    Dimension size = arg0.getComponent().getSize();
			    chartPanel.setBounds(0, 0, size.width, size.height);
			    if(spTextParam != null)
			    	spTextParam.setBounds(size.width - spTextParam.getPreferredSize().width, 35, spTextParam.getPreferredSize().width, spTextParam.getPreferredSize().height);
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) { }
			
			@Override
			public void componentHidden(ComponentEvent arg0) { }
		});
		
	    JFreeChart chart = createChart(dataset, strXAxis, strXAxis, strYAxis);
	    chartPanel = new ChartPanel(chart);
	    chartContentPanel.setLayout(null);
	    chartContentPanel.add(chartPanel);
	    this.add(chartContentPanel, BorderLayout.CENTER);
	    
	    if(strParams != null && strParams.replaceAll("\n", "").replaceAll("\t", "").trim().length() > 0) {
		    JTextArea txtParams = new JTextArea(strParams);
		    txtParams.setEditable(false);
		    StyleSet.setRegular(txtParams);
	        StyleSet.setEmptyBorder(txtParams, 5);
	        
	        spTextParam = new JScrollPane(txtParams);
	        spTextParam.setPreferredSize(new Dimension(190, 150));
	        chartContentPanel.add(spTextParam);
	        chartContentPanel.setLayer(spTextParam, 1);
	    }
	    lstValues = new ArrayList<SeriesValues>();
	}
	
	public void addData(double xValue, double[] data)
	{
		if(lstSeries.length != data.length) return;
		
		int index = 0;
		double minX = Double.MAX_VALUE; double maxX = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE; double maxY = Double.MIN_VALUE;
		for(XYSeries series: lstSeries) {
			SeriesValues prevValues = null;
			for(SeriesValues values : lstValues)
			{
				if(values != null && values.getX() == xValue && values.getIndex() == index) {
					prevValues = values;
					break;
				}
			}
			if(prevValues == null) {
				prevValues = new SeriesValues(index, xValue);
			}
			prevValues.addY(data[index]);
			
			for(int iLoop = 0; iLoop < series.getItemCount(); iLoop++) {
				if(series.getDataItem(iLoop).getX().doubleValue() == xValue) {
					series.remove(iLoop);
					iLoop--;
				}
			}
			series.add(xValue, prevValues.getAverageY());
			index++;
			minX = Math.min(minX, series.getMinX());
			minY = Math.min(minY, series.getMinY());
			
			maxX = Math.max(maxX, series.getMaxX());
			maxY = Math.max(maxY, series.getMaxY());
		}
		minX *= (minX > 0 ? 0.9 : 1.1);
		minY *= (minY > 0 ? 0.9 : 1.1);
		maxX *= 1.1;
		maxY *= 1.1;
		if(minY != maxY)
			chartPanel.getChart().getXYPlot().getRangeAxis().setRange(minY, maxY);
		if(minX != maxX)
			chartPanel.getChart().getXYPlot().getDomainAxis().setRange(minX, maxX);
		chartPanel.validate();
	}
	
	public String[] getSeriesNames()
	{
		return seriesNames;
	}
	
	public String getXAxisName()
	{
		return chartPanel.getChart().getXYPlot().getDomainAxis().getLabel();
	}
	
	public double[][] getData()
	{
		double[][] data = new double[0][0];
		if(lstSeries.length > 0)
		{
			XYSeries firstSeries = lstSeries[0];
			data = new double[firstSeries.getItemCount()][lstSeries.length + 1];
			for(int iLoop = 0; iLoop < firstSeries.getItemCount(); iLoop++) {
				data[iLoop][0] = firstSeries.getDataItem(iLoop).getXValue();
				int seriesIndex = 0;
				for(XYSeries series: lstSeries) {
					for(int iLoopIn = 0; iLoopIn < series.getItemCount(); iLoopIn++) {
						if(series.getDataItem(iLoopIn).getXValue() == data[iLoop][0]) {
							data[iLoop][seriesIndex + 1] = series.getDataItem(iLoopIn).getYValue();
							break;
						}
					}
					seriesIndex++;
				}
			}
		}
		return data;
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
	        
	        LegendTitle legend = chart.getLegend();
	        legend.setPosition(RectangleEdge.RIGHT);
	        
	        XYLineAndShapeRenderer br = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
	        br.setBaseShapesVisible(true);
//	        NumberFormat format = NumberFormat.getNumberInstance();
//	        XYItemLabelGenerator generator = new StandardXYItemLabelGenerator("{2}", format, format);
//	        br.setBaseItemLabelGenerator(generator);
//	        br.setBaseItemLabelsVisible(true);
	        for(int index = 0; index < lstSeries.length; index++) {
	        	br.setSeriesStroke(index, new BasicStroke(4.0f));
	        }
	        //chart.setBackgroundPaint(this.getBackground());
	        plot.setBackgroundPaint(Color.WHITE);
	        return chart;
	}
	 
	private class SeriesValues
	{
		private int index;
		private double x;
		private ArrayList<Double> y;
		
		public SeriesValues(int index, double x)
		{
			this.index = index;
			this.x = x;
			y = new ArrayList<Double>();
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public void addY(double y)
		{
			this.y.add(y);
		}
		public ArrayList<Double> getY()
		{
			return y;
		}
		public double getAverageY()
		{
			double dblAvg = 0;
			for(double yValue : this.y)
			{
				dblAvg += yValue;
			}
			dblAvg = dblAvg / this.y.size();
			return dblAvg;
		}
	}
}
