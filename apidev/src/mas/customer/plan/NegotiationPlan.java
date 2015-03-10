package mas.customer.plan;

import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.ZoneDataUpdate;

import org.apache.logging.log4j.Logger;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import bdi4jade.core.BeliefBase;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class NegotiationPlan extends Behaviour implements PlanBody {
	private static final long serialVersionUID = 1L;
	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job negotiationJob;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {

		ACLMessage msg = ( (MessageGoal)pInstance.getGoal()).getMessage();

		try {
			negotiationJob = (job) msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}

		this.bfBase = pInstance.getBeliefBase();

		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();
	}

	@Override
	public void action() {

		setNegotiation(negotiationJob);

	}

	public void setNegotiation(job j) {
		/**
		 *  Write your own negotiation logic here. You have job j to negotiate with scheduler.
		 *  Take this job as input and change due date or profit or some other parameter
		 *  and return that job from this method.
		 *  first check if the sent negotiation is acceptable or not.
		 *  if it is acceptable, then job is simply being sent back to blackboard.
		 */

		long newDueDate = (long) (1.1 * j.getDuedate().getTime());
		double newprofit = 0.9 * j.getProfit();

		if(newDueDate < j.getDuedate().getTime() ){
			ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate(
					ID.Customer.ZoneData.customerConfirmedJobs,
					negotiationJob);

			AgentUtil.sendZoneDataUpdate(this.bba,negotiationJobDataUpdate, myAgent);

			return;
		}else {
			j.setDuedate(newDueDate);
			j.setProfit(newprofit);
			ZoneDataUpdate negotiationJobDataUpdate = new ZoneDataUpdate(
					ID.Customer.ZoneData.customerJobsUnderNegotiation,
					negotiationJob);

			AgentUtil.sendZoneDataUpdate(this.bba,negotiationJobDataUpdate, myAgent);
		}
	}

	@Override
	public boolean done() {
		return true;
	}
}
