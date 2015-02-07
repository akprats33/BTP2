package mas.machine;

import java.util.ArrayList;

import mas.machine.component.IComponent;

import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import simulator.machine.Component;

public class Methods {
	
	//generating random number array following a normal distribution with mean and sd
	public static double[] normalRandom(double mean, double sd, int arraySize) {
		RandomGenerator rg = new JDKRandomGenerator();
		GaussianRandomGenerator g= new GaussianRandomGenerator(rg);
		double[] a=new double[arraySize];
		for(int i=0;i<arraySize;i++){
			a[i]=mean+g.nextNormalizedDouble()*sd;
		}
		return a;
	}

	//gives random number following nomral distribution with mean and sd
	public static double normalRandom(double mean, double sd) {				
		RandomGenerator rg = new JDKRandomGenerator();
		GaussianRandomGenerator g= new GaussianRandomGenerator(rg);
		double a=mean+g.nextNormalizedDouble()*sd;
		return a;
	}

	//generate random number array following exponential distribution
	public static double[] rexp(double rate,int sizeArray) {
		ExponentialDistribution g=new ExponentialDistribution(rate);
		int i=0;
		double[] arr=new double[sizeArray];
		while(i<sizeArray){
			arr[i]=g.sample();
			i++;
		}
		return arr;
	}

	//generate random number(between 0 and 1) following discrete distribution of weights
	public static int[] runif(int numSamples,int[] weights) {
		
		int[] numsToGenerate = new int[] {0 ,1  , 2  ,  3,  4 ,  5,    6,  7 ,  8 , 9 };

		int[] temp=weights;
		double sum=0.0;
		double[] discreteProbabilities=new double[numsToGenerate.length];
		int i;
		for(i=0;i<numsToGenerate.length;i++) {

			discreteProbabilities[i]=temp[i];
			sum+=temp[i];
		}
		for(i=0;i<numsToGenerate.length;i++) {
			discreteProbabilities[i]=discreteProbabilities[i]/sum;
		}

		EnumeratedIntegerDistribution distribution = 
				new EnumeratedIntegerDistribution(numsToGenerate, discreteProbabilities);

		int[] samples = distribution.sample(numSamples);
		return samples;
	}
	
	public double getLoadingTime(double mean,double sd)	{
		double[] r_arr = normalRandom(0, sd, 1);
		return (mean + r_arr[0]);
	}

	public double getunloadingTime(double mean,double sd) {
		double[] r_arr = normalRandom(0, sd, 1);
		return (mean + r_arr[0]);
	}
	
	//returns index of component with minimum Time to failure
	public static int findMin(ArrayList<IComponent> components) { 
		int IndMin = 0;
		for(int index = 0;index < components.size(); index++) {
			if(components.get(IndMin).TTF > components.get(index).TTF){
				IndMin = index;
			}
		}
		
		return IndMin;
	}
}
