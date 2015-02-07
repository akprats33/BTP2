package mas.customer;

import mas.util.AgentUtil;
import mas.util.ID;
import bdi4jade.core.BDIAgent;

public abstract class AbstractCustomerAgent extends BDIAgent {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void init() {
		super.init();
		AgentUtil.register2DF(this, ID.Customer.Service);
	}
}
