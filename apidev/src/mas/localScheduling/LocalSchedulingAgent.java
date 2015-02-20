package mas.localScheduling;

import jade.core.AID;
import mas.localScheduling.capability.LocalSchedulingBasicCapability;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class LocalSchedulingAgent extends AbstractlocalSchedulingAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	protected void init() {
		super.init();
		log = LogManager.getLogger();

		// Add capability to agent 
		Capability bCap = new LocalSchedulingBasicCapability();
		addCapability(bCap);

		AID bba = AgentUtil.findBlackboardAgent(this);
		// store AID of blackboard agent into belief base
		bCap.getBeliefBase().updateBelief(
				ID.LocalScheduler.BeliefBase.blackAgent, bba);

		//create zones in blackboard for this agent
		String[] zones = {ID.LocalScheduler.ZoneData.bidForJob,
				ID.LocalScheduler.ZoneData.WaitingTime,
				ID.LocalScheduler.ZoneData.jobQueue};

//		AgentUtil.makeZoneBB(this,zones);

		// subscribe for parameters of other agents
		AID target = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);

		String[] params = {ID.GlobalScheduler.ZoneData.jobForMachine,
				ID.GlobalScheduler.ZoneData.askforBid,
				ID.GlobalScheduler.ZoneData.waitingTime};

		SubscriptionForm subform = new SubscriptionForm();
		subform.AddSubscriptionReq(target, params);

		AgentUtil.subscribeToParam(this, bba, subform);
	}
}
