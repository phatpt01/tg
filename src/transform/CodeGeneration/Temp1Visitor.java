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
	ArrayList<String> para; // lưu giá trị mới của tham số đầu vào sau một phép gán
	ArrayList<String> var; // lưu giá trị mới của 1 biến nội bộ sau một phép gán
	ArrayList<Condition> con;
	ArrayList<Integer> conditionline;
	ArrayList<Integer> branch;
	String result;

	public Temp1Visitor(ArrayList<Parameter> listPara,
			ArrayList<Variable> listVar, ArrayList<Condition> listCon) {
		this.lstVariable = listVar;
		this.lstParameter = listPara;
		this.con = listCon;
		para = new ArrayList<String>();
		var = new ArrayList<String>();
		branch = new ArrayList<Integer>();
		result = "";
		para.clear();
		var.clear();
		for (int i = 0; i < listVar.size(); i++)
			var.add("");
		for (int j = 0; j < listPara.size(); j++)
			para.add("");
	}

	public void clear() {
		result = "";
		para.clear();
		var.clear();
		for (int i = 0; i < lstVariable.size(); i++)
			var.add("");
		for (int j = 0; j < lstParameter.size(); j++)
			para.add("");
	}

	private int findCon(int line) {
		for (int i = 0; i < this.con.size(); i++) {
			if ((this.con.get(i).getStmtID() == line)) {
				return i;
			}
		}
		Condition temp = new Condition();
		temp.setStmtID(line);
		this.con.add(temp);
		return this.con.size() - 1;
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

	public ArrayList<Condition> getCon() {
		return this.con;
	}

	// BinExprAST
	@Override
	public Object visitBinExprAST(BinExprAST ast, Object o)
			throws CompilationException {
		if ((ast.opType == BinExprAST.ASSIGN)) { // && (ast.parent instanceof
													// ExprStmtAST)) {

			String var = (String) ast.e1.visit(this, o);
			String temp = "";
			int i = this.findVar(var);
			if (i >= 0) {
				temp = (String) ast.e2.visit(this, "c");
				this.var.set(i, temp);
			} else {
				i = this.findPara(var);
				if (i >= 0) {
					temp = (String) ast.e2.visit(this, "c");
					this.para.set(i, temp);
				}
			}
			return "";
		} else {
			String output = "";

			String val1 = (String) ast.e1.visit(this, "c");
			String val2 = (String) ast.e2.visit(this, "c");
			if (val1.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")
					&& val2.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
				int temp = Integer.parseInt(val1);
				switch (ast.opType) {
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
				if ((ast.opType >= 24) && (ast.opType <= 28)) {
					if (ast.op.getText().equals("/"))
						output += "div";
					else if (ast.op.getText().equals("%"))
						output += "mod";
					else
						output += ast.op.getText();
				} else {
					if (ast.op.getText().equals("||"))
						output += "or";
					else if (ast.op.getText().equals("&&"))
						output += "and";
					else if (ast.op.getText().equals("=="))
						output += "=";
					else if (ast.op.getText().equals("!=")) {
						check = true;
						output += "=";
					} else if (ast.op.getText().equals("!")) {
						check = true;
					} else
						output += ast.op.getText();
				}
				/*
				 * switch(ast.opType) { case BinExprAST.LOGICAL_AND: output +=
				 * "and"; break; case BinExprAST.LOGICAL_OR: output += "or";
				 * break; case BinExprAST.NOT_EQUAL:
				 * 
				 * case BinExprAST.EQUAL: output += "="; break; case
				 * BinExprAST.LESS_OR_EQUAL: output += "<="; break; case
				 * BinExprAST.GREATER_OR_EQUAL: output += ">="; break; case
				 * BinExprAST.LESS_THAN: output += "<"; break; case
				 * BinExprAST.GREATER_THAN: output += ">"; break; case 24:
				 * output += "+"; break; case 25: output += "-"; break; case 26:
				 * output += "*"; break; case 27: output += "div"; break; case
				 * 28: output += "mod"; break; default: valid = false; break; }
				 */
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
					int constmt = findCon(ast.parent.line);
					System.out.println("Parent line1 " + ast.parent.line + " "
							+ constmt);
					boolean check = true;
					if (branch == 0) {
						result += "(assert " + output + ")\n";
						ArrayList<String> temp = con.get(constmt).getTruepath();
						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;
						if (check == true) {
							con.get(constmt).getTruepath().add(result);
							con.get(constmt).getTruecon().add(obj.con);
						}
					}
					if (branch == 1) {
						result += "(assert (not " + output + "))\n";
						ArrayList<String> temp = con.get(constmt)
								.getFalsepath();
						for (int i = 0; i < temp.size(); i++)
							if (temp.get(i).equals(result))
								check = false;
						if (check == true) {
							con.get(constmt).getFalsepath().add(result);
							con.get(constmt).getFalsecon().add(obj.con);
						}
					}

				}
				return "";
			}
		}
	}

	@Override
	public Object visitCallExprAST(CallExprAST ast, Object o)
			throws CompilationException {
		String text = "(" + ast.name.getText() + " " + ast.e.visit(this, "c")
				+ ")";
		return text;
	}

	@Override
	public Object visitDeclarationListAST(DeclarationListAST ast, Object o)
			throws CompilationException {
		String var;
		String value;
		var = (String)ast.declarationAST.visit(this, o);
		if (ast.declarationListAST instanceof EmptyDeclarationListAST) {
			value = (String) ast.declarationAST.visit(this, "c");
		} else {
			value = (String) ast.declarationListAST.visit(this, o);
			int i = this.findVar(var);
			if (i >= 0) {
				this.var.set(i, value);
			} else {
				i = this.findPara(var);
				value = (String) ast.declarationListAST.visit(this, o);
				this.para.set(i, value);
			}
		}
		return "";
		// return var + " = " + value;
	}

	@Override
	public Object visitDeclarationStmtAST(DeclarationStmtAST ast, Object o)
			throws CompilationException {
		ast.dl.visit(this, "c");
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
		return ast.literal.getText();
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

		int constmt = findCon(ast.parent.line);
		System.out.println("Parent line " + ast.parent.line + " " + constmt);
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
					ArrayList<String> temp = con.get(constmt).getTruepath();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						con.get(constmt).getTruepath().add(res);
						con.get(constmt).getTruecon().add(obj.con);
					}
				} else {
					res += "(assert " + ast.e.visit(this, "c") + ")\n";
					ArrayList<String> temp = con.get(constmt).getFalsepath();
					for (int i = 0; i < temp.size(); i++)
						if (temp.get(i).equals(res))
							check = false;
					if (check == true) {
						con.get(constmt).getFalsepath().add(res);
						con.get(constmt).getFalsecon().add(obj.con);
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
				this.var.set(i, temp);
		} else {
			i = this.findPara(ast.id.toString());
			if (i >= 0) {
				temp = (String) ast.init.visit(this, o);
				if (temp != "")
					this.para.set(i, temp);
			}
		}
		if (o == "c") {
			return temp;
		} else {
			return ast.id.getText();
		}
		// return null;
	}

	// VarExprAST
	@Override
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		String value = ast.name.getText();
		String val = "";
		int i = this.findVar(ast.name.getText());
		if (i >= 0) {
			if (this.var.get(i) != "")
				val = this.var.get(i);
			else
				val = value;
		} else {
			i = this.findPara(ast.name.getText());
			if (i >= 0) {
				if (this.para.get(i) != "")
					val = this.para.get(i);
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
