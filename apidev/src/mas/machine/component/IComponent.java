package mas.machine.component;

public abstract class IComponent {

	public abstract double getEta();
	public abstract double getBeta();
	public abstract double getAge();
	public abstract double getLife();
	public abstract double getTTR();
	public abstract MachineComponent getStatus();
	public abstract double getFailureCost();
	public abstract double getMTTR();
	public abstract double getDelayMean();
	public abstract double getDelayVariation();
	public abstract void repair();
	public abstract void addAge(long millis) ;
}
