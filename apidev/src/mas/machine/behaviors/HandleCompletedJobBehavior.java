package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.behaviours.Behaviour;
import mas.job.job;
import mas.machine.Simulator;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

public class HandleCompletedJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;
	private int step = 0;

	public HandleCompletedJobBehavior(job comingJob) {

		this.comingJob = comingJob;
		this.log = LogManager.getLogger();
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			/**
			 * update zonedata for completed jobs from machine
			 */
			ZoneDataUpdate completedJobUpdate = new ZoneDataUpdate(
					ID.Machine.ZoneData.myHealth,
					comingJob);

			completedJobUpdate.send(Simulator.blackboardAgent ,
					completedJobUpdate, myAgent);

			log.info("Job no: '"+comingJob.getJobNo() + 
					"' --> completion : " + comingJob.getCompletionTime() + 
					"Starting time : " + comingJob.getStartTime());
			log.info("sending completed job to blackboard");

			step = 1;

			break;

		case 1:
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 1;
	}
}
