package mas.maintenance.behavior;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import mas.job.job;
import mas.util.MessageIds;

public class SendMaintenanceJobBehavior extends Behaviour{

	/**
	 * 
	 */
	
	private int step = 0;
	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private job j;
	private AID bbAgent;
	
	public SendMaintenanceJobBehavior(job jobToSend, AID blackboard) {
		this.j = jobToSend;
		this.bbAgent = blackboard;
	}
	
	@Override
	public void action() {
		msg = new ACLMessage(ACLMessage.INFORM);
		try {
			msg.setContentObject(this.j);
			msg.addReceiver(this.bbAgent);
			msg.setConversationId(MessageIds.maintenanceJob);
			myAgent.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}

}
