package se;

import java.io.File;
import java.util.ArrayList;

import system.Condition;
import system.Variable;
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

	private void addUnsolvableConditionToMappingTable() {
		for (Condition unsolvableCondition : lstUnSolvalbecondition) {
			String expression1 = getExpression1(unsolvableCondition);
			String expression2 = getExpression2(unsolvableCondition);

			String symbol1, symbol2;
			MappingRecord mappingRecord;

			/*
			 * Check if expression1 is exists in mappingTable or not If exists,
			 * get existsing symbol, doesnot create new symbol If not exists,
			 * create new symbol
			 */
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
		if (this.mappingTable == null) {
			this.mappingTable = new SE_MappingTable();
		}
	}

//	public void createSESmt2() {
//		// Copy Z3Formular.smt2 to Z3FormulaSE.smt2
//		String z3output = "Z3OUTPUT";
//		File z3outFolder = new File(z3output);
//
//		if (!z3outFolder.exists()) {
//			z3outFolder.mkdirs();
//		}
//
//		String z3FilePath = z3outFolder.getAbsolutePath() + File.separatorChar
//				+ "Z3Formula.smt2";
//		String z3FilePathSE = z3outFolder.getAbsolutePath()
//				+ File.separatorChar + "Z3FormulaSE.smt2";
//
//		SymbolicExecutionFile.createBlankFile(z3FilePathSE);
//
//		// Write new variable to SE file
//		SymbolicExecutionFile.appendAtBeginning(z3FilePathSE,
//				getListNewVariable());
//
//		// Copy existing declare and assert from Z3Formula.smt2 to SE
//		// Z3FormulaSE.smt2
//		SymbolicExecutionFile.copyfile(z3FilePath, z3FilePathSE, false);
//
//		// Read details in z3FilePathSE.smt2
//		String smt2SEDetails = SymbolicExecutionFile.readFile(z3FilePathSE);
//		System.out.println(smt2SEDetails);
//
//		// Update assert to match with mapping table
//		for (MappingRecord mappingRecord : mappingTable.getMappingRecords()) {
//			smt2SEDetails = smt2SEDetails.replace(
//					mappingRecord.getExpression(), mappingRecord.getSymbol());
//		}
//
//		// Write smt2SEDetails to file Z3FormulaSE.smt2
//		SymbolicExecutionFile.writeFile(z3FilePathSE, smt2SEDetails);
//	}

	public String getExpression1(Condition condition) {
		String expression1 = "";

		BinExprAST binExprAST = (BinExprAST) condition.getAst();
		try {
			expression1 = (String) binExprAST.exprAST1.visit(
					new Temp1Visitor(codeAnalyzer.getLstParameter(),
							codeAnalyzer.getLstVariable(), codeAnalyzer
									.getLstCondition()), "c");
		} catch (CompilationException e1) {
			e1.printStackTrace();
		}
		return expression1;
	}

	public String getExpression2(Condition condition) {
		String expression2 = "";

		BinExprAST binExprAST = (BinExprAST) condition.getAst();
		try {
			expression2 = (String) binExprAST.exprAST2.visit(
					new Temp1Visitor(codeAnalyzer.getLstParameter(),
							codeAnalyzer.getLstVariable(), codeAnalyzer
									.getLstCondition()), "c");
		} catch (CompilationException e1) {
			e1.printStackTrace();
		}
		return expression2;
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

	// private String getSymbolOfExpression(String expression1) {
	// for (MappingRecord mappingRecord : mappingTable.getMappingRecords()) {
	// if (expression1.equals(mappingRecord.getExpression())) {
	// return mappingRecord.getSymbol();
	// }
	// }
	// return "";
	// }

	private boolean isExistsInMappingTable(String expression) {
		for (int i = 0; i < mappingTable.getMappingRecords().size(); i++) {
			if (expression.equals(mappingTable.getMappingRecord(i)
					.getExpression())) {
				return true;
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

	//
	// private void printConditionList() {
	// for (Condition condition : codeAnalyzer.getLstCondition()) {
	// String expression1 = getExpression1(condition);
	// String operation = getOperation(condition);
	// String expression2 = getExpression2(condition);
	//
	// System.out.println(expression1 + "\t\t" + operation + "\t\t"
	// + expression2);
	// }
	// }

	public void symbolicExecution(CodeAnalyzer codeAnalyzer, String sourceCode) {
		this.codeAnalyzer = codeAnalyzer;
		this.sourceCode = sourceCode;

		createMappingTable();
		createListOfUnsolvableCondition();
		addUnsolvableConditionToMappingTable();

		updateListVariable();
		
//		createSESmt2();
//		updateSourceCode();
	}

	public ArrayList<String> generateTestCaseWithSE() {
		String z3FormulaFile = "Z3FormulaSE.smt2";
		return codeAnalyzer.getNewTestcaseAfterSE(codeAnalyzer.getAbsolutePathOfSmt2() + z3FormulaFile);
	}

//	public void updateSourceCode() {
//
//	}
	
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

//	public ArrayList<Variable> getVariableList() {
//		ArrayList<Variable> lstVariableFull = codeAnalyzer.getLstVariable();
//
//		lstVariableFull.addAll(getListNewVariable());
//		return lstVariableFull;
//	}
}
