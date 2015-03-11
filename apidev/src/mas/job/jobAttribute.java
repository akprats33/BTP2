package mas.job;
import java.io.Serializable;

public class jobAttribute implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String Name;
	private boolean Conforming;
	
	public jobAttribute(String name) {
		this.Name = name;
	}

	public void setTitle(String s){
		this.Name = s;
	}
	public void setConforming(Boolean b){
		this.Conforming = b;
	}
}