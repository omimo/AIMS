package massim.ui.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;

import javax.swing.SwingWorker;

import massim.Experiment;
import massim.Experiment.SimulationRange;
import massim.ExperimentLogger;
import massim.SimulationEngine;
import massim.Experiment.AgentStats;

public class ConfigConnector extends SwingWorker<Void, Void> {
	private ExperimentConfiguration expConfig;
	private ExperimentLogger logger;
	private Experiment experiment;
	private String strErrorMessage;
	//private ActionListener updateInterfaceListener;
	private SimulationEngine simEng;
	private int threadDelay;
	private int currentRun;
	private volatile boolean pauseSimulation, keepInDelaySleep;
	private boolean isActive = false;
	private StepType stepType = StepType.None;
	Integer iNumOfRuns, totalRuns; 
	List<SimulationRange> simParameters;
	private boolean isBatchMode = false;
	//private long threadId;
	
	public String getErrorMessage() {
		return strErrorMessage;
	}
	
	public void setThreadDelay(int threadDelay) {
		this.threadDelay = threadDelay;
		//System.out.println("1threadid " + threadId);
		//System.out.println("1threadid " + Thread.currentThread().getId());
		//this.notify();
	}
	
	public long initExperiment(ExperimentConfiguration expConfig)
	{
		long expId = Calendar.getInstance().getTimeInMillis();
		this.expConfig = expConfig;
		this.logger = new ExperimentLogger(expId);
		this.experiment = new Experiment();
		return expId;
	}
	
	public void changeConfig(ExperimentConfiguration expConfig)
	{
		experiment.changeConfig(expConfig);
	}
	
	public boolean initSimulation()
	{
		strErrorMessage = "";
		if(experiment.init(expConfig, getLogger())) {
			return true;
		} else strErrorMessage = experiment.getErrorMessage();
		return false;
	}
	
	public boolean runSimulation()
	{
		this.execute();
		isActive = true;
		return true;
	}
	
	public void updateAgentStats(RunStatus runStatus)
	{
		if(runStatus == RunStatus.RunInit) {
			currentRun++;
			String strRun = "";
			if(isBatchMode())
				strRun = "Finished Runs: " + totalRuns + "\n";
			strRun += experiment.getSimulationParametersText() + "\nCurrent Run# : " + currentRun;
			this.firePropertyChange("RunInit", null, strRun);
		}
		
		if(runStatus == RunStatus.RunComplete) {
			List<Integer> lstTeamScore = experiment.getTeamScores();
			this.firePropertyChange("TeamScore", null, lstTeamScore);
		}
		
		if(isBatchMode()) return;
		
		if(isInStepMode())
		{
			if(getStepType() == StepType.Run && 
					((currentRun < iNumOfRuns && runStatus == RunStatus.RunComplete)
					|| (currentRun == iNumOfRuns && runStatus == RunStatus.RunSetComplete))) {
				
				pauseSimulation = true;
				this.firePropertyChange("Step", null, "AgentStats");
				this.firePropertyChange("AgentStats", null, experiment.getAgentStatistics());
				this.firePropertyChange("Step", null, "Board");
				this.firePropertyChange("Board", null, simEng.getBoard());
				if(getLogger().isLogOn()) {
					this.firePropertyChange("Step", null, "Log");
					this.firePropertyChange("Log", null, experiment.getEngineLog());
					this.firePropertyChange("Step", null, "TeamLog");
					this.firePropertyChange("TeamLog", null, experiment.getTeamLogs());
				}
				this.firePropertyChange("Step", null, "End");
				sleepForPauseOrDelay();
			}
			else if(getStepType() == StepType.Round) {
				
				pauseSimulation = true;
				this.firePropertyChange("Step", null, "AgentStats");
				this.firePropertyChange("AgentStats", null, experiment.getAgentStatistics());
				this.firePropertyChange("Step", null, "Board");
				this.firePropertyChange("Board", null, simEng.getBoard());
				if(getLogger().isLogOn()) {
					this.firePropertyChange("Step", null, "Log");
					this.firePropertyChange("Log", null, experiment.getEngineLog());
					this.firePropertyChange("Step", null, "TeamLog");
					this.firePropertyChange("TeamLog", null, experiment.getTeamLogs());
				}
				this.firePropertyChange("Step", null, "End");
				sleepForPauseOrDelay();
			}
			else if(getStepType() == StepType.Experiment && runStatus == RunStatus.RunSetComplete) {
				
				pauseSimulation = true;
				if(getLogger().isLogOn()) {
					this.firePropertyChange("Step", null, "Log");
					this.firePropertyChange("Log", null, experiment.getEngineLog());
					this.firePropertyChange("Step", null, "TeamLog");
					this.firePropertyChange("TeamLog", null, experiment.getTeamLogs());
				}
				this.firePropertyChange("Step", null, "End");
				sleepForPauseOrDelay();
			}
		}
		else
		{
			if(threadDelay > 0) {
				this.firePropertyChange("AgentStats", null, experiment.getAgentStatistics());
				this.firePropertyChange("Board", null, simEng.getBoard());
				if(getLogger().isLogOn()) {
					this.firePropertyChange("Log", null, experiment.getEngineLog());
					this.firePropertyChange("TeamLog", null, experiment.getTeamLogs());
				}
			}
			sleepForPauseOrDelay();
		}
		
		if(isInStepMode())
		{
			this.firePropertyChange("Step", null, "Init");
		}
	}
	
