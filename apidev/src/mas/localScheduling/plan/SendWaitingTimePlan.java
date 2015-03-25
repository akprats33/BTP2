package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

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
	private job job;
	private BeliefBase bfBase;
	private StatsTracker sTracker;
	private double averageProcessingTime;
	private double averageQueueSize;
	private AID blackboard;
	private Logger log;
	private String replyWith;
	private String[] SupportedOps;
	private boolean CurrentOpSupport=false;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		bfBase = pInstance.getBeliefBase();
		SupportedOps=(String[])bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.supportedOperations).getValue();
		
		log = LogManager.getLogger();
		try {
			msg = ((MessageGoal)pInstance.getGoal()).getMessage();
			job = (job)(msg.getContentObject());
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
		
		replyWith=msg.getReplyWith();
	}

	@Override
	public void action() {		
		sTracker.addSize( jobQueue.size() );
		
		for(int i=0;i<SupportedOps.length;i++){
			if(SupportedOps[i].equalsIgnoreCase(job.getCurrentOperation().getJobOperationType().toString())){
				CurrentOpSupport=true;
			}
		}
		log.info("op->"+job.getCurrentOperation().getJobOperationType().toString());
		
		long WaitingTime=0;
		if(CurrentOpSupport){
			
			
			for(int i=0;i<jobQueue.size();i++){
				WaitingTime=WaitingTime+jobQueue.get(i).getCurrentOperationProcessTime()*1000;
			}
		}
		else{
			log.info(myAgent.getLocalName()+" doesn't support "+job.getCurrentOperation().getJobOperationType().toString());
			
			WaitingTime=(long)(-1);
		}
			
			
			job.setWaitingTime(WaitingTime+ job.getCurrentOperationProcessTime());
			ZoneDataUpdate waitingTimeUpdate=new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.WaitingTime)
				.value(this.job).setReplyWith(replyWith).Build();
			AgentUtil.sendZoneDataUpdate(blackboard ,waitingTimeUpdate, myAgent);
	}
}
