package mas.machine;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.leap.Serializable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mas.machine.behaviors.AcceptJobBehavior;
import mas.machine.behaviors.ComponentAgeMonitorBehavior;
import mas.machine.behaviors.Connect2BlackBoardBehvaior;
import mas.machine.behaviors.GetRootCauseDataBehavior;
import mas.machine.behaviors.LoadComponentBehavior;
import mas.machine.behaviors.LoadMachineParameterBehavior;
import mas.machine.behaviors.LoadSimulatorParamsBehavior;
import mas.machine.behaviors.Register2DF;
import mas.machine.behaviors.SimulatorStatusListener;
import mas.machine.component.Component;
import mas.machine.component.IComponent;
import mas.machine.parametrer.Parameter;
import mas.machine.parametrer.RootCause;
import mas.util.MessageIds;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang3.SerializationUtils;

public class Simulator extends IMachine implements Serializable{

	private static final long serialVersionUID = 1L;
	private static ArrayList<IComponent> myComponents;
	private long epochTime;
	private MachineStatus status;
	protected transient PropertyChangeSupport statusChangeSupport;
	public static AID blackboardAgent;
	public static String mySimulator = "Simulator";

	public String IpAddress,JadePort ,ComPort;
	public int portNumber;

	//percentage variation in processing time
	public static double percent = 0.10;		

	// parameters of loading time normal distribution ( in Milliseconds)
	public static double meanLoadingTime = 1000.0;					
	public static double sdLoadingTime = 1.0;

	// parameters of loading time normal distribution ( in Milliseconds)
	public static double meanUnloadingTime = 1000.0;				
	public static double sdUnloadingTime=1.0;

	// parameters of normal distribution causing shift in process mean
	public static double mean_shiftInMean = 0;				
	public static double sd_shiftInMean = 1;

	// parameters of normal distribution causing shift in process standard deviation
	public static double mean_shiftInSd = 0;				
	public static double sd_shiftInSd = 1;

	// parameters of process mean
	public static double mean_shift = 0;					
	public static double sd_shift = 1;

	//rate of process mean shifting (per hour)
	public static double rateShift = 0.01;				

	// parameters of normal distribution causing shift in process paramters
	public static double mean_shiftInMeanParam = 0;				
	public static double sd_shiftInMeanParam = 1;

	// parameters of normal distribution causing shift in process standard deviation
	public static double mean_shiftInSdParam = 0;				
	public static double sd_shiftSdparam = 1;

	// parameters of process parameters
	public static double mean_shiftParam = 0;					
	public static double sd_shiftparam = 1;

	public static double fractionDefective = 0.10;

	// for writing data to file
	public static BufferedWriter fout;					
	public static String filename;

	public static long systemStopwatch;

	public static int numOfRootCauseParams = 1;

	public transient static ArrayList<String> nameParams  = new ArrayList<String>();
	public transient static ArrayList<Double> valueParams = new ArrayList<Double>();
	public transient static int[] frequencyParams;
	public transient static ArrayList<Integer> inspectionParamsIndex = new ArrayList<Integer>();
	public transient static ArrayList<Integer> inspectionParamFrequency = new ArrayList<Integer>();

	public transient static double rootCause[];

	//public node[] rootcauseAffectedParams;
	public static double w_alpha;//=10.0;
	public static double w_beta;//=10.0;
	//	public static long abs_next_failure_time;
	//	public static failedComp failed_c;

	public transient static double[] rootcause_timeto_occur;

	public static  DefaultTableModel AttrDTM, DimDTM;
	public static JPanel jobAttrPanel=new JPanel(new MigLayout());
	public static JTable AttrTable, DimTable;
	public static JScrollPane AttrScrollPane, DimScrollPane;
	public static JFrame win_Dimtable,win_attrTable;

	public static ArrayList<Parameter> params ;
	public static ArrayList<ArrayList<RootCause>> rootcauses; 

	public void init() {
		statusChangeSupport = new PropertyChangeSupport(this);
		epochTime = System.currentTimeMillis();
		myComponents = new ArrayList<IComponent>();
		params = new ArrayList<Parameter>();
		rootcauses = new ArrayList<ArrayList<RootCause> >();
	}

	private transient SequentialBehaviour loadData;
	private transient Behaviour loadSimulatorParams;
	private transient Behaviour loadComponentData;
	private transient Behaviour loadMachineParams;
	private transient Behaviour loadRootCause;
	private transient Behaviour registerthis;
	private transient Behaviour connect2Blackboard;
	private transient Behaviour acceptIncomingJobs;
	private transient Behaviour componentAgeMonitor;

	private transient ParallelBehaviour functionality ;

	@Override
	protected void setup() {
		super.setup();
		init();

		loadData = new SequentialBehaviour(this);

		loadSimulatorParams = new LoadSimulatorParamsBehavior();
		loadComponentData = new LoadComponentBehavior(this);
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
		functionality.getDataStore().put(mySimulator, Simulator.this);

		acceptIncomingJobs = new AcceptJobBehavior();
		acceptIncomingJobs.setDataStore(functionality.getDataStore());

		componentAgeMonitor = new ComponentAgeMonitorBehavior();
		componentAgeMonitor.setDataStore(functionality.getDataStore());

		functionality.addSubBehaviour(acceptIncomingJobs);
		functionality.addSubBehaviour(componentAgeMonitor);

		addBehaviour(functionality);

		// Adding a listener to the change in value of the status of simulator 
		statusChangeSupport.addPropertyChangeListener(
				new SimulatorStatusListener(Simulator.this));
		
//		ACLMessage correctiveStartMsg = new ACLMessage(ACLMessage.REQUEST);
//		try {
//			correctiveStartMsg.setContentObject(Simulator.this);
//			correctiveStartMsg.setConversationId(MessageIds.Failed);
//			correctiveStartMsg.addReceiver(Simulator.blackboardAgent);
//			send(correctiveStartMsg);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

//		System.out.println("serialization is "+ SerializationUtils.serialize(Simulator.this));



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

	public static void addComponent(Component c) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ComPort == null) ? 0 : ComPort.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Simulator other = (Simulator) obj;
		if (ComPort == null) {
			if (other.ComPort != null)
				return false;
		} else if (!ComPort.equals(other.ComPort))
			return false;
		return true;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeObject(status);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
}
