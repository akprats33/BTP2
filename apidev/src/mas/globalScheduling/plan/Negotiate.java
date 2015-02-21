package mas.globalScheduling.plan;


import mas.job.job;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class Negotiate extends OneShotBehaviour implements PlanBody {

//	private AID CustomerAgent;
	private AID bb;
	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance PI) {
		bb=(AID)PI.getBeliefBase().getBelief(ID.Blackboard.LocalName).getValue();
	}

	@Override
	public void action() {
		ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.NegotiationJob, (Object)1); 
		//Negotiation logic under development
		update.send(bb, update, myAgent);
	}
}
