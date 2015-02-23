package mas.job;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;

public class jobOperation implements operationInterface,Serializable {
	
	private static final long serialVersionUID = 1L;
	private long processingTime;
	private ArrayList<jobDimension> jDims;
	private ArrayList<jobAttribute> jAttributes;
	private Date localDueDate;
	private Date globalDueDate;
	private OperationType jobOperationType;

	@Override
	public long getProcessingTime() {
		return processingTime;
	}
	
	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}

	public OperationType getJobOperationType() {
		return jobOperationType;
	}

	public void setJobOperationType(OperationType jobOperationType) {
		this.jobOperationType = jobOperationType;
	}

	@Override
	public java.util.Date getLocalDueDate() {
		return localDueDate;
	}

	@Override
	public java.util.Date getGlobalDueDate() {
		return globalDueDate;
	}

}
