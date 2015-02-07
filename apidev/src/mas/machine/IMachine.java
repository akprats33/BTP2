package mas.machine;

import jade.core.Agent;

import java.util.ArrayList;

import mas.machine.component.IComponent;

public abstract class IMachine extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public abstract ArrayList<IComponent> getComponents();
	public abstract long getStartTime();
	public abstract MachineStatus getStatus();
}
