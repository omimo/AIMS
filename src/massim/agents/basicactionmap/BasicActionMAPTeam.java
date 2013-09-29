package massim.agents.basicactionmap;

import massim.Team;
/**
 * BasicAMAP Team
 * 
 * @author Omid Alemi
 */
public class BasicActionMAPTeam extends Team {
	
	/**
	 * New team - BasicAMAP
	 */
	public BasicActionMAPTeam() {
		
		super();		
		BasicActionMAPAgent[] baMAPAgents = new BasicActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			baMAPAgents[i] = new BasicActionMAPAgent(i,commMedium());
		
		setAgents(baMAPAgents);
	}	
}
