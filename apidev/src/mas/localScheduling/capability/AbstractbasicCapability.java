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

		Belief<AID> bboard = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBase.blackAgent);

		Belief<AID> myMachine = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBase.machine);

		Belief<AID> myMcMaintAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBase.maintAgent);

		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.LocalScheduler.BeliefBase.globalSchAgent);

		Belief<StatsTracker> dtrack = new TransientBelief<StatsTracker>(
				ID.LocalScheduler.BeliefBase.dataTracker);

		Belief<ArrayList<job> > jobSet = new TransientBelief<ArrayList<job> >(
				ID.LocalScheduler.BeliefBase.jobQueue);

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

//		plans.add(new SimplePlan(EnqueueJobGoal.class,
//				EnqueueJobPlan.class));
//
//		plans.add(new SimplePlan(ReceiveCompletedJobGoal.class,
//				ReceiveCompletedJobPlan.class));
//		
//		plans.add(new SimplePlan(SendBidGoal.class,
//				SendBidPlan.class));
//		
//		plans.add(new SimplePlan(SendJobGoal.class,
//				SendJobPlan.class));
//		
//		plans.add(new SimplePlan(SendWaitingTimeGoal.class,
//				SendWaitingTimePlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.LSjobFromGS),
				EnqueueJobPlan.class));

		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.LSgetCompletedJobFromMachine),
				ReceiveCompletedJobPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.LSsendBidToGS),
				SendBidPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.LSsendJobToMachine),
				SendJobPlan.class));
		
		plans.add(new SimplePlan(MessageTemplate.MatchConversationId(MessageIds.LSsendWaitingTimeGS),
				SendWaitingTimePlan.class));
		
		plans.add(new SimplePlan(RegisterLSAgentToBlackboardGoal.class,
				RegisterLSAgentToBlackboardPlan.class));
		
		plans.add(new SimplePlan(RegisterLSAgentServiceGoal.class,
				RegisterLSAgentServicePlan.class));
		
		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterLSAgentServiceGoal());
		myAgent.addGoal(new RegisterLSAgentToBlackboardGoal());
		myAgent.addGoal(new SendBidGoal());
		myAgent.addGoal(new SendJobGoal());
		myAgent.addGoal(new SendWaitingTimeGoal());
		myAgent.addGoal(new EnqueueJobGoal());
		myAgent.addGoal(new ReceiveCompletedJobGoal());
	}
}
