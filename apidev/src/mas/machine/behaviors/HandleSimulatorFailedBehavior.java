package mas.machine.behaviors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import mas.machine.Simulator;
import mas.util.MessageIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleSimulatorFailedBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private int step = 0;
	private MessageTemplate correctiveDataMsgTemplate;
	private ACLMessage correctiveStartMsg;
	private ACLMessage correctiveDataMsg;
	private StringTokenizer token;
	private long repairTime;
	private ArrayList<Integer> componentsToRepair;
	private Simulator sim;

	public HandleSimulatorFailedBehavior() {
		log = LogManager.getLogger();
		this.sim = (Simulator) myAgent;
		correctiveDataMsgTemplate = MessageTemplate.MatchConversationId(
				MessageIds.machinePrevMaintenanceData);
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			correctiveStartMsg = new ACLMessage(ACLMessage.REQUEST);
			try {
				correctiveStartMsg.setContentObject(sim);
				correctiveStartMsg.setConversationId(MessageIds.Failed);
				correctiveStartMsg.addReceiver(Simulator.blackboardAgent);
				myAgent.send(correctiveStartMsg);
				step = 1;
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case 1:
			correctiveDataMsg = myAgent.receive(correctiveDataMsgTemplate);
			if(correctiveDataMsg != null) {

				token = new StringTokenizer(correctiveDataMsg.getContent());
				repairTime = Long.parseLong(token.nextToken());
				block(repairTime);
				componentsToRepair = new ArrayList<Integer>();

				while(token.hasMoreTokens()) {
					componentsToRepair.add(Integer.parseInt(token.nextToken()));
				}
				step = 2;
			}
			else{
				block();
			}
			break;
			
		case 3:
			sim.repair(componentsToRepair);
			step = 3;
			break;
		}
	}

	@Override
	public boolean done() {
		return step >= 3;
	}

}
