package mas.machine.behaviors;

import jade.core.behaviours.OneShotBehaviour;

import java.util.ArrayList;
import mas.job.job;
import mas.job.jobAttribute;
import mas.job.jobDimension;
import mas.machine.Methods;
import mas.machine.Simulator;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessJobBehavior extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private job comingJob;
	private Logger log;

	public ProcessJobBehavior(job processJob) {
		this.comingJob = processJob;
	}

	@Override
	public void action() {

		log = LogManager.getLogger();

		// Assign dimensions to the job
		ArrayList<jobDimension> jDimensions = comingJob.getDimensions();
		int numDims = jDimensions.size();
		int dIndex;

		for(dIndex = 0; dIndex < numDims; dIndex++) {

			jDimensions.get(dIndex).setTargetDimension(
					jDimensions.get(dIndex).getTargetDimension() +
					Methods.normalRandom(Simulator.mean_shift,Simulator.sd_shift));

			//			jDimensions.get(dIndex).add(jDimensions.get(dIndex) + 3*Simulator.sd_shift);
			//			jDimensions.get(dIndex).add(jDimensions.get(dIndex) - 3*Simulator.sd_shift);
		}
		comingJob.setDimensions(jDimensions);

		// Assign attributes to the job
		ArrayList<jobAttribute> jAttributes = comingJob.getAttributes();
		int numAttributes = jDimensions.size();
		int AttIndex;

		BinomialDistribution bernoulli =
				new BinomialDistribution(1, Simulator.fractionDefective);

		boolean conforming;
		for(AttIndex = 0; AttIndex < numAttributes; AttIndex++) {

			conforming = (bernoulli.sample()==1)? Boolean.TRUE :Boolean.FALSE;
			jAttributes.get(dIndex).setConforming(conforming);
		}
		comingJob.setAttributes(jAttributes);
	}
}
