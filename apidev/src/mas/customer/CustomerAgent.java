package mas.customer;

import jade.core.AID;
import mas.util.AgentUtil;
import mas.util.ID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class CustomerAgent extends AbstractCustomerAgent {

	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	protected void init() {
		super.init();
		Capability bCap = new basicCapability();
		addCapability(bCap);
		log = LogManager.getLogger();

		AID bba = AgentUtil.findBlackboardAgent(this);
		bCap.getBeliefBase().updateBelief(
				ID.Customer.BeliefBaseConst.blackboardAgent, bba);
		
	}
	
	
}
