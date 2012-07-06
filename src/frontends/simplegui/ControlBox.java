package frontends.simplegui;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ControlBox extends JPanel {

	JButton btnStart;
	JButton btnStop; 
	JCheckBox chkLoop;
	JLabel lblStat;
	
	boolean loop = false;
	Thread st;
	
	public ControlBox(final Object parent) {
		btnStart = new JButton("Start");
		btnStop = new JButton("Stop");
		chkLoop = new JCheckBox("Loop");
		lblStat = new JLabel("Ready");
		lblStat.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		btnStart.addActionListener(new ActionListener() {
	           public void actionPerformed(ActionEvent event) {
	        	   
	        	   st = new Thread(new Runnable() {
	                   public void run() {
	                	   int count = 0;
	                	   loop = chkLoop.isSelected();
	                	   do {
	                		   count++;
	                		   lblStat.setText("Working...");
	                		   ((SimpleSim)parent).sec.setupExeperiment(500);
	                		   int[] ts = ((SimpleSim)parent).sec.startExperiment();
	                		   ((SimpleSim)parent).resultsBox.addResults(Integer.toString(ts[0]));
	                		   //((SimpleSim)parent).sec.getParamD("env.disturbance")
	                		   ((SimpleSim)parent).chartBox.add((double)(count),(double)ts[0]);
	                		   lblStat.setText("Done");
	                	   }  while(loop);
	    	               }
	               });		
	        	   
	        	  
	        	   st.start();
	               
	          }
	       });
		
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				loop = false;
				st.stop(); // it's unsafe!
                lblStat.setText("Stopped");

			}
		});
		this.add(chkLoop);
		this.add(btnStart);
		this.add(btnStop);
		this.add(lblStat);
	}
}
