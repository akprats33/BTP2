package mas.maintenance.agent;

import jade.core.AID;

import java.util.HashSet;
import java.util.Set;

import mas.job.job;
import mas.machine.SimulatorInternals;
import mas.maintenance.goal.CorrectiveMachineComponentsRepairGoal;
import mas.maintenance.goal.MaintenanceStartSendInfoGoal;
import mas.maintenance.goal.PreventiveMaintenanceGoal;
import mas.maintenance.goal.RegisterMaintenanceAgentServiceGoal;
import mas.maintenance.goal.RegisterMaintenanceAgentToBlackboardGoal;
import mas.maintenance.goal.machineHealthCheckGoal;
import mas.maintenance.plan.CorrectiveMachineComponentsRepairPlan;
import mas.maintenance.plan.MaintenanceStartSendInfoPlan;
import mas.maintenance.plan.PreventiveMaintenancePlan;
import mas.maintenance.plan.RegisterMaintenanceAgentServicePlan;
import mas.maintenance.plan.RegisterMaintenanceAgentToBlackboardPlan;
import mas.maintenance.plan.machineHealthCheckPlan;
import mas.util.ID;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BeliefBase;
import bdi4jade.core.Capability;
import bdi4jade.core.PlanLibrary;
import bdi4jade.plan.Plan;
import bdi4jade.util.plan.SimplePlan;

public class RootMaintenanceBasicCapability extends Capability{

	private static final long serialVersionUID = 1L;
	
	public RootMaintenanceBasicCapability() {
		super(new BeliefBase(getBeliefs()), new PlanLibrary(getPlans()));
	}

	public static Set<Belief<?>> getBeliefs() {
		Set<Belief<?>> beliefs = new HashSet<Belief<?>>();

		Belief<AID> bboard = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.blackboardAgentAID);
		
		Belief<SimulatorInternals> myMachine = new TransientBelief<SimulatorInternals>(
				ID.Maintenance.BeliefBaseConst.machine);
		
		Belief<AID> mygsAgent = new TransientBelief<AID>(
				ID.Maintenance.BeliefBaseConst.globalSchAgentAID);
		
		Belief<job> maintJob  = new TransientBelief<job>(
				ID.Maintenance.BeliefBaseConst.maintenanceJob);

		beliefs.add(bboard);
		beliefs.add(myMachine);
		beliefs.add(mygsAgent);
		beliefs.add(maintJob);

		return beliefs;
	}

	public static Set<Plan> getPlans() {
		Set<Plan> plans = new HashSet<Plan>();

		plans.add(new SimplePlan(RegisterMaintenanceAgentServiceGoal.class,
				RegisterMaintenanceAgentServicePlan.class));

		plans.add(new SimplePlan(RegisterMaintenanceAgentToBlackboardGoal.class,
				RegisterMaintenanceAgentToBlackboardPlan.class));
		
		plans.add(new SimplePlan(machineHealthCheckGoal.class,
				machineHealthCheckPlan.class));

		plans.add(new SimplePlan(CorrectiveMachineComponentsRepairGoal.class,
				CorrectiveMachineComponentsRepairPlan.class));

		plans.add(new SimplePlan(MaintenanceStartSendInfoGoal.class,
				MaintenanceStartSendInfoPlan.class));
		
		plans.add(new SimplePlan(PreventiveMaintenanceGoal.class,
				PreventiveMaintenancePlan.class));

		return plans;
	}	

	@Override
	protected void setup() {
		myAgent.addGoal(new RegisterMaintenanceAgentServiceGoal());
		myAgent.addGoal(new RegisterMaintenanceAgentToBlackboardGoal());
		myAgent.addGoal(new machineHealthCheckGoal());
		myAgent.addGoal(new CorrectiveMachineComponentsRepairGoal() );
		myAgent.addGoal(new MaintenanceStartSendInfoGoal() );
//		myAgent.addGoal(new PreventiveMaintenanceGoal() );
	}
}
