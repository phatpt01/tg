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

	private MappingTable mapTable;
	private Transform transform;

	private ArrayList<ArrayList<AST>> lstPath;
	private ArrayList<ArrayList<Integer>> lstBranch;
	private ArrayList<Variable> lstVariable;
	private ArrayList<Parameter> lstParameter;
	private ArrayList<Condition> lstCondition;

	private int currentTestCase = 0;

	private String testcase = "";

	private int numUnSolvableCondition;

	int temp1 = 1;

	public CodeAnalyzer() {
	}

	public CodeAnalyzer(String strSourceFile) {

		transform = new Transform(strSourceFile);

		this.lstCondition = new ArrayList<Condition>();
		this.mapTable = transform.getMapTable();
		this.pdg = transform.getPdg();
		this.astTree = transform.getAstree();
		this.lstParameter = transform.getListParameters();
		this.lstVariable = transform.getListVariables();
	}

	public int[] check(int[][] testcase) {
		int numberConditions = this.lstCondition.size();
		int numberParameters = this.lstParameter.size();
		int[] result = new int[numUnSolvableCondition * 2];
		int count = 0;

		for (int i = 0; i < numberConditions; i++) {
			if (this.lstCondition.get(i).isHasTestcase() == false) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp1 = new ArrayList<String>();
				for (int j = 0; j < numberParameters; j++) {
					temp.add(String.valueOf(testcase[count][j]));
					temp1.add(String.valueOf(testcase[count + 1][j]));
				}
				result[count] = checkCondition(temp, i, 0);
				result[count + 1] = checkCondition(temp1, i, 1);
				count += 2;
			}
		}
		return result;
	}

	private int checkCondition(ArrayList<String> testcase, int con, int branch) {
		int result = 100;
		int temp;
		int count = 0;
		Temp2Visitor visitor = new Temp2Visitor(this.lstParameter,
				this.lstVariable, testcase);
		try {
			boolean check;
			if (branch == 0) {
				for (int i = 0; i < this.lstCondition.get(con)
						.getTrueConditions().size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.lstPath.get(this.lstCondition
							.get(con).getTrueConditions().get(i));
					ArrayList<Integer> lbranch = this.lstBranch
							.get(this.lstCondition.get(con).getTrueConditions()
									.get(i));
					for (j = 0; path.get(j).line < this.lstCondition.get(con)
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
				for (int i = 0; i < this.lstCondition.get(con)
						.getFalseConditions().size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.lstPath.get(this.lstCondition
							.get(con).getFalseConditions().get(i));
					ArrayList<Integer> lbranch = this.lstBranch
							.get(this.lstCondition.get(con)
									.getFalseConditions().get(i));
					for (j = 0; path.get(j).line < this.lstCondition.get(con)
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

	private void generateNextTestCase(int i) {
		boolean check = false;
		for (int j = 0; j < this.lstCondition.get(i).getTruePaths().size(); j++) {
			String res = generateTestCase(this.lstCondition.get(i)
					.getTruePaths().get(j));
			if (!res.equals("")) {
				this.lstCondition.get(i).setTrueTestcase(res);
				this.lstCondition.get(i).hasTrueTestCase = true;
				check = true;
				break;
			}
		}
		for (int k = 0; k < this.lstCondition.get(i).getFalsePaths().size(); k++) {
			String res = generateTestCase(this.lstCondition.get(i)
					.getFalsePaths().get(k));
			if (!res.equals("")) {

				this.lstCondition.get(i).setFalseTestcase(res);
				this.lstCondition.get(i).hasFalseTestCase = true;
				check = true;
				break;
			}

		}
		this.lstCondition.get(i).setHasTestcase(check);
	}

	public String generateSolvable() {
		int count = 0;
		String output = "";

		// Generate testcaee by Z3 for all condition in list
		for (int i = 0; i < this.lstCondition.size(); i++) {
			generateNextTestCase(i);
		}

		for (int i = 0; i < this.lstCondition.size(); i++) {
			Condition condition = this.lstCondition.get(i);
			// If Z3 can solve this condition
			if (condition.isHasTestcase()) {
				count++;
				output += "Condition: " + condition.getCondition() + "\n";
				output += "\t True: " + condition.getTrueTestCase() + "\t"
						+ condition.hasTrueTestCase + "\n";
				output += "\t False: " + condition.getFalseTestCase() + "\t"
						+ condition.hasFalseTestCase + "\n";
			}
		}
		output = "Number of solvable condition: " + count + "\n" + output;

		printAllUnsolvableTestCase();

		return output;
	}

	private String generateTestCase(String con) {

		String z3output = "Z3OUTPUT";
		File z3outFolder = new File(z3output);

		if (!z3outFolder.exists()) {
			z3outFolder.mkdirs();
		}

		String z3FilePath = z3outFolder.getAbsolutePath() + File.separatorChar
				+ "Z3Formula.smt2";

		// Print the parameters, variables, and reindexed variables to stmt file
		try {
			FileWriter fw = new FileWriter(z3FilePath);
			BufferedWriter out = new BufferedWriter(fw);

			// Print the parameters of program
			for (int i = 0; i < this.lstParameter.size(); i++) {
				Parameter p = this.lstParameter.get(i);
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
			for (int i = 0; i < this.lstVariable.size(); i++) {
				Variable v = this.lstVariable.get(i);
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
			StringBuffer strTestcase = new StringBuffer();

			for (int i = 0; i < testcase.size(); i++) {
				strTestcase.append(testcase.get(i));
				strTestcase.append("\n");
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
					ast = ((IfThenStmtAST) ast).exprAST;
				} else if (ast instanceof IfThenElseStmtAST) {
					ast = ((IfThenElseStmtAST) ast).exprAST;
				} else if (ast instanceof WhileStmtAST) {
					ast = ((WhileStmtAST) ast).exprAST;
				} else if (ast instanceof DoStmtAST) {
					ast = ((DoStmtAST) ast).exprAST;
				} else {
					ast = null;
				}
				if (ast != null) {
					Condition con = new Condition();
					con.setStmtID(ast.line);
					con.setAST(ast);
					String conExp = visitor.print(ast);
					con.setCondition(conExp);
					lstCondition.add(con);
					listConditionExpr.add(conExp);
				}
			}
		}
		return listConditionExpr;
	}

	public ArrayList<Boolean> getFalseList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < this.lstCondition.size(); i++) {
			if (this.lstCondition.get(i).hasFalseTestCase == true) {
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
				for (int i = 0; i < this.lstParameter.size(); i++) {
					for (int j = 0; j < z3result.size(); j += 2) {
						if (z3result.get(j).equals(
								this.lstParameter.get(i).getName())) {
							testcase.add(z3result.get(j + 1)); // add new value
																// to testcase
							break;
						}
					}
					if (testcase.size() < i) {
						Object result = 0;
						Random ran1 = new Random();
						double n = ran1.nextDouble() * 100;
						switch (this.lstParameter.get(i).getType()) {
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
				if (testcase.size() < this.lstParameter.size()) {
					int num = this.lstParameter.size() - testcase.size();
					Random ran1 = new Random();
					for (int i = 0; i < num; i++) {
						Object result = 0;
						double n = ran1.nextDouble() * 100;
						switch (this.lstParameter.get(i).getType()) {
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
		if (currentTestCase < this.lstCondition.size() * 2 - 1)
			currentTestCase++;
		if (currentTestCase % 2 == 0) {
			testcase = this.lstCondition.get(currentTestCase / 2)
					.getTrueTestCase();
		} else {
			testcase = this.lstCondition.get(currentTestCase / 2)
					.getFalseTestCase();
		}
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			result.add(Integer.parseInt(st.nextToken()));
		}
		return result;
	}

	public int getNumParameter() {
		return this.lstParameter.size();
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

	public int getNumUnSolvableCondition() {
		numUnSolvableCondition = 0;
		for (int i = 0; i < this.lstCondition.size(); i++) {
			if (this.lstCondition.get(i).isHasTestcase() == false)
				numUnSolvableCondition++;
		}
		return numUnSolvableCondition;
	}

	public ArrayList<String> getParameterNameList() {
		ArrayList<String> listParaName = new ArrayList<String>();
		for (int i = 0; i < lstParameter.size(); i++) {
			listParaName.add(lstParameter.get(i).getName());
		}
		return listParaName;
	}

	public ArrayList<Integer> getPrevTestCase() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (currentTestCase > 0)
			currentTestCase--;
		if (currentTestCase % 2 == 0) {
			testcase = this.lstCondition.get(currentTestCase / 2)
					.getTrueTestCase();
		} else {
			testcase = this.lstCondition.get(currentTestCase / 2)
					.getFalseTestCase();
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
		for (int i = 0; i < this.lstCondition.size(); i++) {
			if (this.lstCondition.get(i).hasTrueTestCase == true) {
				result.add(true);
			} else {
				result.add(false);
			}
		}
		return result;
	}

	public ArrayList<String> getVarNameList() {
		ArrayList<String> listVarName = new ArrayList<String>();
		for (int i = 0; i < lstVariable.size(); i++) {
			listVarName.add(lstVariable.get(i).getName());
		}
		return listVarName;
	}

	public void loadFile(String strSourceFile) {
		lstCondition = new ArrayList<Condition>();
		transform = new Transform(strSourceFile);
		mapTable = transform.getMapTable();
		astTree = transform.getAstree();
		pdg = transform.getPdg();
		lstParameter = transform.getListParameters();
		lstVariable = transform.getListVariables();
		lstPath = transform.getListPath();
		lstBranch = transform.getListBranch();
	}

	private void printAllData() {
		// Print all parameter
		for (Parameter parameter : this.lstParameter) {
			System.out.println("Parameter: " + " name: " + parameter.getName()
					+ " type: " + parameter.getType());
		}
		System.out.println();

		// Print all variable
		for (Variable variable : this.lstVariable) {
			System.out.println("Variable: " + " name: " + variable.getName()
					+ " type: " + variable.getType());
		}
		System.out.println();

		// Print all condition
		for (Condition condition : this.lstCondition) {
			System.out.println("Condition: " + " name: "
					+ condition.getCondition() + ", " + "True testcase: "
					+ condition.getTrueTestCase() + ", " + "False testcase: "
					+ condition.getFalseTestCase() + ", " + "Has testcase: "
					+ condition.isHasTestcase());
		}
	}

	private void printAllUnsolvableTestCase() {
		System.out.println("\n Print all unsolvable condition");

		for (int i = 0; i < this.lstCondition.size(); i++) {
			Condition condition = this.lstCondition.get(i);
			if (!condition.isHasTestcase()) {
				System.out.print("Condition: " + condition.getCondition());

				BinExprAST binExprAST = (BinExprAST) condition.getAst();
				String expression1 = "";
				String operation = "";
				String expression2 = "";

				try {
					expression1 = (String) binExprAST.exprAST1.visit(
							new Temp1Visitor(lstParameter, lstVariable,
									lstCondition), "c");
					operation = (String) binExprAST.op.getText();
					expression2 = (String) binExprAST.exprAST2.visit(
							new Temp1Visitor(lstParameter, lstVariable,
									lstCondition), "c");
				} catch (CompilationException e1) {
					e1.printStackTrace();
				}

				System.out.print(". Expr1: " + expression1 + ", " + "op: "
						+ operation + ", " + "Expr2: " + expression2);
				System.out.print(", convert to AST: ");
				System.out.println(operation + " " + expression1 + " "
						+ expression2);
			}
		}

	}

	public String scanCondition() {

		String output = "";
		lstCondition = transform.updateConditionList(lstCondition);

		removeNullCondition();

		if (hasCondition()) {
			output = getConditions();
		} else
			output = "Does not have condition";

		printAllData();

		return output;
	}

	private String getConditions() {
		String output = "";
		for (int i = 0; i < lstCondition.size(); i++) {
			output += "Condition " + (i + 1) + ":"
					+ this.lstCondition.get(i).getCondition() + "\n";
			output += "True:\n";
			for (int j = 0; j < lstCondition.get(i).getTruePaths().size(); j++) {
				output += lstCondition.get(i).getTruePaths().get(j) + "\n";
			}
			output += "False:\n";
			for (int j = 0; j < lstCondition.get(i).getFalsePaths().size(); j++) {
				output += lstCondition.get(i).getFalsePaths().get(j) + "\n";
			}
		}
		return output;
	}

	private boolean hasCondition() {
		return lstCondition.size() > 0 ? true : false;
	}

	private void removeNullCondition() {
		int sizeOfListCodition = lstCondition.size();

		for (int j = sizeOfListCodition - 1; j >= 0; j--) {
			if (lstCondition.get(j).getAst() == null) {
				lstCondition.remove(j);
			}
		}

	}

	public String showAllTestCase() {
		String output = "";
		for (int i = 0; i < this.lstCondition.size(); i++) {
			Condition temp = this.lstCondition.get(i);
			output += "Condition " + (i + 1) + ": " + temp.getCondition()
					+ "\n";
			output += "\tTrue: " + temp.getTrueTestCase() + "\t"
					+ (temp.hasTrueTestCase) + "\n";
			output += "\tFalse: " + temp.getFalseTestCase() + "\t"
					+ (temp.hasFalseTestCase) + "\n";
		}
		return output;
	}

	public String update(int[][] res) {
		String output = "";
		int count = 0;
		int numCondition = this.lstCondition.size();
		int numParameter = this.lstParameter.size();

		for (int i = 0; i < numCondition; i++) {
			if (this.lstCondition.get(i).isHasTestcase() == false) {
				int j;
				String truetc = "[";
				String falsetc = "[";

				for (j = 0; j < numParameter; j++) {
					truetc += res[count][j];
					falsetc += res[count + 1][j];
					if (j < numParameter - 1) {
						truetc += ", ";
						falsetc += ", ";
					}
				}
				truetc += "]";
				this.lstCondition.get(i).setTrueTestcase(truetc);

				falsetc += "]";
				this.lstCondition.get(i).setFalseTestcase(falsetc);

				if (res[count][j] == 0) {
					this.lstCondition.get(i).hasTrueTestCase = true;
				} else {
					this.lstCondition.get(i).hasTrueTestCase = false;
				}

				if (res[count + 1][j] == 0) {
					this.lstCondition.get(i).hasFalseTestCase = true;
				} else {
					this.lstCondition.get(i).hasFalseTestCase = false;
				}

				output += "Condition: "
						+ this.lstCondition.get(i).getCondition() + "\n";
				output += "\t True: "
						+ this.lstCondition.get(i).getTrueTestCase() + "\t"
						+ this.lstCondition.get(i).hasTrueTestCase + "\n";
				output += "\t False: "
						+ this.lstCondition.get(i).getFalseTestCase() + "\t"
						+ this.lstCondition.get(i).hasFalseTestCase + "\n";
				count += 2;
			}
		}
		output = "Number of unsolvable condition: " + numUnSolvableCondition
				+ "\n" + output;
		return output;
	}
}
