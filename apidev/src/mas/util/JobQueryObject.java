package mas.util;

import jade.core.AID;
import mas.job.job;
import mas.job.jobOperation;

public class JobQueryObject {

	private job currentJob;
	private AID currentMachine;
	
	public static class Builder{
		job currJob;
		AID currMachine;
		
		public Builder(){
			
		}
		
		public Builder currentJob(job j){
			currJob=j;
			return this;
		}
		
		public Builder currentMachine(AID machineAID){
			currMachine=machineAID;
			return this;
		}
		
		public JobQueryObject build(){
			return new JobQueryObject(this);
		}
	}
	
	private JobQueryObject(Builder builder) {
		currentJob=builder.currJob;
		currentMachine=builder.currMachine;
	}
		
	public AID getCurrentMachine() {
		return currentMachine;
	}

	public job getCurrentJob() {
		return currentJob;
	}

	
}
