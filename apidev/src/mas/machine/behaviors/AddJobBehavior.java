package mas.machine.behaviors;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.tools.logging.LogManager;
import mas.job.job;
import mas.machine.Methods;
import mas.machine.Simulator;

import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.SchemaDocumentImpl.SchemaImpl;

public class AddJobBehavior extends OneShotBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private job comingJob;
	boolean IsJobComplete;
	private String maintJobID = "0";
	private Logger log;

	public AddJobBehavior(job comingJob) {
		this.comingJob = comingJob;
		this.IsJobComplete = false;
	}

	public void action() {
//		log = LogManager.g
		long tim = System.currentTimeMillis() / 1000;

		comingJob.setProcessingTime((long) (
				Methods.normalRandom(comingJob.getProcessingTime(),Simulator.percent )+
				Methods.getLoadingTime(Simulator.meanLoadingTime, Simulator.sdLoadingTime) +
				Methods.getunloadingTime(Simulator.meanUnloadingTime, Simulator.sdUnloadingTime)));

		double processingTime;
		processingTime = comingJob.getProcessingTime();

		while(!IsJobComplete) {

			long next_failure_time = failed_c.abs_next_failure_time- System.currentTimeMillis();
			if(next_failure_time < 0){
				while(next_failure_time<0){
					comp_fail.action();
					next_failure_time = failed_c.abs_next_failure_time- System.currentTimeMillis();
				}
				System.out.println("nft="+next_failure_time);

			}
			System.out.println("t="+processingTime+" next_failure "+(next_failure_time/1000.0)+"newjob.getjobID() "+newjob.getjobID());

			//job will complete after occurrence of 'just next failure'
			if(processingTime > 0 &&
					next_failure_time > 0 &&
					processingTime>(next_failure_time/1000.0) &&
					comingJob.getJobID().equals(maintJobID)) {	

				//job will be processed for next_failure_time->then will be interrupted.

				processingTime = processingTime - (next_failure_time/1000.0); 

				block(next_failure_time); 
				//After recieving 'maintainance complete' message,
				comp_fail.action(); //sets failstatus=false inside
				//currently machine can fail only when it is processing job.
			}

			else if(processingTime < next_failure_time) {
				IsJobComplete = true;
				myAgent.addBehaviour(new WakerBehaviour(myAgent, (long) (processingTime * 1000)) {
					private static final long serialVersionUID = 1L;

					protected void handleElapsedTimeout() {
						myAgent.addBehaviour(new ProcessJobBehavior(comingJob));
					}
				});
			}
		}
	}
}
