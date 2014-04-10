package massim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import massim.ExperimentLogger.LogEvent;
import massim.ExperimentLogger.LogType;
import massim.agents.advancedactionmap.AdvActionMAPAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepAgent;
import massim.agents.advancedactionmap.AdvActionMAPRepTeam;
import massim.agents.advancedactionmap.AdvActionMapTeam;
import massim.agents.basicactionmap.BasicActionMAPAgent;
import massim.agents.basicactionmap.BasicActionMAPTeam;
import massim.agents.helperinitactionmap.HelperInitActionMAPAgent;
import massim.agents.helperinitactionmap.HelperInitActionMAPTeam;
import massim.agents.nohelp.NoHelpTeam;
import massim.ui.config.Configuration;
import massim.ui.config.ExperimentConfiguration;
import massim.ui.config.TeamConfiguration;
import massim.ui.config.Utilities;

public class Experiment {
	private ExperimentLogger logger;
	private String strErrorMessage;
	private Team[] teams;
	private List<SimulationRange> lstSimRange;
	private ExperimentConfiguration expConfig;
	long logCounter = 0, logCounterExp = 0;
	
	public Team[] getTeams() {
		return teams;
	}

	public String getErrorMessage() {
		return strErrorMessage;
	}
	
	private boolean validateConfiuration()
	{
		strErrorMessage = "";
		lstSimRange = new ArrayList<SimulationRange>();
		
		//Number of runs
		Integer iNumOfRuns = Utilities.getInteger(expConfig.getPropertyValue("Number of Runs"), null);
		if(iNumOfRuns == null || iNumOfRuns < 1) {
			strErrorMessage += "Invalid Number of Runs.\n";
		}
		
		//Number of colors
		Integer iNumOfCol = Utilities.getInteger(expConfig.getPropertyValue("Number of Colors"), null);
		if(iNumOfCol == null || iNumOfCol < 1) {
			strErrorMessage += "Invalid Number of Colors.\n";
		} else {
			SimulationEngine.colorRange = new int[iNumOfCol];
			for(int i = 0; i < iNumOfCol; i++) SimulationEngine.colorRange[i] = i;
		}
		SimulationEngine.numOfColors =  SimulationEngine.colorRange.length;
		
		//Action costs
		String strActCosts = Utilities.trim(expConfig.getPropertyValue("Action Costs"), ',');
		if(strActCosts == null || strActCosts.length() == 0) {
			strErrorMessage += "Invalid Action cost value.\n";
		} else {
			String[] strParts = expConfig.getPropertyValue("Action Costs").split(",");
			SimulationEngine.actionCostsRange = new int[strParts.length];
			for(int i = 0; i < strParts.length; i++) {
				Integer intActCost = Utilities.getInteger(strParts[i], null);
				if(intActCost == null) {
					strErrorMessage += "Invalid Number of Colors.\n";
				} else {
					SimulationEngine.actionCostsRange[i] = intActCost;
				}
			}
		}
		
		//Team Size
		Integer iTeamSize = Utilities.getInteger(expConfig.getPropertyValue("Team Size"), null);
		if(iNumOfCol == null || iTeamSize < 1) {
			strErrorMessage += "Invalid Team Size.\n";
		} else {
			Team.teamSize = iTeamSize;
		}
		
		//Board Size
		Integer iBoardSize = Utilities.getInteger(expConfig.getPropertyValue("Board Size"), null);
		if(iBoardSize == null || iBoardSize < 1) {
			strErrorMessage += "Invalid Board Size.\n";
		} else {
			SimulationEngine.boardh = iBoardSize;
			SimulationEngine.boardw = iBoardSize;
		}
		
		Double[] unicast = validateRangeValue(expConfig, "Unicast Cost");
		if(unicast.length == 1) {
			Team.unicastCost = (int)Math.floor(unicast[0]);
			Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
		}
		else if(unicast.length == 3)
			lstSimRange.add(new SimulationRange("Unicast Cost", unicast[0], unicast[1], unicast[2]));
		
		Double[] calccost = validateRangeValue(expConfig, "Calc Cost");
		if(calccost.length == 1)
			Agent.calculationCost = (int)Math.floor(calccost[0]);
		else if(calccost.length == 3)
			lstSimRange.add(new SimulationRange("Calc Cost", calccost[0], calccost[1], calccost[2]));
		
		Double[] disturb = validateRangeValue(expConfig, "Disturbance");
		if(disturb.length == 1)
			SimulationEngine.disturbanceLevel = disturb[0];
		else if(disturb.length == 3)
			lstSimRange.add(new SimulationRange("Disturbance", disturb[0], disturb[1], disturb[2]));
		
		//Help Overhead
		Integer iHelpOverhead = Utilities.getInteger(expConfig.getPropertyValue("Help Overhead"), null);
		if(iHelpOverhead == null) {
			strErrorMessage += "Invalid Help Overhead.\n";
		} else {
			TeamTask.helpOverhead = iHelpOverhead;
		}
		
		//Assignment Overhead
		Integer iAssignOverhead = Utilities.getInteger(expConfig.getPropertyValue("Assignment Overhead"), null);
		if(iAssignOverhead == null) {
			strErrorMessage += "Invalid Assignment Overhead.\n";
		} else {
			TeamTask.assignmentOverhead = iAssignOverhead;
		}
		
		//Cell Reward
		Integer iCellReward = Utilities.getInteger(expConfig.getPropertyValue("Cell Reward"), null);
		if(iCellReward == null) {
			strErrorMessage += "Invalid Cell Reward.\n";
		} else {
			TeamTask.cellReward = iCellReward;
		}
		
		//Achievement Reward
		Integer iAchieveReward = Utilities.getInteger(expConfig.getPropertyValue("Achievement Reward"), null);
		if(iAchieveReward == null) {
			strErrorMessage += "Invalid Achievement Reward.\n";
		} else {
			TeamTask.achievementReward = iAchieveReward;
		}
		
		//Init Resource Coeff
		Integer iInitResCoeff = Utilities.getInteger(expConfig.getPropertyValue("Initial Resource Coefficient"), null);
		if(iInitResCoeff == null) {
			strErrorMessage += "Invalid Initial Resource Coefficient.\n";
		} else {
			TeamTask.initResCoef = iInitResCoeff;
		}
		
		Integer iNumOfTeams = expConfig.getTeams().size();
		if(iNumOfTeams < 1) {
			strErrorMessage += "Atleast one team is required.\n";
		} else {
			teams = new Team[iNumOfTeams];
			int iIndex = 0;
			for(TeamConfiguration teamConfig : expConfig.getTeams()) {
				switch(teamConfig.getTeamType()) {
					case AdvActionMAP:
						getTeams()[iIndex] = new AdvActionMapTeam();
						getTeams()[iIndex].setLogger(logger, iIndex);
						validateAdvActionMapParams(teamConfig, iIndex + 1, 0);
						break;
					case AdvActionMAPRep:
						getTeams()[iIndex] = new AdvActionMAPRepTeam();
						getTeams()[iIndex].setLogger(logger, iIndex);
						validateAdvActionMapParams(teamConfig, iIndex + 1, 1);
						break;
					case BasicActionMAP:
						getTeams()[iIndex] = new BasicActionMAPTeam();
						getTeams()[iIndex].setLogger(logger, iIndex);
						validateBasicActionMapParams(teamConfig, iIndex + 1);
						break;
					case HIAMAP:
						getTeams()[iIndex] = new HelperInitActionMAPTeam();
						getTeams()[iIndex].setLogger(logger, iIndex);
						validateHelperInitAMAPParams(teamConfig, iIndex + 1);
						break;
					case NoHelp:
						getTeams()[iIndex] = new NoHelpTeam();
						getTeams()[iIndex].setLogger(logger, iIndex);
						break;
					default:
						strErrorMessage += "Invalid team type " + teamConfig.getTeamType() + "\n";
						break;
				}
				iIndex++;
			}
		}
		return strErrorMessage == null || strErrorMessage.trim().length() == 0;
	}
	
