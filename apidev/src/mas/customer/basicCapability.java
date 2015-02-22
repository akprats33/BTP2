package mas.customer;

import mas.customer.goal.RegisterAgentToBlackboardGoal;
import mas.customer.plan.RegisterCustomerAgentToBlackboardPlan;
import mas.job.job;
import mas.util.ID;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.util.plan.SimplePlan;

public class basicCapability extends parentBasicCapability{

	private static final long serialVersionUID = 1L;
	// If you want to change the jobgenerator,
	// simply override the getbelief method
	
	public basicCapability() {
		super();
		
		getPlanLibrary().addPlan(new SimplePlan(RegisterAgentToBlackboardGoal.class,
				RegisterCustomerAgentToBlackboardPlan.class));
		
		Belief<job> currentJob = 
				new TransientBelief<job>(ID.Customer.BeliefBase.CURRENT_JOB);
				
		this.getBeliefBase().addBelief(currentJob);
	}
	
	@Override
	protected void setup() {
		super.setup();
		
		myAgent.addGoal(new RegisterAgentToBlackboardGoal());
	}
}
