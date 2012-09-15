package system;

import java.util.ArrayList;

import transform.AST.*;

public class Condition {
	int stmtID;
	boolean hastc;
	AST ast;
	String condition;
	ArrayList<String> truepath;
	ArrayList<String> falsepath;
	ArrayList<Integer> truecon;
	ArrayList<Integer> falsecon;
	String truetc;
	public boolean hastruetc;
	String falsetc;
	public boolean hasfalsetc;

	public Condition() {
		stmtID = 0;
		ast = null;
		truepath = new ArrayList<String>();
		falsepath = new ArrayList<String>();
		truecon = new ArrayList<Integer>();
		falsecon = new ArrayList<Integer>();
		truetc = "";
		falsetc = "";
	}

	public AST getAst() {
		return ast;
	}

	public String getCondition() {
		return condition;
	}

	public ArrayList<Integer> getFalsecon() {
		return falsecon;
	}

	public ArrayList<String> getFalsepath() {
		return falsepath;
	}

	public String getFalsetc() {
		return falsetc;
	}

	public int getStmtID() {
		return stmtID;
	}

	public ArrayList<Integer> getTruecon() {
		return truecon;
	}

	public ArrayList<String> getTruepath() {
		return truepath;
	}

	public String getTruetc() {
		return truetc;
	}

	public boolean isHastc() {
		return hastc;
	}

	public void setAst(AST ast) {
		this.ast = ast;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public void setFalsecon(ArrayList<Integer> falsecon) {
		this.falsecon = falsecon;
	}

	public void setFalsepath(ArrayList<String> falsepath) {
		this.falsepath = falsepath;
	}

	public void setFalsetc(String falsetc) {
		this.falsetc = falsetc;
	}

	public void setHastc(boolean hastc) {
		this.hastc = hastc;
	}

	public void setStmtID(int stmtID) {
		this.stmtID = stmtID;
	}

	public void setTruecon(ArrayList<Integer> truecon) {
		this.truecon = truecon;
	}

	public void setTruepath(ArrayList<String> truepath) {
		this.truepath = truepath;
	}

	public void setTruetc(String truetc) {
		this.truetc = truetc;
	}
}
