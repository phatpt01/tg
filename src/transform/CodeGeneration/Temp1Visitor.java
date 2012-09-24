package transform.CodeGeneration;

import java.util.ArrayList;

import system.Condition;
import system.Parameter;
import system.Variable;
import system.Temp;
import transform.AST.BinExprAST;
import transform.AST.CallExprAST;
import transform.AST.CompilationException;
import transform.AST.DeclarationListAST;
import transform.AST.DeclarationStmtAST;
import transform.AST.EmptyDeclarationListAST;
import transform.AST.EmptyExprListAST;
import transform.AST.ExprListAST;
import transform.AST.ExprStmtAST;
import transform.AST.IntLiteralAST;
import transform.AST.ProgramAST;
import transform.AST.TernaryExprAST;
import transform.AST.UnaryExprAST;
import transform.AST.VarDeclAST;
import transform.AST.VarExprAST;
import transform.AST.VarInitializerAST;

public class Temp1Visitor extends DoNothingVisitor {

	boolean hasReal;
	
	ArrayList<Variable> lstVariable;
	ArrayList<Parameter> lstParameter;
	ArrayList<String> arrParameter; // lưu giá trị mới của tham số đầu vào sau
									// một phép gán
	ArrayList<String> arrVariable; // lưu giá trị mới của 1 biến nội bộ sau một
									// phép gán
	ArrayList<Condition> lstCondition;
	ArrayList<Integer> arrConditionline;
	ArrayList<Integer> arrBranch;
	String result;

	public Temp1Visitor(ArrayList<Parameter> lstParameter,
			ArrayList<Variable> lstVariable, ArrayList<Condition> lstCondition) {

		this.lstVariable = lstVariable;
		this.lstParameter = lstParameter;
		this.lstCondition = lstCondition;
		arrParameter = new ArrayList<String>();
		arrVariable = new ArrayList<String>();
		arrBranch = new ArrayList<Integer>();

		result = "";
		arrParameter.clear();
		arrVariable.clear();

		for (int i = 0; i < lstVariable.size(); i++)
			arrVariable.add("");
		for (int j = 0; j < lstParameter.size(); j++)
			arrParameter.add("");
	}

	public void clear() {
		result = "";
		arrParameter.clear();
		arrVariable.clear();
		for (int i = 0; i < lstVariable.size(); i++)
			arrVariable.add("");
		for (int j = 0; j < lstParameter.size(); j++)
			arrParameter.add("");
	}

	private int findCondition(int line) {
		for (int i = 0; i < this.lstCondition.size(); i++) {
			if ((lstCondition.get(i).getStmtID() == line)) {
				return i;
			}
		}
		Condition condition = new Condition();
		condition.setStmtID(line);
		this.lstCondition.add(condition);

		return this.lstCondition.size() - 1;
	}

	private int findParameter(String paraName) {
		if (this.lstParameter.size() <= 0) {
			return -1;
		} else {
			for (int i = 0; i < this.lstParameter.size(); i++) {
				if (paraName.equals(this.lstParameter.get(i).getName())) {
					return i;
				}
			}
			return -1;
		}
	}

	// get the index of the varName in varReindex
	private int findVariable(String variableName) {
		if (this.lstVariable.size() <= 0) {
			return -1;
		} else {
			for (int i = 0; i < this.lstVariable.size(); i++) {
				if (variableName.equals(this.lstVariable.get(i).getName())) {
					return i;
				}
			}
			return -1;
		}
	}

	public ArrayList<Condition> getListCondition() {
		return this.lstCondition;
	}

