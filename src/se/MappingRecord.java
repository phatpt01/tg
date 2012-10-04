package se;

public class MappingRecord {

	// Condition
	String condition;
	
	// Expression which is symbolic
	String expression;

	// Symbol which is represent for expression
	String symbol;

	// Return type of expression
	String returnType;
	
	// Min value
	
	// Max value
	
	public MappingRecord(String condition, String expression, String symbol, String returnType) {
		super();
		this.condition = condition;
		this.expression = expression;
		this.symbol = symbol;
		this.returnType = returnType;
	}

	public String getCondition() {
		return condition;
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

	public void setCondition(String condition) {
		this.condition = condition;
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
