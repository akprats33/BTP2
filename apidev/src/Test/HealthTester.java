package Test;

import mas.machine.IMachine;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class HealthTester extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ACLMessage msg;
	IMachine mach;
	
	@Override
	protected void setup() {
		addBehaviour(new CyclicBehaviour() {
			
			int step =0;
			@Override
			public void action() {
				switch(step) {
				case 0:
					msg = receive();
					if(msg != null) {
						try {
							mach = (IMachine) msg.getContentObject();
							System.out.println("Status is "+ mach.getComponents().get(0).getLife());
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					}
					else {
						block();
					}
					break;
				case 1:
					break;
					
				}
			}
		});
	}
	
	

}
