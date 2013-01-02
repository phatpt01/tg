package system;

import java.util.ArrayList;

import transform.AST.*;

public class Condition {
	
	int stmtID;
	boolean hasTestCase;
	AST ast;
	String condition;
	
	ArrayList<String> lstTruePath;
	ArrayList<String> lstFalsePath;
	ArrayList<Integer> lstTrueCondition;
	ArrayList<Integer> lstFalseCondition;
	
	String trueTestCase;
	String falseTestCase;
	
	//20121213
	String trueTestCaseFull;
	
	String falseTestCaseFull;

	public boolean hasTrueTestCase;

	public boolean hasFalseTestCase;
	
	public Condition() {
		stmtID = 0;
		ast = null;
		lstTruePath = new ArrayList<String>();
		lstFalsePath = new ArrayList<String>();
		lstTrueCondition = new ArrayList<Integer>();
		lstFalseCondition = new ArrayList<Integer>();
		trueTestCase = "";
		falseTestCase = "";
	}

//	public void addFalsePaths(String falsePath){
//		this.lstFalsePath.add(falsePath);
//	}

	public AST getAst() {
		return ast;
	}
	public String getCondition() {
		return condition;
	}

	public ArrayList<Integer> getFalseConditions() {
		return lstFalseCondition;
	}

	public ArrayList<String> getFalsePaths() {
		return lstFalsePath;
	}

	public String getFalseTestCase() {
		return falseTestCase;
	}

	public String getFalseTestcaseFull() {
		return falseTestCaseFull;
	}

	public int getStmtID() {
		return stmtID;
	}

	public ArrayList<Integer> getTrueConditions() {
		return lstTrueCondition;
	}

	public ArrayList<String> getTruePaths() {
		return lstTruePath;
	}

	public String getTrueTestCase() {
		return trueTestCase;
	}

	public String getTrueTestcaseFull() {
		return trueTestCaseFull;
	}

	public boolean hasTestcase() {
		return hasTestCase;
	}

	public void setAST(AST ast) {
		this.ast = ast;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setFalseConditions(ArrayList<Integer> falseConditions) {
		this.lstFalseCondition = falseConditions;
	}

	public void setFalseTestcase(String falseTestcase) {
		this.falseTestCase = falseTestcase;
	}

	public void setFalseTestcaseFull(String falseTestCaseFull) {
		this.falseTestCaseFull = falseTestCaseFull;
	}

	public void setHasTestcase(boolean hasTestcase) {
		this.hasTestCase = hasTestcase;
	}

	public void setStmtID(int stmtID) {
		this.stmtID = stmtID;
	}

	public void setTrueConditions(ArrayList<Integer> trueConditions) {
		this.lstTrueCondition = trueConditions;
	}
	
	public void setTrueTestcase(String trueTestcase) {
		this.trueTestCase = trueTestcase;
	}

	public void setTrueTestcaseFull(String trueTestCaseFull) {
		this.trueTestCaseFull = trueTestCaseFull;
	}

//	public void clearFalsePaths() {
//		this.lstFalsePath.clear();		
//	}
//
//	public void clearTruePaths() {
//		this.lstTruePath.clear();
//	}

//	public void addTruePaths(String truePath) {
//		this.lstTruePath.add(truePath);		
//	}
}
