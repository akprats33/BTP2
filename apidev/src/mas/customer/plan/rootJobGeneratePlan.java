package mas.customer.plan;

import mas.customer.JobGenerator;
import mas.customer.JobGeneratorIFace;
import mas.customer.goal.dispatchJobGoal;
import mas.util.ID;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;

public class rootJobGeneratePlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	public static double rate = 0.2;
	public static int initialDelay = 5000;
	private PlanInstance planInstance;
	private ExponentialDistribution exp;
	private JobGeneratorIFace jGen;
	private BeliefBase bfBase;
	private Logger log;

	@Override
	public EndState getEndState() {
		return EndState.SUCCESSFUL;
	}

	@Override
	public void init(PlanInstance pInstance) {
		log = LogManager.getLogger();
		this.planInstance = pInstance;
		this.jGen = new JobGenerator();
		this.exp = new ExponentialDistribution(1/rate);
		bfBase = pInstance.getBeliefBase();
		this.jGen.readFile();
		bfBase.updateBelief(ID.Customer.BeliefBaseConst.JOB_GENERATOR, jGen);
	}

	@Override
	public void action() {

		myAgent.addBehaviour(new TickerBehaviour(myAgent, initialDelay) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				bfBase.updateBelief(ID.Customer.BeliefBaseConst.CURRENT_JOB, jGen.getNextJob());
				planInstance.dispatchSubgoal(new dispatchJobGoal());
				reset(getInterArrivalTimeMillis());			
			}
		});
	}

	/**
	 * this method returns the inter-arrival time between two jobs 
	 * which by default is exponential distribution
	 * @return
	 */

	public long getInterArrivalTimeMillis() {
//		return (long) Math.max(1, exp.sample()*1000);
		return (long) 100000;
	}

	@Override
	public boolean done() {
		return true;
	}
}
