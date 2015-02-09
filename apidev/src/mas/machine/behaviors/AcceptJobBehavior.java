package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.job.job;
import mas.machine.MachineStatus;
import mas.machine.Simulator;
import mas.util.ID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AcceptJobBehavior extends CyclicBehaviour {

	private static final long serialVersionUID = 1L;
	private MessageTemplate jobMsgTemplate;
	private Logger log;
	private job jobToProcess;
	private Simulator sim;

	public AcceptJobBehavior() {
		log = LogManager.getLogger();
		jobMsgTemplate = MessageTemplate.
				MatchConversationId(ID.Machine.Service);
	}

	@Override
	public void action() {
		sim = (Simulator) getDataStore().get(Simulator.mySimulator);
		if(sim.getStatus() != MachineStatus.FAILED) {
			try {
				ACLMessage msg = myAgent.receive(jobMsgTemplate);
				if (msg != null) {
					this.jobToProcess = (job) msg.getContentObject();
					log.info(" Job accepted with Processing time= " +
							jobToProcess.getProcessingTime());
					
					myAgent.addBehaviour(new AddJobBehavior(jobToProcess));
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
