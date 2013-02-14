package massim.agents.advancedactionmap;

import massim.Team;

public class AdvActionMapTeam extends Team {

	public static boolean useExp = false;
	
	public AdvActionMapTeam() {
		super();		
			
		AdvActionMAPAgent[] agents = new AdvActionMAPAgent[paramI("Team.teamSize")];
		
		for(int i=0;i<paramI("Team.teamSize");i++)
			{
				agents[i] = new AdvActionMAPAgent(i,commMedium());	
				if (useExp)
					agents[i].useExperience(useExp);
			}
		
		setAgents(agents);
	}	
}
