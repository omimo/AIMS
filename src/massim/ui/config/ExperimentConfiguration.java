package massim.ui.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExperimentConfiguration extends Configuration {
	
	@XmlElement(type = TeamConfiguration.class)
	private List<TeamConfiguration> teams;
	
	private boolean isDistributedExec;
	private String distributedExecFileName;
	
	public ExperimentConfiguration()
	{
		super();
		teams = new ArrayList<TeamConfiguration>();
		isDistributedExec = false;
	}
	
	public void add(TeamConfiguration team)
	{
		teams.add(team);
	}
	
	public void remove(TeamConfiguration team)
	{
		teams.remove(team);
	}
	
	public void removeTeamAt(int index)
	{
		if(teams.size() > index)
		{
			teams.remove(index);
		}
	}
	
	public List<TeamConfiguration> getTeams() {
		return teams;
	}

	public void setTeamConfigs(List<TeamConfiguration> lstTeams) {
		teams = new ArrayList<TeamConfiguration>();
		teams.addAll(lstTeams);
	}

	public boolean isDistributedExec() {
		return isDistributedExec;
	}

	public void setDistributedExec(String distributedExecFileName) {
		this.isDistributedExec = true;
		this.distributedExecFileName = distributedExecFileName;
	}

	public String getDistributedExecFileName() {
		return distributedExecFileName;
	}
	
	public String toStringParams()
	{
		return super.toString();
	}
	
	public String toString()
	{
		String strText = "";
		for(TeamConfiguration team : teams)
			strText += team.toString() + "\n";
		strText += super.toString();
		return strText;
	}
}