package mas.machine;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Serializable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import mas.machine.behaviors.AcceptJobBehavior;
import mas.machine.behaviors.Connect2BlackBoardBehvaior;
import mas.machine.behaviors.GetRootCauseDataBehavior;
import mas.machine.behaviors.LoadComponentBehavior;
import mas.machine.behaviors.LoadMachineParameterBehavior;
import mas.machine.behaviors.LoadSimulatorParamsBehavior;
import mas.machine.behaviors.Register2DF;
import mas.machine.component.Component;
import mas.machine.component.IComponent;
import mas.machine.parametrer.Parameter;
import mas.machine.parametrer.RootCause;
import mas.util.MessageIds;

public class Simulator extends Agent implements IMachine,Serializable {

	private static final long serialVersionUID = 1L;

	// ID of this simulator
	public String ID_Simulator;

	// AID of blackboard agent to which it will connect and publish-receive information from
	public static AID blackboardAgent;

	// name of data store used in behavior's to update this object
	public static String simulatorStoreName = "simulatorStoreName";
	
	public static long healthReportTimeMillis = 20000;

	// list of components of machine
	private ArrayList<IComponent> myComponents;

	//starting time of this machine agent 
	private long epochTime;

	// status of this machine i.e. working/failed etc.
	private MachineStatus status;

	// property change support for status of simulator
	protected transient PropertyChangeSupport statusChangeSupport;

	// percentage variation in processing time of jobs
	private double percentProcessingTimeVariation = 0.10;		

	// parameters of loading time normal distribution ( in Milliseconds)
	private double meanLoadingTime = 1000.0;					
	private double sdLoadingTime = 1.0;

	// parameters of loading time normal distribution ( in Milliseconds)
	private double meanUnloadingTime = 1000.0;				
	private double sdUnloadingTime = 1.0;

	// fraction defective for attributes of jobs
	private transient double fractionDefective = 0.10;

	// parameters of process
	private transient double mean_shift = 0;					
	private transient double sd_shift = 1;

	// parameters of normal distribution causing shift in process mean
	private double mean_shiftInMean = 0;				
	private double sd_shiftInMean = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private transient double mean_shiftInSd = 0;				
	private transient double sd_shiftInSd = 1;

	//rate of process mean shifting (per hour)
	private transient double rateProcessShift = 0.01;				

	// parameters of process parameters
	private transient double mean_shiftParam = 0;					
	private transient double sd_shiftparam = 1;

	// parameters of normal distribution causing shift in process parameters
	private transient double mean_shiftInMeanParam = 0;				
	private transient double sd_shiftInMeanParam = 1;

	// parameters of normal distribution causing shift in process standard deviation
	private transient double mean_shiftInSdParam = 0;				
	private transient double sd_shiftInSdparam = 1;

	// machine parameters
	private transient ArrayList<Parameter> machineParameters ;
	
	// root causes for machine parameters which will make a shift in parameter's generative process
	private transient ArrayList<ArrayList<RootCause>> mParameterRootcauses; 

	private void init() {
		statusChangeSupport = new PropertyChangeSupport(this);
		epochTime = System.currentTimeMillis();
		myComponents = new ArrayList<IComponent>();
		machineParameters = new ArrayList<Parameter>();
		mParameterRootcauses = new ArrayList<ArrayList<RootCause> >();
	}

	private transient SequentialBehaviour loadData;
	
	private transient Behaviour loadSimulatorParams;
	private transient Behaviour loadComponentData;
	private transient Behaviour loadMachineParams;
	private transient Behaviour loadRootCause;
	private transient Behaviour registerthis;
	private transient Behaviour connect2Blackboard;
	private transient Behaviour acceptIncomingJobs;
	private transient Behaviour reportHealth;

	private transient ParallelBehaviour functionality ;

