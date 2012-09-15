package system;

public class VariableReIndex {
	private String varName;
	private int index;

	public VariableReIndex(String text) {
		this.varName = text;
		this.index = 0;
	}

	public int getIndex() {
		return this.index;
	}

	public String getVarName() {
		return this.varName;
	}

	public void increaseIndex() {
		this.index++;
	}
}