	private void sleepForPauseOrDelay()
	{
		if(isBatchMode()) return;
		
		this.firePropertyChange("Waiting", null, null);
		do{
			try {
				if(isInStepMode()) {
					if(pauseSimulation) {
						Thread.sleep(50);
					} 
				} else {
					int totalDelay = 0;
					keepInDelaySleep = true;
					while(keepInDelaySleep && totalDelay < (threadDelay * 1000))
					{
						Thread.sleep(500);
						totalDelay += 500;
					}
				}
			} catch (InterruptedException e1) { }
		} while(pauseSimulation);
	}
	
	public void pauseSimulation()
	{
		experiment.pause();
		pauseSimulation = true;
	}
	
	public void resumeSimulation()
	{
		experiment.resume();
		pauseSimulation = false;
		keepInDelaySleep = false;
	}
	
	public void stopExperiment()
	{
		cancel(true);
		experiment.stop();
	}
	
	public void nextRound()
	{
		nextStep(StepType.Round);
	}
	
	public void clearStepMode()
	{
		nextStep(StepType.None);
	}
	
	public void nextRun()
	{
		nextStep(StepType.Run);
	}
	
	public void nextMatch()
	{
		nextStep(StepType.Run);
	}
	
	public void nextExperiment()
	{
		nextStep(StepType.Experiment);
	}
	
	private void nextStep(StepType stepType)
	{
		pauseSimulation = false;
		keepInDelaySleep = false;
		setStepType(stepType);
	}

	public ExperimentLogger getLogger() {
		return logger;
	}
	
	/*public void setUpdateInterfaceListener(ActionListener updateInterfaceListener) {
		this.updateInterfaceListener = updateInterfaceListener;
	}*/

	@Override
	protected Void doInBackground() {
		try {
			strErrorMessage = "";
			iNumOfRuns = Integer.parseInt(expConfig.getPropertyValue("Number of Runs"));
			totalRuns = 0;
			if(isBatchMode()) {
				iNumOfRuns = Math.min(iNumOfRuns, 100);
				setLogOn(false);
			}
			boolean bContinue = experiment.setNewParamsForSimulation(true);
			pauseSimulation = false;
			keepInDelaySleep = false;
			while(bContinue && !isStopped()) {
				simEng = new SimulationEngine(experiment.getTeams());
				simEng.setLogger(logger);
				simEng.initializeExperiment(iNumOfRuns);
				
				simEng.setRunInitializedListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updateAgentStats(RunStatus.RunInit);
					}
				});
				if(!isBatchMode()) {
					simEng.setRoundCompleteListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateAgentStats(RunStatus.RoundComplete);
						}
					});
					simEng.setRunCompleteListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							updateAgentStats(RunStatus.RunComplete);
						}
					});
				}
				currentRun = 0;
				int[] finalScores = simEng.runExperiment();
				simParameters = experiment.getSimulationParameters();
				firePropertyChange("ExpScores", null, finalScores);
				updateAgentStats(RunStatus.RunSetComplete);
				
				bContinue = experiment.setNewParamsForSimulation(false);
				if(!bContinue && isBatchMode()) {
					totalRuns += iNumOfRuns;
					if(totalRuns < Integer.parseInt(expConfig.getPropertyValue("Number of Runs"))) {
						iNumOfRuns = Math.min(iNumOfRuns, Integer.parseInt(expConfig.getPropertyValue("Number of Runs")) - totalRuns);
						bContinue = experiment.setNewParamsForSimulation(true);
					}
				}
				if(bContinue) {
					experiment.initForNewRun();
				}
			}
			isActive = false;
			if(!isStopped())
				firePropertyChange("SimComplete", null, null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private boolean isStopped() {
		return isCancelled() || Thread.currentThread().isInterrupted();
	}
	
	public boolean isPaused() {
		return pauseSimulation;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public boolean isInStepMode() {
		return getStepType() != StepType.None;
	}
	
	public void setLogOn(boolean bLogOn)
	{
		getLogger().setLogOn(bLogOn);
	}
	
	public StepType getStepType() {
		return stepType;
	}
	
	public void setStepType(StepType stepType) {
		this.stepType = stepType;
	}
	
	public List<SimulationRange> getCurrentSimulationParameters() {
		return simParameters;
	}

	public boolean isBatchMode() {
		return isBatchMode;
	}

	public void setBatchMode(boolean isBatchMode) {
		this.isBatchMode = isBatchMode;
	}

	private enum RunStatus
	{
		RunInit,
		RoundComplete,
		RunComplete,
		RunSetComplete
	}
	
	public enum StepType 
	{
		None,
		Round,
		Run,
		Match,
		Experiment
	}
}