	@Override
	protected void setup() {
		super.setup();
		
		/**
		 * initialize the variables
		 */
		init();

		loadData = new SequentialBehaviour(this);
		loadData.getDataStore().put(simulatorStoreName, Simulator.this);

		loadSimulatorParams = new LoadSimulatorParamsBehavior();
		loadComponentData = new LoadComponentBehavior();
		loadMachineParams = new LoadMachineParameterBehavior();
		loadRootCause = new GetRootCauseDataBehavior();
		registerthis = new Register2DF();
		connect2Blackboard = new Connect2BlackBoardBehvaior();

		loadData.addSubBehaviour(loadSimulatorParams);
		loadData.addSubBehaviour(loadComponentData);
		loadData.addSubBehaviour(loadMachineParams);
		loadData.addSubBehaviour(loadRootCause);

		loadData.addSubBehaviour(registerthis);
		loadData.addSubBehaviour(connect2Blackboard);

		addBehaviour(loadData);

		functionality = new ParallelBehaviour(this,
				ParallelBehaviour.WHEN_ALL);
		functionality.getDataStore().put(simulatorStoreName, Simulator.this);

		acceptIncomingJobs = new AcceptJobBehavior();
		reportHealth = new reportHealthBehavior(this, healthReportTimeMillis);

		functionality.addSubBehaviour(acceptIncomingJobs);
		functionality.addSubBehaviour(reportHealth);

		addBehaviour(functionality);

		/**
		 *  Adding a listener to the change in value of the status of simulator 
		 */
		statusChangeSupport.addPropertyChangeListener(
				new SimulatorStatusListener(Simulator.this));

	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}

	@Override
	public ArrayList<IComponent> getComponents() {
		return myComponents;
	}

	@Override
	public long getStartTime() {
		return this.epochTime;
	}

	@Override
	public MachineStatus getStatus() {
		return status;
	}

	public void setStatus(MachineStatus newStatus) {
		MachineStatus oldStatus = this.status;
		status = newStatus;
		if(newStatus == MachineStatus.FAILED){
			statusChangeSupport.
			firePropertyChange(
					"Machine status",oldStatus, newStatus);
		}
	}

	public void addComponent(Component c) {
		myComponents.add(c);
	}

