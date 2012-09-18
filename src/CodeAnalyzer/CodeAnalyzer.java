package CodeAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import system.*;
import transform.CodeGeneration.AstSimulationVisitor;
import transform.*;
import transform.AST.*;
import transform.AST.CompilationException;
import transform.DependenceGraph.*;
import transform.CodeGeneration.*;

public class CodeAnalyzer {
	private AST astTree;

	private PDG pdg;

	// Mapping Table
	private MappingTable mapTable;

	// Transform
	private Transform transform;

	// List path
	private ArrayList<ArrayList<AST>> listPath;

	// List Branch
	private ArrayList<ArrayList<Integer>> listBranch;

	// List Variable
	private ArrayList<Variable> listVariable;

	// List Parameter
	private ArrayList<Parameter> listParameter;

	// List Condition
	private ArrayList<Condition> listCondition;

	private int currentTestCase = 0;

	private String testcase = "";

	private int numUnSolvableCondition;

	int temp1 = 1;

	public CodeAnalyzer() {
	}

	public CodeAnalyzer(String strSourceFile) {
		this.listCondition = new ArrayList<Condition>();
		transform = new Transform(strSourceFile);
		this.mapTable = transform.getMapTable();
		this.pdg = transform.getPdg();
		this.astTree = transform.getAstree();
		this.listParameter = transform.getListParameters();
		this.listVariable = transform.getListVariables();
	}

