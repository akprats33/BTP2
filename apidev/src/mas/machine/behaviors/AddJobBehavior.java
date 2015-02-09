package mas.machine.behaviors;

import jade.core.behaviours.OneShotBehaviour;
import mas.job.job;
import mas.machine.MachineStatus;
import mas.machine.Methods;
import mas.machine.Simulator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddJobBehavior extends OneShotBehaviour {

	private static final long serialVersionUID = 1L;
	private job comingJob;
	boolean IsJobComplete;
	private String maintJobID = "0";
	private Logger log;
	// time step in milliseconds
	private long TIME_STEP = 10;

	public AddJobBehavior(job comingJob) {
		this.comingJob = comingJob;
		this.IsJobComplete = false;
	}

	public void action() {
		log = LogManager.getLogger();
		double processingTime;

		if(! comingJob.getJobID().equals(maintJobID)) {
			
			log.info("Job No : "+ comingJob.getJobNo() +" loading");
			
			comingJob.setProcessingTime((long) (
					Methods.normalRandom(comingJob.getProcessingTime(),Simulator.percent )+
					Methods.getLoadingTime(Simulator.meanLoadingTime, Simulator.sdLoadingTime) +
					Methods.getunloadingTime(Simulator.meanUnloadingTime, Simulator.sdUnloadingTime)));

			processingTime = comingJob.getProcessingTime();
			Simulator.status = MachineStatus.PROCESSING;

			while(!IsJobComplete) {

				if(processingTime > 0 &&
						Simulator.status != MachineStatus.FAILED ) {

					processingTime = processingTime - TIME_STEP; 
					block(TIME_STEP); 
				}

				else if( processingTime <= 0) {
					IsJobComplete = true;
					log.info("Job No:" + comingJob.getJobNo() + " completed");
					myAgent.addBehaviour(new ProcessJobBehavior(comingJob));
					Simulator.status = MachineStatus.IDLE;
				}
			}
		}
		else if (comingJob.getJobID().equals(maintJobID)){
			log.info("Maintenance Job loading");
		}
	}
}
