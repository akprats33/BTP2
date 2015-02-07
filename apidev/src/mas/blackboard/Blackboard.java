package mas.blackboard;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import mas.blackboard.capability.CommunicationCenter;
import mas.util.BlackboardId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.BDIAgent;

public class Blackboard extends BDIAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static BDIAgent BBagent;
	private Logger log;

	public void init() {

		log = LogManager.getLogger(this.getClass());

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName( getAID() ); 
		ServiceDescription sd  = new ServiceDescription();
		sd.setType(BlackboardId.Agents.Blackboard);

		sd.setName( getLocalName() );
		dfd.addServices(sd);

		try {  

			DFService.register(this, dfd );
			log.info("BB Registered with DF");
		}
		catch (FIPAException fe) { fe.printStackTrace(); }
		BBagent = this;
		addCapability(new CommunicationCenter(BBagent));
	}


}

