package mas.globalScheduling.plan;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.JobQueryObject;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class QueryFromLSA extends OneShotBehaviour implements PlanBody {

	private AID blackboard_AID;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		blackboard_AID = new AID(ID.Blackboard.LocalName, false);
	}

	@Override
	public void action() {
		job j=new job.Builder("doesn'tmatter").build();
		j.setJobNo(1);
		JobQueryObject queryForm=new JobQueryObject(j);
		ZoneDataUpdate QueryRequest= new ZoneDataUpdate.Builder(ID.GlobalScheduler.ZoneData.QueryRequest)
		.value(queryForm).Build();
		AgentUtil.sendZoneDataUpdate(blackboard_AID, QueryRequest, myAgent);
		
		
	}

}
