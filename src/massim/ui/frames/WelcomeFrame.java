package massim.ui.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import massim.ui.FrameManager;
import massim.ui.StyleSet;

public class WelcomeFrame extends JFrame  {
	
	private ActionListener btnClick = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent e) {
			openConfiguration();
		}
		
	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		StyleSet.doInitialStyleSettings();
		FrameManager.startApplication();
	}

	/**
	 * Create the application.
	 */
	public WelcomeFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(680, 500);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setTitle("MASSIM - Welcome");
		setLocationRelativeTo(null);
		
		JPanel panelTop = new JPanel();
		((FlowLayout) panelTop.getLayout()).setAlignment(FlowLayout.CENTER);
		getContentPane().add(panelTop, BorderLayout.NORTH);
		StyleSet.setEmptyBorder(panelTop, 5);
		
		JLabel lblTitle = new JLabel("Welcome to Simulator for MAP");
		StyleSet.setBorder(lblTitle, 0, 0, 2, 0);
		StyleSet.setWelcomeFont(lblTitle);
		panelTop.add(lblTitle);
			
		JPanel panelMain = new JPanel();
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.X_AXIS));
		StyleSet.setEmptyBorder(panelMain, 20, 20, 20, 40);
		getContentPane().add(panelMain, BorderLayout.CENTER);
		
		JPanel panelLeft = new JPanel();
		panelMain.add(panelLeft);
		StyleSet.setBorder(panelLeft, 0, 1, 0, 0);
		panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
		panelLeft.setMaximumSize(new Dimension(280, 500));
		
		lblTitle = new JLabel("Run");
		StyleSet.setTitleFont(lblTitle);
		panelLeft.add(lblTitle);
		addMarginPanel(panelLeft);
		
		JButton btnButton = new JButton("New Experiment");
		btnButton.setIcon(new ImageIcon(WelcomeFrame.class.getResource("/massim/ui/images/Experiment.png")));
		StyleSet.setTitleFont(btnButton);
		btnButton.setHorizontalAlignment(SwingConstants.LEADING);
		btnButton.setMaximumSize(new Dimension(230, 70));
		btnButton.addActionListener(btnClick);
		panelLeft.add(btnButton);
		addMarginPanel(panelLeft);
		
		btnButton = new JButton("<html>Saved Configuration</html>");
		btnButton.setIcon(new ImageIcon(WelcomeFrame.class.getResource("/massim/ui/images/configuration.png")));
		StyleSet.setTitleFont(btnButton);
		btnButton.setHorizontalAlignment(SwingConstants.LEADING);
		btnButton.setMaximumSize(new Dimension(230, 70));
		panelLeft.add(btnButton);
		addMarginPanel(panelLeft);
		
		btnButton = new JButton("Recording");
		btnButton.setIcon(new ImageIcon(WelcomeFrame.class.getResource("/massim/ui/images/Recording.png")));
		StyleSet.setTitleFont(btnButton);
		btnButton.setHorizontalAlignment(SwingConstants.LEADING);
		btnButton.setMaximumSize(new Dimension(230, 70));
		panelLeft.add(btnButton);
		
		JPanel panelRight = new JPanel();
		panelMain.add(panelRight, BorderLayout.EAST);
		StyleSet.setEmptyBorder(panelRight, 0, 0, 0, 40);
		panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
		panelRight.setMaximumSize(new Dimension(280, 500));
		
		lblTitle = new JLabel("Analyze");
		StyleSet.setTitleFont(lblTitle);
		panelRight.add(lblTitle);
		addMarginPanel(panelRight);
		
		btnButton = new JButton("<html>Graphs of Previous <br/> Experiments</html>");
		btnButton.setIcon(new ImageIcon(WelcomeFrame.class.getResource("/massim/ui/images/graphs.png")));
		StyleSet.setTitleFont(btnButton);
		btnButton.setHorizontalAlignment(SwingConstants.LEADING);
		btnButton.setMaximumSize(new Dimension(250, 70));
		panelRight.add(btnButton);
		addMarginPanel(panelRight);
		
		btnButton = new JButton("<html>Step-by-Step <br/>Execution</html>");
		btnButton.setIcon(new ImageIcon(WelcomeFrame.class.getResource("/massim/ui/images/Step.png")));
		StyleSet.setTitleFont(btnButton);
		btnButton.setHorizontalAlignment(SwingConstants.LEADING);
		btnButton.setMaximumSize(new Dimension(250, 70));
		panelRight.add(btnButton);
	}
	
	private void addMarginPanel(JPanel panel)
	{
		JPanel panelBottom = new JPanel();
		panelBottom.setMaximumSize(new Dimension(0, 10));
		panel.add(panelBottom);
	}
	
	private void openConfiguration()
	{
		FrameManager.openNewFrame(this, ConfigureFrame.class);
	}
}