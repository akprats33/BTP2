package mas.customer.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.customer.basicCapability;
import mas.job.job;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class DispatchJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private job jobToDispatch;
	private AID bba;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		
//		log.info(bfBase.getBelief(basicCapability.CURR_JOB));
		
		jobToDispatch = (job) bfBase
					.getBelief(ID.Customer.BeliefBase.CURRENT_JOB)
					.getValue();
		
		this.bba = (AID) bfBase
					.getBelief(ID.Customer.BeliefBase.blackAgent)
					.getValue();
	}

	/**
	 *  send the generated job to blackboard agent by a FIPA-CFP message
	 */
	@Override
	public void action() {
		
		try {
			ACLMessage msg = new ACLMessage(ACLMessage.CFP);
			msg.setContentObject(jobToDispatch);
			msg.setConversationId(MessageIds.JobFromCustomer);
			msg.addReceiver(this.bba);
			myAgent.send(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean done() {
		return true;
	}
}
