package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendJobToMachinePlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private ArrayList<job> jobQueue;
	private AID blackboard;
	private String status;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		status  = msg.getContent();
		bfBase = pInstance.getBeliefBase();

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

	}

	@Override
	public void action() {
		if("1".equals(status)) {
			if(jobQueue.size() > 0) {
				ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate(
						ID.LocalScheduler.ZoneData.bidForJob,
						jobQueue.get(0));

				jobQueue.remove(0);
				AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);
			}
		}
	}
}
