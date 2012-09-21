package se;

public class MappingRecord {

	// Symbol which is represent for expression
	String symbol;

	// Expression which is symbolic
	String expression;

	// Return type of expression
	String returnType;

	public MappingRecord(String symbol, String expression, String returnType) {
		super();
		this.symbol = symbol;
		this.expression = expression;
		this.returnType = returnType;
	}

	public String getExpression() {
		return expression;
	}

	public String getReturnType() {
		return returnType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
