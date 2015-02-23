package mas.customer;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import mas.customer.goal.GenerateJobGoal;
import mas.customer.goal.RegisterAgentToBlackboardGoal;
import mas.customer.goal.dispatchJobGoal;
import mas.customer.plan.DispatchJobPlan;
import mas.customer.plan.RegisterCustomerAgentToBlackboardPlan;
import mas.customer.plan.jobGeneratorPlan;
import mas.util.ID;
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
 * This capability contains two goals of customer - one for generating jobs
 * and one for dispatching them to Global scheduling agent
 * 
 */

public class parentBasicCapability extends Capability {

	private static final long serialVersionUID = 1L;
	
	public parentBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<JobGeneratorIFace> generator = 
				new TransientBelief<JobGeneratorIFace>(ID.Customer.BeliefBaseConst.JOB_GENERATOR);
		
		Belief<AID> bboard = new TransientBelief<AID>(ID.Customer.BeliefBaseConst.blackboardAgent);
		
		beliefs.add(generator);
		beliefs.add(bboard);
		return beliefs;
	}
	
	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();
		
		plans.add(new SimplePlan(RegisterAgentToBlackboardGoal.class,
					RegisterCustomerAgentToBlackboardPlan.class));

		plans.add(new SimplePlan(GenerateJobGoal.class,
				jobGeneratorPlan.class));
		
		plans.add(new SimplePlan(dispatchJobGoal.class,
				DispatchJobPlan.class));
		
		return plans;
	}	
	
	@Override
	protected void setup() {
		myAgent.addGoal(new GenerateJobGoal());
		
	}
}
