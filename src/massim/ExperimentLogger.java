package massim;

import java.util.ArrayList;
import java.util.List;

public class ExperimentLogger {
	private long expId;
	private List<LogEvent> lstEvents;
	private List<LogEvent> lstEventsRead;
	long logCounter  = 10000;
	private boolean logOn;
	private ArchiveThread archThead;
	public ExperimentLogger(long expId)
	{
		this.expId = expId;
		lstEvents = new ArrayList<LogEvent>();
		lstEventsRead = new ArrayList<LogEvent>();
		logCounter  = 10000;
		logOn = true;
	}
	
	public void logEvent(LogEvent event)
	{
		if(isLogOn())
			lstEvents.add(event);
	}
	
	public void logEvent(LogType logType, Object id, Object value)
	{
		if(isLogOn()) {
		//	System.out.println(Thread.currentThread().getId() + " " + logType + " " + id + " " + value);
			logCounter++;
			lstEvents.add(new LogEvent(logCounter, logType, id, value));
		}
	}
	
	public boolean hasNext(long index)
	{
		if(isLogOn())
			return logCounter > index;
		return false;
	}
	
	public LogEvent getNextEvent(long index)
	{
		if(isLogOn()) {
			if(archThead != null) {
				archThead.cancel();
				archThead = null;
				try { Thread.sleep(500); } catch (InterruptedException e) { }
			}
			int count = 0;
			while(lstEvents.size() > count) {
				if(lstEvents.get(count) != null && lstEvents.get(count).getIndex() >= index) {
					return lstEvents.get(count);
				}
				count++;
			}
			
			//look in archive
			count = 0;
			while(lstEventsRead.size() > count) {
				if(lstEventsRead.get(count).getIndex() >= index) {
					return lstEventsRead.get(count);
				}
				count++;
			}
		}
		return null;
	}
	
	public boolean isLogOn() {
		return logOn;
	}

	public void setLogOn(boolean logOn) {
		this.logOn = logOn;
	}
	
	public void startArchive()
	{
		archThead = new ArchiveThread();
		archThead.start();
	}

	public class LogEvent
	{
		private long index;
		private LogType type;
		private Object id;
		private Object value;
		public LogEvent(long index, LogType logType, Object id, Object value)
		{
			this.setIndex(index);
			this.setType(logType);
			this.setId(id);
			this.setValue(value);
		}
		public long getIndex() {
			return index;
		}
		public void setIndex(long index) {
			this.index = index;
		}
		public LogType getType() {
			return type;
		}
		public void setType(LogType type) {
			this.type = type;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public Object getId() {
			return id;
		}
		public void setId(Object id) {
			this.id = id;
		}
	}
	
	public enum LogType
	{
		Engine,
		Team,
		Agent
	}
	
	private class ArchiveThread extends Thread
	{
		private volatile boolean cancel = false;
		public ArchiveThread() {
		}
		
		@Override
		public void run() {
			super.run();
			
			while(lstEvents.size() > 1000 && !cancel) {
				LogEvent event = lstEvents.get(0);
				lstEventsRead.add(event);
				if(!cancel)
					lstEvents.remove(0);
			}
		}
		
		public void cancel()
		{
			cancel = true;
		}
	}
}
