package program;

import java.util.ArrayList;

import CodeAnalyzer.CodeAnalyzer;
import GA.GA;
import se.MappingRecord;
import se.SymbolicExecution;
import system.*;
import transform.AST.CompilationException;

public class Control {

	private InOut io;
	private String standardFile;
	private String standardSource;
	private String originalSource;
	private CodeAnalyzer codeAnalyzer;
	
	private SymbolicExecution se;
	private GA ga;

	
	public Control() {
		io = new InOut();
		codeAnalyzer = new CodeAnalyzer();
		ga = new GA();
	}

//	public void addMinMaxAssert() {
//		se.addMinMaxAssert();
//	}
	
	public String generateSolvable() {
		return codeAnalyzer.generateSolvable();
	}

	public String generateSolvableAfterSE() {
		se.replaceExpressionBySymbol();
		return se.generateSolvableAfterSE();
	}

	public ArrayList<String> getConditionList() throws CompilationException {
		return codeAnalyzer.getConditionList();
	}

	public ArrayList<Boolean> getFalseList() {
		return codeAnalyzer.getFalseList();
	}

	public ArrayList<MappingRecord> getMappingRecordList() {
		return se.getMappingRecordList();
	}

	public ArrayList<Integer> getNextTestCase() {
		return codeAnalyzer.getNextTestCase();
	}

	public int getNumUnSolvableCondition(){
		return codeAnalyzer.getNumUnSolvableCondition();
	}

	public ArrayList<String> getParameterList() {
		return codeAnalyzer.getParameterNameList();
	}

	public ArrayList<Integer> getPrevTestCase() {
		return codeAnalyzer.getPrevTestCase();
	}

	public ArrayList<Integer> getSlide() {
		return codeAnalyzer.getSlide();
	}

	public ArrayList<Boolean> getTrueList() {
		return codeAnalyzer.getTrueList();
	}

	public ArrayList<Variable> getVariableList() {
		return codeAnalyzer.getLstVariable();
	}

	public String readSourceFile(String filename) {
		originalSource = io.readFile(filename);
		return originalSource;
	}

	public String runGA() {
		ga.run(codeAnalyzer);
		ga.reset();
		return codeAnalyzer.update(ga.getResult());
	}

	public String scanCondition() {
		return codeAnalyzer.scanCondition();
	}

	public void setRangeOfSymbol() {
		se.setRangeOfSymbol();		
	}

	public String showAllTestCase() {
		return codeAnalyzer.showAllTestCase();
	}

	public String standardSource(String filename) {
		standardFile = codeAnalyzer.getStandardSource(filename);
		standardSource = io.readFile(standardFile);
		return standardSource;
	}

	public void symbolicExecution() {
		se = new SymbolicExecution();
		se.symbolicExecution(codeAnalyzer);
	}
}
