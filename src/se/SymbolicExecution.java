package se;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import system.Condition;
import system.Variable;
import transform.AST.AST;
import transform.AST.BinExprAST;
import transform.AST.CompilationException;
import transform.CodeGeneration.TestcaseGenerationVisitor;

import CodeAnalyzer.CodeAnalyzer;

public class SymbolicExecution {

	private static final String DEFAULT_TYPE = "Int";

	CodeAnalyzer codeAnalyzer = null;
	SE_MappingTable mappingTable = null;
	ArrayList<Condition> lstUnSolvalbecondition = null;

	String sourceCode = "";

	public SymbolicExecution() {
		mappingTable = new SE_MappingTable();
		lstUnSolvalbecondition = new ArrayList<Condition>();
	}

	private void addUnsolvableConditionToMappingTable() {
		for (Condition unsolvableCondition : lstUnSolvalbecondition) {
			String expression1 = getExpression1(unsolvableCondition);
			String expression2 = getExpression2(unsolvableCondition);

			String symbol1, symbol2;
			MappingRecord mappingRecord;

			if (!isExistsInMappingTable(expression1) && !isNumber(expression1)) {
				symbol1 = getNewSymbol();
				mappingRecord = new MappingRecord(
						unsolvableCondition.getCondition(), expression1,
						symbol1, DEFAULT_TYPE);
				mappingTable.addMappingRecord(mappingRecord);
			}

			if (!isExistsInMappingTable(expression2) && !isNumber(expression2)) {
				symbol2 = getNewSymbol();
				mappingRecord = new MappingRecord(
						unsolvableCondition.getCondition(), expression2,
						symbol2, DEFAULT_TYPE);
				mappingTable.addMappingRecord(mappingRecord);
			}
		}

	}

	private void createListOfUnsolvableCondition() {
		for (int i = 0; i < codeAnalyzer.getLstCondition().size(); i++) {
			Condition condition = codeAnalyzer.getLstCondition().get(i);
			if (!condition.hasTestcase()) {
				lstUnSolvalbecondition.add(condition);
			}
		}
	}

	public void createMappingTable() {
		this.mappingTable = new SE_MappingTable();
	}

	public void generateNextTestCaseAfterSE(int i) {

		boolean check = false;
		String conditionString = "";
		String result = "";
		for (int j = 0; j < codeAnalyzer.getLstCondition().get(i)
				.getTruePaths().size(); j++) {
			conditionString = codeAnalyzer.getLstCondition().get(i)
					.getTruePaths().get(j);
			result = codeAnalyzer.generateTestCaseAfterSE(conditionString);

			if (!result.equals("")) {
				codeAnalyzer.getLstCondition().get(i).setTrueTestcase(result);
				codeAnalyzer.getLstCondition().get(i).hasTrueTestCase = true;
				check = true;
				break;
			}
		}

		for (int k = 0; k < codeAnalyzer.getLstCondition().get(i)
				.getFalsePaths().size(); k++) {
			conditionString = (codeAnalyzer.getLstCondition().get(i)
					.getFalsePaths().get(k));
			result = codeAnalyzer.generateTestCaseAfterSE(conditionString);
			if (!result.equals("")) {

				codeAnalyzer.getLstCondition().get(i).setFalseTestcase(result);
				codeAnalyzer.getLstCondition().get(i).hasFalseTestCase = true;
				check = true;
				break;
			}
		}
		codeAnalyzer.getLstCondition().get(i).setHasTestcase(check);
	}

	public String generateSolvableAfterSE() {
		int count = 0;
		String output = "";

		// Generate testcase by Z3 for all condition in list
		for (int i = 0; i < codeAnalyzer.getLstCondition().size(); i++) {
			if (codeAnalyzer.getLstCondition().get(i).hasTestcase() == false)
				generateNextTestCaseAfterSE(i);
		}

		for (int i = 0; i < codeAnalyzer.getLstCondition().size(); i++) {
			Condition condition = codeAnalyzer.getLstCondition().get(i);
			// If Z3 can solve this condition
			if (condition.hasTestcase()) {
				count++;

				output += "\n\nCondition: " + condition.getCondition();
				output += "\n\tTrue path(s): ";
				for (String truePath : condition.getTruePaths())
					output += "\n\t\t" + truePath;
				output += "\n\tTrue condition: " + condition.getTrueTestCase();
				output += "\n\tFalse path(s): ";
				for (String falsePath : condition.getFalsePaths())
					output += "\n\t\t" + falsePath;
				output += "\n\tFalse condition: "
						+ condition.getFalseTestCase();

			}

		}
		output = "After symbolic execution, solve " + count + " condition(s)"
				+ output + "\n";
		return output;
	}

	public String getExpression1(Condition condition) {
		String expression1 = "";

		AST ast = condition.getAst();

		if (ast instanceof BinExprAST) {
			BinExprAST binExprAST = (BinExprAST) ast;
			try {
				expression1 = (String) binExprAST.exprAST1.visit(
						new TestcaseGenerationVisitor(codeAnalyzer.getLstParameter(),
								codeAnalyzer.getLstVariable(), codeAnalyzer
										.getLstCondition()), "c");
			} catch (CompilationException e1) {
				e1.printStackTrace();
			}
		}

		// Chua xu ly cho UnaryExprAST

		return expression1;
	}

	public String getExpression2(Condition condition) {
		String expression2 = "";

		AST ast = condition.getAst();

		if (ast instanceof BinExprAST) {
			BinExprAST binExprAST = (BinExprAST) ast;
			try {
				expression2 = (String) binExprAST.exprAST2.visit(
						new TestcaseGenerationVisitor(codeAnalyzer.getLstParameter(),
								codeAnalyzer.getLstVariable(), codeAnalyzer
										.getLstCondition()), "c");
			} catch (CompilationException e1) {
				e1.printStackTrace();
			}
		}

		// Chua xu ly cho UnaryExprAST

		return expression2;
	}

