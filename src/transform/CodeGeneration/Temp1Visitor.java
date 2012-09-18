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

	ArrayList<Variable> lstVariable;
	ArrayList<Parameter> lstParameter;
	ArrayList<String> arrParameter; // lưu giá trị mới của tham số đầu vào sau
									// một phép gán
	ArrayList<String> arrVariable; // lưu giá trị mới của 1 biến nội bộ sau một
									// phép gán
	ArrayList<Condition> arrCondition;
	ArrayList<Integer> arrConditionline;
	ArrayList<Integer> arrBranch;
	String result;

	public Temp1Visitor(ArrayList<Parameter> lstParameter,
			ArrayList<Variable> lstVariable, ArrayList<Condition> lstCondition) {

		this.lstVariable = lstVariable;
		this.lstParameter = lstParameter;
		this.arrCondition = lstCondition;
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

	private int findCon(int line) {
		for (int i = 0; i < this.arrCondition.size(); i++) {
			if ((this.arrCondition.get(i).getStmtID() == line)) {
				return i;
			}
		}
		Condition temp = new Condition();
		temp.setStmtID(line);
		this.arrCondition.add(temp);
		return this.arrCondition.size() - 1;
	}

	private int findPara(String paraName) {
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
	private int findVar(String varName) {
		if (this.lstVariable.size() <= 0) {
			return -1;
		} else {
			for (int i = 0; i < this.lstVariable.size(); i++) {
				if (varName.equals(this.lstVariable.get(i).getName())) {
					return i;
				}
			}
			return -1;
		}
	}

	public ArrayList<Condition> getArrayCondition() {
		return this.arrCondition;
	}

	// BinExprAST
	@Override
	public Object visitBinExprAST(BinExprAST binExprAST, Object o)
			throws CompilationException {
		if ((binExprAST.opType == BinExprAST.ASSIGN)) {
			String var = (String) binExprAST.exprAST1.visit(this, o);
			String temp = "";
			int i = this.findVar(var);
			if (i >= 0) {
				temp = (String) binExprAST.exprAST2.visit(this, "c");
				this.arrVariable.set(i, temp);
			} else {
				i = this.findPara(var);
				if (i >= 0) {
					temp = (String) binExprAST.exprAST2.visit(this, "c");
					this.arrParameter.set(i, temp);
				}
			}
			return "";
		} else {
			String output = "";

			String val1 = (String) binExprAST.exprAST1.visit(this, "c");
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
				case BinExprAST.STAR:
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
				if ((binExprAST.opType >= 24) && (binExprAST.opType <= 28)) {
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
					int constmt = findCon(binExprAST.parentAST.line);
					System.out.println("Parent line1 " + binExprAST.parentAST.line
							+ " " + constmt);
					boolean check = true;
					if (branch == 0) {
						result += "(assert " + output + ")\n";
						ArrayList<String> temp = arrCondition.get(constmt)
								.getTruepath();
						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;
						if (check == true) {
							arrCondition.get(constmt).getTruepath().add(result);
							arrCondition.get(constmt).getTruecon().add(obj.con);
						}
					}
					if (branch == 1) {
						result += "(assert (not " + output + "))\n";
						ArrayList<String> temp = arrCondition.get(constmt)
								.getFalsepath();
						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;
						if (check == true) {
							arrCondition.get(constmt).getFalsepath()
									.add(result);
							arrCondition.get(constmt).getFalsecon()
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
			int i = this.findVar(var);
			if (i >= 0) {
				this.arrVariable.set(i, value);
			} else {
				i = this.findPara(var);
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
		String text = (String) ast.e.visit(this, "c");
		if (!(ast.l instanceof EmptyExprListAST)) {
			text += " " + ast.l.visit(this, "c");
		}
		return text;
	}

	// ExprStmtAST
	@Override
	public Object visitExprStmtAST(ExprStmtAST ast, Object o)
			throws CompilationException {
		if (!(ast.e instanceof TernaryExprAST)) {
			// ast.line = this.line;
			// this.em.setFilter(true);
		}
		ast.e.visit(this, o);
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

		int constmt = findCon(ast.parentAST.line);
		System.out.println("Parent line " + ast.parentAST.line + " " + constmt);
		String res = "";
		if (ast.opType == UnaryExprAST.LOGICAL_NOT) {
			if (o == "c") {
				res += "(not " + ast.e.visit(this, "c") + ")";
				return res;
			} else {
				boolean check = true;
				Temp obj = (Temp) o;
				int branch = obj.branch;
				if (branch == 0) {
					res += "(assert (not " + ast.e.visit(this, "c") + "))\n";
					ArrayList<String> temp = arrCondition.get(constmt)
							.getTruepath();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						arrCondition.get(constmt).getTruepath().add(res);
						arrCondition.get(constmt).getTruecon().add(obj.con);
					}
				} else {
					res += "(assert " + ast.e.visit(this, "c") + ")\n";
					ArrayList<String> temp = arrCondition.get(constmt)
							.getFalsepath();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						arrCondition.get(constmt).getFalsepath().add(res);
						arrCondition.get(constmt).getFalsecon().add(obj.con);
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
		int i = this.findVar(ast.id.getText());
		if (i >= 0) {
			temp = (String) ast.init.visit(this, o);
			if (temp != "")
				this.arrVariable.set(i, temp);
		} else {
			i = this.findPara(ast.id.toString());
			if (i >= 0) {
				temp = (String) ast.init.visit(this, o);
				if (temp != "")
					this.arrParameter.set(i, temp);
			}
		}
		if (o == "c") {
			return temp;
		} else {
			return ast.id.getText();
		}
	}

	// VarExprAST
	@Override
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		String value = ast.name.getText();
		String val = "";
		int i = this.findVar(ast.name.getText());
		if (i >= 0) {
			if (this.arrVariable.get(i) != "")
				val = this.arrVariable.get(i);
			else
				val = value;
		} else {
			i = this.findPara(ast.name.getText());
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
		return (String) ast.e.visit(this, o);
	}
}
