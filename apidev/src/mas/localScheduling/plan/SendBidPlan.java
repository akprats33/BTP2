package mas.localScheduling.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;

import mas.job.job;
import mas.localScheduling.algorithm.ScheduleSequence;
import mas.util.ID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class SendBidPlan extends OneShotBehaviour implements PlanBody{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ACLMessage msg;
	private job jobToBidFor;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;
	private Logger log;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
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
			ACLMessage myBid = msg.createReply();
			setBid(jobToBidFor);
			myBid.setPerformative(ACLMessage.PROPOSE);
			myBid.setContentObject(jobToBidFor);
			myAgent.send(myBid);
			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void setBid(job j){
		jobQueue = (ArrayList<job>) bfBase.
						getBelief(ID.LocalScheduler.BeliefBase.jobQueue).
						getValue();
		
		long tim = System.currentTimeMillis() / 1000 ;
		
		ArrayList<job> tempQueue = new  ArrayList<job>();
		tempQueue.addAll(jobQueue);
		
		tempQueue.add(j);
		
		ScheduleSequence sch = new ScheduleSequence(tempQueue);
		ArrayList<job> tempqSolution = sch.getSolution();
		
		j.setBidByLSA(getPenaltyLocalDD(tempqSolution) - getPenaltyLocalDD(jobQueue) );
	}
	
	public double getPenaltyLocalDD(ArrayList<job> sequence) {
		double finishTime = 0.0;
		double cost = 0.0;
		int l = sequence.size();

		for (int i = 0; i < l; i++) {
			
			finishTime = sequence.get(i).getProcessingTime() +
						sequence.get(i).getStartTime().getTime();

			double tardiness = 0.0;

			if (finishTime > sequence.get(i).getDuedate().getTime())
				tardiness = finishTime- sequence.get(i).getDuedate().getTime();
			else
				tardiness = 0.0;

			cost += tardiness * sequence.get(i).getPenalty() + sequence.get(i).getCost();
		}
		return cost;
	}
}	
