package se;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import system.Condition;
import system.Variable;
import transform.AST.AST;
import transform.AST.BinExprAST;
import transform.AST.CompilationException;
import transform.CodeGeneration.Temp1Visitor;

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

	// public void addMinMaxAssert() {
	// String smt2SEDetails = "";
	// String z3FileNameSE = "";
	//
	// for (int i = 0; i < SymbolicExecutionFile
	// .countNumberOfFiles(SymbolicExecutionFile
	// .getAbsolutePathOfSmt2()) - 1; i++) {
	//
	// z3FileNameSE = SymbolicExecutionFile.getAbsolutePathOfSmt2()
	// + "Z3FormulaSE" + i + ".smt2";
	//
	// smt2SEDetails = SymbolicExecutionFile.readFile(z3FileNameSE);
	//
	// smt2SEDetails = smt2SEDetails.substring(0,
	// smt2SEDetails.indexOf("(check-sat)"));
	//
	// String newAssert;
	// String regexTrue;
	// String regexFalse;
	//
	// for (MappingRecord mappingRecord : mappingTable.getMappingRecords()) {
	//
	// regexTrue = "[a-zA-Z0-9\\(\\)\\s\\-]*\\)\\(assert \\(\\D "
	// + mappingRecord.getSymbol() + " \\w\\)\\).*";
	//
	// regexFalse = "[a-zA-Z0-9\\(\\)\\s\\-]*\\)\\(assert \\(not \\(\\D "
	// + mappingRecord.getSymbol() + " \\w\\)\\)\\).*";
	//
	// Pattern pattern = Pattern.compile(regexFalse);
	// Matcher matcher = pattern.matcher(smt2SEDetails);
	// if (matcher.find()) {
	// newAssert = "(assert (> " + mappingRecord.getSymbol() + " "
	// + mappingRecord.getMinValue() + "))";
	// smt2SEDetails += newAssert;
	//
	// newAssert = "(assert (< " + mappingRecord.getSymbol() + " "
	// + mappingRecord.getMaxValue() + "))";
	// smt2SEDetails += newAssert;
	//
	// break; // Khong co file nao assert va assert not
	// }
	//
	// pattern = Pattern.compile(regexTrue);
	// matcher = pattern.matcher(smt2SEDetails);
	// if (matcher.find()) {
	// newAssert = "(assert (> " + mappingRecord.getSymbol() + " "
	// + mappingRecord.getMinValue() + "))";
	// smt2SEDetails += newAssert;
	//
	// newAssert = "(assert (< " + mappingRecord.getSymbol() + " "
	// + mappingRecord.getMaxValue() + "))";
	// smt2SEDetails += newAssert;
	// }
	//
	// }
	// smt2SEDetails += "(check-sat) \n (model)";
	//
	// // Update file smt2SE
	// SymbolicExecutionFile.writeFile(z3FileNameSE, smt2SEDetails);
	// }
	//
	// }

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

	// public void generateNextTestCaseAfterSE(int i) {
	// boolean check = false;
	// boolean matchFalse = false;
	//
	// int numberOfFiles = SymbolicExecutionFile
	// .countNumberOfFiles(SymbolicExecutionFile
	// .getAbsolutePathOfSmt2());
	//
	// // Xoa nhung path chua duoc symbolic
	// codeAnalyzer.getLstCondition().get(i).clearFalsePaths();
	// codeAnalyzer.getLstCondition().get(i).clearTruePaths();
	//
	// if (numberOfFiles > 0) {
	// // Each condition will read only 2 SE files
	// for (int j = fileNumber; j < fileNumber + 2; j++) {
	// String z3FormulaSEFile = "Z3FormulaSE" + j + ".smt2";
	// String smt2SEDetails = SymbolicExecutionFile
	// .readFile(SymbolicExecutionFile.getAbsolutePathOfSmt2()
	// + z3FormulaSEFile);
	//
	// smt2SEDetails = smt2SEDetails.substring(0,
	// smt2SEDetails.indexOf("(check-sat)"));
	//
	// for (MappingRecord mappingRecord : mappingTable
	// .getMappingRecords()) {
	//
	// matchFalse = false;
	// String result = "";
	//
	// String regexFalse = "[a-zA-Z0-9\\(\\)\\s\\-]*\\)\\(assert \\(not \\(\\D "
	// + mappingRecord.getSymbol() + " \\w\\)\\)\\).*";
	// Pattern pattern = Pattern.compile(regexFalse);
	// Matcher matcher = pattern.matcher(smt2SEDetails);
	//
	// if (matcher.find()) {
	// matchFalse = true;
	// // codeAnalyzer.getLstCondition().get(i).clearFalsePaths();
	// String falsePath = smt2SEDetails
	// .substring(smt2SEDetails.indexOf("(assert ("));
	//
	// codeAnalyzer.getLstCondition().get(i)
	// .addFalsePaths(falsePath);
	//
	// for (int k = 0; k < codeAnalyzer.getLstCondition()
	// .get(i).getFalsePaths().size(); k++) {
	// result = codeAnalyzer
	// .generateTestCaseAfterSE(codeAnalyzer
	// .getLstCondition().get(i)
	// .getFalsePaths().get(k));
	// if (!result.equals("")) {
	// codeAnalyzer.getLstCondition().get(i)
	// .setFalseTestcase(result);
	// codeAnalyzer.getLstCondition().get(i).hasFalseTestCase = true;
	// check = true;
	// }
	// }
	// }
	//
	// String regexTrue = "[a-zA-Z0-9\\(\\)\\s\\-]*\\)\\(assert \\(\\D "
	// + mappingRecord.getSymbol() + " \\w\\)\\).*";
	// pattern = Pattern.compile(regexTrue);
	// matcher = pattern.matcher(smt2SEDetails);
	//
	// if (matcher.find() && !matchFalse) {
	// // codeAnalyzer.getLstCondition().get(i).clearTruePaths();
	// String truePath = smt2SEDetails.substring(smt2SEDetails
	// .indexOf("(assert ("));
	// codeAnalyzer.getLstCondition().get(i)
	// .addTruePaths(truePath);
	//
	// for (int k = 0; k < codeAnalyzer.getLstCondition()
	// .get(i).getTruePaths().size(); k++) {
	// result = codeAnalyzer
	// .generateTestCaseAfterSE(codeAnalyzer
	// .getLstCondition().get(i)
	// .getTruePaths().get(k));
	// if (!result.equals("")) {
	// codeAnalyzer.getLstCondition().get(i)
	// .setTrueTestcase(result);
	// codeAnalyzer.getLstCondition().get(i).hasTrueTestCase = true;
	// check = true;
	// }
	// }
	// }
	//
	// codeAnalyzer.getLstCondition().get(i).setHasTestcase(check);
	// }
	// }
	// }
	// fileNumber += 2;
	// }

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
				output += "\n\tFalse condition: " + condition.getFalseTestCase();

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
						new Temp1Visitor(codeAnalyzer.getLstParameter(),
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
						new Temp1Visitor(codeAnalyzer.getLstParameter(),
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

	private boolean isExistsInMappingTable(String expression) {
		int mappingTableSize = mappingTable.getMappingRecords().size();

		if (mappingTableSize > 0) {
			for (int i = 0; i < mappingTableSize; i++) {
				if ((mappingTable.getMappingRecord(i).getExpression())
						.equals(expression)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isNumber(String expression) {
		if (expression.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+"))
			return true;
		else
			return false;
	}

	// public void replaceExpressionBySymbol() {
	// String smt2SEDetails = "";
	// String z3FileNameSE = "";
	//
	// for (int i = 0; i < SymbolicExecutionFile
	// .countNumberOfFiles(SymbolicExecutionFile
	// .getAbsolutePathOfSmt2()) - 1; i++) {
	//
	// z3FileNameSE = SymbolicExecutionFile.getAbsolutePathOfSmt2()
	// + "Z3FormulaSE" + i + ".smt2";
	//
	// smt2SEDetails = SymbolicExecutionFile.readFile(z3FileNameSE);
	//
	// // Add new variable
	// String newSmt2SEDetails = "";
	// for (Variable variable : getListNewVariable()) {
	// newSmt2SEDetails += ("(declare-const " + variable.getName()
	// + " " + variable.getType() + ") \n");
	// }
	//
	// // Update smt2SEDetails from beginning
	// newSmt2SEDetails += smt2SEDetails;
	//
	// // Replace unsolvable condition by get item from mapping table
	// for (MappingRecord mappingRecord : mappingTable.getMappingRecords()) {
	// newSmt2SEDetails = newSmt2SEDetails.replace(
	// mappingRecord.getExpression(),
	// mappingRecord.getSymbol());
	// }
	//
	// // Update file smt2SE
	// SymbolicExecutionFile.writeFile(z3FileNameSE, newSmt2SEDetails);
	// }
	// }

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

		updateListVariable();
	}

	public void updateListVariable() {
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
