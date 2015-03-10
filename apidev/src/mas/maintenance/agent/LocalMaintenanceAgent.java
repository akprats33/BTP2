package mas.maintenance.agent;

import mas.localScheduling.capability.LocalSchedulingBasicCapability;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bdi4jade.core.Capability;

public class LocalMaintenanceAgent extends AbstractLocalMaintenanceAgent {

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
		Capability bCap = new MaitenanceBasicCapability();
		addCapability(bCap);

	}

}