	private Double[] validateRangeValue(Configuration config, String strPropertyName)
	{
		boolean bValid = true;
		if(config.getPropertyValue(strPropertyName) != null 
				&& config.getPropertyValue(strPropertyName).contains(",")) {
			String[] strParts = config.getPropertyValue(strPropertyName).split(",");
			Double[] dblValues = new Double[strParts.length];
			for(int i = 0; i < strParts.length; i++) {
				Double dblValue = Utilities.getDouble(strParts[i], null);
				if(dblValue == null) {
					strErrorMessage += "Invalid " + strPropertyName + " value.\n";
					bValid = false;
				} else {
					dblValues[i] = dblValue;
				}
			}
			if(bValid) return dblValues;
		} else {
			Double dValue = Utilities.getDouble(config.getPropertyValue(strPropertyName), null);
			if(dValue == null || dValue < 0 || dValue > 1) {
				strErrorMessage += "Invalid " + strPropertyName + " value.\n";
			} else {
				return new Double[] { dValue };
			}
		}
		return new Double[] { };
	}
	
	public boolean init(ExperimentConfiguration mExpConfig, ExperimentLogger logger)
	{
		this.expConfig = mExpConfig;
		this.logger = logger;
		logCounter = 0; logCounterExp = 0;
		return validateConfiuration();
	}
	
