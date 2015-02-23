package mas.globalScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import mas.globalScheduling.behaviour.WaitTimeBehvr;
import mas.job.job;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class AskForWaitingTime extends OneShotBehaviour implements PlanBody {

	private job j;
	private AID blackboard;
	private int NoOfMachines;

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PlanInstance PI) {
		try {
			 j=(job)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		blackboard=(AID)PI.getBeliefBase().getBelief(ID.Blackboard.LocalName).getValue();
	}

	@Override
	public void action() {
		
		ACLMessage msg=new ACLMessage(ACLMessage.CFP);
		msg.setConversationId(MessageIds.UpdateParameter);
		ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GetWaitingTime, j);
		try {
			msg.setContentObject(update);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		msg.setReplyWith(Integer.toString(j.getJobNo()));
		
		msg.addReceiver(blackboard);
		myAgent.send(msg);
		
		
		myAgent.addBehaviour(new WaitTimeBehvr(blackboard, NoOfMachines,Integer.toString(j.getJobNo())));
	}

}
