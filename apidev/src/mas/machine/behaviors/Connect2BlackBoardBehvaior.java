package mas.machine.behaviors;

import mas.machine.Simulator;
import mas.util.ID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Connect2BlackBoardBehvaior extends OneShotBehaviour{

	private static final long serialVersionUID = 1L;

	@Override
	public void action() {
		
		// first find blackboard and store it's AID
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd  = new ServiceDescription();
		sd.setType(ID.Blackboard.Service);
		dfd.addServices(sd);

		DFAgentDescription[] result;
		try {
			result = DFService.search(myAgent, dfd);
			while(result.length == 0) {
				result = DFService.search(myAgent, dfd);
				Thread.sleep(1000);
			} 
			if (result.length > 0) {
				Simulator.blackboardAgent = result[0].getName();
			}
		}catch (FIPAException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Now create zones on blackboard where data of machine will be kept for
		// other agents to locate and receive 
		
	}
}
