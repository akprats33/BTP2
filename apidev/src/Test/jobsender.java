package Test;

import java.io.IOException;
import org.apache.commons.math3.distribution.ExponentialDistribution;

import mas.job.job;
import mas.util.MessageIds;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class jobsender extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JobGenerator jGen;
	private ExponentialDistribution exp;
	
	AID seller;
	int job_no = 1;

	public void setup()
	{
		this.seller = new AID();
		this.jGen = new JobGenerator();
		this.exp = new ExponentialDistribution(50);
		jGen.readFile();

//		System.out.println("Job sender "+getLocalName() +" started");
		// generate job every 2 seconds

		addBehaviour(new TickerBehaviour(this,1000) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("Machine");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent,template);
					if ((result != null) && (result.length > 0)) {
						seller  = result[0].getName(); 	
						//				    	  System.out.println("found" + seller);
					}
				} catch (Exception fe) {
					fe.printStackTrace();
					//doDelete();
				}
				myAgent.addBehaviour(new dispatchJob());
				reset(getInterArrivalTime());
//				System.out.println("job "+ getInterArrivalTime());
			}
		});
	}
	
	public long getInterArrivalTime(){
		return (long) Math.max(1, exp.sample()*1000);
	}

	public class dispatchJob extends OneShotBehaviour {
		private static final long serialVersionUID = 1L;
		
		public void action() {
				try{
					ACLMessage cfp = new ACLMessage(ACLMessage.INFORM);
					cfp.addReceiver(seller);
					job newJob = (job) jGen.getNextJob();
					cfp.setContentObject(newJob);
					cfp.setConversationId(MessageIds.msgnewWorkOrderFromCustomer);
					cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					send(cfp);
					
//					System.out.println("message" + seller);
				}
				catch (IOException e ) {
					e.printStackTrace();
				}	
		}
	}
}
