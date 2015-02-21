package mas.customer.plan;

import mas.customer.JobGenerator;
import mas.customer.JobGeneratorIFace;
import mas.customer.goal.dispatchJobGoal;
import mas.util.ID;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;

public class jobGeneratorPlan extends Behaviour implements PlanBody{

	private static final long serialVersionUID = 1L;
	private double rate;
	private PlanInstance planInstance;
	private ExponentialDistribution exp;
	private JobGeneratorIFace jGen;
	private BeliefBase bfBase;
	
	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance pInstance) {
		this.planInstance = pInstance;
		this.jGen = new JobGenerator();
		this.exp = new ExponentialDistribution(1/rate);
		bfBase = pInstance.getBeliefBase();
		
	}

	@Override
	public void action() {
		this.jGen.readFile();
		bfBase.updateBelief(ID.Customer.BeliefBase.JOB_GENERATOR, jGen);
		myAgent.addBehaviour(new TickerBehaviour(myAgent, 1000) {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				bfBase.updateBelief(ID.Customer.BeliefBase.CURRENT_JOB, jGen.getNextJob());
				planInstance.dispatchSubgoal(new dispatchJobGoal());
				reset(getInterArrivalTime());
			}
		});
	}
	
	public long getInterArrivalTime(){
		return (long) Math.max(1, exp.sample());
	}

	@Override
	public boolean done() {
		return true;
	}

}