	public boolean setNewParamsForSimulation(boolean bFirst)
	{
		boolean bNewParams = false;
		if(bFirst) {
			for(SimulationRange range : lstSimRange) {
				range.setCurrentValue(range.getFromValue());
				bNewParams = true;
			}
		} else {
			boolean bIncrParent = true;
			for(int i = lstSimRange.size() - 1; i >= 0; i--) {
				if(bIncrParent) {
					bIncrParent = false;
					SimulationRange range = lstSimRange.get(i);
					range.setCurrentValue(range.getCurrentValue() + range.getIncrementValue());//increment
					if(range.getCurrentValue() > range.getToValue()) {
						range.setCurrentValue(range.getFromValue());
						bIncrParent = true;
					}
				}
			}
			bNewParams = !bIncrParent;
		}
		for(SimulationRange range : lstSimRange) {
			if(range.getProperty().equalsIgnoreCase("Unicast Cost")) {
				Team.unicastCost = (int)Math.floor(range.getCurrentValue());
				Team.broadcastCost = Team.unicastCost * (Team.teamSize - 1);
			}
			else if(range.getProperty().equalsIgnoreCase("Calc Cost")) {
				Agent.calculationCost = (int)Math.floor(range.getCurrentValue());
			}
			else if(range.getProperty().equalsIgnoreCase("Disturbance")) {
				SimulationEngine.disturbanceLevel = range.getCurrentValue();
			}
			else if(range.getProperty().equalsIgnoreCase("ADVAMAP-WLL")) {
				AdvActionMAPAgent.WLL = range.getCurrentValue();
			}
			else if(range.getProperty().equalsIgnoreCase("ADVAMAP-Proximity Bias")) {
				AdvActionMAPAgent.impFactor = (int)Math.floor(range.getCurrentValue());
			}
			else if(range.getProperty().equalsIgnoreCase("ADVAMAPREP-WLL")) {
				AdvActionMAPRepAgent.WLL = range.getCurrentValue();
			}
			else if(range.getProperty().equalsIgnoreCase("ADVAMAPREP-Proximity Bias")) {
				AdvActionMAPRepAgent.impFactor = (int)Math.floor(range.getCurrentValue());
			}
			else if(range.getProperty().equalsIgnoreCase("ADVAMAPREP-WREP")) {
				AdvActionMAPRepAgent.WREP = range.getCurrentValue();
			}
			else if(range.getProperty().equalsIgnoreCase("HIAMAP-WHH")) {
				HelperInitActionMAPAgent.WHH = range.getCurrentValue();
			}
			else if(range.getProperty().equalsIgnoreCase("HIAMAP-Proximity Bias")) {
				HelperInitActionMAPAgent.impFactor = (int)Math.floor(range.getCurrentValue());
			}
			//System.out.println(range.toString());
		}
		//System.out.println("------------------------------------------------------------");
		return bNewParams;
	}
	
	public String getSimulationParametersText() {
		String strParams = "";
		for(SimulationRange range : lstSimRange) {
			strParams += range.toString() + "\n";
		}
		return strParams;
	}
	
	public List<List<AgentStats>> getAgentStatistics()
	{
		if(getTeams() == null) return null;
		List<List<AgentStats>> lstAgentStats = new ArrayList<List<AgentStats>>();
		for(Team team : getTeams()) {
			lstAgentStats.add(team.getAgentStatistics(this));
		}
		return lstAgentStats;
	}
	
	public List<String> getTeamLogs()
	{
		if(getTeams() == null) return null;
		List<String> lstTeamLogs = new ArrayList<String>();
		for(int i = 0; i < teams.length; i++) {
			lstTeamLogs.add("");
		}
		String strValue; int id;
		while(logger.hasNext(logCounter)) {
			LogEvent event = logger.getNextEvent(logCounter);
			if(event != null) {
				if(event.getType() == LogType.Team || event.getType() == LogType.Agent) {
					logCounter = event.getIndex();
					id = Integer.parseInt(event.getId().toString().split("-")[0]);
					strValue = event.getValue() + "";
					lstTeamLogs.set(id, lstTeamLogs.get(id) + strValue + (strValue.endsWith("\n") ? "" : "\n"));
				}
			}
			logCounter++;
		}
		logger.startArchive();
		return lstTeamLogs;
	}
	
