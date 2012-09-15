package transform.DependenceGraph;

import java.util.ArrayList;

public class ExecutionHistory {
	ArrayList<ExecNode> EH;

	String newline = "\r\n";

	public ExecutionHistory() {
		this.EH = new ArrayList<ExecNode>();
	}

	// Append one execNode to EH
	public void add(Node node, char state) {
		ExecNode execNode = new ExecNode(node, state);
		this.EH.add(execNode);
	}

	public void addExecNode(ExecNode n) {
		this.EH.add(n);
	}

	public void changeLineIdAtExecNodePointToNode(PDG graph) {
		for (int i = 0; i < this.EH.size(); i++) {
			this.EH.get(i).node = graph.findNodeAtLine(this.EH.get(i).lineID);
		}
	}

	public boolean equals(ExecutionHistory eh) {
		if (this.size() != eh.size()) {
			return false;
		} else {
			for (int i = 0; i < this.size(); i++) {
				ExecNode node1 = this.get(i);
				ExecNode node2 = eh.get(i);
				if (!node1.equals(node2)) {
					return false;
				}
			}
			return true;
		}
	}

	// Return ExecNode at index
	public ExecNode get(int index) {
		return this.EH.get(index);
	}

	public ArrayList<Integer> getSlide() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < this.EH.size(); i++) {
			result.add(this.EH.get(i).lineID);
		}
		return result;
	}

	/*
	 * Return the length of EH
	 */
	public int size() {
		this.EH.trimToSize();
		int num = this.EH.size();
		return num;
	}

	@Override
	public String toString() {
		String result = this.newline + this.newline + "Execution History: "
				+ this.newline + this.newline;
		for (int i = 0; i < this.EH.size(); i++) {
			result += "ExecNode at lineID =  " + this.EH.get(i).lineID + ":"
					+ this.newline;
			result += this.EH.get(i).toString() + this.newline;
			result += this.newline;
		}
		return result;
	}
}