	public long getNextFailureTime() {
		return epochTime;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		statusChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param arr is index of all components to get repaired
	 * 
	 * this function calls repair method on the passed components 
	 */

	public void repair(ArrayList<Integer> arr) {

		for (int index = 0; index < arr.size(); index++) { 
			myComponents.get(arr.get(index)).repair();
		}
		this.status = MachineStatus.IDLE;
	}

	/**
	 * @param millis
	 * Age all the components of this simulator by an amount millis
	 * 
	 */
	public synchronized void AgeComponents(long millis) {
		for(int index = 0; index < myComponents.size(); index++) {
			myComponents.get(index).addAge(millis);
		}
	}

	public String getID_Simulator() {
		return ID_Simulator;
	}

	public void setID_Simulator(String iD_Simulator) {
		this.ID_Simulator = iD_Simulator;
	}

	public double getPercentProcessingTimeVariation() {
		return percentProcessingTimeVariation;
	}

	public void setPercentProcessingTimeVariation(
			double percentProcessingTimeVariation) {
		this.percentProcessingTimeVariation = percentProcessingTimeVariation;
	}

	public double getMeanLoadingTime() {
		return meanLoadingTime;
	}

	public void setMeanLoadingTime(double meanLoadingTime) {
		this.meanLoadingTime = meanLoadingTime;
	}

	public double getSdLoadingTime() {
		return sdLoadingTime;
	}

	public void setSdLoadingTime(double sdLoadingTime) {
		this.sdLoadingTime = sdLoadingTime;
	}

	public double getMeanUnloadingTime() {
		return meanUnloadingTime;
	}

	public void setMeanUnloadingTime(double meanUnloadingTime) {
		this.meanUnloadingTime = meanUnloadingTime;
	}

	public double getSdUnloadingTime() {
		return sdUnloadingTime;
	}

	public void setSdUnloadingTime(double sdUnloadingTime) {
		this.sdUnloadingTime = sdUnloadingTime;
	}

	public double getFractionDefective() {
		return fractionDefective;
	}

	public void setFractionDefective(double fractionDefective) {
		this.fractionDefective = fractionDefective;
	}

	public double getMean_shiftInMean() {
		return mean_shiftInMean;
	}

	public void setMean_shiftInMean(double mean_shiftInMean) {
		this.mean_shiftInMean = mean_shiftInMean;
	}

	public double getSd_shiftInMean() {
		return sd_shiftInMean;
	}

	public void setSd_shiftInMean(double sd_shiftInMean) {
		this.sd_shiftInMean = sd_shiftInMean;
	}

	public double getMean_shiftInSd() {
		return mean_shiftInSd;
	}

	public void setMean_shiftInSd(double mean_shiftInSd) {
		this.mean_shiftInSd = mean_shiftInSd;
	}

	public double getSd_shiftInSd() {
		return sd_shiftInSd;
	}

	public void setSd_shiftInSd(double sd_shiftInSd) {
		this.sd_shiftInSd = sd_shiftInSd;
	}

	public double getMean_shift() {
		return mean_shift;
	}

	public void setMean_shift(double mean_shift) {
		this.mean_shift = mean_shift;
	}

	public double getSd_shift() {
		return sd_shift;
	}

	public void setSd_shift(double sd_shift) {
		this.sd_shift = sd_shift;
	}

	public double getRateShift() {
		return rateProcessShift;
	}

	public void setRateShift(double rateShift) {
		this.rateProcessShift = rateShift;
	}

	public double getMean_shiftInMeanParam() {
		return mean_shiftInMeanParam;
	}

	public void setMean_shiftInMeanParam(double mean_shiftInMeanParam) {
		this.mean_shiftInMeanParam = mean_shiftInMeanParam;
	}

	public double getSd_shiftInMeanParam() {
		return sd_shiftInMeanParam;
	}

	public void setSd_shiftInMeanParam(double sd_shiftInMeanParam) {
		this.sd_shiftInMeanParam = sd_shiftInMeanParam;
	}

	public double getMean_shiftInSdParam() {
		return mean_shiftInSdParam;
	}

	public void setMean_shiftInSdParam(double mean_shiftInSdParam) {
		this.mean_shiftInSdParam = mean_shiftInSdParam;
	}

	public double getSd_shiftSdparam() {
		return sd_shiftInSdparam;
	}

	public void setSd_shiftSdparam(double sd_shiftSdparam) {
		this.sd_shiftInSdparam = sd_shiftSdparam;
	}

	public double getMean_shiftParam() {
		return mean_shiftParam;
	}

	public void setMean_shiftParam(double mean_shiftParam) {
		this.mean_shiftParam = mean_shiftParam;
	}

	public double getSd_shiftparam() {
		return sd_shiftparam;
	}

	public void setSd_shiftparam(double sd_shiftparam) {
		this.sd_shiftparam = sd_shiftparam;
	}

	public ArrayList<Parameter> getMachineParameters() {
		return machineParameters;
	}

	public void addMachineParameter(Parameter p ){
		this.machineParameters.add(p);
	}

	public ArrayList<ArrayList<RootCause>> getmParameterRootcauses() {
		return mParameterRootcauses;
	}

	public void addmParameterRootCause(ArrayList<RootCause> rootcause) {
		this.mParameterRootcauses.add(rootcause);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
				append(ID_Simulator).
				append(myComponents).
				hashCode();
	}

	/**
	 *  to check for equality
	 *  required for serializability
	 *  correct the logic inside
	 */

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Simulator){
			final Simulator other = (Simulator) obj;
			return new EqualsBuilder()
			.append(ID_Simulator, other.ID_Simulator)
			.append(myComponents, other.myComponents)
			.isEquals();
		} else {
			return false;
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(status);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	public class reportHealthBehavior extends TickerBehaviour {

		private static final long serialVersionUID = 1L;
		private ACLMessage health;
		public reportHealthBehavior(Agent a, long period) {
			super(a, period);
			health = new ACLMessage(ACLMessage.INFORM);
			health.setConversationId(MessageIds.MaintMachineHealth);
			//			health.addReceiver(Simulator.blackboardAgent);
			health.addReceiver(new AID("maint",AID.ISLOCALNAME));
		}

		@Override
		protected void onTick() {
			try {
				health.setContentObject(Simulator.this);
				myAgent.send(health);
				System.out.println("reporting health to maitenance agent");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
