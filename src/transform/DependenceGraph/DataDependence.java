package transform.DependenceGraph;

public class DataDependence {
	int lineID;
	String var_depend = null;
	Node node = null;

	String newline = "\r\n";
	String tab = "\t";

	public DataDependence(int id, String var) {
		this.lineID = id;
		this.var_depend = var;
	}

	public DataDependence(Node nodeY, String varName) {
		this.node = nodeY;
		this.var_depend = varName;
	}

	public int getID() {
		return this.lineID;
	}

	public Node getNode() {
		return this.node;
	}

	public String getVarName() {
		return this.var_depend;
	}

	public void setNode(Node n) {
		this.node = n;
	}

	public void setVarName(String var) {
		this.var_depend = var;
	}

	@Override
	public String toString() {
		String result = "";
		if (this.node != null) {
			result = "{Node " + this.node.getIndex() + " (lineID="
					+ this.node.getID() + "),     " + this.var_depend + "}";
		} else {
			result = "{Node with lineID = " + this.lineID + ", "
					+ this.var_depend + "}";
		}
		return result;
	}
}
