package mas.customer;

import jade.core.AID;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class CustomerAgent extends AbstractCustomerAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	protected void init() {
		super.init();
		Capability bCap = new basicCapability();
		addCapability(bCap);
		log = LogManager.getLogger();

		AID bba = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Customer.BeliefBase.blackAgent, bba);
		
		String[] zones = { ID.Customer.ZoneData.acceptedJobs,
				ID.Customer.ZoneData.JobList,
				ID.Customer.ZoneData.Negotiation };
//
//		AgentUtil.makeZoneBB(this,zones);
//
//		AID target = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);
//
//		String[] params = {ID.GlobalScheduler.ZoneData.ConfirmedOrder,
//				ID.GlobalScheduler.ZoneData.NegotiationJob,
//				ID.GlobalScheduler.ZoneData.WorkOrder};
//
//		SubscriptionForm subform = new SubscriptionForm();
//		subform.AddSubscriptionReq(target, params);
//
//		AgentUtil.subscribeToParam(this, bba, subform);
	}
}
