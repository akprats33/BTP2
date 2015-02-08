package mas.machine.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.machine.Methods;
import mas.machine.Simulator;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 * 
 * @author Anand Prajapati
 *
 */
public class ParameterShifterBehavaior extends TickerBehaviour{

	private static final long serialVersionUID = 1L;
	long interArrival;
	int index;
	private int numRootCauses;
	private Logger log;
	public ParameterShifterBehavaior(Agent a, long period) {

		super(a, period);
		log = LogManager.getLogger();

		this.numRootCauses = Simulator.rootcauses.size();
		Simulator.rootcause_timeto_occur = new double[numRootCauses];
		double[] expRandom = Methods.rexp (5, numRootCauses );

		for (int p = 0 ; p < numRootCauses; p++) {
			Simulator.rootcause_timeto_occur[p] = expRandom[p] ;
		}
		index = Methods.findMinElemet(Simulator.rootcause_timeto_occur);
		interArrival = (long) Simulator.rootcause_timeto_occur [index]*1000;
		reset(interArrival);
	}

	@Override
	protected void onTick() {
		for(int j = 0; j < this.numRootCauses; j++) {
			if((!Simulator.rootcauses.get(index).isEmpty()) &&
					index < this.numRootCauses ) {

				Simulator.params.get(j).setvalue(
						Simulator.params.get(j).getvalue() +
						Methods.normalRandom(Simulator.rootcauses.get(index).get(j).getmean(),
								Simulator.rootcauses.get(index).get(j).getsd()));
			}

			for (int p = 0 ; p < this.numRootCauses; p++) {
				Simulator.rootcause_timeto_occur [p] -=
						Simulator.rootcause_timeto_occur [index];
			}

			Simulator.rootcause_timeto_occur[index] = Methods.rexp(5, 1)[0];

			index = Methods.findMinElemet(Simulator.rootcause_timeto_occur);
			interArrival = (long)(Simulator.rootcause_timeto_occur[index]*1000);
			log.info("interarrival for rootcause" + interArrival);
			reset(interArrival);
		}
	}
}
