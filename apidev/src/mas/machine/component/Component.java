package mas.machine.component;

import java.util.Random;

import org.apache.commons.math3.distribution.WeibullDistribution;

public class Component extends IComponent {

	private double startingAge;
	private double timeSinceLastFailure;
	private double beta;
	private double eta;
	private double timeToFailure;
	private double RestorationFactor;
	private int restorationType;
	private double MTTR;
	private double TTR_sd;
	private double MeanDelay;
	private double VarianceDelay;
	private MachineComponent status;
	private double currentAge;
	private double failureCost;
	private double replacementCost;
	private double preventiveMaintenanceCost;
	private long lastMaintTime;
	private double lifeToFailure;

	public Component(Builder builder){
		this.beta = builder.beta;
		this.eta = builder.eta;
		this.RestorationFactor = builder.RestorationFactor;
		this.restorationType = builder.restorationType;
		this.MTTR = builder.MTTR;
		this.TTR_sd = builder.TTR_sd;
		this.MeanDelay = builder.MeanDelay;
		this.VarianceDelay = builder.VarianceDelay;
		this.failureCost = builder.failureCost;
		this.replacementCost = builder.replacementCost;
		this.preventiveMaintenanceCost = builder.prevMaintCost;

		this.startingAge = 0;
		//		this.TTF = age_init;
		this.status = MachineComponent.WORKING;
		this.currentAge = 0;
		this.lifeToFailure = 0;
	}

	public static class Builder {
		//Required parameters
		private double beta;
		private double eta;
		private double RestorationFactor;
		private int restorationType;
		private double MTTR;
		private double TTR_sd;
		private double MeanDelay;
		private double VarianceDelay;
		private double failureCost;
		private double prevMaintCost;
		private double replacementCost;

		public Builder(double eta, double beta) {
			this.eta = eta;
			this.beta = beta;
		}

		public Builder restorationFactor(double val)
		{ this.RestorationFactor = val; return this; }

		public Builder restorationFactorType(int type)
		{ this.restorationType = type; return this; }

		public Builder MTTR(double val)
		{ this.MTTR = val; return this; }

		public Builder TTR_sd(double val)
		{ this.TTR_sd = val; return this; }

		public Builder meanDelay(double val)
		{ this.MeanDelay = val; return this; }

		public Builder sdDelay(double val)
		{ this.VarianceDelay = val; return this; }

		public Builder failureCost(double val)
		{ this.failureCost = val; return this; }

		public Builder prevMaintCost(double val)
		{ this.prevMaintCost = val; return this; }

		public Builder replacementCost(double val)
		{ this.replacementCost = val; return this; }

		public Component build() {
			return new Component(this);
		}
	}

	/**
	 * generate and assign life to this component using weibull distribution
	 */
	public void generateLife() {
		Random unifEnd = new Random();
		double uniformRandom =unifEnd.nextDouble();
		double life = this.eta * (Math.pow 
				( Math.pow( this.currentAge/eta,beta) - Math.log(1-uniformRandom),
						(1/beta) ));  
		this.lifeToFailure = life;
	}

	/**
	 * generate new initial age for the component after being repaired based on
	 * restoration factor type 1
	 * For theory of restoration factor, please refer to 
	 * http://www.reliawiki.org/index.php/Imperfect_Repairs
	 * 
	 * repairs will only fix the wear-out and damage incurred during
	 * the last period of operation
	 */

	public void generateLifeRFtype1() {

		WeibullDistribution wb = new WeibullDistribution(this.beta,this.eta);
		Random unifEnd = new Random();
		double uniformRandom =unifEnd.nextDouble();

		this.startingAge = this.startingAge + 
				this.timeSinceLastFailure * (1 - this.RestorationFactor);

		double futureReliability = uniformRandom*
				(1.0 - wb.cumulativeProbability(this.startingAge));

		this.lifeToFailure = wb.inverseCumulativeProbability(1.0 - futureReliability) -
				this.startingAge;

		if(	this.lifeToFailure < 0) {
			this.lifeToFailure = 0;
			//			System.out.println("this is the fault");
		}
		//		this.age_init = this.age_init + this.TTF;
	}

	/**
	 * generate new initial age for the component after being repaired based on
	 * restoration factor type 2
	 * For theory of restoration factor, please refer to 
	 * http://www.reliawiki.org/index.php/Imperfect_Repairs
	 * 
	 * repairs fix all of the wear-out and damage accumulated up to the current time
	 */

	public void generateLifeRFtype2() {

		WeibullDistribution wb = new WeibullDistribution(this.beta,this.eta);
		Random unifEnd = new Random();
		double uniformRandom =unifEnd.nextDouble();

		this.startingAge = this.currentAge * (1 - this.RestorationFactor);

		double futureReliability = uniformRandom*
				(1.0 - wb.cumulativeProbability(this.startingAge));

		this.lifeToFailure = wb.inverseCumulativeProbability(1.0 - futureReliability) -
				this.startingAge;

		if(	this.lifeToFailure < 0) {
			this.lifeToFailure = 0;
			//			System.out.println("this is the fault");
		}
		//		this.age_init = this.age_init + this.TTF;
	}

	/**
	 * 
	 * @param a is shape parameter
	 * @param b is scale parameter
	 * @return weibull distributed random number
	 */
	public double WeibullRnd(double a, double b)
	{
		WeibullDistribution wb = new WeibullDistribution(a,b);
		Random unifEnd = new Random();
		double uniformRandom = unifEnd.nextDouble();
		double wbrnd = wb.inverseCumulativeProbability(1.0 - uniformRandom);
		return wbrnd;
	}

	@Override
	public double getEta() {
		return this.eta;
	}

	@Override
	public double getBeta() {
		return this.beta;
	}

	@Override
	public double getAge() {
		return this.currentAge;
	}

	@Override
	public double getLife() {
		return this.lifeToFailure;
	}

	@Override
	public double getTTR() {
		return this.TTR_sd;
	}

	@Override
	public MachineComponent getStatus() {
		return this.status;
	}

	@Override
	public double getFailureCost() {
		return this.failureCost;
	}

	@Override
	public double getMTTR() {
		return this.MTTR;
	}

	@Override
	public double getDelayMean() {
		return this.MeanDelay;
	}

	@Override
	public double getDelayVariation() {
		return this.VarianceDelay;
	}

}
