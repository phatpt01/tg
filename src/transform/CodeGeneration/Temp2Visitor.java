package transform.CodeGeneration;

import java.util.ArrayList;

import system.Parameter;
import system.Variable;
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

public class Temp2Visitor extends DoNothingVisitor {
	ArrayList<Variable> listVar;
	ArrayList<Parameter> listPara;
	ArrayList<Integer> para;
	ArrayList<Integer> var;
	ArrayList<String> tc;

	public Temp2Visitor(ArrayList<Parameter> listPara,
			ArrayList<Variable> listVar, ArrayList<String> testcase) {
		this.listVar = listVar;
		this.listPara = listPara;
		this.para = new ArrayList<Integer>();
		this.tc = testcase;
		for (int i = 0; i < testcase.size(); i++) {
			para.add(Integer.parseInt(testcase.get(i)));
		}
		this.var = new ArrayList<Integer>();
		for (int i = 0; i < listVar.size(); i++) {
			var.add(0);
		}

	}

	public void clear() {
		var.clear();
		para.clear();
		for (int i = 0; i < tc.size(); i++) {
			para.add(Integer.parseInt(tc.get(i)));
		}
		for (int i = 0; i < listVar.size(); i++) {
			var.add(null);
		}
	}

	private int findPara(String paraName) {
		if (this.listPara.size() <= 0) {
			return -1;
		} else {
			for (int i = 0; i < this.listPara.size(); i++) {
				if (paraName.equals(this.listPara.get(i).getName())) {
					return i;
				}
			}
			return -1;
		}
	}

	// get the index of the varName in varReindex
	private int findVar(String varName) {
		if (this.listVar.size() <= 0) {
			return -1;
		} else {
			for (int i = 0; i < this.listVar.size(); i++) {
				if (varName.equals(this.listVar.get(i).getName())) {
					return i;
				}
			}
			return -1;
		}
	}

