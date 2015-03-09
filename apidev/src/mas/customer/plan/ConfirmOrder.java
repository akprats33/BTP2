package mas.customer.plan;

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
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.UnreadableException;

public class ConfirmOrder extends OneShotBehaviour implements PlanBody{

	private Logger log;
	private BeliefBase bfBase;
	private AID bba;
	private job ConfirmedJob;

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		log = LogManager.getLogger();
		bfBase = arg0.getBeliefBase();
		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBaseConst.blackboardAgent)
				.getValue();
		
		try {
			this.ConfirmedJob=(job)((MessageGoal)(arg0.getGoal())).getMessage().getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void action() {
		ZoneDataUpdate ConfirmedOrderZoneDataUpdate = new ZoneDataUpdate(
				ID.Customer.ZoneData.customerConfirmedJobs,
				ConfirmedJob);
		AgentUtil.sendZoneDataUpdate(bba, ConfirmedOrderZoneDataUpdate, myAgent);
		
	}

}
