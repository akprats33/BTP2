package mas.localScheduling.plan;

import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.OneShotBehaviour;

public class SendJobToMachine extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {

	}

	@Override
	public void action() {

	}
}
