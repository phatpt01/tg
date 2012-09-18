package program;

import java.util.ArrayList;

import CodeAnalyzer.CodeAnalyzer;
import GA.GA;
import system.*;
import transform.AST.CompilationException;

public class Control {
	
	private InOut io;
	private String standardFile;
	private String standardSource;
	private String originalSource;
	private CodeAnalyzer codeAnalyzer;
	private GA m_GA;

	public Control() {
		io = new InOut();
		codeAnalyzer = new CodeAnalyzer();
		m_GA = new GA();
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

	public ArrayList<String> getParaList() {
		return this.codeAnalyzer.getParaNameList();
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
		m_GA.run(codeAnalyzer);
		m_GA.reset();
		return this.codeAnalyzer.update(m_GA.getRes());

	}

	public String scanCondition() {
		return this.codeAnalyzer.scanCondition();
	}

	public String showAllTestCase() {
		return this.codeAnalyzer.showAllTestCase();
	}

	public String standardSource(String filename) {
		standardFile = codeAnalyzer.getStandardSource(filename);
		standardSource = io.readFile(standardFile);
		return standardSource;
	}
}
