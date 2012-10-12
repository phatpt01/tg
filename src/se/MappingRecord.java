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
	int minValue;
	
	// Max value
	int maxValue;

	public MappingRecord(String condition, String expression, String symbol,
			String returnType) {
		super();
		this.condition = condition;
		this.expression = expression;
		this.symbol = symbol;
		this.returnType = returnType;

		this.minValue = 0;
		this.maxValue = 0;
	}

	public String getCondition() {
		return condition;
	}

	public String getExpression() {
		return expression;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getMinValue() {
		return minValue;
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

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}
