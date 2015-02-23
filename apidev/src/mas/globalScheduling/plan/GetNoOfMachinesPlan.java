package mas.globalScheduling.plan;

import java.util.ArrayList;

import mas.util.ID;
import bdi4jade.belief.Belief;
import bdi4jade.belief.TransientBelief;
import bdi4jade.core.BDIAgent;
import bdi4jade.core.BeliefBase;
import bdi4jade.plan.PlanBody;
import bdi4jade.plan.PlanInstance;
import bdi4jade.plan.PlanInstance.EndState;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class GetNoOfMachinesPlan extends Behaviour implements PlanBody{

	ArrayList list_machines =  new ArrayList();
	boolean register = true;
	long time;
	long limit; //how many seconds u want to run this Behaviour;
	private BeliefBase bb;
	@Override
	public EndState getEndState() {
		return null;
	}

	@Override
	public void init(PlanInstance arg0) {
		limit=10*1000;//set limit
		time=System.currentTimeMillis();
		bb=arg0.getBeliefBase();	
	}

	@Override
	public void action() {
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchSender(myAgent.getDefaultDF()));
                
            if (msg != null)
            {
              try {
                 DFAgentDescription[] dfds =    
                      DFService.decodeNotification(msg.getContent());
                 
                 if (dfds.length > 0) {              	   
                	 Integer NoOfMachines= (Integer)((BDIAgent)myAgent).getRootCapability()
                			 .getBeliefBase().getBelief(ID.Blackboard.BeliefBaseConst.NoOfMachines).getValue();
                	 NoOfMachines++;
					Belief<Integer> new_belief=new TransientBelief<Integer>(ID.Blackboard.BeliefBaseConst.NoOfMachines, NoOfMachines);
                	 ((BDIAgent)myAgent).getRootCapability().getBeliefBase().addOrUpdateBelief(new_belief); //update belief base              	   
                 }
               }
               catch (Exception ex) {
            	   
               }
            }
            block();
		
	}

	@Override
	public boolean done() {
		if((System.currentTimeMillis()-time)<limit){
			return false;
		}
		else{
			return true;
		}
	}

}
