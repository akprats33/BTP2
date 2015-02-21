package mas.machine.behaviors;

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

public class RegisterAgentToBlackboardBehavior extends OneShotBehaviour implements PlanBody {

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
				new NamedZoneData.Builder(ID.Machine.ZoneData.finishedJob).
				MsgID(MessageIds.ReplyFromScheduler).
				appendValue(true).
				build();

		NamedZoneData ZoneDataName2 = 
				new NamedZoneData.Builder(ID.Machine.ZoneData.myHealth).
				MsgID(MessageIds.JobFromScheduler).
				appendValue(true).
				build();

		NamedZoneData[] ZoneDataNames =  { ZoneDataName1,
				ZoneDataName2};
		
		AgentUtil.makeZoneBB(myAgent,ZoneDataNames);

		AID maintTarget = new AID(ID.Maintenance.LocalName, AID.ISLOCALNAME);
		AID lSchedulingTarget = new AID(ID.LocalScheduler.LocalName, AID.ISLOCALNAME);
		
		// subscription form for maintenance agent
		SubscriptionForm MaintenanceSubform = new SubscriptionForm();
		String[] maintParams = {ID.Maintenance.ZoneData.PMdata,
				ID.Maintenance.ZoneData.correctiveMaintdata};
		MaintenanceSubform.AddSubscriptionReq(maintTarget, maintParams);
		
		// subscription form for local scheduling agent
		SubscriptionForm lSchedulingSubform = new SubscriptionForm();
		String[] lSchedulingParams = {ID.LocalScheduler.ZoneData.jobQueue };
		lSchedulingSubform.AddSubscriptionReq(lSchedulingTarget, lSchedulingParams);

		AgentUtil.subscribeToParam(myAgent, bb_aid, MaintenanceSubform);
		AgentUtil.subscribeToParam(myAgent, bb_aid, lSchedulingSubform);
	}
}
