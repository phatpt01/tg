package program;

import java.util.ArrayList;

import CodeAnalyzer.CodeAnalyzer;
import GA.GA;
import se.SymbolicExecution;
import system.*;
import transform.AST.CompilationException;

public class Control {

	private InOut io;
	private String standardFile;
	private String standardSource;
	private String originalSource;
	private CodeAnalyzer codeAnalyzer;
	private GA ga;
	
	private SymbolicExecution se;

	public Control() {
		io = new InOut();
		codeAnalyzer = new CodeAnalyzer();
		ga = new GA();
		
		se = new SymbolicExecution();
	}

	public String generateSolvable() {
		return this.codeAnalyzer.generateSolvable();
	}

	public ArrayList<String> getConditionList() throws CompilationException {
		return this.codeAnalyzer.getConditionList();
	}

	public ArrayList<Boolean> getFalseList() {
		return codeAnalyzer.getFalseList();
	}

	public ArrayList<Integer> getNextTestCase() {
		return codeAnalyzer.getNextTestCase();
	}

	public ArrayList<String> getParameterList() {
		return this.codeAnalyzer.getParameterNameList();
	}

	public ArrayList<Integer> getPrevTestCase() {
		return codeAnalyzer.getPrevTestCase();
	}

	public ArrayList<Integer> getSlide() {
		return this.codeAnalyzer.getSlide();
	}

	public ArrayList<Boolean> getTrueList() {
		return codeAnalyzer.getTrueList();
	}

	public String readSourceFile(String filename) {
		originalSource = io.readFile(filename);
		return originalSource;
	}

	public String runGA() {
		ga.run(codeAnalyzer);
		ga.reset();
		return codeAnalyzer.update(ga.getRes());

	}

	public void runSE(String txtSourceCode) {
		se.runSE(codeAnalyzer, txtSourceCode);
	}

	public String scanCondition() {
		return codeAnalyzer.scanCondition();
	}

	public String showAllTestCase() {
		return codeAnalyzer.showAllTestCase();
	}

	public String standardSource(String filename) {
		standardFile = codeAnalyzer.getStandardSource(filename);
		standardSource = io.readFile(standardFile);
		return standardSource;
	}
}
