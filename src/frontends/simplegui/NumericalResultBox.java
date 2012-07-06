package frontends.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;


public class NumericalResultBox extends JPanel {
	
	private JTextArea txtResults;
	private JScrollPane scMain;
	public NumericalResultBox() {
		this.setLayout(new BorderLayout());
		txtResults = new JTextArea();
		txtResults.setBorder(BorderFactory.createLineBorder(Color.black));
		txtResults.setSize(300, 100);
		
		scMain = new JScrollPane(txtResults);
		setPreferredSize(new Dimension(500,100));
		this.add(scMain,BorderLayout.CENTER);
	}
	
	public void addResults(String r) {
		txtResults.append(r+"\n");
		
	}
}