	public List<SimulationRange> getSimulationParameters()
	{
		List<SimulationRange> lstCopy = new ArrayList<SimulationRange>();
		for(SimulationRange simValue : lstSimRange) {
			lstCopy.add(new SimulationRange(simValue.getProperty(), simValue.getFromValue(), simValue.getIncrementValue(), simValue.getToValue()));
			lstCopy.get(lstCopy.size() - 1).setCurrentValue(simValue.getCurrentValue());
		}
		return lstCopy;
	}
	
	public String getEngineLog()
	{
		String strLogText = "", strValue;
		while(logger.hasNext(logCounterExp)) {
			LogEvent event = logger.getNextEvent(logCounterExp);
			if(event != null) {
				if(event.getType() == LogType.Engine) {
					logCounterExp = event.getIndex();
					strValue = event.getValue() + "";
					strLogText += strValue + (strValue.endsWith("\n") ? "" : "\n");
				}
			}
			logCounterExp++;
		}
		return strLogText;
	}
	
	public List<Integer> getTeamScores()
	{
		if(getTeams() == null) return null;
		List<Integer> lstTeamScores = new ArrayList<Integer>();
		for(Team team : getTeams()) {
			lstTeamScores.add(team.getTeamScore());
		}
		return lstTeamScores;
	}
	
	public void changeConfig(ExperimentConfiguration mExpConfig)
	{
		
	}
	
	public void pause()
	{
		
	}
	
	public void resume()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	private void validateAdvActionMapParams(TeamConfiguration teamConfig, int index, int childType)
	{
		String suffix = "for AdvActionMap [Team - " + index + "].\n";
		if(childType == 1)
			suffix = "for AdvActionMapReplaning [Team - " + index + "].\n";
		
		//Request Threshold
		Integer iRequestThreshold = Utilities.getInteger(teamConfig.getPropertyValue("Request Threshold"), null);
		if(iRequestThreshold == null) {
			strErrorMessage += "Invalid Request Threshold " + suffix;
		} else {
			if(childType == 1)
				AdvActionMAPRepAgent.requestThreshold = iRequestThreshold;
			else
				AdvActionMAPAgent.requestThreshold = iRequestThreshold;
		}
		
		//Lowcost Threshold
		Integer iLowCostThreshold = Utilities.getInteger(teamConfig.getPropertyValue("Lowcost Threshold"), null);
		if(iLowCostThreshold == null) {
			strErrorMessage += "Invalid Lowcost Threshold " + suffix;
		} else {
			if(childType == 1)
				AdvActionMAPRepAgent.lowCostThreshold = iLowCostThreshold;
			else
				AdvActionMAPAgent.lowCostThreshold = iLowCostThreshold;
		}
		
		//Importance Version
		Integer iImpVersion = Utilities.getInteger(teamConfig.getPropertyValue("Importance Version"), null);
		if(iImpVersion == null) {
			strErrorMessage += "Invalid Importance Version " + suffix;
		} else {
			if(childType == 1)
				AdvActionMAPRepAgent.importanceVersion = iImpVersion;
			else
				AdvActionMAPAgent.importanceVersion = iImpVersion;
		}
		
		//WLL
		Double[] wll = validateRangeValue(teamConfig, "WLL");
		if(wll.length == 1) {
			if(childType == 1)
				AdvActionMAPRepAgent.WLL = wll[0];
			else
				AdvActionMAPAgent.WLL = wll[0];
		}
		else if(wll.length == 3) {
			if(childType == 1)
				lstSimRange.add(new SimulationRange("ADVAMAPREP-WLL", wll[0], wll[1], wll[2]));
			else
				lstSimRange.add(new SimulationRange("ADVAMAP-WLL", wll[0], wll[1], wll[2]));
		}
		
		//Proximity Bias
		Double[] proxBias = validateRangeValue(teamConfig, "Proximity Bias");
		if(proxBias.length == 1) {
			if(childType == 1)
				AdvActionMAPRepAgent.impFactor = (int)Math.floor(proxBias[0]);
			else
				AdvActionMAPAgent.impFactor = (int)Math.floor(proxBias[0]);
		}
		else if(proxBias.length == 3) {
			if(childType == 1)
				lstSimRange.add(new SimulationRange("ADVAMAPREP-Proximity Bias", proxBias[0], proxBias[1], proxBias[2]));
			else
				lstSimRange.add(new SimulationRange("ADVAMAP-Proximity Bias", proxBias[0], proxBias[1], proxBias[2]));
		}
		
		//WREP
		if(childType == 1) {
			Double[] wrep = validateRangeValue(teamConfig, "WREP");
			if(wrep.length == 1) {
				AdvActionMAPRepAgent.WREP = wrep[0];
			}
			else if(wrep.length == 3) {
				lstSimRange.add(new SimulationRange("ADVAMAPREP-WREP", wrep[0], wrep[1], wrep[2]));
			}
		}
	}
	
