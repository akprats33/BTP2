package mas.globalScheduling.plan;

import jade.lang.acl.ACLMessage;

public class AskForWaitingTime extends RootAskForWaitingTime {

	@Override
	public ACLMessage ChooseWaitingTimeToSend(ACLMessage[] WaitingTime){
		//takes array of msg got froms all LSAs
		return super.ChooseWaitingTimeToSend(WaitingTime);
	}
}
