package mas.machine.behaviors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import mas.job.job;
import mas.machine.MachineStatus;
import mas.machine.Simulator;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;

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
	private ACLMessage maintenanceDataMsg;
	private StringTokenizer token;
	private Simulator machineSimulator;
	private int remainingMaintenanceTime;
	private ArrayList<Integer> componentsToRepair;

	public HandlePreventiveMaintenanceBehavior(job comingJob) {
		this.comingJob = comingJob;
		log = LogManager.getLogger();
		pmDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.msgprevMaintData);
		machineSimulator = (Simulator) getDataStore().get(Simulator.simulatorStoreName);

	}

	@Override
	public void action() {
		switch(step) {
		case 0 :
			
			ZoneDataUpdate maintenanceStartUpdate = new ZoneDataUpdate(
					ID.Machine.ZoneData.maintenanceStart,
					comingJob);

			maintenanceStartUpdate.send(Simulator.blackboardAgent ,
					maintenanceStartUpdate, myAgent);

			machineSimulator.setStatus(MachineStatus.UNDER_MAINTENANCE);
			step = 1;
			break;

		case 1:
			maintenanceDataMsg = myAgent.receive(pmDataMsgTemplate);
			if(maintenanceDataMsg != null) {
				// parse the received data and perform maintenance of the machine
				log.info("Maintenenace data arrived");
				token = new StringTokenizer(maintenanceDataMsg.getContent());
				
				remainingMaintenanceTime = Integer.parseInt(token.nextToken());

				componentsToRepair = new ArrayList<Integer>();

				while(token.hasMoreTokens()) {
					componentsToRepair.add(Integer.parseInt(token.nextToken()));
				}
				step = 2;

			}
			else {
				block();
			}
			break;
			
		case 2:
			
			if(remainingMaintenanceTime >= 0) {
				remainingMaintenanceTime = remainingMaintenanceTime - Simulator.TIME_STEP;
				block(Simulator.TIME_STEP);
			} else if(remainingMaintenanceTime < 0) {
				step = 3;
			}
			
			break;
			
		case 3:
			/**
			 * perform the maintenance for the machine now
			 */
			
			machineSimulator.repair(componentsToRepair);
			step = 4;
			break;
		}
	}

	@Override
	public boolean done() {
		return step > 3;
	}
}
