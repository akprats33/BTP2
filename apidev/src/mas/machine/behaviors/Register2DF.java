package mas.machine.behaviors;

import mas.util.ID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Register2DF extends OneShotBehaviour{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void action() {
		DFAgentDescription mc = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(ID.Machine.Service);
		sd.setName(myAgent.getLocalName());
		mc.addServices(sd);

		try {
			DFService.register(myAgent, mc);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
}
