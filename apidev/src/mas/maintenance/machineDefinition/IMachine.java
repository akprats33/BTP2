package mas.maintenance.machineDefinition;

import java.util.ArrayList;

public abstract class IMachine {

	public abstract ArrayList<IComponent> getComponents();
	public abstract long getStartTime();
	public abstract MachineStatus getStatus();
}
