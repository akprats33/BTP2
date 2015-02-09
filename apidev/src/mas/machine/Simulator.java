package mas.machine;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

import java.io.BufferedWriter;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import mas.machine.behaviors.GetRootCauseDataBehavior;
import mas.machine.behaviors.LoadComponentBehavior;
import mas.machine.behaviors.LoadMachineParameterBehavior;
import mas.machine.behaviors.LoadSimulatorParamsBehavior;
import mas.machine.behaviors.Register2DF;
import mas.machine.component.Component;
import mas.machine.component.IComponent;
import mas.machine.parametrer.Parameter;
import mas.machine.parametrer.RootCause;
import net.miginfocom.swing.MigLayout;

public class Simulator extends IMachine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<IComponent> myComponents;
	private long epochTime;
	public static MachineStatus status;
	public static AID blackboardAgent;
	
	public  String IpAddress,JadePort ,ComPort;
	public int portNumber;

	//percentage variation in processing time
	public static double percent = 10.0;		

	// parameters of loading time normal distribution ( in Minutes)
	public static double meanLoadingTime = 0.0;					
	public static double sdLoadingTime = 1.0;

	// parameters of loading time normal distribution ( in Minutes)
	public static double meanUnloadingTime = 0.0;				
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

	public static double fractionDefective = 0.1;

	// for writing data to file
	public static BufferedWriter fout;					
	public static String filename;

	public static long systemStopwatch;

	public static int numOfRootCauseParams = 1;
	
	public static ArrayList<String> nameParams  = new ArrayList<String>();
	public static ArrayList<Double> valueParams = new ArrayList<Double>();
	public static int[] frequencyParams;
	public static ArrayList<Integer> inspectionParamsIndex = new ArrayList<Integer>();
	public static ArrayList<Integer> inspectionParamFrequency = new ArrayList<Integer>();
	
	public static double rootCause[];
	
	//public node[] rootcauseAffectedParams;
	public static double w_alpha;//=10.0;
	public static double w_beta;//=10.0;
//	public static long abs_next_failure_time;
//	public static failedComp failed_c;

	public static double[] rootcause_timeto_occur;
	
	public static  DefaultTableModel AttrDTM, DimDTM;
	public static JPanel jobAttrPanel=new JPanel(new MigLayout());
	public static JTable AttrTable, DimTable;
	public static JScrollPane AttrScrollPane, DimScrollPane;
	public static JFrame win_Dimtable,win_attrTable;
	
	public static ArrayList<Parameter> params ;
	public static ArrayList<ArrayList<RootCause>> rootcauses; 

	public void init(){
		myComponents = new ArrayList<IComponent>();
		params = new ArrayList<Parameter>();
		rootcauses = new ArrayList<ArrayList<RootCause>>();
	}
	
	@Override
	protected void setup() {
		super.setup();
		init();
		
		SequentialBehaviour loadData = new SequentialBehaviour(this);
		loadData.addSubBehaviour(new LoadSimulatorParamsBehavior());
		loadData.addSubBehaviour(new LoadComponentBehavior());
		loadData.addSubBehaviour(new LoadMachineParameterBehavior());
		loadData.addSubBehaviour(new GetRootCauseDataBehavior());
		
		loadData.addSubBehaviour(new Register2DF());
		
		addBehaviour(loadData);
		
		
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
		return this.status;
	}
	
	public static void addComponent(Component c) {
		myComponents.add(c);
	}
	
	public long getNextFailureTime() {
		return epochTime;
	}
	
}
