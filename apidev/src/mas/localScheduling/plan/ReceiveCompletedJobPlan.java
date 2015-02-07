package mas.localScheduling.plan;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sun.util.logging.resources.logging;
import mas.job.job;
import mas.localScheduling.algorithm.BranchNboundRegretRigid;
import mas.localScheduling.algorithm.ScheduleSequence;
import mas.localScheduling.capability.AbstractbasicCapability;
import mas.util.ID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;
import bdi4jade.belief.BeliefSet;
import bdi4jade.belief.TransientBeliefSet;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class ReceiveCompletedJobPlan extends OneShotBehaviour implements PlanBody {

	/**
	 * Takes the complete job 
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private job j;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private Logger log;
	
	@Override
	public void action() {
		
		sTracker.addSize(jobQueue.size());
		sTracker.storeJob(j);
				
		log.info("updating belief" + sTracker);
		bfBase.updateBelief(ID.LocalScheduler.BeliefBase.dataTracker, sTracker);
	}

	@Override
	public void init(PlanInstance pInstance) {
		
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		
		try {
			j = (job)((MessageGoal)pInstance.getGoal()).getMessage().getContentObject();
			
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
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
