package mas.localScheduling.plan;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import mas.job.job;
import mas.util.ID;
import mas.util.MessageIds;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

/**
 * @author Anand Prajapati
 *
 * this picks a job from the queue and sends it to the machine for processing
 */

public class SendJobPlan extends OneShotBehaviour implements PlanBody {

	private static final long serialVersionUID = 1L;
	private ACLMessage SendJobMsg;
	private ArrayList<job> jobQueue;
	private BeliefBase bfBase;

	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		
		bfBase = pInstance.getBeliefBase();
		ACLMessage msg = ((MessageGoal)pInstance.getGoal()).getMessage();
		
		// create a new message 
		SendJobMsg = new ACLMessage(ACLMessage.INFORM);	
		SendJobMsg.addReceiver(msg.getSender());		
		
		jobQueue = (ArrayList<job>) pInstance.getBeliefBase().
				getBelief(ID.LocalScheduler.BeliefBase.jobQueue).
				getValue();
	}

	@Override
	public void action() {
		
		if(jobQueue.size() != 0){
			try {
				SendJobMsg.setContentObject(jobQueue.get(0));
				SendJobMsg.setConversationId(MessageIds.SendJob);
				jobQueue.remove(0);			
				// send the job to message sender 
				myAgent.send(SendJobMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/**
			 * update the belief base
			 */
			bfBase.updateBelief(ID.LocalScheduler.BeliefBase.jobQueue, jobQueue);		
		}
	}
}
