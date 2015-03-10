package mas.customer.plan;

import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class jobGeneratorPlan extends rootJobGeneratePlan{

	private static final long serialVersionUID = 1L;

	/**
	 * return inter-arrival time between jobs in mililiseconds
	 * used by tootJobGeneratePlan to reset tick interval
	 */
	@Override
	public long getInterArrivalTimeMillis() {
		return super.getInterArrivalTimeMillis(); 
	}

	@Override
	public EndState getEndState() {
		return super.getEndState();
	}

	@Override
	public void init(PlanInstance pInstance) {
		super.init(pInstance);
	}

	@Override
	public void action() {
		super.action();
	}

	@Override
	public boolean done() {
		return super.done();
	}
}
