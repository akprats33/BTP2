package mas.globalScheduling.plan;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterAgentToBlackboard extends OneShotBehaviour implements PlanBody {
	int step;

	@Override
	public EndState getEndState() {

		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		step=0;


	}

	@Override
	public void action() {
		AID bb_aid=AgentUtil.findBlackboardAgent(myAgent);

		ACLMessage msg2=new ACLMessage(ACLMessage.CFP);
		msg2.setConversationId(MessageIds.RegisterMe);

		NamedZoneData ZoneDataName2=new NamedZoneData.Builder
				(ID.GlobalScheduler.ZoneData.ConfirmedOrder).
				MsgID(MessageIds.ReplyFromScheduler).
				build();

		NamedZoneData ZoneDataName3=new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.askBidForJobFromLSA).
				MsgID(MessageIds.GSABidForJobFromLSA).
				build();

		NamedZoneData ZoneDataName4=new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.GetWaitingTime).
				MsgID(MessageIds.GSAwaitingTimeFromLSA).
				build();

		NamedZoneData ZoneDataName5=new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.jobsUnderNegaotiation).
				MsgID(MessageIds.GSANegotiationJobsCustomer).
				build();

		NamedZoneData ZoneDataName6=new NamedZoneData.Builder(
				ID.GlobalScheduler.ZoneData.WorkOrder).
				MsgID(MessageIds.WorkOrder).
				build();

		NamedZoneData[] ZoneDataNames={ZoneDataName2,
				ZoneDataName3,ZoneDataName4,
				ZoneDataName5,ZoneDataName6};
		try {
			msg2.setContentObject(ZoneDataNames);
		} catch (IOException e1) {
			e1.printStackTrace();
		}



		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);



		SubscriptionForm subform = new SubscriptionForm();
		AID target = new AID(ID.Customer.LocalName, AID.ISLOCALNAME);
		String[] params = {ID.Customer.ZoneData.confirmedJobs,ID.Customer.ZoneData.newWorkOrderFromCustomer,
				ID.Customer.ZoneData.jobsUnderNegotiation};

		subform.AddSubscriptionReq(target, params);



		AgentUtil.subscribeToParam(myAgent, bb_aid, subform);

	}

}
