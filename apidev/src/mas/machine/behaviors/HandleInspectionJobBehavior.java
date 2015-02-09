package mas.machine.behaviors;

import mas.job.job;
import mas.util.MessageIds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleInspectionJobBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	boolean IsJobComplete;
	private Logger log;
	private int step = 0;
	private MessageTemplate InspectionDataMsgTemplate;
	private ACLMessage inspectionStartMsg;
	private ACLMessage inspectioneDataMsg;

	public HandleInspectionJobBehavior(job comingJob) {
		this.comingJob = comingJob;
		this.IsJobComplete = false;
		log = LogManager.getLogger();
		InspectionDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.machinePrevMaintenanceData);

	}
	
	@Override
	public void action() {
		
	}

	@Override
	public boolean done() {
		return step >= 2;
	}

}
