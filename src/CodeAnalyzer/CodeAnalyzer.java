package CodeAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import se.SymbolicExecutionFile;
import system.*;
import system.Variable;
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

	public CodeAnalyzer() {
	}

	public CodeAnalyzer(String strSourceFile) {

		transform = new Transform(strSourceFile);

		lstCondition = new ArrayList<Condition>();
		mapTable = transform.getMapTable();
		pdg = transform.getPdg();
		astTree = transform.getAstree();
		lstParameter = transform.getListParameters();
		lstVariable = transform.getListVariables();
	}

	public int[] check(int[][] testcase) {
		int numberConditions = lstCondition.size();
		int numberParameters = lstParameter.size();
		int[] result = new int[numUnSolvableCondition * 2];
		int count = 0;

		for (int i = 0; i < numberConditions; i++) {
			if (this.lstCondition.get(i).hasTestcase() == false) {
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
		Temp2Visitor visitor = new Temp2Visitor(lstParameter, lstVariable,
				testcase);
		try {
			boolean check;
			if (branch == 0) {
				for (int i = 0; i < lstCondition.get(con).getTrueConditions()
						.size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = lstPath.get(lstCondition.get(con)
							.getTrueConditions().get(i));
					ArrayList<Integer> lbranch = lstBranch.get(lstCondition
							.get(con).getTrueConditions().get(i));
					for (j = 0; path.get(j).line < lstCondition.get(con)
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
				for (int i = 0; i < lstCondition.get(con).getFalseConditions()
						.size(); i++) {
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = lstPath.get(lstCondition.get(con)
							.getFalseConditions().get(i));
					ArrayList<Integer> lbranch = lstBranch.get(lstCondition
							.get(con).getFalseConditions().get(i));
					for (j = 0; path.get(j).line < lstCondition.get(con)
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
		String conditionString = "";

		for (int j = 0; j < lstCondition.get(i).getTruePaths().size(); j++) {
			conditionString = lstCondition.get(i).getTruePaths().get(j);
			// String result = generateTestCase(conditionString);
			ArrayList<String> result = generateTestCase(conditionString);
			if (result.size() > 0) {
				if (!result.get(0).equals("")) {
					lstCondition.get(i).setTrueTestcase(result.get(0));
					// 20121213
					lstCondition.get(i).setTrueTestcaseFull(result.get(1));
					lstCondition.get(i).hasTrueTestCase = true;
					check = true;
					break;
				}
			}
		}

		for (int k = 0; k < lstCondition.get(i).getFalsePaths().size(); k++) {
			conditionString = (lstCondition.get(i).getFalsePaths().get(k));
			// String result = generateTestCase(conditionString);
			ArrayList<String> result = generateTestCase(conditionString);
			if (result.size() > 0) {
				if (!result.get(0).equals("")) {

					lstCondition.get(i).setFalseTestcase(result.get(0));
					// 20121213
					lstCondition.get(i).setFalseTestcaseFull(result.get(1));
					lstCondition.get(i).hasFalseTestCase = true;
					check = true;
					break;
				}
			}
		}
		lstCondition.get(i).setHasTestcase(check);
	}

	public String generateSolvable() {
		int count = 0;
		String output = "";

		// Generate testcase by Z3 for all condition in list
		for (int i = 0; i < lstCondition.size(); i++) {
			generateNextTestCase(i);
		}

		for (int i = 0; i < lstCondition.size(); i++) {
			Condition condition = lstCondition.get(i);
			// If Z3 can solve this condition
			if (condition.hasTestcase()) {
				count++;
				output += "Condition: " + condition.getCondition() + "\n";
				// output += "\t True: " + condition.getTrueTestCase() + "\n";
				// 20121213
				output += "\t True: " + condition.getTrueTestCase() + " ("
						+ condition.getTrueTestcaseFull() + ")" + "\n";
				// output += "\t False: " + condition.getFalseTestCase() + "\n";
				// 20121213
				output += "\t False: " + condition.getFalseTestCase() + " ("
						+ condition.getFalseTestcaseFull() + ")" + "\n";
			}
		}
		output = count + "\n\n" + output;
		return output;
	}

	// public String generateTestCase(String condition) { //20121213
	public ArrayList<String> generateTestCase(String condition) {
		String z3FilePath = SymbolicExecutionFile.getAbsolutePathOfSmt2()
				+ "Z3Formula.smt2";

		try {
			FileWriter fw = new FileWriter(z3FilePath);
			BufferedWriter out = new BufferedWriter(fw);

			// Print the parameters of program
			for (int i = 0; i < lstParameter.size(); i++) {
				Parameter parameter = lstParameter.get(i);
				out.write("(declare-const ");
				out.write(parameter.getName() + " ");

				switch (parameter.getType()) {
				case "Int":
					out.write(parameter.getType() + ")");
					break;
				case "Real":
					out.write("Real)");
					break;
				case "Float":
					out.write("Real)");
					break;
				case "Double":
					out.write("Real)");
					break;

				}
				out.write("\n");
			}

			// Print variables of program
			for (int i = 0; i < lstVariable.size(); i++) {
				Variable v = lstVariable.get(i);
				out.write("(declare-const ");
				out.write(v.getName() + " ");

				switch (v.getType()) {
				case "Int":
					out.write(v.getType() + ")");
					break;
				case "Real":
					out.write("Real)");
					break;
				case "Float":
					out.write("Real)");
					break;
				case "Double":
					out.write("Real)");
					break;
				}
				out.write("\n");
			}

			out.write(condition);
			out.write("(check-sat)\n");
			out.write("(model)\n");

			out.close();
			fw.close();
		} catch (IOException | NullPointerException exception) {
			exception.printStackTrace();
		}

		ArrayList<ArrayList<String>> arr = getNewTestcase(z3FilePath);

		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> testcase = null;
		ArrayList<String> testcaseFull = null;

		if (arr != null) {
			testcase = arr.get(0);
			testcaseFull = arr.get(1); // 20121213
		}

		if (testcase != null) {
			ArrayList<String> temp = new ArrayList<String>();
			StringBuffer strTestcase = new StringBuffer();

			for (int i = 0; i < testcase.size(); i++) {
				strTestcase.append(testcase.get(i));
				strTestcase.append("\n");
				temp.add(testcase.get(i));
			}
			result.add(temp.toString());

			// 20121213
			temp = new ArrayList<String>();
			strTestcase = new StringBuffer();

			for (int i = 0; i < testcaseFull.size(); i++) {
				strTestcase.append(testcaseFull.get(i));
				strTestcase.append("\n");
				temp.add(testcaseFull.get(i));
			}
			result.add(temp.toString());

			// return temp.toString();
			return result;
		} else
			return result;
	}

	public String generateTestCaseAfterSE(String conditionString) {

		String z3FilePath = SymbolicExecutionFile.getAbsolutePathOfSmt2()
				+ "Z3Formula.smt2";

		try {
			FileWriter fw = new FileWriter(z3FilePath);
			BufferedWriter out = new BufferedWriter(fw);

			// Print the parameters of program
			for (int i = 0; i < lstParameter.size(); i++) {
				Parameter parameter = lstParameter.get(i);
				out.write("(declare-const ");
				out.write(parameter.getName() + " ");

				switch (parameter.getType()) {
				case "Int":
					out.write(parameter.getType() + ")");
					break;
				case "Real":
					out.write("Real)");
					break;
				case "Float":
					out.write("Real)");
					break;
				case "Double":
					out.write("Real)");
					break;

				}
				out.write("\n");
			}

			// Print variables of program
			for (int i = 0; i < lstVariable.size(); i++) {
				Variable v = lstVariable.get(i);
				out.write("(declare-const ");
				out.write(v.getName() + " ");

				switch (v.getType()) {
				case "Int":
					out.write(v.getType() + ")");
					break;
				case "Real":
					out.write("Real)");
					break;
				case "Float":
					out.write("Real)");
					break;
				case "Double":
					out.write("Real)");
					break;
				}
				out.write("\n");
			}

			out.write(conditionString);
			out.write("(check-sat)\n");
			out.write("(model)\n");

			out.close();
			fw.close();
		} catch (IOException | NullPointerException exception) {
			exception.printStackTrace();
		}

		ArrayList<String> testcase = getNewTestcaseAfterSE(z3FilePath);
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

		for (int i = 0; i < mapTable.size(); i++) {
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

	private String getConditions() {
		String output = "";
		for (int i = 0; i < lstCondition.size(); i++) {
			output += "\nCondition " + (i + 1) + ":"
					+ lstCondition.get(i).getCondition() + "\n";

			output += "\t True:";

			for (int j = 0; j < lstCondition.get(i).getTruePaths().size(); j++) {
				output += "\n" + "\t\t" + lstCondition.get(i).getTruePaths().get(j);
			}

			output += "\n \t False:";

			for (int j = 0; j < lstCondition.get(i).getFalsePaths().size(); j++) {
				output += "\n" + "\t\t" + lstCondition.get(i).getFalsePaths().get(j);
			}
		}
		return output;
	}

	public ArrayList<Boolean> getFalseList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < lstCondition.size(); i++) {
			if (lstCondition.get(i).hasFalseTestCase == true) {
				result.add(true);
			} else {
				result.add(false);
			}
		}
		return result;
	}

	public ArrayList<ArrayList<Integer>> getLstBranch() {
		return lstBranch;
	}

	public ArrayList<Condition> getLstCondition() {
		return lstCondition;
	}

	public ArrayList<Parameter> getLstParameter() {
		return lstParameter;
	}

	public ArrayList<ArrayList<AST>> getLstPath() {
		return lstPath;
	}

	public ArrayList<Variable> getLstVariable() {
		for (int i = 0; i < lstVariable.size() - 1; i++)
			for (int j = i + 1; j < lstVariable.size(); j++)
				if (lstVariable.get(i).getName()
						.equals(lstVariable.get(j).getName()))
					lstVariable.remove(j);

		return lstVariable;
	}

	public ArrayList<ArrayList<String>> getNewTestcase(String z3FormulaPath) {
		// public ArrayList<String> getNewTestcase(String z3FormulaPath) {
		ArrayList<String> testcaseFull = new ArrayList<String>();

		ArrayList<String> testcase = new ArrayList<String>();
		ArrayList<String> z3result = new ArrayList<String>();
		Runtime run = Runtime.getRuntime();
		try {
			String runZ3 = "./Z3/z3.exe";
			String config = " /m ";

			Process pp = run.exec(runZ3 + config + z3FormulaPath);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					pp.getInputStream()));
			String line = in.readLine();

			if (line.contains("sat") && !line.contains("unsat")) {
				while ((line = in.readLine()) != null) {
					if (line.contains("define")) {
						String sub;
						if (!line.contains(")\")")) {
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
				for (int i = 0; i < lstParameter.size(); i++) {
					for (int j = 0; j < z3result.size(); j += 2) {
						if (z3result.get(j).equals(
								lstParameter.get(i).getName())) {
							testcase.add(z3result.get(j + 1));

							// add new value to testcase, sua ngay 20121213 de
							// hien thi ro hon ket qua test case sinh ra
							testcaseFull.add(z3result.get(j) + " = "
									+ z3result.get(j + 1));
							break;
						}
					}
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// return testcase;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		result.add(testcase);
		result.add(testcaseFull);
		return result;
	}

	public ArrayList<String> getNewTestcaseAfterSE(String z3FormulaPath) {
		ArrayList<String> z3result = new ArrayList<String>();
		Runtime run = Runtime.getRuntime();
		try {
			String runZ3 = "./Z3/z3.exe";
			String config = " /m ";

			Process pp = run.exec(runZ3 + config + z3FormulaPath);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					pp.getInputStream()));
			String line = in.readLine();

			if (line.contains("sat") && !line.contains("unsat")) {
				while ((line = in.readLine()) != null) {
					if (line.contains("define")) {
						String sub;
						if (!line.contains(")\")")) {
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
			} else {
				// <unsatisfied>
				z3result.add("<unsatisfied>");
				z3result.add("<unsatisfied>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return z3result;
	}

	public ArrayList<Integer> getNextTestCase() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (currentTestCase < lstCondition.size() * 2 - 1)
			currentTestCase++;
		if (currentTestCase % 2 == 0) {
			testcase = lstCondition.get(currentTestCase / 2).getTrueTestCase();
		} else {
			testcase = lstCondition.get(currentTestCase / 2).getFalseTestCase();
		}
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			result.add(Integer.parseInt(st.nextToken()));
		}
		return result;
	}

	public int getNumParameter() {
		return lstParameter.size();
	}

	public int getNumUnSolvableCondition() {
		numUnSolvableCondition = 0;
		for (int i = 0; i < lstCondition.size(); i++) {
			if (this.lstCondition.get(i).hasTestcase() == false)
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
			testcase = lstCondition.get(currentTestCase / 2).getTrueTestCase();
		} else {
			testcase = lstCondition.get(currentTestCase / 2).getFalseTestCase();
		}
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			int temp = Integer.parseInt(st.nextToken());
			result.add(temp);
		}
		return result;
	}

	// private void printSMT2(String z3FormulaFilename, Path pathCondition)
	// throws CompilationException {
	// // Print the parameters, variables, and reindexed variables
	// try {
	// FileWriter fw = new FileWriter(z3FormulaFilename);
	// BufferedWriter out = new BufferedWriter(fw);
	//
	// // Print the parameters of program
	// for (int i = 0; i < this.lstParameter.size(); i++) {
	// Parameter p = this.lstParameter.get(i);
	// out.write("(declare-const ");
	// out.write(p.getName() + " ");
	// out.write(p.getType() + ")");
	// out.write("\n");
	// }
	// // Print variables of program
	// for (int i = 0; i < this.lstVariable.size(); i++) {
	// Variable v = this.lstVariable.get(i);
	// out.write("(declare-const ");
	// out.write(v.getName() + " ");
	// out.write(v.getType() + ")");
	// out.write("\n");
	// }
	// // Print variables after reindexing
	// for (int i = 0; i < pathCondition.getListVariableReIndexed().size(); i++)
	// {
	// Variable v = pathCondition.getListVariableReIndexed().get(i);
	// out.write("(declare-const ");
	// out.write(v.getName() + " ");
	// out.write(v.getType() + ")");
	// out.write("\n");
	// }
	// out.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// // Print the negated path condition in the SMT2 form Z3PathPrintVisitor
	// Z3PathPrintVisitor z3Visitor = new Z3PathPrintVisitor(
	// z3FormulaFilename, false);
	// z3Visitor.printSMT2(pathCondition);
	// }

	public ArrayList<Integer> getSlide() {
		ArrayList<String> input = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(testcase, "[, ]");
		while (st.hasMoreTokens()) {
			input.add(st.nextToken());
		}
		AstSimulationVisitor simulationAST = new AstSimulationVisitor(this.pdg,
				input);
		try {
			this.astTree.visit(simulationAST, "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ExecutionHistory executionHistory = simulationAST.getExecutionHistory();
		executionHistory.changeLineIdAtExecNodePointToNode(pdg);
		ArrayList<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < executionHistory.size(); i++) {
			result.add(executionHistory.get(i).getNode().getID());
		}
		return result;
	}

	public String getStandardSource(String filename) {
		this.loadFile(filename);
		return transform.getStandardSourceFile();
	}

	public ArrayList<Boolean> getTrueList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for (int i = 0; i < lstCondition.size(); i++) {
			if (lstCondition.get(i).hasTrueTestCase == true) {
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

	private boolean hasCondition() {
		return lstCondition.size() > 0 ? true : false;
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

	private void removeNullCondition() {
		int sizeOfListCodition = lstCondition.size();

		for (int j = sizeOfListCodition - 1; j >= 0; j--) {
			if (lstCondition.get(j).getAst() == null) {
				lstCondition.remove(j);
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

		return output;
	}

	public void setLstBranch(ArrayList<ArrayList<Integer>> lstBranch) {
		this.lstBranch = lstBranch;
	}

	public void setLstCondition(ArrayList<Condition> lstCondition) {
		this.lstCondition = lstCondition;
	}

	public void setLstParameter(ArrayList<Parameter> lstParameter) {
		this.lstParameter = lstParameter;
	}

	public void setLstPath(ArrayList<ArrayList<AST>> lstPath) {
		this.lstPath = lstPath;
	}

	public void setLstVariable(ArrayList<Variable> lstVariable) {
		this.lstVariable = lstVariable;
	}

	public String showAllTestCase() {
		String output = "";
		for (int i = 0; i < lstCondition.size(); i++) {
			Condition condition = lstCondition.get(i);
			output += "Condition " + (i + 1) + ": " + condition.getCondition()
					+ "\n";
			output += "\tTrue: " + condition.getTrueTestCase() + "\n";
			output += "\tFalse: " + condition.getFalseTestCase() + "\n";
		}
		return output;

	}

	public String update(int[][] res) {
		String output = "";
		int count = 0;
		int numCondition = lstCondition.size();
		int numParameter = lstParameter.size();

		for (int i = 0; i < numCondition; i++) {
			if (lstCondition.get(i).hasTestcase() == false) {
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
				lstCondition.get(i).setTrueTestcase(truetc);

				falsetc += "]";
				lstCondition.get(i).setFalseTestcase(falsetc);

				if (res[count][j] == 0) {
					lstCondition.get(i).hasTrueTestCase = true;
				} else {
					lstCondition.get(i).hasTrueTestCase = false;
				}

				if (res[count + 1][j] == 0) {
					lstCondition.get(i).hasFalseTestCase = true;
				} else {
					lstCondition.get(i).hasFalseTestCase = false;
				}

				output += "Condition: " + lstCondition.get(i).getCondition()
						+ "\n";
				output += "\t True: " + lstCondition.get(i).getTrueTestCase()
						+ "\t" + lstCondition.get(i).hasTrueTestCase + "\n";
				output += "\t False: " + lstCondition.get(i).getFalseTestCase()
						+ "\t" + lstCondition.get(i).hasFalseTestCase + "\n";
				count += 2;
			}
		}
		output = "Number of unsolvable condition: " + numUnSolvableCondition
				+ "\n" + output;
		return output;
	}
}
