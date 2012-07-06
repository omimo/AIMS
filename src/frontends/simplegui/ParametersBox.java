package frontends.simplegui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ParametersBox extends JPanel {
	private JSlider sldDist;
	private JLabel lblDistSlider;
	
	public ParametersBox(final Object parent) {
		lblDistSlider = new JLabel("env.disturbance:");
		
		sldDist = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		sldDist.setMajorTickSpacing(10);
		
		sldDist.setPaintTicks(true);
		sldDist.setPaintLabels(true);
		
		sldDist.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        int d = (int)source.getValue();			        
			        ((SimpleSim)parent).sec.changeParam("env.disturbance", d/100.0);
			    }
			}
		});
		
		
		this.add(lblDistSlider);
		this.add(sldDist);
	}
	
}
