package mas.localScheduling.capability;

import jade.core.AID;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mas.customer.goal.RegisterAgentToBlackboardGoal;
import mas.job.job;
import mas.localScheduling.goal.EnqueueJobGoal;
import mas.localScheduling.goal.ReceiveCompletedJobGoal;
import mas.localScheduling.goal.RegisterLSAgentServiceGoal;
import mas.localScheduling.goal.RegisterLSAgentToBlackboardGoal;
import mas.localScheduling.goal.SendBidGoal;
import mas.localScheduling.goal.SendJobGoal;
import mas.localScheduling.goal.SendWaitingTimeGoal;
import mas.localScheduling.plan.EnqueueJobPlan;
import mas.localScheduling.plan.ReceiveCompletedJobPlan;
import mas.localScheduling.plan.RegisterLSAgentServicePlan;
import mas.localScheduling.plan.RegisterLSAgentToBlackboardPlan;
import mas.localScheduling.plan.SendBidPlan;
import mas.localScheduling.plan.SendJobPlan;
import mas.localScheduling.plan.SendJobToMachine;
import mas.localScheduling.plan.SendWaitingTimePlan;
import mas.localScheduling.plan.StatsTracker;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

/**
 * @author Anand Prajapati
 * 
 */

public class AbstractbasicCapability extends Capability {

	private static final long serialVersionUID = 1L;
	
	public AbstractbasicCapability(){
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		AID temp_bb_AID=new AID(ID.Blackboard.LocalName,false);
		Belief<AID> bboard = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.blackboardAgent, temp_bb_AID);
		
		

		Belief<AID> myMachine = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.machine);

		Belief<AID> myMcMaintAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.maintAgent);

		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBaseConst.globalSchAgent);

		Belief<StatsTracker> dtrack = new TransientBelief<StatsTracker>(
				ID.LocalScheduler.BeliefBaseConst.dataTracker);

		Belief<ArrayList<job> > jobSet = new TransientBelief<ArrayList<job> >(
				ID.LocalScheduler.BeliefBaseConst.jobQueue);

		beliefs.add(bboard);
		beliefs.add(jobSet);
		beliefs.add(myMachine);
		beliefs.add(myMcMaintAgent);
		beliefs.add(mygsAgent);
		beliefs.add(dtrack);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgbidResultJob),
				EnqueueJobPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgfinishedJob),
				ReceiveCompletedJobPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskBidForJobFromLSA),
				SendBidPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgaskJobFromLSA),
				SendJobPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgGetWaitingTime),
				SendWaitingTimePlan.class));
		
		plans.add(new SimplePlan(RegisterLSAgentToBlackboardGoal.class,
				RegisterLSAgentToBlackboardPlan.class));
		
		plans.add(new SimplePlan(RegisterLSAgentServiceGoal.class,
				RegisterLSAgentServicePlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.msgjobForLSA),
				SendJobToMachine.class));
		
		
		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterLSAgentServiceGoal());
		myAgent.addGoal(new RegisterLSAgentToBlackboardGoal());
	/*	myAgent.addGoal(new SendBidGoal());
		myAgent.addGoal(new SendJobGoal());
		myAgent.addGoal(new SendWaitingTimeGoal());
		myAgent.addGoal(new EnqueueJobGoal());
		myAgent.addGoal(new ReceiveCompletedJobGoal());*/
	}
}
