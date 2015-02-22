package mas.machine.behaviors;

import mas.machine.Simulator;
import mas.util.ID;
import mas.util.ZoneDataUpdate;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class ReportHealthBehavior extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	private Simulator machineSimulator;

	public ReportHealthBehavior(Agent a, long period) {
		super(a, period);
		machineSimulator = (Simulator) getParent().
				getDataStore().get(Simulator.simulatorStoreName);
	}

	@Override
	protected void onTick() {
		ZoneDataUpdate machineHealthUpdate = new ZoneDataUpdate(
				ID.Machine.ZoneData.myHealth,
				machineSimulator);

		machineHealthUpdate.send(Simulator.blackboardAgent ,
				machineHealthUpdate, myAgent);
	}
}
