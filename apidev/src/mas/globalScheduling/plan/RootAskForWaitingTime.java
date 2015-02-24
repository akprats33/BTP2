package mas.globalScheduling.plan;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mas.globalScheduling.behaviour.WaitTimeBehvr;
import mas.job.job;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.MessageIds;
import mas.util.ZoneDataUpdate;
import bdi4jade.core.BDIAgent;
import bdi4jade.message.MessageGoal;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;

public class RootAskForWaitingTime extends Behaviour implements PlanBody {

	private job j;
	private AID blackboard;
	private int NoOfMachines;
	private String msgReplyID;
	private MessageTemplate mt;
	private int step = 0;
	private int MachineCount;
	protected Logger log;
	private ACLMessage[] WaitingTime;
	private int repliesCnt = 0; // The counter of replies from seller agents

	@Override
	public EndState getEndState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PlanInstance PI) {
		log=LogManager.getLogger();
		
		try {
			j=(job)((MessageGoal)PI.getGoal()).getMessage().getContentObject();
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		blackboard=(AID)PI.getBeliefBase().getBelief(ID.Blackboard.LocalName).getValue();

		
		msgReplyID=Integer.toString(j.getJobNo());
		mt=MessageTemplate.and(
				MessageTemplate.MatchConversationId(MessageIds.msgWaitingTime),
				MessageTemplate.MatchInReplyTo(msgReplyID));

	}

	@Override
	public void action() {
		switch (step) {
		case 0:

			ACLMessage msg=new ACLMessage(ACLMessage.CFP);
			msg.setConversationId(MessageIds.UpdateParameter);
			ZoneDataUpdate update=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GetWaitingTime, j);
			AgentUtil.sendZoneDataUpdate(blackboard, update, myAgent);
			
			
			this.MachineCount=(int)((BDIAgent)myAgent).getRootCapability().getBeliefBase().getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines).getValue();
			log.info(MachineCount);
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
				
				ACLMessage max=ChooseWaitingTimeToSend(WaitingTime);
				job JobToSend=(job)(max.getContentObject());
				ACLMessage replyToCust = new ACLMessage(ACLMessage.PROPOSE);
				if(JobToSend.getWaitingTime()<=JobToSend.getWaitingTime()){			

				}
				else{
					ZoneDataUpdate NegotiationUpdate=new ZoneDataUpdate(ID.GlobalScheduler.ZoneData.GSAjobsUnderNegaotiation, JobToSend);
					AgentUtil.sendZoneDataUpdate(blackboard, NegotiationUpdate, myAgent);
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

	public ACLMessage ChooseWaitingTimeToSend(ACLMessage[] WaitingTime ) {
		ACLMessage MaxwaitingTimeMsg=WaitingTime[0]; 
		for(int i = 0; i<WaitingTime.length;i++){

			try {
				if(((job)(WaitingTime[i].getContentObject())).
						getWaitingTime() > ((job)(MaxwaitingTimeMsg.
								getContentObject())).getWaitingTime()){
					MaxwaitingTimeMsg=WaitingTime[i];
				}
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return MaxwaitingTimeMsg; //return maximum of all waiting times recieved from LSAs
	}

	@Override
	public boolean done() {

		return step==3;
	}
}
