package mas.localScheduling.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import mas.job.job;
import mas.localScheduling.capability.AbstractbasicCapability;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendWaitingTimePlan extends OneShotBehaviour implements PlanBody{

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private ArrayList<job> jobQueue;
	private job j;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private double averageProcessingTime;
	private double averageQueueSize;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();

		try {
			msg = ((MessageGoal)pInstance.getGoal()).getMessage();
			j = (job)(msg.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBase.jobQueue).
				getValue();
		
		sTracker = (StatsTracker) bfBase.
				getBelief(ID.LocalScheduler.BeliefBase.dataTracker).
				getValue();
	}

	@Override
	public void action() {		
		sTracker.addSize( jobQueue.size() );
		
		averageQueueSize = sTracker.getAverageQueueSize().doubleValue();
		averageProcessingTime = sTracker.getAvgProcessingTime();

		double avgWaitingTime = averageProcessingTime*averageQueueSize;
		j.setWaitingTime(avgWaitingTime + j.getProcessingTime());

		ACLMessage waitTime = new ACLMessage(ACLMessage.INFORM);
		waitTime.addReceiver(msg.getSender());
		waitTime.setConversationId(MessageIds.waitingTime);
		try {
			waitTime.setContentObject(j);
			myAgent.send(waitTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		myAgent.addBehaviour(new CalculateWaitTimeBehavior(JobQueue.size(),GlobalSchedulingAID, j));
	}
}
