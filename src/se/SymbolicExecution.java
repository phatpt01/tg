package se;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import system.Condition;
import system.Parameter;
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

	public void createSESmt2() {
		// Copy Z3Formular.smt2 to Z3FormulaSE.smt2
		String z3output = "Z3OUTPUT";
		File z3outFolder = new File(z3output);

		if (!z3outFolder.exists()) {
			z3outFolder.mkdirs();
		}

		String z3FilePath = z3outFolder.getAbsolutePath() + File.separatorChar
				+ "Z3Formula.smt2";
		String z3FilePathSE = z3outFolder.getAbsolutePath() + File.separatorChar
				+ "Z3FormulaSE.smt2";
		
		SEFile.createBlankFile(z3FilePathSE);
		
		// Write new variable to SE file
		SEFile.appendAtBeginning(z3FilePathSE, getListNewVariable());

		// Copy existing declare and assert from Z3Formula.smt2 to SE Z3FormulaSE.smt2
		SEFile.copyfile(z3FilePath, z3FilePathSE,false);
		
		// Read details in z3FilePathSE.smt2
		String smt2SEDetails = SEFile.readFile(z3FilePathSE);
		System.out.println(smt2SEDetails);
		
		// Update assert to match with mapping table
		for (MappingRecord mappingRecord : mappingTable.getMappingRecords()){
			smt2SEDetails = smt2SEDetails.replace(mappingRecord.getExpression(),mappingRecord.getSymbol());
		}
		
		// Write smt2SEDetails to file Z3FormulaSE.smt2
		SEFile.writeFile(z3FilePathSE, smt2SEDetails);
	
		
	}

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

//	private String getSymbolOfExpression(String expression1) {
//		for (MappingRecord mappingRecord : mappingTable.getMappingRecords()) {
//			if (expression1.equals(mappingRecord.getExpression())) {
//				return mappingRecord.getSymbol();
//			}
//		}
//		return "";
//	}

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

	private void printConditionList() {
		for (Condition condition : codeAnalyzer.getLstCondition()) {
			String expression1 = getExpression1(condition);
			String operation = getOperation(condition);
			String expression2 = getExpression2(condition);

			System.out.println(expression1 + "\t\t" + operation + "\t\t"
					+ expression2);
		}

	}

	public void printMappingTable() {
		System.out.println("\n Print mapping table");

		ArrayList<MappingRecord> mappingRecords = null;
		mappingRecords = mappingTable.getMappingRecords();

		MappingRecord mappingRecord = null;

		for (int i = 0; i < mappingRecords.size(); i++) {
			mappingRecord = mappingRecords.get(i);
			System.out.println("Mapping record: "
					+ mappingRecord.getCondition() + "\t\t"
					+ mappingRecord.getExpression() + "\t\t"
					+ mappingRecord.getSymbol() + "\t\t"
					+ mappingRecord.getReturnType());
		}
	}

	private void printVariableList() {
		for (Variable variable : codeAnalyzer.getLstVariable()) {
			System.out
					.println(variable.getName() + "\t\t" + variable.getType());
		}
	}

	public void runSE(CodeAnalyzer codeAnalyzer, String sourceCode) {
		this.codeAnalyzer = codeAnalyzer;
		this.sourceCode = sourceCode;

		createMappingTable();
		createListOfUnsolvableCondition();
		addUnsolvableConditionToMappingTable();

		printMappingTable();

//		System.out.println("\n Print variable before SE");
		//printVariableList();

		System.out.println("\n Print condition list before SE");
		printConditionList();

		getListNewVariable();
		updateConditionList();

//		System.out.println("\n Print variable after SE");
		printVariableList();

		printParamterList();
		// change source code
		// System.out.println(sourceCode);

		createSESmt2();
		updateSourceCode();
	}

	private void printParamterList() {
		for (Parameter parameter : codeAnalyzer.getLstParameter()){
			System.out.println(parameter.getName() + "\t\t" + parameter.getType());
		}
		
	}

	public void updateConditionList() {
		
		
	}

	public void updateSourceCode() {

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
}
