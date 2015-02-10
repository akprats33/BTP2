package mas.machine.behaviors;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import mas.job.job;
import mas.machine.Simulator;
import mas.util.MessageIds;

public class HandleCompletedJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;
	private int step = 0;
	private ACLMessage msg;

	public HandleCompletedJobBehavior(job comingJob) {
		
		this.comingJob = comingJob;
		this.log = LogManager.getLogger();
		this.msg = new ACLMessage(ACLMessage.INFORM);
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			try {
				msg.setContentObject(comingJob);
				msg.setConversationId(MessageIds.completedJobFromMachine);
				msg.addReceiver(Simulator.blackboardAgent);
				myAgent.send(msg);
				
				log.info("Job no: '"+comingJob.getJobNo() + 
						"' --> completion : " + comingJob.getCompletionTime() + 
						"Starting time : " + comingJob.getStartTime());
				log.info("sending completed job to blackboard");
				
				step = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
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
