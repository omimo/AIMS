package massim.agents.empathic;

import massim.Team;

/**
 * The team for empathic agents
 * 
 * @author Omid Alemi
 * @version 1.0 2011/11/15
 *
 */
public class EmpathicTeam extends Team {

	public EmpathicTeam() {
		super();		
			
		EmpathicAgent[] empAgents = new EmpathicAgent[Team.teamSize];
		
		for(int i=0;i<Team.teamSize;i++)
			empAgents[i] = new EmpathicAgent(i,commMedium());
		
		setAgents(empAgents);
	}	
}
