package mas.maintenance.plan;

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

public class RegisterAgentToBlackboardPlan extends OneShotBehaviour implements PlanBody {

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
				new NamedZoneData.Builder(ID.Maintenance.ZoneData.correctiveMaintdata).
				MsgID(MessageIds.ReplyFromScheduler).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.Maintenance.ZoneData.PMdata).
				MsgID(MessageIds.JobFromScheduler).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2};

		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		SubscriptionForm subform = new SubscriptionForm();
		AID target = new AID(ID.Maintenance.LocalName, AID.ISLOCALNAME);

		String[] params = {ID.Machine.ZoneData.myHealth,
				ID.Machine.ZoneData.finishedJob};

		subform.AddSubscriptionReq(target, params);

		AgentUtil.subscribeToParam(myAgent, bb_aid, subform);
	}
}
