package frontends.simplegui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class ParametersBox extends JPanel {
	private JSlider sldDist;
	private JLabel lblDistSlider;
	JTable tblParams = new JTable(10, 2);
	DefaultTableModel dataModel;
	
	public ParametersBox(final Object parent) {
		this.setBorder(BorderFactory.createTitledBorder("Experiment Setup"));
		this.setLayout(new GridLayout(1,1));
		this.setPreferredSize(new Dimension(300, 500));
		//
		
		JPanel pnlParams = new JPanel();
		pnlParams.setLayout(new BoxLayout(pnlParams,BoxLayout.PAGE_AXIS));
		
		JToolBar toolbar = new JToolBar();
		ImageIcon loadIcon = new ImageIcon ((new ImageIcon("bin/frontends/simplegui/res/document-open.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		ImageIcon saveIcon = new ImageIcon ((new ImageIcon("bin/frontends/simplegui/res/document-save.png")).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
		JButton btnLoad = new JButton(loadIcon);
		JButton btnSave = new JButton(saveIcon);
		toolbar.add(btnLoad);
		toolbar.add(btnSave);
		
		btnLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select an experiment file");
				
				int retval = fc.showOpenDialog((Component)parent);
				if (retval == JFileChooser.APPROVE_OPTION) {
					try {
						System.out.println(fc.getSelectedFile());
						((SimpleSim)parent).sec.loadFromFile(fc.getSelectedFile().getPath());
						updateTable(parent);
					} catch (IOException e1) {
						
					JOptionPane.showMessageDialog((Component)parent, "Error in parsing the experiment file","Parsing Error", JOptionPane.ERROR);
					}
				}
				
			}
		});
		
		//
		dataModel = new DefaultTableModel(0, 2);
		tblParams = new JTable(dataModel);
		JScrollPane scrollPane = new JScrollPane(tblParams);
		pnlParams.setPreferredSize(new Dimension(300, 500));
		tblParams.getColumnModel().getColumn(0).setHeaderValue("Parameter");
		tblParams.getColumnModel().getColumn(1).setHeaderValue("Value");
		
		updateTable(parent);
		//
	
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
		
		pnlParams.add(toolbar);
		pnlParams.add(scrollPane);
		JPanel pan2 = new JPanel();
		pan2.add(lblDistSlider);
		pan2.add(sldDist);
		pnlParams.add(pan2);
		//
		JPanel pnlExp = new JPanel();
		
		//
		JTabbedPane mainPane = new JTabbedPane();
		mainPane.add("Parameters",pnlParams);
		mainPane.add("Experiment Settings",pnlExp);
		
		this.add(mainPane);
		
	}

	private void updateTable(Object parent) {
		Map<String,Object> list = ((SimpleSim)parent).sec.getList();

		dataModel.setRowCount(0);
		
		for (String key : list.keySet()) {
			dataModel.addRow(new Object[] {key,list.get(key)});
		}
		
		
	}
	
}
