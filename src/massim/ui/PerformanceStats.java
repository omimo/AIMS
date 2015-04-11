package massim.ui;

import java.util.Date;

public class PerformanceStats {
	private Date dateStart;
	private Date dateEnd;
	private String logName;
	
	public PerformanceStats(String mLogName)
	{
		dateStart = new Date();
		logName = mLogName;
	}
	
	public void startTracking()
	{
		dateStart = new Date();
	}
	
	public void endAndPrint()
	{
		System.out.println("#####  " + logName  +  " - Performance statistics #####");
		
		dateEnd = new Date();
		double diffInSeconds = (dateEnd.getTime() - dateStart.getTime()) / 1000.0;
		System.out.println("Total run-time [Seconds]:" + diffInSeconds);
		
		int mb = 1024 * 1024;
    	Runtime runtime = Runtime.getRuntime();
    	
        //Print used memory
        System.out.println("Used Memory [MB]:"
            + (runtime.totalMemory() - runtime.freeMemory()) / mb);
        //Print free memory
        System.out.println("Free Memory [MB]:"
            + runtime.freeMemory() / mb);
        //Print total available memory
        System.out.println("Total Memory [MB]:" + runtime.totalMemory() / mb);
        //Print Maximum available memory
        System.out.println("Max Memory [MB]:" + runtime.maxMemory() / mb);
	}
}
