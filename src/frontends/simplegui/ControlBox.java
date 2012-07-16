package frontends.simplegui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ControlBox extends JPanel {

	JButton btnStart;
	JButton btnStep;
	JButton btnStop; 
	JCheckBox chkLoop;
	JTextField txtStat;
	JLabel lblStat;
	JPanel pnlInside;
	JCheckBox chkLog;
	
	boolean loop = false;
	Thread st;

	int stepCount = 0; 
	
	
	public ControlBox(final Object parent) {
		
		ImageIcon startIcon = new ImageIcon ((new ImageIcon("bin/frontends/simplegui/res/start.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		ImageIcon stopIcon = new ImageIcon ((new ImageIcon("bin/frontends/simplegui/res/stop.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		ImageIcon stepIcon = new ImageIcon ((new ImageIcon("bin/frontends/simplegui/res/step-forward.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		
		btnStart = new JButton("Start",startIcon);
		btnStep = new JButton("Step",stepIcon);
		btnStop = new JButton("Stop",stopIcon);
		chkLoop = new JCheckBox("Loop");
		lblStat = new JLabel("State:");
		txtStat = new JTextField("Ready",10);
		txtStat.setEditable(false);
		chkLog = new JCheckBox("Logs on");
		this.setBorder(BorderFactory.createTitledBorder("Control"));
		
		//lblStat.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		btnStart.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent event) {
	        	   
	        	   st = new Thread(new Runnable() {
	                   public void run() {
	                	   int count = 0;
	                	   loop = chkLoop.isSelected();
	                	   do {
	                		   count++;
	                		   txtStat.setText("Working...");
	                		   ((SimpleSim)parent).sec.setupExeperiment(500);
	                		   int[] ts = ((SimpleSim)parent).sec.startExperiment();
	                		   ((SimpleSim)parent).consoleBox.addResults(Integer.toString(ts[0]));
	                		   //((SimpleSim)parent).sec.getParamD("env.disturbance")
	                		   ((SimpleSim)parent).visualBox.add((double)(count),(double)ts[0]);
	                		   
	                		   ((SimpleSim)parent).visualBox.setGameBoard();	        	   
	                		   
	                		   txtStat.setText("Done");
	                	   }  while(loop);
	    	               }
	               });		
	        	   
	        	  
	        	   st.start();
	               
	          }
	       });
		
		btnStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
			
				
				stepCount++;
				if (stepCount == 1) {
					st = new Thread(new Runnable() {
		                   public void run() {
		                	   txtStat.setText("Working...");
		                	   ((SimpleSim)parent).sec.setupExeperiment(500);
		                	   int[] ts = ((SimpleSim)parent).sec.startExperiment();
	                		   ((SimpleSim)parent).consoleBox.addResults(Integer.toString(ts[0]));
		                		   // ((SimpleSim)parent).sec.getParamD("env.disturbance")                		
	                 	        	  
		                		   
	 	                	   txtStat.setText("Done");	  
		                   }
		               });		
 
		        	   st.start();
				}
				else
				{
					st.resume();
					((SimpleSim)parent).visualBox.setGameBoard();
				}
				
			}
		});
		
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				loop = false;
				st.stop(); // it's unsafe!
				txtStat.setText("Stopped");

			}
		});
		
		chkLog.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((SimpleSim)parent).setupJdkLoggerHandler(chkLog.isSelected());
				
			}
		});
		
		//this.setLayout();		
		pnlInside = new JPanel();
		pnlInside.setLayout(new BoxLayout(pnlInside, BoxLayout.PAGE_AXIS));
		
		JPanel pnlControls = new JPanel();
		pnlControls.add(btnStart);
		pnlControls.add(btnStep);
		pnlControls.add(btnStop);
		pnlControls.add(chkLoop);
		pnlInside.add(pnlControls);
		
		JPanel pnlSecond = new JPanel();
		pnlSecond.add(chkLog);
		pnlInside.add(pnlSecond);
		
		JPanel pnlStat = new JPanel();
		pnlStat.add(lblStat);
		pnlStat.add(txtStat);
		pnlInside.add(pnlStat);
		
		
		
		this.add(pnlInside);
		
	}
}
