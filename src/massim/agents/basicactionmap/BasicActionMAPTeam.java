package massim.agents.basicactionmap;

import massim.Team;


public class BasicActionMAPTeam extends Team {

	public static boolean useExp = false;
	
	public BasicActionMAPTeam() {
		super();		
			
		BasicActionMAPAgent[] agents = new BasicActionMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			{
				agents[i] = new BasicActionMAPAgent(i,commMedium());	
				if (useExp)
					agents[i].useExperience(useExp);
			}
		
		setAgents(agents);
	}	
}





