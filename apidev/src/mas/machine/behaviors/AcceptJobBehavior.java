package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.job.job;
import mas.machine.MachineStatus;
import mas.machine.Simulator;
import mas.util.MessageIds;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AcceptJobBehavior extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private transient MessageTemplate jobMsgTemplate;
	private transient Logger log;
	private transient job jobToProcess;
	private transient Simulator machineSimulator;

	public AcceptJobBehavior() {
		log = LogManager.getLogger();
		jobMsgTemplate = MessageTemplate.
				MatchConversationId(MessageIds.SendJob);
	}

	@Override
	public void action() {
		machineSimulator = (Simulator) getParent().getDataStore().get(Simulator.simulatorStoreName);
		if(machineSimulator.getStatus() != MachineStatus.FAILED) {
			try {
//				log.info("Job accepter running");
				ACLMessage msg = myAgent.receive(jobMsgTemplate);
				if (msg != null) {
					this.jobToProcess = (job) msg.getContentObject();
					
					log.info(" Job No : '" + jobToProcess.getJobNo()+ "'accepted" );
					
					AddJobBehavior addjob = new AddJobBehavior(this.jobToProcess);
					addjob.setDataStore(this.getDataStore());
					myAgent.addBehaviour(addjob);
				} 
				else {
					block();
				}
			} catch (UnreadableException e3) {
				log.debug("Error in parsing job");
			}
		}
	}
}
