package mas.util;

/**
 * 
 * @author Anand Prajapati, Nikhil chilwant
 * Class contains Id's for all message communication between agents
 *
 */
public class MessageIds {

	public static final String LSjobFromGS = "LSjobFromGS";
	public static final String LSaskForGS = "LSaskForGS";
	public static final String LSsendBidToGS = "LSsendBidToGS";
	public static final String LSsendWaitingTimeGS = "LSsendWaitingTimeGS";
	public static final String LSsendJobToMachine = "LSsendJobToMachine";
	public static final String LSgetCompletedJobFromMachine = "LSgetCompletedJobFromMachine";
	
	public static final String Success="sucess";
	public static final String Failed="failed";
	public static final String RegisterMe = "Register";

	public static final String UpdateParameter = "UpdateParam";

	public static final String SubscribeParameter = "subscribe-parameter";
	public static final String AskWaitTime = "GiveWaitingTime";

	public static String TotalJobsSent = "jobs-sent-total";

	public static String JobsRejected = "jobs-rejected";

	public static String JobFromCustomer = "Customer-Job";

	public static String JobFromScheduler ="Job-To-Machine";

	public static String ReplyFromScheduler ="Reply-to-Job-From-Scheduler";

	public static String BidForJob ="Bid-For-Job";

	public static String MachineFailed = "Failure-of-Machine";

	public static String WaitTime = "MaxWaitingTimeForJob";

	public static String Negotiate = "Negotiation";

	public static String OrderConfirmation = "OrderConfirmation";

	public static String WorkOrder = "WorkOrder";

	public static String SendJob="SendJob";

	public static String waitingTime="waiting-time";

	public static String GiveJob="GiveJob";

	public static String correctiveData = "Corrective-Repair-Data";
	public static String maintMachineFailureInfo = "Fail-Start";
	public static String machinePrevMaintenanceData = "machineMaintenanceData";
	public static String machinePrevMaintenanceStart = "machineMaintenanceStart";
	public static String machineInspectionStart = "machineInspectionStart";
	public static String machineInspectionData = "machineInspectionStart";
	
	public static String failEnd = "Fail-End";
	public static String MaintMachineHealth = "Machine-State";
	public static String MaintMachineRepaired = "Machine-failure-End";
	public static String maintenanceJob = "Maintenance-Job";
	public static String maintenanceJobStartData = "maintenanceJobStartData";
	public static String inspect_job_data = "IJTime";
}
