package mas.machine.behaviors;

import mas.machine.MachineStatus;
import mas.machine.Simulator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jade.core.behaviours.Behaviour;

public class ComponentAgeMonitorBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private transient Logger log;
	private transient int step = 0;
	private transient int WAIT_STEP = 100;
	private transient Simulator mySim;

	public ComponentAgeMonitorBehavior() {
		log = LogManager.getLogger();
	}

	@Override
	public void action() {
		switch(step) {
		case 0:
			mySim = (Simulator) getDataStore().get(Simulator.mySimulator);
			if (mySim.getStatus() != MachineStatus.FAILED) {
				
			}
			else {
				block(WAIT_STEP);
			}
			break;
		case 1:
			break;

		}
	}

	@Override
	public boolean done() {
		return step >= 2;
	}

}
