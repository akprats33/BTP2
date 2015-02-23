package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.machine.MachineStatus;
import mas.machine.Methods;
import mas.machine.Simulator;
import jade.core.behaviours.CyclicBehaviour;

/**
 * @author Anand Prajapati
 *
 */

public class ParameterShifterBehavaior extends CyclicBehaviour{

	private static final long serialVersionUID = 1L;
	private int occuredRootCause;
	private int numRootCauses;
	private Logger log;
	private Simulator machineSimulator;
	private double[] timeToOccur;
	private int step = 0;
	private double parameterShiftRate;

	public ParameterShifterBehavaior() {

		log = LogManager.getLogger();

		this.numRootCauses = machineSimulator.getmParameterRootcauses().size();
		timeToOccur = new double[numRootCauses];
		//		this.parameterShiftRate = machineSimulator.get

	}

	@Override
	public void action() {
		switch(step) {

		case 0:
			timeToOccur = Methods.rexp (this.parameterShiftRate, numRootCauses );
			break;

		case 1:
			if(machineSimulator.getStatus() != MachineStatus.FAILED) {
				for(int cause = 0; cause < numRootCauses ; cause ++) {

					if(timeToOccur[cause] >= 0) {
						timeToOccur[cause] -= Simulator.TIME_STEP;
					}
					else if(timeToOccur[cause] < 0) {

						step = 2;
					}
				}
				block(Simulator.TIME_STEP);

			} else {
				// machine is failed
				block(Simulator.TIME_STEP);
			}

			break;
		case 2:

			for(int j = 0; j < machineSimulator.getmParameterRootcauses().get(occuredRootCause).size(); j++) {
				if((!machineSimulator.getmParameterRootcauses().get(occuredRootCause).isEmpty()) &&
						occuredRootCause < this.numRootCauses ) {

					machineSimulator.getMachineParameters().get(j).setvalue(
							machineSimulator.getMachineParameters().get(j).getvalue() +
							Methods.normalRandom( machineSimulator.getmParameterRootcauses().get(occuredRootCause).get(j).getmean(),
									machineSimulator.getmParameterRootcauses().get(occuredRootCause).get(j).getsd()));
				}
			}

			step = 0;
			break;
		}
	}
}