	// BinExprAST
	@Override
	public Object visitBinExprAST(BinExprAST ast, Object o)
			throws CompilationException {
		if ((ast.opType == BinExprAST.ASSIGN)) { // && (ast.parent instanceof
													// ExprStmtAST)) {

			String var = (String) ast.exprAST1.visit(this, "c");
			int temp = 0;
			int i = this.findVar(var);
			if (i >= 0) {
				temp = (Integer) ast.exprAST2.visit(this, o);
				this.var.set(i, temp);
			} else {
				i = this.findPara(var);
				if (i >= 0) {
					temp = (Integer) ast.exprAST2.visit(this, o);
					this.para.set(i, temp);
				}
			}
			return -1;
		} else {
			int val1 = (Integer) ast.exprAST1.visit(this, null);
			int val2 = (Integer) ast.exprAST2.visit(this, null);
			int value = 0;
			switch (ast.opType) {
			case BinExprAST.PLUS:
				value = (int) val1 + (int) val2;
				break;
			case BinExprAST.MINUS:
				value = (int) val1 - (int) val2;
				break;
			case BinExprAST.STAR:
				value = (int) val1 * (int) val2;
				break;
			case BinExprAST.DIV:
				if ((int) val2 != 0)
					value = (int) val1 / (int) val2;
				else
					value = 100;
				break;
			case BinExprAST.MOD:
				value = (int) val1 % (int) val2;
				break;
			case BinExprAST.LOGICAL_AND:
				if ((Integer) o == 0) {
					if ((int) val1 == 0 && (int) val2 == 0)
						value = 0;
					else
						value = val1 + val2;
				} else {
					if ((int) val1 != 0 || (int) val2 != 0)
						value = 0;
					else
						value = 1;
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.LOGICAL_OR:
				if ((Integer) o == 0) {
					if ((int) val1 == 0 || (int) val2 == 0)
						value = 0;
					else
						value = Math.min(val1, val2);
				} else {
					if ((int) val1 != 0 && (int) val2 != 0)
						value = 0;
					else
						value = val1 + val2;
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.NOT_EQUAL:
				if ((Integer) o == 0) {
					if ((int) val1 != (int) val2)
						value = 0;
					else
						value = 1;
				} else {
					if (((int) val1 == (int) val2))
						value = 0;
					else
						value = Math.abs(val1 - val2);
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.EQUAL:
				if ((Integer) o == 0) {
					if ((int) val1 == (int) val2)
						value = 0;
					else
						value = Math.abs(val1 - val2);
				} else {
					if ((int) val1 != (int) val2)
						value = 0;
					else
						value = 1;
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.LESS_OR_EQUAL:
				if ((Integer) o == 0) {
					if ((int) val1 <= (int) val2)
						value = 0;
					else
						value = Math.abs(val1 - val2);
				} else {
					if ((int) val1 > (int) val2)
						value = 0;
					else
						value = 1 + Math.abs(val1 - val2);
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.GREATER_OR_EQUAL:
				if ((Integer) o == 0) {
					if ((int) val1 >= (int) val2)
						value = 0;
					else
						value = Math.abs(val1 - val2);
					// if(value == 0)
					// System.out.println(">="+val1 + " "+ val2);
				} else {
					if ((int) val1 < (int) val2)
						value = 0;
					else
						value = 1 + Math.abs(val1 - val2);
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.LESS_THAN:
				if ((Integer) o == 0) {
					if ((int) val1 < (int) val2)
						value = 0;
					else
						value = 1 + Math.abs(val1 - val2);
				} else {
					if ((int) val1 >= (int) val2)
						value = 0;
					else
						value = Math.abs(val1 - val2);
				}
				if (value > 100)
					value = 100;
				break;
			case BinExprAST.GREATER_THAN:
				if ((Integer) o == 0) {
					if ((int) val1 > (int) val2)
						value = 0;
					else
						value = 1 + Math.abs(val1 - val2);
				} else {
					if ((int) val1 <= (int) val2)
						value = 0;
					else
						value = Math.abs(val1 - val2);
					if (value == 0 && val1 == 0 && val2 == 0) {
						System.out.println(this.para);
						System.out.println("=" + val1 + " " + val2);
					}
				}
				if (value > 100)
					value = 100;
				break;
			}
			return value;
		}
	}

	@Override
	public Object visitCallExprAST(CallExprAST ast, Object o)
			throws CompilationException {
		int value = 0;
		switch (ast.op.getText()) {
		case "cos":
			value = (int) Math.cos((Integer) ast.exprListAST.visit(this, o));
			break;
		case "sin":
			value = (int) Math.sin((Integer) ast.exprListAST.visit(this, o));
			break;
		case "tan":
			value = (int) Math.tan((Integer) ast.exprListAST.visit(this, o));
			break;
		case "exp":
			value = (int) Math.exp((Integer) ast.exprListAST.visit(this, o));
			break;
		case "log":
			value = (int) Math.log((Integer) ast.exprListAST.visit(this, o));
			break;
		case "pow":
			value = (int) Math.pow((Integer) ast.exprListAST.visit(this, o),
					(Integer) ast.exprListAST.exprListAST.visit(this, o));
			System.out.println("POW " + value);
			break;
		case "sqrt":
			value = (int) Math.sqrt((Integer) ast.exprListAST.visit(this, o));
			break;
		case "abs":
			value = (int) Math.abs((Integer) ast.exprListAST.visit(this, o));
			System.out.println("ABS " + value);
			break;
		}
		return value;
	}

	@Override
	public Object visitDeclarationListAST(DeclarationListAST ast, Object o)
			throws CompilationException {
		String var;
		int value;
		var = (String) ast.declarationAST.visit(this, "c");
		if (ast.declarationListAST instanceof EmptyDeclarationListAST) {
			return 0;
		} else {
			value = (Integer) ast.declarationListAST.visit(this, o);
			int i = this.findVar(var);
			if (i >= 0) {
				this.var.set(i, value);
			} else {
				i = this.findPara(var);
				value = (Integer) ast.declarationListAST.visit(this, o);
				this.para.set(i, value);
			}
		}
		return 0;
	}

	@Override
	public Object visitDeclarationStmtAST(DeclarationStmtAST ast, Object o)
			throws CompilationException {
		ast.declarationListAST.visit(this, o);
		return 0;
	}

	@Override
	public Object visitEmptyExprListAST(EmptyExprListAST ast, Object o)
			throws CompilationException {
		return 0;
	}

	@Override
	public Object visitExprListAST(ExprListAST ast, Object o)
			throws CompilationException {
		Object value = ast.exprAST.visit(this, o);
		if (value instanceof Integer)
			return (Integer) value;
		return 0;
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
		return 0;
	}

	@Override
	public Object visitIntLiteralAST(IntLiteralAST ast, Object o)
			throws CompilationException {
		return Integer.parseInt(ast.literalToken.getText());
	}

	// ProgramAST
	@Override
	public Object visitProgramAST(ProgramAST ast, Object o)
			throws CompilationException {
		return 0;
	}

	@Override
	public Object visitUnaryExprAST(UnaryExprAST ast, Object o)
			throws CompilationException {
		int value = 0;
		if (ast.opType == UnaryExprAST.LOGICAL_NOT) {
			if ((Integer) o == 0) {
				value = (Integer) ast.exprAST.visit(this, null);
			} else {
				if ((Integer) ast.exprAST.visit(this, null) == 0) {
					value = 1;
				} else {
					value = 0;
				}
			}
		}
		return value;
	}

	@Override
	public Object visitVarDeclAST(VarDeclAST ast, Object o)
			throws CompilationException {
		int value;
		int i = this.findVar(ast.op.getText());
		if (i >= 0) {
			value = (Integer) ast.init.visit(this, o);
			this.var.set(i, value);
		} else {
			i = this.findPara(ast.op.toString());
			if (i >= 0) {
				value = (Integer) ast.init.visit(this, o);
				this.para.set(i, value);
			}
		}
		if (o == "c")
			return ast.op.getText();
		return 0;
		// return null;
	}

	// VarExprAST
	@Override
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		int val = 0;
		int i = this.findVar(ast.op.getText());
		if (i >= 0) {
			val = this.var.get(i);
		} else {
			i = this.findPara(ast.op.getText());
			if (i >= 0) {
				val = this.para.get(i);
			}
		}
		if (o == "c") {
			return ast.op.getText();
		}
		return val;
	}

	@Override
	public Object visitVarInitializerAST(VarInitializerAST ast, Object o)
			throws CompilationException {
		return (Integer) ast.exprAST.visit(this, o);
	}
}