	private void validateBasicActionMapParams(TeamConfiguration teamConfig, int index)
	{
		String suffix = "for BasicActionMap [Team - " + index + "].\n";
		
		//Request Threshold
		Integer iRequestThreshold = Utilities.getInteger(teamConfig.getPropertyValue("Request Threshold"), null);
		if(iRequestThreshold == null) {
			strErrorMessage += "Invalid Request Threshold " + suffix;
		} else {
			BasicActionMAPAgent.requestThreshold = iRequestThreshold;
		}	
	}
	
	private void validateHelperInitAMAPParams(TeamConfiguration teamConfig, int index)
	{
		String suffix = "for HelperInitAMAP [Team - " + index + "].\n";
		
		//Request Threshold
		Integer iRequestThreshold = Utilities.getInteger(teamConfig.getPropertyValue("Request Threshold"), null);
		if(iRequestThreshold == null) {
			strErrorMessage += "Invalid Request Threshold " + suffix;
		} else {
			HelperInitActionMAPAgent.requestThreshold = iRequestThreshold;
		}
		
		//Importance Version
		Integer iImpVersion = Utilities.getInteger(teamConfig.getPropertyValue("Importance Version"), null);
		if(iImpVersion == null) {
			strErrorMessage += "Invalid Importance Version " + suffix;
		} else {
			HelperInitActionMAPAgent.importanceVersion = iImpVersion;
		}
		
		//WHH
		Double[] whh = validateRangeValue(teamConfig, "WHH");
		if(whh.length == 1)
			HelperInitActionMAPAgent.WHH = whh[0];
		else if(whh.length == 3)
			lstSimRange.add(new SimulationRange("HIAMAP-WHH", whh[0], whh[1], whh[2]));
		
		//Proximity Bias
		Double[] proxBias = validateRangeValue(teamConfig, "Proximity Bias");
		if(proxBias.length == 1)
			HelperInitActionMAPAgent.impFactor = (int)Math.floor(proxBias[0]);
		else if(proxBias.length == 3)
			lstSimRange.add(new SimulationRange("HIAMAP-Proximity Bias", proxBias[0], proxBias[1], proxBias[2]));
	}
	
	public class SimulationRange
	{
		private String property;
		private Double fromValue;
		private Double incrValue;
		private Double toValue;
		private Double currentValue;
		
		public SimulationRange(String property, Double fromValue, Double incrValue, Double toValue)
		{
			this.property = property;
			this.fromValue = fromValue;
			this.incrValue = incrValue;
			this.toValue = toValue;
		}
		public String getProperty() {
			return property;
		}
		public Double getFromValue() {
			return fromValue;
		}
		public Double getIncrementValue() {
			return incrValue;
		}
		public Double getToValue() {
			return toValue;
		}
		public Double getCurrentValue() {
			return currentValue;
		}
		public void setCurrentValue(Double currentValue) {
			this.currentValue = currentValue;
		}
		public String toString()
		{
			String strText = "";
			strText += property + " = " + currentValue + "\t[" + fromValue + " > " + incrValue + " > " + toValue + "]";
			return strText;
		}
	}
	
	public class AgentStats
	{
		int id;
		int initX;
		int initY;
		int goalX;
		int goalY;
		int currX;
		int currY;
		int initResources;
		int remainResources;
		String lastAction;
		List<int[]> path;
		
		public AgentStats(int id, int initX, int initY, int goalX, int goalY, int currX, int currY, int initResources, int remainResources, String lastAction, List<int[]> path)
		{
			this.id = id;
			this.initX = initX;
			this.initY = initY;
			this.goalX = goalX;
			this.goalY = goalY;
			this.currX = currX;
			this.currY = currY;
			this.initResources = initResources;
			this.remainResources = remainResources;
			this.lastAction = lastAction;
			this.path = path;
		}
		public int getId() {
			return id;
		}
		public int getInitX() {
			return initX;
		}
		public int getInitY() {
			return initY;
		}
		public int getGoalX() {
			return goalX;
		}
		public int getGoalY() {
			return goalY;
		}
		public int getCurrX() {
			return currX;
		}
		public int getCurrY() {
			return currY;
		}
		public int getInitResources() {
			return initResources;
		}
		public int getRemainResources() {
			return remainResources;
		}
		public String getLastAction() {
			return lastAction;
		}
		public List<int[]> getPath() {
			return path;
		}
	}
}