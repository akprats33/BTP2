package mas.machine.behaviors;

import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import mas.machine.Methods;
import mas.machine.Simulator;

public class ShiftInProcessBahavior extends CyclicBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean mean,sd;
	int count;
	double expRandom;

	//shifter shifts the process mean or standard deviation
	ShiftInProcessBahavior(boolean byMean,boolean bySd ) 
	{
		this.mean = byMean;
		this.sd = bySd;
		//shifting of process is exponentially distributed
		expRandom = Methods.rexp(1/Simulator.rateShift, 1)[0];			 		
	}
	@Override
	public void action() {
		expRandom = Methods.rexp(1/Simulator.rateShift, 1)[0];
		long timeToOccur = (long) Math.max(1, expRandom*1000);
		myAgent.addBehaviour(new WakerBehaviour(myAgent,timeToOccur) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void handleElapsedTimeout() {
				if(mean == true) {
					Simulator.mean_shift = Simulator.mean_shift*(1.0 + 
							(Methods.normalRandom(Simulator.mean_shiftInMean,
									Simulator.sd_shiftInMean)/100.0));	
				}
				if(sd == true) {
					Simulator.sd_shift = Simulator.sd_shift*(1.0 + 
							(Methods.normalRandom(Simulator.mean_shiftInSd,
									Simulator.sd_shiftInSd)/100.0));
				}
			}
		});
	}
}