	public int[] check(int[][] testcase) {
		int numCon = this.listCondition.size();
		int numPar = this.listParameter.size();
		int[] result = new int[numUnSolvableCondition * 2];
		int count = 0;

		for (int i = 0; i < numCon; i++) {
			if (this.listCondition.get(i).isHastc() == false) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp1 = new ArrayList<String>();
				for (int j = 0; j < numPar; j++) {
					temp.add(String.valueOf(testcase[count][j]));
					temp1.add(String.valueOf(testcase[count + 1][j]));
				}
				result[count] = checkCon(temp, i, 0);
				result[count + 1] = checkCon(temp1, i, 1);
				count += 2;
			}
		}
		return result;
	}

	private int checkCon(ArrayList<String> testcase, int con, int branch) {
		int result = 100;
		int temp;
		int count = 0;
		Temp2Visitor visitor = new Temp2Visitor(this.listParameter,
				this.listVariable, testcase);
		try {
			boolean check;
			if (branch == 0) {
				for (int i = 0; i < this.listCondition.get(con).getTruecon()
						.size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.listPath.get(this.listCondition
							.get(con).getTruecon().get(i));
					ArrayList<Integer> lbranch = this.listBranch
							.get(this.listCondition.get(con).getTruecon()
									.get(i));
					for (j = 0; path.get(j).line < this.listCondition.get(con)
							.getStmtID(); j++) {
						count++;
						int res = (Integer) path.get(j).visit(visitor,
								lbranch.get(j));
						if (check == true) {
							temp += 100;
						}
						if (res != 0) {
							temp += res;
							check = true;
						}
					}
					temp += (Integer) path.get(j).visit(visitor, branch);
					count++;
					if (temp != 0)
						temp = temp / count + 1;
					else
						temp = 0;
					if (temp < result)
						result = temp;
					visitor.clear();
				}
			} else {
				for (int i = 0; i < this.listCondition.get(con).getFalsecon()
						.size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.listPath.get(this.listCondition
							.get(con).getFalsecon().get(i));
					ArrayList<Integer> lbranch = this.listBranch
							.get(this.listCondition.get(con).getFalsecon()
									.get(i));
					for (j = 0; path.get(j).line < this.listCondition.get(con)
							.getStmtID(); j++) {
						count++;
						int res = (Integer) path.get(j).visit(visitor,
								lbranch.get(j));
						if (check == true) {
							temp += 100;
						}
						if (res != 0) {
							temp += res;
							check = true;
						}
					}
					temp += (Integer) path.get(j).visit(visitor, branch);
					count++;
					if (temp != 0)
						temp = temp / count + 1;
					else
						temp = 0;
					if (temp < result)
						result = temp;
					visitor.clear();
				}
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("Invalid Input!!!"); // if the input
																// is invalid
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Type is not correct!!!");
		} catch (CompilationException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String generateSolvable() {
		int count = 0;
		String output = "";
		for (int i = 0; i < this.listCondition.size(); i++) {
			generateNextTestCase(i);
		}

		for (int i = 0; i < this.listCondition.size(); i++) {
			Condition temp = this.listCondition.get(i);
			if (temp.isHastc()) {
				count++;
				output += "Condition: " + temp.getCondition() + "\n";
				output += "\tTrue: " + temp.getTruetc() + "\t" + temp.hastruetc
						+ "\n";
				output += "\tFalse: " + temp.getFalsetc() + "\t"
						+ temp.hasfalsetc + "\n";
			}
		}
		output = "Number of solvable condition: " + count + "\n" + output;
		return output;
	}

	private void generateNextTestCase(int i) {
		boolean check = false;
		for (int j = 0; j < this.listCondition.get(i).getTruepath().size(); j++) {
			String res = generateTestCase(this.listCondition.get(i).getTruepath()
					.get(j));
			if (!res.equals("")) {
				this.listCondition.get(i).setTruetc(res);
				this.listCondition.get(i).hastruetc = true;
				check = true;
				break;
			}
		}
		for (int k = 0; k < this.listCondition.get(i).getFalsepath().size(); k++) {
			String res = generateTestCase(this.listCondition.get(i).getFalsepath()
					.get(k));
			if (!res.equals("")) {

				this.listCondition.get(i).setFalsetc(res);
				this.listCondition.get(i).hasfalsetc = true;
				check = true;
				break;
			}

		}
		this.listCondition.get(i).setHastc(check);
	}

	private String generateTestCase(String con) {
		String z3output = "Z3OUTPUT";
		File z3outFolder = new File(z3output);
		if (!z3outFolder.exists()) {
			z3outFolder.mkdirs();
		}

		String z3FilePath = z3outFolder.getAbsolutePath() + File.separatorChar
				+ "Z3Formula.smt2";
		// Print the parameters, variables, and reindexed variables
		try {
			FileWriter fw = new FileWriter(z3FilePath);
			BufferedWriter out = new BufferedWriter(fw);

			// Print the parameters of program
			for (int i = 0; i < this.listParameter.size(); i++) {
				Parameter p = this.listParameter.get(i);
				out.write("(declare-const ");
				out.write(p.getName() + " ");
				switch (p.getType()) {
				case "Int":
					out.write(p.getType() + ")");
					break;
				case "Float":
				case "Double":
					out.write("Real)");
					break;
				}
				out.write("\n");
			}
			// Print variables of program
			for (int i = 0; i < this.listVariable.size(); i++) {
				Variable v = this.listVariable.get(i);
				out.write("(declare-const ");
				out.write(v.getName() + " ");
				switch (v.getType()) {
				case "Int":
					out.write(v.getType() + ")");
					break;
				case "Float":
				case "Double":
					out.write("Real)");
					break;
				}
				out.write("\n");
			}
			out.write(con);
			out.write("(check-sat)\n");
			out.write("(model)\n");
			out.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> testcase = this.getNewTestcase(z3FilePath);
		if (testcase != null) {
			ArrayList<String> temp = new ArrayList<String>();
			StringBuffer tc = new StringBuffer();
			for (int i = 0; i < testcase.size(); i++) {
				tc.append(testcase.get(i));
				tc.append("\n");
				temp.add(testcase.get(i));
			}
			return temp.toString();
		} else
			return "";
	}

	public ArrayList<String> getConditionList() throws CompilationException {
		ConditionPrintVisitor visitor = new ConditionPrintVisitor(null, true);
		ArrayList<String> listConditionExpr = new ArrayList<String>();
		for (int i = 0; i < this.mapTable.size(); i++) {
			AST ast = (AST) mapTable.get(i).getStatementAST();
			if (!(ast instanceof RetStmtAST)) {
				if (ast instanceof IfThenStmtAST) {
					ast = ((IfThenStmtAST) ast).e;
				} else if (ast instanceof IfThenElseStmtAST) {
					ast = ((IfThenElseStmtAST) ast).e;
				} else if (ast instanceof WhileStmtAST) {
					ast = ((WhileStmtAST) ast).e;
				} else if (ast instanceof DoStmtAST) {
					ast = ((DoStmtAST) ast).e;
				} else {
					ast = null;
				}
				if (ast != null) {
					Condition con = new Condition();
					con.setStmtID(ast.line);
					con.setAst(ast);
					String conExp = visitor.print(ast);
					con.setCondition(conExp);
					listCondition.add(con);
					listConditionExpr.add(conExp);
				}
			}
		}
		return listConditionExpr;
	}

	public ArrayList<Boolean> getFalseList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < this.listCondition.size(); i++) {
			if (this.listCondition.get(i).hasfalsetc == true) {
				result.add(true);
			} else {
				result.add(false);
			}
		}
		return result;
	}

	private ArrayList<String> getNewTestcase(String z3FormulaFilename) {
		ArrayList<String> testcase = new ArrayList<String>();
		ArrayList<String> z3result = new ArrayList<String>();
		Runtime run = Runtime.getRuntime();
		try {
			String runZ3 = "./Z3/z3.exe";
			String config = " /m ";
			String formulaFile = z3FormulaFilename;
			Process pp = run.exec(runZ3 + config + formulaFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					pp.getInputStream()));
			String line = in.readLine();
			if (line.contains("sat") && !line.contains("unsat")) {
				while ((line = in.readLine()) != null) {
					if (line.contains("define")) {
						String sub;
						if (!line.contains(")\")")) { // not contain
							sub = line.substring(8, line.length() - 1);
						} else {
							sub = line.substring(8, line.length() - 3);
						}
						Scanner sc = new Scanner(sub);
						sc.useDelimiter(" ");
						z3result.add(sc.next());
						z3result.add(sc.next());
						sc.close();
					}
				}
				// process z3result
				for (int i = 0; i < this.listParameter.size(); i++) {
					for (int j = 0; j < z3result.size(); j += 2) {
						if (z3result.get(j).equals(
								this.listParameter.get(i).getName())) {
							testcase.add(z3result.get(j + 1)); // add new value
																// to testcase
							break;
						}
					}
					if (testcase.size() < i) {
						Object result = 0;
						Random ran1 = new Random();
						double n = ran1.nextDouble() * 100;
						switch (this.listParameter.get(i).getType()) {
						case "Int":
							result = (int) n;
							break;
						case "Double":
							result = (double) n;
							break;
						case "Float":
							result = (float) n;
							break;
						}
						testcase.add(result.toString());
					}
				}
				if (testcase.size() < this.listParameter.size()) {
					int num = this.listParameter.size() - testcase.size();
					Random ran1 = new Random();
					for (int i = 0; i < num; i++) {
						Object result = 0;
						double n = ran1.nextDouble() * 100;
						switch (this.listParameter.get(i).getType()) {
						case "Int":
							result = (int) n;
							break;
						case "Double":
							result = (double) n;
							break;
						case "Float":
							result = (float) n;
							break;
						}
						testcase.add(result.toString());
					}
				}
			} else {
				return null;
			}
			// if Z3 doesn't generate all values then randomly generate value
			// for the rest

		} catch (Exception e) {
			e.printStackTrace();
		}
		return testcase;
	}

	public ArrayList<Integer> getNextTestCase() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (currentTestCase < this.listCondition.size() * 2 - 1)
			currentTestCase++;
		if (currentTestCase % 2 == 0) {
			testcase = this.listCondition.get(currentTestCase / 2).getTruetc();
		} else {
			testcase = this.listCondition.get(currentTestCase / 2).getFalsetc();
		}
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			result.add(Integer.parseInt(st.nextToken()));
		}
		return result;
	}

	/*
	 * private void printSMT2(String z3FormulaFilename, Path pathCondition)
	 * throws CompilationException { // Print the parameters, variables, and
	 * reindexed variables try { FileWriter fw = new
	 * FileWriter(z3FormulaFilename); BufferedWriter out = new
	 * BufferedWriter(fw);
	 * 
	 * // Print the parameters of program for (int i = 0; i <
	 * this.listPara.size(); i++) { Parameter p = this.listPara.get(i);
	 * out.write("(declare-const "); out.write(p.getName() + " ");
	 * out.write(p.getType() + ")"); out.write("\n"); } // Print variables of
	 * program for (int i = 0; i < this.listVar.size(); i++) { Variable v =
	 * this.listVar.get(i); out.write("(declare-const "); out.write(v.getName()
	 * + " "); out.write(v.getType() + ")"); out.write("\n"); } // Print
	 * variables after reindexing for (int i = 0; i <
	 * pathCondition.getListVariableReIndexed().size(); i++) { Variable v =
	 * pathCondition.getListVariableReIndexed().get(i);
	 * out.write("(declare-const "); out.write(v.getName() + " ");
	 * out.write(v.getType() + ")"); out.write("\n"); } out.close(); } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * // Print the negated path condition in the SMT2 form Z3PathPrintVisitor
	 * z3Visitor = new Z3PathPrintVisitor(z3FormulaFilename, false);
	 * z3Visitor.printSMT2(pathCondition); }
	 */

	public int getNumCon() {
		numUnSolvableCondition = 0;
		for (int i = 0; i < this.listCondition.size(); i++) {
			if (this.listCondition.get(i).isHastc() == false)
				numUnSolvableCondition++;
		}
		return numUnSolvableCondition;
	}

	public int getNumPar() {
		return this.listParameter.size();
	}

	public ArrayList<String> getParaNameList() {
		ArrayList<String> listParaName = new ArrayList<String>();
		for (int i = 0; i < listParameter.size(); i++) {
			listParaName.add(listParameter.get(i).getName());
		}
		return listParaName;
	}

	public ArrayList<Integer> getPrevTestCase() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (currentTestCase > 0)
			currentTestCase--;
		if (currentTestCase % 2 == 0) {
			testcase = this.listCondition.get(currentTestCase / 2).getTruetc();
		} else {
			testcase = this.listCondition.get(currentTestCase / 2).getFalsetc();
		}
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			int temp = Integer.parseInt(st.nextToken());
			result.add(temp);
		}
		return result;
	}

	public ArrayList<Integer> getSlide() {
		ArrayList<String> input = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			input.add(st.nextToken());
		}
		System.out.println("ABC:" + input);
		AstSimulationVisitor simulationAST = new AstSimulationVisitor(this.pdg,
				input);
		try {
			this.astTree.visit(simulationAST, "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ExecutionHistory eh = simulationAST.getExecutionHistory();
		eh.changeLineIdAtExecNodePointToNode(this.pdg);
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < eh.size(); i++) {
			result.add(eh.get(i).getNode().getID());
		}
		return result;
	}

	public String getStandardSource(String filename) {
		this.loadFile(filename);
		return transform.getStandardSourceFile();
	}

	public ArrayList<Boolean> getTrueList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < this.listCondition.size(); i++) {
			if (this.listCondition.get(i).hastruetc == true) {
				result.add(true);
			} else {
				result.add(false);
			}
		}
		return result;
	}

	public ArrayList<String> getVarNameList() {
		ArrayList<String> listVarName = new ArrayList<String>();
		for (int i = 0; i < listVariable.size(); i++) {
			listVarName.add(listVariable.get(i).getName());
		}
		return listVarName;
	}

	public void loadFile(String strSourceFile) {
		listCondition = new ArrayList<Condition>();
		transform = new Transform(strSourceFile);
		mapTable = transform.getMapTable();
		astTree = transform.getAstree();
		pdg = transform.getPdg();
		listParameter = transform.getListParameters();
		listVariable = transform.getListVariables(); 
		listPath = transform.getListPath();
		listBranch = transform.getListBranch();
	}

	public String scanCondition() {

		String output = "";
		listCondition = transform.updateConditionList(listCondition);

		for (int i = 0; i < this.listCondition.size(); i++) {
			output += "Condition " + (i + 1) + ":"
					+ this.listCondition.get(i).getCondition() + "\n";
			output += "True:\n";
			for (int j = 0; j < this.listCondition.get(i).getTruepath().size(); j++) {
				output += this.listCondition.get(i).getTruepath().get(j) + "\n";
			}
			output += "False:\n";
			for (int j = 0; j < this.listCondition.get(i).getFalsepath().size(); j++) {
				output += this.listCondition.get(i).getFalsepath().get(j)
						+ "\n";
			}
		}
		return output;
	}

	public String showAllTestCase() {
		String output = "";
		for (int i = 0; i < this.listCondition.size(); i++) {
			Condition temp = this.listCondition.get(i);
			output += "Condition " + (i + 1) + ": " + temp.getCondition()
					+ "\n";
			output += "\tTrue: " + temp.getTruetc() + "\t" + (temp.hastruetc)
					+ "\n";
			output += "\tFalse: " + temp.getFalsetc() + "\t"
					+ (temp.hasfalsetc) + "\n";
		}
		return output;
	}

	public String update(int[][] res) {
		String output = "";
		int count = 0;
		int numCon = this.listCondition.size();
		int numPar = this.listParameter.size();
		for (int i = 0; i < numCon; i++) {
			if (this.listCondition.get(i).isHastc() == false) {
				int j;
				String truetc = "[";
				String falsetc = "[";
				for (j = 0; j < numPar; j++) {
					truetc += res[count][j];
					falsetc += res[count + 1][j];
					if (j < numPar - 1) {
						truetc += ", ";
						falsetc += ", ";
					}
				}
				truetc += "]";
				this.listCondition.get(i).setTruetc(truetc);
				falsetc += "]";
				this.listCondition.get(i).setFalsetc(falsetc);
				if (res[count][j] == 0) {
					this.listCondition.get(i).hastruetc = true;
				} else {
					this.listCondition.get(i).hastruetc = false;
				}

				if (res[count + 1][j] == 0) {
					this.listCondition.get(i).hasfalsetc = true;
				} else {
					this.listCondition.get(i).hasfalsetc = false;
				}

				output += "Condition: "
						+ this.listCondition.get(i).getCondition() + "\n";
				output += "\tTrue: " + this.listCondition.get(i).getTruetc()
						+ "\t" + this.listCondition.get(i).hastruetc + "\n";
				output += "\tFalse: " + this.listCondition.get(i).getFalsetc()
						+ "\t" + this.listCondition.get(i).hasfalsetc + "\n";
				count += 2;
			}
		}
		output = "Number of unsolvable condition: "
				+ this.numUnSolvableCondition + "\n" + output;
		return output;
	}
}
