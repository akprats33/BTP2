package Test;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import mas.util.BlackboardId;
import mas.util.ID;
import mas.util.MASconstants;
import mas.util.ParameterSubscription;
import mas.util.ZoneDataUpdate;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class SendMsgPlan extends OneShotBehaviour implements PlanBody{

	private AID bb;
	private Logger log;
	
	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		log=LogManager.getLogger();
		
	}

	@Override
	public void action() {
        
        DFAgentDescription dfd2 = new DFAgentDescription();
        ServiceDescription sd2  = new ServiceDescription();
        sd2.setType( BlackboardId.Agents.Blackboard );
        dfd2.addServices(sd2);
        
        DFAgentDescription[] result;
		try {
			
			result = DFService.search(myAgent, dfd2);
//			 System.out.println(result.length + " results" );
	            if (result.length>0)
	                {//System.out.println(" " + result[0].getName() );
	                bb=result[0].getName();
	                }
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		
        ACLMessage msg2=new ACLMessage(ACLMessage.CFP);
		msg2.setConversationId(mas.util.MessageIds.RegisterMe);
		String[] ZoneDataNames={"JobStatus"};
		try {
			msg2.setContentObject(ZoneDataNames);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		msg2.addReceiver(bb);
		myAgent.send(msg2);
		log.info(msg2.getSender().getLocalName()+" sent "+mas.util.MessageIds.RegisterMe);
		
		ParameterSubscription ps=new ParameterSubscription(ID.Customer.Service);
		String[] PSstring= new String[1];
//		PSstring[0] = MASconstants.parameters.jobStatus;
		ps.AddSubscriptionReq(myAgent.getAID(), PSstring );
				
		ACLMessage msg3=new ACLMessage(ACLMessage.CFP);
		msg3.setConversationId(mas.util.MessageIds.SubscribeParameter);
		try {
			msg3.setContentObject(ps);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg3.addReceiver(bb);				
		
		myAgent.send(msg3);
		
		log.info(msg3.getSender().getLocalName()+" sent "+mas.util.MessageIds.SubscribeParameter);
		
		ACLMessage msg4=new ACLMessage(ACLMessage.CFP);
		msg4.setConversationId(mas.util.MessageIds.UpdateParameter);
		try {
			ZoneDataUpdate zdu=new ZoneDataUpdate.Builder("JobStatus")
				.value((Object)1).Build();
//			ZoneDataUpdate zdu=new ZoneDataUpdate("JobStatus", (Object)1);
			msg4.setContentObject(zdu);
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg4.addReceiver(bb);
		myAgent.send(msg4);
		
		log.info(msg4.getSender().getLocalName()+" sent "+mas.util.MessageIds.UpdateParameter);
		
		msg4.setConversationId(mas.util.MessageIds.UpdateParameter);
		try {
			ZoneDataUpdate zdu=new ZoneDataUpdate.Builder("JobStatus")
			.value((Object)2).Build();	
//			ZoneDataUpdate zdu=new ZoneDataUpdate("JobStatus", (Object)2);
			msg4.setContentObject(zdu);
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg4.addReceiver(bb);
		myAgent.send(msg4);
		log.info(msg4.getSender().getLocalName()+" sent "+mas.util.MessageIds.UpdateParameter);
		
	}

}
