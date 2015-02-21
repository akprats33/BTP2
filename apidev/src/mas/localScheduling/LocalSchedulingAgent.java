package mas.localScheduling;

import jade.core.AID;
import mas.localScheduling.capability.LocalSchedulingBasicCapability;
import mas.util.AgentUtil;
import mas.util.ID;
import mas.util.SubscriptionForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class LocalSchedulingAgent extends AbstractlocalSchedulingAgent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger log;

	@Override
	protected void init() {
		super.init();
		log = LogManager.getLogger();

		// Add capability to agent 
		Capability bCap = new LocalSchedulingBasicCapability();
		addCapability(bCap);

	}
}
