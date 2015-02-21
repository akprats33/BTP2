package mas.util;

import jade.util.leap.Serializable;

public class ID implements Serializable{

	private static final long serialVersionUID = 1L;

	//It is recommonded to keep each String in ID.java to be kept unique in order to avoid confusion and debugging purpose
	public class MAS {
		public static final String main_container_ipaddress = "127.0.0.1";
	}

	public class Blackboard {
		public static final String Service = "blackboard";
		public static final String LocalName = "blackboard";

		public class ZoneData {

		}
	}

	public class Customer {
		public static final String Service = "customer";
		public static final String LocalName = "customer";

		public class BeliefBase {
			public static final String JobList = "customerBeliefBase_JobList";
			public static final String JOB_GENERATOR = "customerBeliefBase_JOB-GENERATOR";
			public static final String blackAgent = "customerBeliefBase_blackboard-agent";
			public static final String CURRENT_JOB = "customerBeliefBase_Current-job";
		}
		/*		public class Parameters{
			public static final String JobList="JobList";
			public static final String NegotiationBidList="NegotiationBids";
			public static final String IsJobAcceptedList="IsJobAcceptedList";
		}*/

		public class ZoneData{
			public static final String JobList = "customer_JobList";
			public static final String Negotiation = "customer_jobsUnderNegotiation";
			public static final String acceptedJobs = "customer_jobsAccepted";
		}
	}

	public class LocalScheduler{
		public static final String Service="LSA_machine-simulator-schedule";
		public static final String LocalName="Local_Scheduling_Agent";
		/*public class Parameters{
			public static final String WaitingTime="WaititngTimeData";
			public static final String Bid="BidData";
			public static final String JobForMachine="JobForMachineData";
			public static final String WorkOrder="WorkOrderData";
		}*/

		public class BeliefBase {
			public static final String blackAgent = "LSABeliefBase_blackboard-agent";
			public static final String machine = "LSABeliefBase_machine";
			public static final String jobQueue = "LSABeliefBase_job-list";
			public static final String maintAgent = "LSABeliefBase_maintenanceAgent";
			public static final String globalSchAgent = "LSABeliefBase_gsAgent";
			public static final String dataTracker = "LSABeliefBase_data-tracker";
		}

		public class ZoneData{
			public static final String WaitingTime = "LSA_WaitingTime";
			public static final String bidForJob = "LSA_BidForJob";
			public static final String jobQueue = "LSA_JobQueueFormyMachine";
		}
	}


	public class GlobalScheduler{
		public static final String Service ="global-scheduling-agent";
		public static final String LocalName ="GlobalSchedulingAgent";
		
		/*public class Parameters{

		}
*/		
		public class ZoneData{
			//contains confirmed jobs
			public static final String WorkOrder="GSA_WorkOrderData"; 
			//jobs under negotiation
			public static final String NegotiationJob = "GSA_jobsUnderNegotiation"; 
			public static final String ConfirmedOrder = "GSA_ConfirmedOrder";
			public static final String jobForMachine = "GSA_job-for-machine";
			public static final String askforBid = "GSA_ask-for-bid";
			public static final String waitingTime = "GSA_waiting-time"; //queue of jobs with calculated expected waiting time
			public static final String GetWaitingTime = "GSA_LocalSchedulingwaiting-time";//contains queue of jobs with  
		}
	}

	public class Maintenance {
		public static final String Service="machine-simulator-maint";
		public static final String LocalName ="machine-simulator-maint";

		/*		public class Parameters {

		}*/

		public class ZoneData {
			public static final String PMdata = "Maintenance_preventiveMaintenanceData";
			public static final String correctiveMaintdata = "Maintenance_correctiveMaintenancedata";
		}

		public class BeliefBase {
			public static final String blackAgent = "Maintenance_blackboard-agent";
			public static final String machine = "Maintenance_machine";
			public static final String globalSchAgent = "Maintenance_gsAgent";
			public static final String dataTracker = "Maintenance_data-tracker";
			public static final String maintenanceJob = "Maintenance_machine-maintenance-tracker";
		}
	}

	public class Machine{
		public static final String Service="machine-service";
		public static final String LocalName ="machine-simulator";

		/*public class Parameters{

		}*/

		public class ZoneData{
			public static final String myHealth ="machine_health";
			public static final String finishedJob ="machine_finishedJob";
		}
	}
}
