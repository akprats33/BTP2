package mas.localScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.Date;
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
	private double bidNo;
	private Random r;
	private String replyWith;
	private double processingCost;
	private String[] supportedOps;
	private boolean isOpSupported=false; //IS current operation is supported by this LSA

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();
		//processing cost in Rs. per second
		processingCost=(double)bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.ProcessingCost).getValue();
		supportedOps=(String[])bfBase.getBelief(ID.LocalScheduler.BeliefBaseConst.supportedOperations).getValue();
//		r=new Random();
		
		this.blackboard = (AID) bfBase.
				getBelief(ID.LocalScheduler.BeliefBaseConst.blackboardAgent).
				getValue();

		msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		replyWith=msg.getReplyWith();
		
		try {
			jobToBidFor = (job)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void action() {
		try{
			
			for(int i=0;i<supportedOps.length;i++){
				
				if(supportedOps[i].equalsIgnoreCase(jobToBidFor.getCurrentOperation().
						getJobOperationType().toString())){
					isOpSupported=true;
				}
			}
			
			jobToBidFor=setBid(jobToBidFor);

			ZoneDataUpdate bidForJobUpdate=new ZoneDataUpdate.Builder(ID.LocalScheduler.ZoneData.bidForJob)
				.value(jobToBidFor).setReplyWith(replyWith).Build();

			AgentUtil.sendZoneDataUpdate(blackboard ,bidForJobUpdate, myAgent);

			//			log.info("Sending bid for job :" + jobToBidFor);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private job setBid(job j){
		if(isOpSupported){
			jobQueue = (ArrayList<job>) bfBase.
					getBelief(ID.LocalScheduler.BeliefBaseConst.jobQueue).
					getValue();

			ArrayList<job> tempQueue = new  ArrayList<job>();
			tempQueue.addAll(jobQueue);
			tempQueue.add(j);

			ScheduleSequence sch = new ScheduleSequence(tempQueue);
			ArrayList<job> tempqSolution = sch.getSolution();

			

			double PenaltyAfter=getPenaltyLocalDD(tempqSolution);
//			log.info("PenaltyAfter="+getPenaltyLocalDD(tempqSolution));
			double PenaltyBefore=getPenaltyLocalDD(jobQueue);
			log.info(myAgent.getLocalName()+" job Q size="+jobQueue.size());
//			log.info("PenaltyBefore="+getPenaltyLocalDD(jobQueue));
			double incremental_penalty=PenaltyAfter - PenaltyBefore;
			log.info(myAgent.getLocalName()+" incremental penalty="+incremental_penalty);
			
			bidNo=/*r.nextInt(10)+*/PenaltyAfter-PenaltyBefore;
		}
		else{
			bidNo=Double.MAX_VALUE;
			log.info("Operation " +jobToBidFor.getCurrentOperation().
					getJobOperationType().toString() +"not supported");
		}

		j.setBidByLSA(bidNo);
		j.setLSABidder(myAgent.getAID());
		
		return j;
	}

	public double getPenaltyLocalDD(ArrayList<job> sequence) {
		long finishTime = 0;
		long cumulativeProcessingTime=0;//sum of processing times of jobs in Q standing ahead 
		//in milliseconds
		
		sequence=setStartTimes(sequence);
		
		double cost = 0.0;
		int l = sequence.size();

		for (int i = 0; i < l; i++) {
			
			finishTime = cumulativeProcessingTime+ sequence.get(i).getCurrentOperationProcessTime() +
					sequence.get(i).getCurrentOperationStartTime();
			//getProcessingTime gives in time in milliseconds

			cumulativeProcessingTime=cumulativeProcessingTime+(long)sequence.get(i).getCurrentOperationProcessTime();

			double tardiness = 0.0;
			
//			log.info(myAgent.getLocalName()+ " cpt="+cumulativeProcessingTime +" L="+l+"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());
			
			if (finishTime > sequence.get(i).getCurrentOperationDueDate()){
				tardiness = (finishTime - sequence.get(i).getCurrentOperationDueDate())/1000.0;
//				log.info(myAgent.getLocalName()+ " tardiness="+tardiness+" L="+l+"ft="+new Date(finishTime)+" dd="+sequence.get(i).getDuedate()+" st="+sequence.get(i).getStartTime());
			}
			else{

				tardiness = 0.0;
			}
			/*log.info("slack: "+(finishTime-sequence.get(i).getCurrentOperationDueDate())+
					" DueDate: "+new Date(sequence.get(i).getCurrentOperationDueDate())+
					" cumulativeProcessingTime="+cumulativeProcessingTime+
					" start date: "+new Date(sequence.get(i).getCurrentOperationStartTime()));*/

//			log.info("tardiness="+tardiness+" penalty rate="+sequence.get(i).getPenaltyRate());
			cost += tardiness * sequence.get(i).getPenaltyRate() ;/*+ sequence.get(i).getCost();*/

		}


			log.info(myAgent.getLocalName()+" cost="+cost+" with L="+l);
			
		return cost;
	}

	private ArrayList<job> setStartTimes(ArrayList<job> sequence) {
		long CumulativeWaitingTime=0;
		for(int i=0;i<sequence.size();i++){
			sequence.get(i).setCurrentOperationStartTime(CumulativeWaitingTime+System.currentTimeMillis());
			CumulativeWaitingTime=CumulativeWaitingTime+(long)sequence.get(i).getCurrentOperationProcessTime();
		}
		return sequence;
		
	}
}	