	public ArrayList<Variable> getListNewVariable() {
		ArrayList<Variable> lstVariable = new ArrayList<Variable>();

		MappingRecord mappingRecord = null;
		Variable variable = null;
		for (int i = 0; i < mappingTable.getMappingRecords().size(); i++) {
			mappingRecord = mappingTable.getMappingRecord(i);

			String variableName = mappingRecord.getSymbol();
			String variableType = mappingRecord.getReturnType();
			variable = new Variable(variableName, variableType);

			lstVariable.add(variable);
		}

		return lstVariable;
	}

	public ArrayList<MappingRecord> getMappingRecordList() {
		return mappingTable.getMappingRecords();
	}

	private String getNewSymbol() {
		int maxIndex = mappingTable.getMappingRecords().size() + 1;
		return "X" + maxIndex;
	}

	public String getOperation(Condition condition) {
		String operation = "";

		BinExprAST binExprAST = (BinExprAST) condition.getAst();
		operation = binExprAST.op.getText();

		return operation;
	}

	@SuppressWarnings("finally")
	private boolean isExistsInMappingTable(String expression) {
		boolean result = false;
		try {
			int mappingTableSize = mappingTable.getMappingRecords().size();
			if (mappingTableSize > 0) {
				for (int i = 0; i < mappingTableSize; i++) {
					if ((mappingTable.getMappingRecord(i).getExpression())
							.equals(expression)) {
						return true;
					}
				}
			}
		} catch (NullPointerException exception) {
			result = false;
		} finally {
			return result;
		}
	}

	@SuppressWarnings("finally")
	public boolean isNumber(String expression) {
		boolean result = false;
		try {
			if (expression.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
				result = true;
		} catch (NullPointerException exception) {
			result = false;
		} finally {
			return result;
		}
	}

	public void replaceExpressionBySymbol() {
		for (Condition condition : codeAnalyzer.getLstCondition()) {
			if (condition.hasTestcase() == false) {
				for (int i = 0; i < condition.getTruePaths().size(); i++) {
					String truePath = condition.getTruePaths().get(i);
					for (MappingRecord mappingRecord : mappingTable
							.getMappingRecords()) {
						if (truePath.contains(mappingRecord.getExpression())) {
							truePath = truePath.replace(
									mappingRecord.expression,
									mappingRecord.getSymbol());
							truePath += ("(assert (> "
									+ mappingRecord.getSymbol() + " "
									+ mappingRecord.getMinValue() + "))");
							truePath += ("(assert (< "
									+ mappingRecord.getSymbol() + " "
									+ mappingRecord.getMaxValue() + "))");
						}

					}
					condition.getTruePaths().set(i, truePath);
				}

				for (int i = 0; i < condition.getFalsePaths().size(); i++) {
					String falsePath = condition.getFalsePaths().get(i);
					for (MappingRecord mappingRecord : mappingTable
							.getMappingRecords()) {
						if (falsePath.contains(mappingRecord.getExpression())) {
							falsePath = falsePath.replace(
									mappingRecord.expression,
									mappingRecord.getSymbol());
							falsePath += ("(assert (> "
									+ mappingRecord.getSymbol() + " "
									+ mappingRecord.getMinValue() + "))");
							falsePath += ("(assert (< "
									+ mappingRecord.getSymbol() + " "
									+ mappingRecord.getMaxValue() + "))");
						}
					}
					condition.getFalsePaths().set(i, falsePath);
				}
			}
		}
	}

	public void setRangeOfSymbol() {
		int minValue = 0;
		int maxValue = 0;

		for (MappingRecord mappingRecord : this.mappingTable
				.getMappingRecords()) {
			String symbol = mappingRecord.getSymbol();

			String strMinValue = JOptionPane.showInputDialog(null,
					"What is a less than or equals value of " + symbol
							+ "? (Should be an integer number)",
					"Semi-automation for symbol");

			String strMaxValue = JOptionPane.showInputDialog(null,
					"What is a greater than or equals value of " + symbol
							+ "? (Should be an integer number)",
					"Semi-automation for symbol");

			minValue = 0;
			maxValue = 0;

			try {
				minValue = Integer.valueOf(strMinValue);
				maxValue = Integer.valueOf(strMaxValue);

				mappingRecord.setMinValue(minValue);
				mappingRecord.setMaxValue(maxValue);
			} catch (NumberFormatException e) {
				JOptionPane
						.showMessageDialog(null,
								"Min value/ max value does not match with integer type)");
			}
		}
	}

	public void symbolicExecution(CodeAnalyzer codeAnalyzer) {
		this.codeAnalyzer = codeAnalyzer;

		createMappingTable();
		createListOfUnsolvableCondition();
		addUnsolvableConditionToMappingTable();

		updateVariableList();
	}

	public void updateVariableList() {
		ArrayList<Variable> lstVariable = codeAnalyzer.getLstVariable();

		MappingRecord mappingRecord = null;
		Variable variable = null;
		for (int i = 0; i < mappingTable.getMappingRecords().size(); i++) {
			mappingRecord = mappingTable.getMappingRecord(i);

			String variableName = mappingRecord.getSymbol();
			String variableType = mappingRecord.getReturnType();
			variable = new Variable(variableName, variableType);

			lstVariable.add(variable);
		}
		codeAnalyzer.setLstVariable(lstVariable);
	}
}
