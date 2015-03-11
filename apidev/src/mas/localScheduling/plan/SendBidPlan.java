package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Random;

import mas.job.job;
import mas.localScheduling.algorithm.ScheduleSequence;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 * 
 * receives job from global scheduling agent for bid and then sends a bid 
 * for the received job 
 */

public class SendBidPlan extends OneShotBehaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private job jobToBidFor;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private Logger log;
	private AID blackboard;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();

		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		try {
			jobToBidFor = (job)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		try{
			setBid(jobToBidFor);

			ZoneDataUpdate bidForJobUpdate = new ZoneDataUpdate(
					ID.LocalScheduler.ZoneData.bidForJob,
					jobToBidFor);


			AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);

			//			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setBid(job j){
		//		Random r = new Random();	
		//		j.setBidByLSA(r.nextDouble());

		jobQueue = (ArrayList<job>) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
				getValue();

		ArrayList<job> tempQueue = new  ArrayList<job>();
		tempQueue.addAll(jobQueue);
		tempQueue.add(j);

		ScheduleSequence sch = new ScheduleSequence(tempQueue);
		ArrayList<job> tempqSolution = sch.getSolution();

//		log.info(tempQueue + "");
//		log.info(jobQueue + "");
//		log.info(tempqSolution + "");

		double PenaltyBefore=getPenaltyLocalDD(tempqSolution);
		log.info(getPenaltyLocalDD(tempqSolution));
		double PenaltyAfter=getPenaltyLocalDD(jobQueue);
		log.info(getPenaltyLocalDD(jobQueue));
		log.info(PenaltyAfter - PenaltyBefore);
		j.setBidByLSA(PenaltyAfter - PenaltyBefore );
		j.setLSABidder(myAgent.getAID());
	}

	public double getPenaltyLocalDD(ArrayList<job> sequence) {
		double finishTime = 0.0;
		double cost = 0.0;
		int l = sequence.size();

		for (int i = 0; i < l; i++) {

			finishTime = sequence.get(i).getProcessingTime() +
					(sequence.get(i).getStartTime().getTime() +
							System.currentTimeMillis());

//			log.info(sequence.get(i).getStartTime().getTime());
//			log.info(sequence.get(i).getDuedate().getTime());
			double tardiness = 0.0;

			if (finishTime > sequence.get(i).getDuedate().getTime()){
				tardiness = finishTime - sequence.get(i).getDuedate().getTime();
			}
			else{
				tardiness = 0.0;
			}

			log.info(tardiness);
			cost += tardiness * sequence.get(i).getPenalty() + sequence.get(i).getCost();
		}
		return cost;
	}
}	
