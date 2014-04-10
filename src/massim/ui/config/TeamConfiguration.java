package massim.ui.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TeamConfiguration extends Configuration {
	
	private TeamType teamType;
	public TeamConfiguration(){ 
		super();
	}
	
	public TeamConfiguration(TeamType teamType)
	{
		super();
		this.teamType = teamType;
	}
	
	public TeamType getTeamType() {
		return teamType;
	}

	public void setTeamType(TeamType teamType) {
		this.teamType = teamType;
	}
	
	public String toString()
	{
		return super.toString();
	}

	public enum TeamType
	{
		AdvActionMAP("Advanced Action MAP"),
		AdvActionMAPRep("Advanced Action MAP Replaning"),
		HIAMAP("HIAMAP"),
		BasicActionMAP("Basic Action MAP"),
		NoHelp("No Help"),
		New("New Team");
		private String fName;
	    public String getFullName(){return fName;}
	    private TeamType(String fName){this.fName = fName;}
	}
}