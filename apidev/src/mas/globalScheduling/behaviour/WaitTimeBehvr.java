package mas.globalScheduling.behaviour;

import java.io.IOException;

import bdi4jade.core.BDIAgent;
import mas.job.job;
import mas.util.ID;
import mas.util.MASconstants;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
//askdjakjgshdlaksjds
public class WaitTimeBehvr extends Behaviour {
	//..//
	private String[] s= new String[3] ;  
	private double[] w= new double[3] ;
	private double[] wmax= new double[3] ;  // Max wait time
	private int repliesCnt = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;
	private int MachineCount=0;
	private ACLMessage[] WaitingTime;
	private String CustomerAgent;
	private String msgReplyID;
	private AID bba;
	
	
	public WaitTimeBehvr(AID bb_AID, int NoOfmachines, String replyID){		
		this.MachineCount=(int)((BDIAgent)myAgent).getRootCapability().getBeliefBase().getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines).getValue();

		this.bba = bb_AID;
				
		
		this.msgReplyID=replyID;
			mt=MessageTemplate.and(
					MessageTemplate.MatchConversationId(MessageIds.WaitTime),
					MessageTemplate.MatchInReplyTo(replyID));	
		}

	public void action() {
		switch (step) {
		case 0:
			WaitingTime=new ACLMessage[MachineCount];
			step = 1;
			break;
		case 1:
		try{
	
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				WaitingTime[repliesCnt]=reply;
				repliesCnt++;
				
				if (repliesCnt == MachineCount) {				
					step = 2; 
				}
			}
			
			else {
				block();
			}
			}
			catch (Exception e3) {
				
			}
			break;
		case 2:
			try {
			ACLMessage max=WaitingTime[0];
			for(int i = 0; i<WaitingTime.length;i++){
				
				if(((job)(WaitingTime[i].getContentObject())).getWaitingTime() > ((job)(max.getContentObject())).getWaitingTime()){
					max=WaitingTime[i];
				}
			
			}
			
			job JobToSend=(job)(max.getContentObject());
			
			ACLMessage replyToCust = new ACLMessage(ACLMessage.PROPOSE);
			if(JobToSend.getWaitingTime()<=JobToSend.getWaitingTime()){			
				
			}
			else{
				ZoneDataUpdate NegotiationUpdate=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.NegotiationJob, JobToSend);
				NegotiationUpdate.send(this.bba, NegotiationUpdate, myAgent);
				/*replyToCust.setContentObject(JobToSend);												
				replyToCust.addReceiver(new AID(CustomerAgent, false));
				replyToCust.setConversationId(MessageIds.ReplyFromScheduler.toString());*/
			}
			

			} catch (UnreadableException e) {
			
				e.printStackTrace();
			}
			
			step = 3;
			break;

		}   
			     
	}
	
	public boolean done() {
		return (step == 3);
	}
}
