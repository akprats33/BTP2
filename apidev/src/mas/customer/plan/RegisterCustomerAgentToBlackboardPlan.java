package mas.customer.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterCustomerAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		
	}

	@Override
	public void action() {
		
		AID bb_aid = AgentUtil.findBlackboardAgent(myAgent);

		NamedZoneData ZoneDataName1 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.acceptedJobs).
				MsgID(MessageIds.ReplyFromScheduler).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.JobList).
				MsgID(MessageIds.JobFromScheduler).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName3 = 
				new NamedZoneData.Builder(ID.Customer.ZoneData.Negotiation).
				MsgID(MessageIds.Negotiate).
				appendValue(true).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2,ZoneDataName3};

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		SubscriptionForm subform = new SubscriptionForm();
		AID target = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);
		
		String[] params = {ID.GlobalScheduler.ZoneData.NegotiationJob,
				ID.GlobalScheduler.ZoneData.ConfirmedOrder};

		subform.AddSubscriptionReq(target, params);

		AgentUtil.subscribeToParam(myAgent, bb_aid, subform);
	}
}
