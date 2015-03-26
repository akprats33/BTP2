package mas.globalScheduling.plan;

import mas.job.job;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

public class CallBackChangeDueDate extends Behaviour implements PlanBody{

	private static int step=0;
	private job copyOfJobToCallBack;
	
	@Override
	public EndState getEndState() {
		if(step==1){
			return EndState.SUCCESSFUL;
		}
		else{
			return EndState.FAILED;
		}
	}

	@Override
	public void init(PlanInstance PI) {
		try {
			 copyOfJobToCallBack=(job)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		switch(step){
		case 0:
			
			break;
			
		case 1:
			
			break;
			
		}
		
	}

	@Override
	public boolean done() {
		return step==1;
	}

}
