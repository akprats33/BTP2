package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *	Sends average waiting time for the new job to global scheduling agent
 *  Based on this waiting time global scheduling accepts/negotiates the job 
 *  from customer
 *
 */

public class SendWaitingTimePlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private ArrayList<job> jobQueue;
	private job j;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private double averageProcessingTime;
	private double averageQueueSize;
	private AID blackboard;
	private Logger log;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		log=LogManager.getLogger();
		try {
			msg = ((MessageGoal)pInstance.getGoal()).getMessage();
			j = (job)(msg.getContentObject());
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();
		
		sTracker = (StatsTracker) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.dataTracker).
				getValue();
		
		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();
	}

	@Override
	public void action() {		
//		sTracker.addSize( jobQueue.size() );
		
		// get average queue size and waiting time in the queue
//		averageQueueSize = sTracker.getAverageQueueSize().doubleValue();
//		averageProcessingTime = sTracker.getAvgProcessingTime();
	
		
//		double avgWaitingTime = averageProcessingTime*averageQueueSize;
		
		//////////remove/////////
		 Random randomGenerator = new Random();
		j.setWaitingTime(randomGenerator.nextDouble());
		/////////remove//////////
//		j.setWaitingTime(avgWaitingTime + j.getProcessingTime());

		log.info(j.getWaitingTime());
		ZoneDataUpdate waitingTimeUpdate = new ZoneDataUpdate(
				ID.LocalScheduler.ZoneData.WaitingTime,
				this.j);
		

		AgentUtil.sendZoneDataUpdate(blackboard ,waitingTimeUpdate, myAgent);
		
		//		myAgent.addBehaviour(new CalculateWaitTimeBehavior(JobQueue.size(),GlobalSchedulingAID, j));
	}
}
