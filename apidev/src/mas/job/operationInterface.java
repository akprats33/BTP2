package mas.job;

import java.util.Date;

public interface operationInterface {
	public long getProcessingTime();
	public OperationType getJobOperationType();
	public Date getLocalDueDate();
	public Date getGlobalDueDate();
	
}
