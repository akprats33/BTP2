package mas.machine;

import java.util.ArrayList;
import mas.machine.component.IComponent;

public interface IMachine {

	public ArrayList<IComponent> getComponents();
	public long getStartTime();
	public MachineStatus getStatus();
}
