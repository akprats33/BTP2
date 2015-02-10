package mas.maintenance.plan;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import mas.machine.IMachine;
import mas.util.ID;
import mas.util.MessageIds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class machineHealthCheckPlan extends Behaviour implements PlanBody {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private IMachine myMachine;
	private ACLMessage msg;
	private int step = 0;
	private Logger log;
	private MessageTemplate machineHealth;
	
	@Override
	public void action() {
		
		switch(step){
		case 0:
			msg = myAgent.receive(machineHealth);
			if(msg != null){
				try {
					myMachine = (IMachine) msg.getContentObject();
					step = 1;
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
			else{
				block();
			}
			
		case 1:
			log.info("updating belief base of machine's health");
			bfBase.updateBelief(ID.Maintenance.BeliefBase.machine, myMachine);
			step = 0;
			break;
		}
	}

	@Override
	public boolean done() {
		return true;
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		myMachine = null;
		machineHealth = MessageTemplate.MatchConversationId(MessageIds.MaintMachineHealth);
	}
}
