package mas.machine.behaviors;

import java.io.IOException;
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

/**
 * 
 * @author Anand Prajapati
 *
 */

public class HandlePreventiveMaintenanceBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;
	private int step = 0;
	private MessageTemplate pmDataMsgTemplate;
	private ACLMessage maintenanceStartMsg;
	private ACLMessage maintenanceDataMsg;
	private StringTokenizer token;
	private Simulator mySim;

	public HandlePreventiveMaintenanceBehavior(job comingJob) {
		this.comingJob = comingJob;
		log = LogManager.getLogger();
		pmDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.machinePrevMaintenanceData);
		
	}

	@Override
	public void action() {
		switch(step) {
		case 0 :
			mySim = (Simulator) getDataStore().get(Simulator.mySimulator);
			mySim.setStatus(MachineStatus.UNDER_MAINTENANCE);
			maintenanceStartMsg = new ACLMessage(ACLMessage.REQUEST);
			maintenanceStartMsg.setConversationId(MessageIds.machinePrevMaintenanceStart);
			try {
				maintenanceStartMsg.setContentObject(null);
				maintenanceStartMsg.addReceiver(null);
				myAgent.send(maintenanceStartMsg);
				step = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case 1:
			maintenanceDataMsg = myAgent.receive(pmDataMsgTemplate);
			if(maintenanceDataMsg != null){
				// parse the received data and perform maintenance of the machine
				log.info("Maintenenace data arrived");
				token = new StringTokenizer(maintenanceDataMsg.getContent());
				
				step = 2;
				
			}
			else {
				block();
			}
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}
}
