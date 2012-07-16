package frontends.simplegui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;


public class ConsoleBox extends JPanel {
	
	private JTextArea txtResults;
	private JScrollPane scMain;
	private JPanel pnlResults;
	private JPanel pnlLogs;
	private JPanel pnlErrors;
	private JTextArea txtLogs;
	private JScrollPane scLogs;
	
	public ConsoleBox() {
		this.setBorder(BorderFactory.createTitledBorder("Console"));
		this.setLayout(new GridLayout(1,1));
		//
		pnlResults = new JPanel();
		pnlResults.setLayout(new BorderLayout());
		txtResults = new JTextArea();
		//txtResults.setBorder(BorderFactory.createLineBorder(Color.black));
		txtResults.setSize(300, 100);
		
		scMain = new JScrollPane(txtResults);
		setPreferredSize(new Dimension(500,200));
		pnlResults.add(scMain,BorderLayout.CENTER);
		//
		
		pnlLogs = new JPanel();
		pnlLogs.setLayout(new BorderLayout());
		txtLogs = new JTextArea();
		scLogs = new JScrollPane(txtLogs);
		pnlLogs.add(scLogs,BorderLayout.CENTER);
		//
		
		pnlErrors = new JPanel();
		
		//
		
		JTabbedPane mainPane = new JTabbedPane();
		mainPane.add("Results", pnlResults);
		mainPane.add("Logs", pnlLogs);
		mainPane.add("Errors", pnlErrors);
		
		this.add(mainPane);
		
	}
	
	public void addResults(String r) {
		txtResults.append(r+"\n");
		
	}
	
	public JTextArea getLogArea() {
		return txtLogs;
	}
}