	// BinExprAST
	@Override
	public Object visitBinExprAST(BinExprAST binExprAST, Object o)
			throws CompilationException {
		if ((binExprAST.opType == BinExprAST.ASSIGN)) {
			String var = (String) binExprAST.exprAST1.visit(this, o);
			String temp = "";

			int i = findVariable(var);
			if (i >= 0) {
				temp = (String) binExprAST.exprAST2.visit(this, "c");
				arrVariable.set(i, temp);
			} else {
				i = findParameter(var);
				if (i >= 0) {
					temp = (String) binExprAST.exprAST2.visit(this, "c");
					arrParameter.set(i, temp);
				}
			}
			return "";
		} else {
			String output = "";

			// Left expression
			String val1 = (String) binExprAST.exprAST1.visit(this, "c");
			// Right expression
			String val2 = (String) binExprAST.exprAST2.visit(this, "c");

			if (val1.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")
					&& val2.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {

				int temp = Integer.parseInt(val1);
				switch (binExprAST.opType) {
				case BinExprAST.PLUS:
					temp += Integer.parseInt(val2);
					break;
				case BinExprAST.MINUS:
					temp -= Integer.parseInt(val2);
					break;
				case BinExprAST.MULTIP:
					temp *= Integer.parseInt(val2);
					break;
				case BinExprAST.DIV:
					temp /= Integer.parseInt(val2);
					break;
				case BinExprAST.MOD:
					temp %= Integer.parseInt(val2);
					break;
				}
				output += temp;
			} else {
				boolean check = false;
				output += "(";
				if ((binExprAST.opType >= 24) && (binExprAST.opType <= 28)) { // +,
																				// -,
																				// *,
																				// /,
																				// %
					if (binExprAST.op.getText().equals("/"))
						output += "div";
					else if (binExprAST.op.getText().equals("%"))
						output += "mod";
					else
						output += binExprAST.op.getText();
				} else {
					if (binExprAST.op.getText().equals("||"))
						output += "or";
					else if (binExprAST.op.getText().equals("&&"))
						output += "and";
					else if (binExprAST.op.getText().equals("=="))
						output += "=";
					else if (binExprAST.op.getText().equals("!=")) {
						check = true;
						output += "=";
					} else if (binExprAST.op.getText().equals("!")) {
						check = true;
					} else
						output += binExprAST.op.getText();
				}

				output += " " + val1 + " ";
				output += val2 + ")";
				if (check == true) {
					output = "(not " + output + ")";
				}
			}
			if (o == "c") {
				return output;
			} else {
				Temp obj = (Temp) o;
				int branch = obj.branch;

				if (!output.equals("(  )")) {
					int conditionStmt = findCondition(binExprAST.parentAST.line);
					System.out.println("Parent line: "
							+ "binExprAST.parentAST.line: " + binExprAST.parentAST.line + " , findCondition(binExprAST.parentAST.line): " + conditionStmt);

					boolean check = true;

					if (branch == 0) { // BRANCH TRUE
						result += "(assert " + output + ")\n";
						ArrayList<String> temp = lstCondition.get(conditionStmt)
								.getTruePaths();

						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;

						if (check == true) {
							lstCondition.get(conditionStmt).getTruePaths()
									.add(result);
							lstCondition.get(conditionStmt).getTrueConditions()
									.add(obj.con);
						}
					}

					if (branch == 1) { // BRANCH FALSE
						result += "(assert (not " + output + "))\n";
						ArrayList<String> temp = lstCondition.get(conditionStmt)
								.getFalsePaths();

						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;

						if (check == true) {
							lstCondition.get(conditionStmt).getFalsePaths()
									.add(result);
							lstCondition.get(conditionStmt).getFalseConditions()
									.add(obj.con);
						}
					}
				}
				return "";
			}
		}
	}

	@Override
	public Object visitCallExprAST(CallExprAST callExprAST, Object o)
			throws CompilationException {
		String text = "(" + callExprAST.op.getText() + " "
				+ callExprAST.exprListAST.visit(this, "c") + ")";
		return text;
	}

	@Override
	public Object visitDeclarationListAST(
			DeclarationListAST declarationListAST, Object o)
			throws CompilationException {

		String var;
		String value;

		var = (String) declarationListAST.declarationAST.visit(this, o);

		if (declarationListAST.declarationListAST instanceof EmptyDeclarationListAST) {
			value = (String) declarationListAST.declarationAST.visit(this, "c");
		} else {
			value = (String) declarationListAST.declarationListAST.visit(this,
					o);
			
			int i = findVariable(var);
			if (i >= 0) {
				this.arrVariable.set(i, value);
			} else {
				i = this.findParameter(var);
				value = (String) declarationListAST.declarationListAST.visit(
						this, o);
				this.arrParameter.set(i, value);
			}
		}
		return "";
	}

	@Override
	public Object visitDeclarationStmtAST(
			DeclarationStmtAST declarationStmtAST, Object o)
			throws CompilationException {
		declarationStmtAST.declarationListAST.visit(this, "c");
		return "";
	}

	@Override
	public Object visitEmptyExprListAST(EmptyExprListAST ast, Object o)
			throws CompilationException {
		return "";
	}

	@Override
	public Object visitExprListAST(ExprListAST ast, Object o)
			throws CompilationException {

		String text = (String) ast.exprAST.visit(this, "c");
		
		if (!(ast.exprListAST instanceof EmptyExprListAST)) {
			text += " " + ast.exprListAST.visit(this, "c");
		}
		return text;
	}

	// ExprStmtAST
	@Override
	public Object visitExprStmtAST(ExprStmtAST ast, Object o)
			throws CompilationException {
		if (!(ast.exprAST instanceof TernaryExprAST)) {
			// ast.line = this.line;
			// this.em.setFilter(true);
		}
		ast.exprAST.visit(this, o);
		return "";
	}

	@Override
	public Object visitIntLiteralAST(IntLiteralAST ast, Object o)
			throws CompilationException {
		return ast.literalToken.getText();
	}

	// ProgramAST
	@Override
	public Object visitProgramAST(ProgramAST ast, Object o)
			throws CompilationException {
		return "";
	}

	// UnaryExprAST
	@Override
	public Object visitUnaryExprAST(UnaryExprAST ast, Object o)
			throws CompilationException {

		int constmt = findCondition(ast.parentAST.line);
		System.out.println("Parent line " + ast.parentAST.line + " " + constmt);
		
		String res = "";
		
		if (ast.opType == UnaryExprAST.LOGICAL_NOT) {
			if (o == "c") {
				res += "(not " + ast.exprAST.visit(this, "c") + ")";
				return res;
			} else {
				boolean check = true;
				Temp obj = (Temp) o;
				
				int branch = obj.branch;
				if (branch == 0) {
					res += "(assert (not " + ast.exprAST.visit(this, "c")
							+ "))\n";
					ArrayList<String> temp = lstCondition.get(constmt)
							.getTruePaths();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						lstCondition.get(constmt).getTruePaths().add(res);
						lstCondition.get(constmt).getTrueConditions()
								.add(obj.con);
					}
				} else {
					res += "(assert " + ast.exprAST.visit(this, "c") + ")\n";
					ArrayList<String> temp = lstCondition.get(constmt)
							.getFalsePaths();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						lstCondition.get(constmt).getFalsePaths().add(res);
						lstCondition.get(constmt).getFalseConditions()
								.add(obj.con);
					}
				}
				return "";
			}
		}
		return "";
	}

	@Override
	public Object visitVarDeclAST(VarDeclAST ast, Object o)
			throws CompilationException {
		String temp = "";
		int i = this.findVariable(ast.op.getText());
		if (i >= 0) {
			temp = (String) ast.init.visit(this, o);
			if (temp != "")
				this.arrVariable.set(i, temp);
		} else {
			i = this.findParameter(ast.op.toString());
			if (i >= 0) {
				temp = (String) ast.init.visit(this, o);
				if (temp != "")
					this.arrParameter.set(i, temp);
			}
		}
		if (o == "c") {
			return temp;
		} else {
			return ast.op.getText();
		}
	}

	// VarExprAST
	@Override
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		String value = ast.op.getText();
		String val = "";
		int i = this.findVariable(ast.op.getText());
		if (i >= 0) {
			if (this.arrVariable.get(i) != "")
				val = this.arrVariable.get(i);
			else
				val = value;
		} else {
			i = this.findParameter(ast.op.getText());
			if (i >= 0) {
				if (this.arrParameter.get(i) != "")
					val = this.arrParameter.get(i);
				else
					val = value;
			}
		}
		if (o == "c")
			return val;
		return value;
	}

	@Override
	public Object visitVarInitializerAST(VarInitializerAST ast, Object o)
			throws CompilationException {
		return (String) ast.exprAST.visit(this, o);
	}
}
