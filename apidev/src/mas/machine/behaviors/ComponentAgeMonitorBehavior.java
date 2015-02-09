package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jade.core.behaviours.Behaviour;

public class ComponentAgeMonitorBehavior extends Behaviour{

	private static final long serialVersionUID = 1L;
	private Logger log;
	private int step = 0;

	public ComponentAgeMonitorBehavior() {
		log = LogManager.getLogger();
	}


	@Override
	public void action() {
		switch(step) {
		case 0:
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
