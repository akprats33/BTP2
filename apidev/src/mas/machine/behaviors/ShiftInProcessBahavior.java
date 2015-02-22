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
	private Simulator machinesSimulator;

	/**
	 * shifter shifts the process mean or standard deviation
	 * @param byMean
	 * @param bySd
	 */
	ShiftInProcessBahavior(boolean byMean,boolean bySd ) 
	{
		this.mean = byMean;
		this.sd = bySd;
		machinesSimulator = (Simulator) getDataStore().get(Simulator.simulatorStoreName);
		//shifting of process is exponentially distributed
		expRandom = Methods.rexp(1/machinesSimulator.getRateShift(), 1)[0];			 		
	}
	
	@Override
	public void action() {
		
		expRandom = Methods.rexp(1/machinesSimulator.getRateShift(), 1)[0];
		long timeToOccur = (long) Math.max(1, expRandom*1000);
		
		myAgent.addBehaviour(new WakerBehaviour(myAgent,timeToOccur) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void handleElapsedTimeout() {
				if(mean == true) {
					machinesSimulator.setMean_shift(machinesSimulator.getMean_shift()*(1.0 + 
							(Methods.normalRandom(machinesSimulator.getMean_shiftInMean(),
									machinesSimulator.getSd_shiftInMean())/100.0) ) );	
				}
				if(sd == true) {
					machinesSimulator.setSd_shift( machinesSimulator.getSd_shift()*(1.0 + 
							(Methods.normalRandom(machinesSimulator.getMean_shiftInSd(),
									machinesSimulator.getSd_shiftInSd())/100.0) ) );
				}
			}
		});
	}
}
