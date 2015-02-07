package mas.machine.component;

import java.util.Random;
import org.apache.commons.math3.distribution.WeibullDistribution;

public class Component extends IComponent {

	private double intitalAge;
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

		this.intitalAge = 0;
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
