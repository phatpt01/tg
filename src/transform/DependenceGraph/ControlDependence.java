package transform.DependenceGraph;

public class ControlDependence {

	Node node = null;
	boolean branch;

	public ControlDependence() {
		this.node = null;
	}

	public ControlDependence(ControlDependence controlDependence) {
		this.node = controlDependence.node;
		this.branch = controlDependence.branch;
	}

	public ControlDependence(Node n, boolean b) {
		this.node = n;
		this.branch = b;
	}

	public boolean getBranch() {
		return this.branch;
	}

	public Node getNode() {
		return this.node;
	}

	public boolean isEmpty() {
		return this.node == null;
	}

	public void set(ControlDependence that) {
		this.node = that.node;
		this.branch = that.branch;
	}

	public void set(Node n, boolean b) {
		this.node = n;
		this.branch = b;
	}

	public void setBranch(boolean b) {
		this.branch = b;
	}

	public void setNode(Node n) {
		this.node = n;
	}

	@Override
	public String toString() {
		String result = "";
		result = "{Node " + this.node.getIndex() + " (lineID="
				+ this.node.getID() + "),     " + this.branch + "}";
		return result;
	}
}
