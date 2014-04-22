package massim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

//import massim.ui.ExpParamsPane.GeneralProperty;
import massim.ui.config.ConfigLoader;
import massim.ui.config.ConfigProperty;
import massim.ui.config.TeamConfiguration;
import massim.ui.config.ConfigProperty.InputType;
import massim.ui.config.ExperimentConfiguration;

//import com.l2fprod.common.propertysheet.PropertySheet;
//import com.l2fprod.common.propertysheet.PropertySheetPanel;
//import com.l2fprod.common.swing.LookAndFeelTweaks;

public class TeamsPanel extends JPanel {

	private static final long serialVersionUID = -6447666589519534754L;
	private ExperimentConfiguration expConfig;
	List<TeamPanel> lstTeams = new ArrayList<TeamPanel>();
	private JPanel panelTeams;
	private int teamCount = 0;
	
	public TeamsPanel(ExperimentConfiguration mExpConfig) {
		
		setLayout(new BorderLayout(0, 0));
		StyleSet.setEmptyBorder(this, 5, 10, 5, 10);
		setBackground(Color.WHITE);
		
		JPanel panel = new JPanel();
		panel.setBackground(getBackground());
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		StyleSet.setEmptyBorder(panel, 0, 0, 5, 0);
		add(panel, BorderLayout.NORTH);
		
		JLabel lblTitle = new JLabel("Teams");
		lblTitle.setMaximumSize(new Dimension(10000, 50));
		StyleSet.setTitleFont(lblTitle);
		panel.add(lblTitle);
		
		JButton btnAddTeam = new JButton("Add Team");
		panel.add(btnAddTeam);
		btnAddTeam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTeam();
			}
		});
		
		panelTeams = new JPanel();
		panelTeams.setBackground(getBackground());
		panelTeams.setLayout(new BoxLayout(panelTeams, BoxLayout.Y_AXIS));
		JScrollPane scrollPane_1 = new JScrollPane(panelTeams);
		scrollPane_1.setBorder(new EmptyBorder(0, 0, 0, 0));
		//scrollPane_1.setMaximumSize(new Dimension(10000, 400));
		add(scrollPane_1, BorderLayout.CENTER);
		setConfiguration(mExpConfig);
	}
	
	public void setConfiguration(ExperimentConfiguration mExpConfig)
	{
		this.expConfig = mExpConfig;
		panelTeams.removeAll();
		lstTeams.clear();
		teamCount = 0;
		for(TeamConfiguration teamConfig : expConfig.getTeams())
		{
			TeamPanel pnlTeam = new TeamPanel(teamCount, teamConfig, false);
			teamCount++;
			panelTeams.add(pnlTeam);
			lstTeams.add(pnlTeam);
			pnlTeam.addDeleteEventListener(new DeleteListener());
		}
	}
	
	private void addTeam() {
		for(TeamPanel team : lstTeams)
		{
			if(team != null) {
				team.hideParameterPanel();
			}
		}
		TeamConfiguration teamConfig = ConfigLoader.readConfigOfTeam("New Team", null);
		TeamPanel newTeam = new TeamPanel(teamCount, teamConfig, false);
		teamCount++;
		panelTeams.add(newTeam);
		lstTeams.add(newTeam);
		newTeam.addDeleteEventListener(new DeleteListener());
		newTeam.showParameterPanel();
	}
	
	private void deleteTeam(int teamId) {
		int confirmDel = JOptionPane.showConfirmDialog(
			    this,
			    "Are you sure you want to delete this team?",
			    "Delete",
			    JOptionPane.YES_NO_OPTION);
		if(confirmDel == 0) {
			for(int iLoop = 0; iLoop < lstTeams.size(); iLoop++)
			{
				TeamPanel team = lstTeams.get(iLoop);
				if(team != null && team.getTeamId() == teamId) {
					panelTeams.remove(team);
					lstTeams.remove(iLoop);
					panelTeams.revalidate();
					panelTeams.repaint();
					break;
				}
			}
		}
	}
	
	public List<TeamConfiguration> getTeamConfigs()
	{
		List<TeamConfiguration> configs = new ArrayList<TeamConfiguration>();
		for(TeamPanel team : lstTeams)
		{
			if(team != null) {
				configs.add(team.getTeamConfig());
			}
		}
		return configs;
	}
	
	public class DeleteListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() != null) {
				JButton button = (JButton) event.getSource();
				if(button.getName() != null) {
					int id = Integer.parseInt(button.getName());
					deleteTeam(id);
				}
			}
		}
	}	
}
