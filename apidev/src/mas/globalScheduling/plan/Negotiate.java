package mas.globalScheduling.plan;

import mas.util.ID;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
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
	/*	ACLMessage NegotiationMsg = new ACLMessage(ACLMessage.CFP);
		NegotiationMsg.setContent("Yes");
		NegotiationMsg.setConversationId(MessageIds.ReplyFromScheduler.toString());
		NegotiationMsg.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
		NegotiationMsg.addReceiver(CustomerAgent);
		myAgent.send(NegotiationMsg);*/
		
		
		ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.NegotiationJob, (Object)1 /*job should be here. Correct after finalizing job class*/, true); 
		//Negotiation logic under development
		update.send(bb, update, myAgent);
	}
}
