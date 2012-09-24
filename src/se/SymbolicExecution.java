package se;

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
	MappingTable mappingTable = null;
	ArrayList<Condition> lstUnSolvalbecondition = null;

	public SymbolicExecution() {
		mappingTable = new MappingTable();
		lstUnSolvalbecondition = new ArrayList<Condition>();		
	}

	public void createMappingTable() {
		if (this.mappingTable == null) {
			this.mappingTable = new MappingTable();
		}
	}

	public void symbolicCode() {
		
		createListOfUnsolvableCondition();
		
		createMappingTable();
		
		addUnsolvableConditionToMappingTable();
	}

	private void addUnsolvableConditionToMappingTable() {
		for (Condition unsolvableCondition : lstUnSolvalbecondition){
			BinExprAST binExprAST = (BinExprAST) unsolvableCondition.getAst();
			
			String expression1 = "";
//			String operation = "";
			String expression2 = "";

			try {
				expression1 = (String) binExprAST.exprAST1.visit(
						new Temp1Visitor(codeAnalyzer.getLstParameter(), codeAnalyzer.getLstVariable(),
								codeAnalyzer.getLstCondition()), "c");
//				operation = (String) binExprAST.op.getText();
				expression2 = (String) binExprAST.exprAST2.visit(
						new Temp1Visitor(codeAnalyzer.getLstParameter(), codeAnalyzer.getLstVariable(),
								codeAnalyzer.getLstCondition()), "c");
			} catch (CompilationException e1) {
				e1.printStackTrace();
			}

//			System.out.print(". Expr1: " + expression1 + ", " + "op: "
//					+ operation + ", " + "Expr2: " + expression2);
//			System.out.print(", convert to AST: ");
//			System.out.println(operation + " " + expression1 + " "
//					+ expression2);
			String symbol;
			MappingRecord mappingRecord;
			
			symbol = getNewSymbol();
			mappingRecord= new MappingRecord(symbol, expression1, DEFAULT_TYPE);
			mappingTable.addMappingRecord(mappingRecord);
			
			symbol = getNewSymbol();
			mappingRecord= new MappingRecord(symbol, expression2, DEFAULT_TYPE);
			mappingTable.addMappingRecord(mappingRecord);
		}
		
	}

	public void printMappingTable() {
		System.out.println("\n Print mapping table");
		
		ArrayList<MappingRecord> mappingRecords = null;
		mappingRecords = mappingTable.getMappingRecords();
		
		MappingRecord mappingRecord = null;
		
		for (int i=0; i<mappingRecords.size(); i++){
			mappingRecord = mappingRecords.get(i);
			System.out.println("Mapping record: " + mappingRecord.getSymbol() + " - "
					+ mappingRecord.getExpression() + " - "
					+ mappingRecord.getReturnType()			
					);
		}
	}

	private String getNewSymbol() {
		int maxIndex = mappingTable.getMappingRecords().size() + 1;
		return "X" + maxIndex;
	}

	private void createListOfUnsolvableCondition() {
		for (int i = 0; i < codeAnalyzer.getLstCondition().size(); i++) {
			Condition condition = codeAnalyzer.getLstCondition().get(i);
			if (!condition.hasTestcase()) {
				lstUnSolvalbecondition.add(condition);
			}
		}
	}

	public void updateVariableList() {
		ArrayList<Variable> lstVariable = null;
		lstVariable = codeAnalyzer.getLstVariable();
		
		MappingRecord mappingRecord = null;
		Variable variable = null;
		for (int i=0; i<mappingTable.getMappingRecords().size() -1; i++){
			mappingRecord = mappingTable.getMappingRecord(i);
			
			String variableName = mappingRecord.getSymbol();
			String variableType = mappingRecord.getReturnType();
			variable = new Variable(variableName, variableType);

			lstVariable.add(variable);
		}
		// Update lstVariable of CodeAnalyzer
		codeAnalyzer.setLstVariable(lstVariable);
	}

	public void createSmt2() {
		
	}

	public void updateSourceCode() {
		
	}

	public void runSE(CodeAnalyzer codeAnalyzer) {
		this.codeAnalyzer = codeAnalyzer;
		
		createMappingTable();
		symbolicCode();
		printMappingTable();

		updateVariableList();
		
		// print variable after se
		for (Variable variable : codeAnalyzer.getLstVariable()) {
			System.out.println(variable.getName() + " - " + variable.getType());
		}
	
		createSmt2();
		updateSourceCode();
	}

}
