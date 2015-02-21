package mas.machine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.machine.behaviors.HandleSimulatorFailedBehavior;

public class SimulatorStatusListener implements PropertyChangeListener {

	private transient Logger log;
	private Simulator sim;

	public SimulatorStatusListener(Simulator sim) {
		this.sim = sim;
		log = LogManager.getLogger();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("Machine status")) {
			
			if(evt.getNewValue().equals(MachineStatus.FAILED)) {
				log.info("Simulator is in failed state :" );

				sim.addBehaviour(new HandleSimulatorFailedBehavior());
			} 
			else if(evt.getNewValue().equals(MachineStatus.IDLE)) {
				log.info("Simulator is in failed state :" );

//				sim.addBehaviour(new HandleSimulatorFailedBehavior());
			}
			else if(evt.getNewValue().equals(MachineStatus.PROCESSING)) {
				log.info("Simulator is in failed state :" );

//				sim.addBehaviour(new HandleSimulatorFailedBehavior());
			}
			else if(evt.getNewValue().equals(MachineStatus.UNDER_MAINTENANCE)) {
				log.info("Simulator is in failed state :" );

//				sim.addBehaviour(new HandleSimulatorFailedBehavior());
			}
		}
	}
}
