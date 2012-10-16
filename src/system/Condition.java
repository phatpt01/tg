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

//	public void setFalsePaths(ArrayList<String> falsepaths) {
//		this.lstFalsePath = falsepaths;
//	}

	public void setFalseTestcase(String falseTestcase) {
		this.falseTestCase = falseTestcase;
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

//	public void setTruePaths(ArrayList<String> truePaths) {
//		this.lstTruePath = truePaths;
//	}

	public void setTrueTestcase(String trueTestcase) {
		this.trueTestCase = trueTestcase;
	}
}
