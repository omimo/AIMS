package massim.agents.basicresourcemap;

import massim.Team;


public class BasicResourceMAPTeam extends Team {

	public static boolean useExp = false;
	
	public BasicResourceMAPTeam() {
		super();		
			
		BasicResourceMAPAgent[] agents = new BasicResourceMAPAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			{
				agents[i] = new BasicResourceMAPAgent(i,commMedium());	
				if (useExp)
					agents[i].useExperience(useExp);
			}
		
		setAgents(agents);
	}	
}

