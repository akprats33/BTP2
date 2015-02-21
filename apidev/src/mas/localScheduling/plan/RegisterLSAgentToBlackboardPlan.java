package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import mas.blackboard.nameZoneData.NamedZoneData;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.SubscriptionForm;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RegisterLSAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private int step;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance planInstance) {
		step = 0;
	}

	@Override
	public void action() {

		AID bb_aid = AgentUtil.findBlackboardAgent(myAgent);

		NamedZoneData ZoneDataName1 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.bidForJob).
				MsgID(MessageIds.ReplyFromScheduler).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.jobQueue).
				MsgID(MessageIds.JobFromScheduler).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName3 = 
				new NamedZoneData.Builder(ID.LocalScheduler.ZoneData.WaitingTime).
				MsgID(MessageIds.Negotiate).
				appendValue(true).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2,ZoneDataName3};

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		AID gSchedulingTarget = new AID(ID.GlobalScheduler.LocalName, AID.ISLOCALNAME);
		AID simulatorTarget = new AID(ID.Machine.LocalName, AID.ISLOCALNAME);

		// subscription form for global scheduling agent
		SubscriptionForm gSchedulingSubform = new SubscriptionForm();
		String[] gSchedulingParams = { ID.GlobalScheduler.ZoneData.askforBid,
				ID.GlobalScheduler.ZoneData.GetWaitingTime };
		gSchedulingSubform.AddSubscriptionReq(gSchedulingTarget, gSchedulingParams);

		// subscription form for simulator
		SubscriptionForm simulatorSubform = new SubscriptionForm();
		String[] simulatorParams = { ID.Machine.ZoneData.finishedJob };
		simulatorSubform.AddSubscriptionReq(simulatorTarget, simulatorParams);

		AgentUtil.subscribeToParam(myAgent, bb_aid, simulatorSubform);
	}
}
