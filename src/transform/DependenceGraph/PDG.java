package transform.DependenceGraph;

import java.util.ArrayList;

public class PDG { // Program Dependence Graph
	public ArrayList<Node> progDepGraph;

	String newline = "\r\n";
	String tab = "\t";

	public PDG() {
		progDepGraph = new ArrayList<Node>();
	}

	public void addNode(Node n) {
		progDepGraph.add(n);
	}

	public void changeLineIdAtDataDepPointToNode() {
		for (int i = 0; i < progDepGraph.size(); i++) {
			Node nodeInPDG = progDepGraph.get(i);

			// luu them index cua node trong PDG
			nodeInPDG.setIndex(i);

			ArrayList<DataDependence> listData = nodeInPDG.getDataDep();
			if (listData == null)
				continue;
			for (int j = 0; j < listData.size(); j++) {
				DataDependence data = listData.get(j);
				data.setNode(findNodeAtLine(data.getID()));
			}
		}
	}

	public Node findNodeAtLine(int line) {
		for (int i = 0; i < progDepGraph.size(); i++) {
			Node n = progDepGraph.get(i);
			if (n.getID() == line)
				return n;
		}
		return null;
	}

	public String toString() {
		String result = newline + newline + "Program Dependence Graph: "
				+ newline + newline;
		for (int i = 0; i < progDepGraph.size(); i++) {
			result += "Node " + i + ":" + newline;
			result += progDepGraph.get(i).toString() + newline;
		}
		return result;
	}
}
