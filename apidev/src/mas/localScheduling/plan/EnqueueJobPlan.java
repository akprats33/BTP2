package mas.localScheduling.plan;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.job.job;
import mas.localScheduling.algorithm.ScheduleSequence;
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

public class EnqueueJobPlan extends OneShotBehaviour implements PlanBody {

	/**
	 * Takes the incoming job from the global scheduling agent and pushes it to the
	 * queue of jobs it is holding and then does scheduling based on some criteria e.g. deviation
	 * from original schedule
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private job j;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	
	@Override
	public void action() {
		
		/**--perform scheduling here for the list of the jobs
		 */
		
		log.info("Performing scheduling of jobs");
		ScheduleSequence bnb = new ScheduleSequence(jobQueue);
		ArrayList<job> solution = bnb.getSolution();
		//----------------------------------------------------
		
		/** Now update the belief base for the queue of jobs 
		 * 
		 */
		BeliefSet<ArrayList<job>> JobQueue = 
				new TransientBeliefSet<ArrayList<job>>(ID.LocalScheduler.BeliefBaseConst.jobQueue);;
				
		JobQueue.addValue(solution);
		bfBase.updateBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue, solution);
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
					getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
					getValue();
		
		jobQueue.add(j);
		
	}

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}
}
