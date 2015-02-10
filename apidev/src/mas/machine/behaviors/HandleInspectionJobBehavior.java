package mas.machine.behaviors;

import java.util.StringTokenizer;

import mas.job.job;
import mas.machine.MachineStatus;
import mas.machine.Simulator;
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
	private String inspectionData;
	private StringTokenizer token;
	private Simulator mySim;
	
	public HandleInspectionJobBehavior(job comingJob) {
		
		this.comingJob = comingJob;
		this.IsJobComplete = false;
		log = LogManager.getLogger();
		InspectionDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.machinePrevMaintenanceData);

	}

	@Override
	public void action() {
		switch(step){
		case 0:
			mySim = (Simulator) getDataStore().get(Simulator.mySimulator);
			mySim.setStatus(MachineStatus.UNDER_MAINTENANCE);
			inspectionStartMsg =  new ACLMessage(ACLMessage.REQUEST);
			inspectionStartMsg.setContent("Inspection about to start.");
			inspectionStartMsg.addReceiver(Simulator.blackboardAgent);
			inspectionStartMsg.setConversationId(MessageIds.machineInspectionStart);
			myAgent.send(inspectionStartMsg);
			step = 1;
			log.info("recieving inspection data for machine");

			break;
		case 1:
			inspectioneDataMsg = myAgent.receive(InspectionDataMsgTemplate);
			
			if(inspectioneDataMsg != null) {
				
				inspectionData = inspectioneDataMsg.getContent();
				token = new StringTokenizer(inspectionData);
				long procTime = Long.parseLong(token.nextToken());	
				comingJob.setProcessingTime(procTime);
				step++;
				log.info("Starting inspection of machine");
			}
			else
				block();
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
