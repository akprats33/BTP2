package mas.globalScheduling.plan;

import jade.lang.acl.ACLMessage;

public class AskForWaitingTime extends RootAskForWaitingTime {

	@Override
	public ACLMessage getWorstWaitingTime(ACLMessage[] WaitingTime){
		//takes array of msg got froms all LSAs
//		super.log.info("chooseing waiting time");
		return super.getWorstWaitingTime(WaitingTime);
	}
}
