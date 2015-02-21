package mas.customer.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.job.job;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class DispatchJobPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private BeliefBase bfBase;
	private job jobToDispatch;
	private AID bba;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		bfBase = pInstance.getBeliefBase();

		//		log.info(bfBase.getBelief(basicCapability.CURR_JOB));

		jobToDispatch = (job) bfBase
				.getBelief(ID.Customer.BeliefBase.CURRENT_JOB)
				.getValue();

		this.bba = (AID) bfBase
				.getBelief(ID.Customer.BeliefBase.blackAgent)
				.getValue();
	}

	/**
	 *  send the generated job to it's zonedata
	 */
	@Override
	public void action() {

		ZoneDataUpdate jobOrderZoneDataUpdate = new ZoneDataUpdate(
				ID.Customer.ZoneData.JobList,
				jobToDispatch,
				true);

		jobOrderZoneDataUpdate.send(this.bba,jobOrderZoneDataUpdate, myAgent);
	}

	@Override
	public boolean done() {
		return true;
	}
}
