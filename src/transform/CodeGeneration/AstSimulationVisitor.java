package transform.CodeGeneration;

import java.util.ArrayList;
import java.util.Stack;

import system.*;
import transform.AST.*;
import transform.DependenceGraph.*;

/***********************************************************************************
 * Visitor duyet qua cay AST de thuc hien simulation tim ra Execution History
 ***********************************************************************************/
public class AstSimulationVisitor extends DoNothingVisitor {
	/**
     * 
     */
	static final String GET_VARIABLE_NAME = "GetVariableName";
	/**
	 * bien tra ve cua 1 function
	 */
	static final String RET_VAR_NAME = "retvar";

	SimulationMemory simulation;
	ExecutionHistory execHistory;
	ArrayList<String> listInput;
	protected PDG pdg;

	// protected PointerTable pointerTable;

	public AstSimulationVisitor() {
		this.simulation = new SimulationMemory();
		this.execHistory = new ExecutionHistory();
		// this.pointerTable = new PointerTable();
	}

	public AstSimulationVisitor(ArrayList<String> list) {
		this.simulation = new SimulationMemory();
		this.execHistory = new ExecutionHistory();
		// this.pointerTable = new PointerTable();
		this.listInput = list;
	}

	public AstSimulationVisitor(PDG pdg, ArrayList<String> list) {
		this.pdg = pdg;
		this.simulation = new SimulationMemory();
		this.execHistory = new ExecutionHistory();
		// this.pointerTable = new PointerTable();
		this.listInput = list;
	}

	public ExecutionHistory getExecutionHistory() {
		return this.execHistory;
	}

	/*
	 * public PointerTable getPointerTable() { return this.pointerTable; }
	 */

	@SuppressWarnings("rawtypes")
	public void printArrayList(ArrayList list) {
		System.out.println("ArrayList:");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("EndArrayList:");
	}

	public void println(Object o) {
		System.out.println(o);
	}

	// ArrayInitializer
	@Override
	public Object visitArrayInitializerAST(ArrayInitializerAST ast, Object o)
			throws CompilationException {
		ast.varInitializerListAST.visit(this, o);
		return null;
	}

	// ArrayInitializerList
	@Override
	public Object visitArrayInitializerListAST(ArrayInitializerListAST ast,
			Object o) throws CompilationException {
		ast.arrayInitializerAST.visit(this, o);
		ast.arrayInitializerAST1.visit(this, o);
		return null;
	}

	// ArrayTypeAST
	@Override
	public Object visitArrayTypeAST(ArrayTypeAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		ast.exprListAST.visit(this, o);
		return null;
	}

	// BinExprAST
	@Override
	public Object visitBinExprAST(BinExprAST ast, Object o)
			throws CompilationException {
		Object val1;
		if ("zero_minus".equals(o)) {
			// phep toan 0 - ast.e2
			val1 = new Integer(0);
		} else {
			if (ast.exprAST1 instanceof UnaryExprAST) {
				val1 = ast.exprAST1.visit(this, "get_pointer_value");
			} else {
				val1 = ast.exprAST1.visit(this, o);
			}
		}

		Object val2;
		if ("increment".equals(o)) {
			// phep toan ast.e1 + 1
			val2 = new Integer(1);
		} else {
			if (ast.exprAST2 instanceof UnaryExprAST) {
				if (((UnaryExprAST) ast.exprAST2).opType == UnaryExprAST.INDIRECTION) {
					val2 = ast.exprAST2.visit(this, "get_pointer_value");
				} else {
					val2 = ast.exprAST2.visit(this, o);
				}
			} else {
				val2 = ast.exprAST2.visit(this, o);
			}
		}

		// cac loai phep gan =, +=, -=, *=, /=, %=
		if ((ast.opType >= BinExprAST.ASSIGN)
				&& (ast.opType <= BinExprAST.MOD_ASSIGN)) {
			// them phep gan dia chi pointer vao pointer table
			if (ast.opType == BinExprAST.ASSIGN) {
				if (ast.exprAST2 instanceof UnaryExprAST) {
					// String pointerName = (String) ast.e1.visit(this,
					// GET_VARIABLE_NAME);
					// String varName = (String) ast.e2.visit(this,
					// GET_VARIABLE_NAME);
					// System.out.println(pointerName + " " + varName);

					// this.pointerTable.add(new PointerRecord(pointerName,
					// varName, ast.line));

					// System.out.println(this.pointerTable.toString());
				}
			}

			String varName = "";
			if (ast.exprAST1 instanceof VarExprAST) {
				varName = ((VarExprAST) ast.exprAST1).op.getText();
			} else if (ast.exprAST1 instanceof EleExprAST) {
				varName = ((EleExprAST) ast.exprAST1).name.getText();
			} else if (ast.exprAST1 instanceof UnaryExprAST) {
				varName = (String) ast.exprAST1.visit(this, "get_pointer_name");
			}

			int scope;
			if (ast.opType == BinExprAST.ASSIGN) {
				// phep gan =
				scope = this.simulation.updateValueOfVariable(varName, val2);
				// set scope
				Node node = this.pdg.findNodeAtLine(ast.line);
				node.getDefinedVar().setScope(scope);
			} else {
				// phep gan +=, -=, *=, /=, %=
				// opType cua phep gan: 1, 2, 3, 4, 5
				// opType cua phep toan tuong ung: 24, 25, 26, 27, 28
				ExprAST binExpr = new BinExprAST(ast.exprAST1, ast.opType + 23, null,
						ast.exprAST2);
				Object val = binExpr.visit(this, o);
				scope = this.simulation.updateValueOfVariable(varName, val);
				// set scope
				Node node = this.pdg.findNodeAtLine(ast.line);
				node.getDefinedVar().setScope(scope);
			}
			return scope;
		}
		// phep luan ly giua cac Boolean
		else if ((ast.opType == BinExprAST.LOGICAL_OR)
				|| (ast.opType == BinExprAST.LOGICAL_AND)) {
			// phep luan ly hoac-va || &&
			// 1. so sanh giua 2 boolean => return Boolean
			if ((val1 instanceof Boolean) && (val2 instanceof Boolean)) {
				boolean v1 = ((Boolean) val1).booleanValue();
				boolean v2 = ((Boolean) val2).booleanValue();
				if (ast.opType == BinExprAST.LOGICAL_OR) {
					// phep luan ly ||
					return new Boolean(v1 || v2);
				} else {
					// phep luan ly &&
					return new Boolean(v1 && v2);
				}
			}
		}
		// cac loai phep so sanh
		else if (ast.opType == BinExprAST.EQUAL) {
			// phep so sanh ==
			// 1. so sanh == giua 2 so nguyen => return Boolean
			// 2. so sanh == giua 2 so thuc => return Boolean
			// 3. so sanh == giua 2 String => return Boolean
			// 4. so sanh == giua so nguyen va thuc => return Boolean
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Boolean(v1 == v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Boolean(v1 == v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Boolean(v1 == v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Boolean(v1 == v2);
			} else if ((val1 instanceof String) && (val2 instanceof String)) {
				String v1 = (String) val1;
				String v2 = (String) val2;
				return new Boolean(v1.equals(v2));
			}
		} else if (ast.opType == BinExprAST.NOT_EQUAL) {
			// phep so sanh !=
			// 1. so sanh != giua 2 so nguyen => return Boolean
			// 2. so sanh != giua 2 so thuc => return Boolean
			// 3. so sanh != giua 2 String => return Boolean
			// 4. so sanh != giua so nguyen va thuc => return Boolean
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Boolean(v1 != v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Boolean(v1 != v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Boolean(v1 != v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Boolean(v1 != v2);
			} else if ((val1 instanceof String) && (val2 instanceof String)) {
				String v1 = (String) val1;
				String v2 = (String) val2;
				return new Boolean(!v1.equals(v2));
			}
		} else if ((ast.opType >= BinExprAST.LESS_OR_EQUAL)
				&& (ast.opType <= BinExprAST.LESS_THAN)) {
			// phep so sanh <= (18) >= (19) > (20) < (21)
			// 1. so sanh giua 2 so nguyen => return Boolean
			// 2. so sanh giua 2 so thuc => return Boolean
			// 3. so sanh giua so nguyen va thuc => return Boolean
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				if (ast.opType == BinExprAST.LESS_OR_EQUAL) {
					// phep <=
					return new Boolean(v1 <= v2);
				} else if (ast.opType == BinExprAST.GREATER_OR_EQUAL) {
					// phep >=
					return new Boolean(v1 >= v2);
				} else if (ast.opType == BinExprAST.GREATER_THAN) {
					// phep >
					return new Boolean(v1 > v2);
				} else {
					// phep <
					return new Boolean(v1 < v2);
				}
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				if (ast.opType == BinExprAST.LESS_OR_EQUAL) {
					// phep <=
					return new Boolean(v1 <= v2);
				} else if (ast.opType == BinExprAST.GREATER_OR_EQUAL) {
					// phep >=
					return new Boolean(v1 >= v2);
				} else if (ast.opType == BinExprAST.GREATER_THAN) {
					// phep >
					return new Boolean(v1 > v2);
				} else {
					// phep <
					return new Boolean(v1 < v2);
				}
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				if (ast.opType == BinExprAST.LESS_OR_EQUAL) {
					// phep <=
					return new Boolean(v1 <= v2);
				} else if (ast.opType == BinExprAST.GREATER_OR_EQUAL) {
					// phep >=
					return new Boolean(v1 >= v2);
				} else if (ast.opType == BinExprAST.GREATER_THAN) {
					// phep >
					return new Boolean(v1 > v2);
				} else {
					// phep <
					return new Boolean(v1 < v2);
				}
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				if (ast.opType == BinExprAST.LESS_OR_EQUAL) {
					// phep <=
					return new Boolean(v1 <= v2);
				} else if (ast.opType == BinExprAST.GREATER_OR_EQUAL) {
					// phep >=
					return new Boolean(v1 >= v2);
				} else if (ast.opType == BinExprAST.GREATER_THAN) {
					// phep >
					return new Boolean(v1 > v2);
				} else {
					// phep <
					return new Boolean(v1 < v2);
				}
			}
		}
		// cac loai phep toan
		else if (ast.opType == BinExprAST.PLUS) {
			// phep toan +
			// 1. cong giua 2 so nguyen => return so nguyen
			// 2. cong giua 2 so thuc => return so thuc
			// 3. cong giua 2 string => return string
			// 4. cong giua so nguyen va thuc => return so thuc
			// 5. cong giua so nguyen va string => return string
			// 6. cong giua so thuc va string => return string
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Integer(v1 + v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 + v2);
			} else if ((val1 instanceof String) && (val2 instanceof String)) {
				String v1 = (String) val1;
				String v2 = (String) val2;
				return (v1 + v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 + v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Float(v1 + v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof String)) {
				int v1 = ((Integer) val1).intValue();
				String v2 = (String) val2;
				return String.valueOf(v1) + v2;
			} else if ((val1 instanceof String) && (val2 instanceof Integer)) {
				String v1 = (String) val1;
				int v2 = ((Integer) val2).intValue();
				return v1 + String.valueOf(v2);
			} else if ((val1 instanceof Float) && (val2 instanceof String)) {
				float v1 = ((Float) val1).floatValue();
				String v2 = (String) val2;
				return String.valueOf(v1) + v2;
			} else if ((val1 instanceof String) && (val2 instanceof Float)) {
				String v1 = (String) val1;
				float v2 = ((Float) val2).floatValue();
				return v1 + String.valueOf(v2);
			}
		} else if (ast.opType == BinExprAST.MINUS) {
			// phep toan -
			// 1. tru giua 2 so nguyen => return so nguyen
			// 2. tru giua 2 so thuc => return so thuc
			// 3. tru giua 2 so nguyen va thuc => return so thuc
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Integer(v1 - v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 - v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 - v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Float(v1 - v2);
			}
		} else if (ast.opType == BinExprAST.STAR) {
			// phep toan *
			// 1. nhan giua 2 so nguyen => return so nguyen
			// 2. nhan giua 2 so thuc => return so thuc
			// 3. nhan giua 2 so nguyen va thuc => return so thuc
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Integer(v1 * v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 * v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 * v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Float(v1 * v2);
			}
		} else if (ast.opType == BinExprAST.DIV) {
			// phep toan /
			// 1. chia giua 2 so nguyen => return so nguyen
			// 2. chia giua 2 so thuc => return so thuc
			// 3. chia giua 2 so nguyen va thuc => return so thuc
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Integer(v1 / v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Float)) {
				float v1 = ((Float) val1).floatValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 / v2);
			} else if ((val1 instanceof Integer) && (val2 instanceof Float)) {
				int v1 = ((Integer) val1).intValue();
				float v2 = ((Float) val2).floatValue();
				return new Float(v1 / v2);
			} else if ((val1 instanceof Float) && (val2 instanceof Integer)) {
				float v1 = ((Float) val1).floatValue();
				int v2 = ((Integer) val2).intValue();
				return new Float(v1 / v2);
			}
		} else if (ast.opType == BinExprAST.MOD) {
			// phep toan %
			// 1. mod giua 2 so nguyen => return so nguyen
			if ((val1 instanceof Integer) && (val2 instanceof Integer)) {
				int v1 = ((Integer) val1).intValue();
				int v2 = ((Integer) val2).intValue();
				return new Integer(v1 % v2);
			}
		}

		return null;
	}

	// BoolLiteralAST
	@Override
	public Object visitBoolLiteralAST(BoolLiteralAST ast, Object o)
			throws CompilationException {
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// chua xu ly cac cau lenh ben duoi
	// BreakStmtAST
	@Override
	public Object visitBreakStmtAST(BreakStmtAST ast, Object o)
			throws CompilationException {
		this.println("BreakStmtAST: " + ast.line);
		return null;
	}

	// CallExprAST
	@Override
	public Object visitCallExprAST(CallExprAST ast, Object o)
			throws CompilationException {
		if (ast.op.getText().equals("println")) {
			Object value = ast.exprListAST.exprAST.visit(this, o);
			this.println(value);
			return null;
		}
		this.println("Simulation of Call Function is NOT SUPPORTED !!!");
		return null;
	}

	// CaseStmtAST
	@Override
	public Object visitCaseStmtAST(CaseStmtAST cAst, Object o)
			throws CompilationException {
		this.println("CaseStmtAST: " + cAst.line);
		cAst.exprAST.visit(this, o);
		cAst.stmtListAST.visit(this, o);
		return null;
	}

	// CharLiteralAST
	@Override
	public Object visitCharLiteralAST(CharLiteralAST ast, Object o)
			throws CompilationException {
		String str = ast.literalToken.getText();
		str = str.substring(1, str.length() - 1);
		return str;
	}

	// CompStmtAST
	@Override
	public Object visitCompStmtAST(CompStmtAST cAst, Object o)
			throws CompilationException {
		cAst.stmtListAST.visit(this, o);
		return null;
	}

	// ContStmtAST
	@Override
	public Object visitContStmtAST(ContStmtAST ast, Object o)
			throws CompilationException {
		this.println("ContStmtAST: " + ast.line);
		return null;
	}

	// DeclarationListAST
	@Override
	public Object visitDeclarationListAST(DeclarationListAST ast, Object o)
			throws CompilationException {
		ast.declarationAST.visit(this, o);
		ast.declarationListAST.visit(this, o);
		return null;
	}

	// DeclarationStmtAST
	@Override
	public Object visitDeclarationStmtAST(DeclarationStmtAST ast, Object o)
			throws CompilationException {
		ast.declarationListAST.visit(this, o);
		return null;
	}

	// DefaultStmtAST
	@Override
	public Object visitDefaultStmtAST(DefaultStmtAST dAst, Object o)
			throws CompilationException {
		this.println("DefaultStmtAST: " + dAst.line);
		dAst.stmtListAST.visit(this, o);
		return null;
	}

	// DoStmtAST
	@Override
	public Object visitDoStmtAST(DoStmtAST wAst, Object o)
			throws CompilationException {
		boolean value;
		do {
			this.simulation.enterScope();
			wAst.oneStmtAST.visit(this, o);
			this.simulation.exitScope();

			value = ((Boolean) wAst.exprAST.visit(this, o)).booleanValue();
			if (value) {
				this.execHistory.addExecNode(new ExecNode(wAst.line, 'T'));
			} else {
				this.execHistory.addExecNode(new ExecNode(wAst.line, 'F'));
			}
		} while (value);
		return null;
	}

	// EleExprAST
	// Bieu thuc truy xuat mang: array[...][...][...]
	@Override
	public Object visitEleExprAST(EleExprAST ast, Object o)
			throws CompilationException {
		this.println("Simulation of Array manipulation is NOT SUPPORTED !!!");
		// String varName = ast.name.getText();
		ast.exprListAST.visit(this, o);
		return null;
	}

	// ExprListAST
	@Override
	public Object visitExprListAST(ExprListAST ast, Object o)
			throws CompilationException {
		ast.exprAST.visit(this, o);
		ast.exprListAST.visit(this, o);
		return null;
	}

	// ExprStmtAST
	@Override
	public Object visitExprStmtAST(ExprStmtAST ast, Object o)
			throws CompilationException {
		ast.exprAST.visit(this, o);
		this.execHistory.addExecNode(new ExecNode(ast.line, 'N'));

		return null;
	}

	// FloatLiteralAST
	@Override
	public Object visitFloatLiteralAST(FloatLiteralAST ast, Object o)
			throws CompilationException {
		return new Float(ast.literalToken.getText());
	}

	// ForInitAST
	@Override
	public Object visitForInitAST(ForInitAST ast, Object o)
			throws CompilationException {
		if (ast.declarationListAST != null) {
			// for (int i = 1 ; ...)
			ast.declarationListAST.visit(this, o);
		}
		if (ast.exprListAST != null) {
			// for (i = 1, j = 2, k = 3 ; ...)
			return ast.exprListAST.visit(this, o);
		}
		return null;
	}

	// ForStmtAST
	// for (int i = 1; i < 10; i++) print(x)
	// <==> int i = 1; while (i < 10) {print(x); i++}
	@Override
	public Object visitForStmtAST(ForStmtAST fAst, Object o)
			throws CompilationException {
		/*
		 * System.out.println("Here"); this.simulation.enterScope(); if (fAst.e1
		 * != null) { fAst.e1.visit(this, o); } boolean value = true; if
		 * (fAst.e2 != null) { value = ((Boolean) fAst.e2.visit(this,
		 * o)).booleanValue(); } if (value) { this.execHistory.addExecNode(new
		 * ExecNode(fAst.line, 'T')); } else { this.execHistory.addExecNode(new
		 * ExecNode(fAst.line, 'F')); } while (value) {
		 * this.simulation.enterScope(); fAst.o.visit(this, o);
		 * this.simulation.exitScope(); if (fAst.e3 != null) {
		 * fAst.e3.visit(this, o); } if (fAst.e2 != null) { value = ((Boolean)
		 * fAst.e2.visit(this, o)).booleanValue(); } if (value) {
		 * this.execHistory.addExecNode(new ExecNode(fAst.line, 'T')); } else {
		 * this.execHistory.addExecNode(new ExecNode(fAst.line, 'F')); } }
		 * this.simulation.exitScope();
		 */
		return null;
	}

	// FuncDeclAST
	@Override
	public Object visitFuncDeclAST(FuncDeclAST fAst, Object o)
			throws CompilationException {
		this.simulation.enterScope();
		fAst.paraListAST.visit(this, o);
		fAst.typeAST.visit(this, o);

		TypeAST retType = ((TypeListAST) fAst.typeAST).typeAST;
		int scope = this.simulation.currentScope;
		this.simulation.addNewVariable(retType, RET_VAR_NAME, scope);

		fAst.compStmtAST.visit(this, o);

		this.simulation.exitScope();
		return null;
	}

	// IfThenElseStmtAST
	@Override
	public Object visitIfThenElseStmtAST(IfThenElseStmtAST iAst, Object o)
			throws CompilationException {
		boolean value = ((Boolean) iAst.exprAST.visit(this, o)).booleanValue();
		if (value) {
			this.execHistory.addExecNode(new ExecNode(iAst.line, 'T'));
			this.simulation.enterScope();
			iAst.oneStmtAST1.visit(this, o);
			this.simulation.exitScope();
		} else {
			this.execHistory.addExecNode(new ExecNode(iAst.line, 'F'));
			this.simulation.enterScope();
			iAst.oneStmtAST2.visit(this, o);
			this.simulation.exitScope();
		}
		return null;
	}

	// IfThenStmtAST
	@Override
	public Object visitIfThenStmtAST(IfThenStmtAST iAst, Object o)
			throws CompilationException {
		boolean value = ((Boolean) iAst.exprAST.visit(this, o)).booleanValue();
		if (value) {
			this.execHistory.addExecNode(new ExecNode(iAst.line, 'T'));
			this.simulation.enterScope();
			iAst.oneStmtAST.visit(this, o);
			this.simulation.exitScope();
		} else {
			this.execHistory.addExecNode(new ExecNode(iAst.line, 'F'));
		}
		return null;
	}

	// IntLiteralAST
	@Override
	public Object visitIntLiteralAST(IntLiteralAST ast, Object o)
			throws CompilationException {
		return new Integer(ast.literalToken.getText());
	}

	// ParaAST
	@Override
	public Object visitParaAST(ParaAST pAst, Object o)
			throws CompilationException {
		pAst.typeAST.visit(this, o);
		TypeAST type = ((TypeListAST) pAst.typeAST).typeAST;
		if (this.listInput.size() > 0) {
			// get the input value from listInput
			String input = this.listInput.remove(0);
			Object value;
			if (input.indexOf('.') != -1) {
				value = Float.parseFloat(input);
			} else if (input.indexOf('"') != -1) {
				value = input.substring(1, input.length() - 1);
			} else {
				value = Integer.parseInt(input);
			}
			int scope = this.simulation.currentScope;
			this.simulation.addNewVariable(type, pAst.op.getText(), value,
					scope);
		} else {
			// System.out.println("Error input");
		}
		return null;
	}

	// ParaListAST
	@Override
	public Object visitParaListAST(ParaListAST pAst, Object o)
			throws CompilationException {
		pAst.paraAST.visit(this, o);
		pAst.paraListAST.visit(this, o);
		return null;
	}

	// PointerTypeAST
	@Override
	public Object visitPointerTypeAST(PointerTypeAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		return null;
	}

	// ProgramAST
	@Override
	public Object visitProgramAST(ProgramAST ast, Object o)
			throws CompilationException {
		ast.declarationListAST.visit(this, o);
		return null;
	}

	// RetStmtAST
	@Override
	public Object visitRetStmtAST(RetStmtAST ast, Object o)
			throws CompilationException {
		if (ast.exprAST != null) {
			this.execHistory.addExecNode(new ExecNode(ast.line, 'N'));
			Object value = ast.exprAST.visit(this, "get_pointer_value");
			// Object value = ast.e.visit(this, o);
			this.simulation.updateValueOfVariable(RET_VAR_NAME, value);
		}
		return null;
	}

	// StmtListAST
	@Override
	public Object visitStmtListAST(StmtListAST sAst, Object o)
			throws CompilationException {
		sAst.oneStmtAST.visit(this, o);
		sAst.stmtListAST.visit(this, o);
		return null;
	}

	// StringLiteralAST
	@Override
	public Object visitStringLiteralAST(StringLiteralAST ast, Object o)
			throws CompilationException {
		String str = ast.literalToken.getText();
		str = str.substring(1, str.length() - 1);
		return str;
	}

	// SwitchStmtAST
	@Override
	public Object visitSwitchStmtAST(SwitchStmtAST sAst, Object o)
			throws CompilationException {
		this.println("SwitchStmtAST: " + sAst.line);
		sAst.exprAST.visit(this, o);
		sAst.oneStmtAST.visit(this, o);
		return null;
	}

	//
	// /////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////
	// chua can xet cac ham ben duoi
	// TernaryExprAST
	@Override
	public Object visitTernaryExprAST(TernaryExprAST ast, Object o)
			throws CompilationException {
		ast.exprAST1.visit(this, o);
		ast.exprAST2.visit(this, o);
		ast.exprAST3.visit(this, o);
		return null;
	}

	// TypeListAST
	@Override
	public Object visitTypeListAST(TypeListAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		ast.typeListAST.visit(this, o);
		return null;
	}

	// UnaryExprAST
	@Override
	public Object visitUnaryExprAST(UnaryExprAST ast, Object o)
			throws CompilationException {
		Object val;
		if (ast.opType == UnaryExprAST.ADDR_OF) {
			if (o.equals(GET_VARIABLE_NAME)) {
				return ast.exprAST.visit(this, GET_VARIABLE_NAME);
			} else {
				val = ast.exprAST.visit(this, "address");
			}
		} else if (ast.opType == UnaryExprAST.INDIRECTION) {
			if (o.equals("get_pointer_value")) {
				val = ast.exprAST.visit(this, "get_pointer_value");
			} else {
				val = ast.exprAST.visit(this, "get_pointed_address");
			}
		} else {
			val = ast.exprAST.visit(this, o);
		}

		if ((ast.opType >= UnaryExprAST.PRE_INC)
				&& (ast.opType <= UnaryExprAST.POST_DEC)) {
			int opType;
			boolean pre_increment; // true if (++i or --i), false if (i++ or
									// i--)
			String varName = "";
			if (ast.exprAST instanceof VarExprAST) {
				varName = ((VarExprAST) ast.exprAST).op.getText();
			} else if (ast.exprAST instanceof EleExprAST) {
				varName = ((EleExprAST) ast.exprAST).name.getText();
			}

			if (ast.opType == UnaryExprAST.PRE_INC) {
				// ++i
				opType = BinExprAST.PLUS;
				pre_increment = true;
			} else if (ast.opType == UnaryExprAST.PRE_DEC) {
				// --i
				opType = BinExprAST.MINUS;
				pre_increment = true;
			} else if (ast.opType == UnaryExprAST.POST_INC) {
				// i++
				opType = BinExprAST.PLUS;
				pre_increment = false;
			} else {
				// i--
				opType = BinExprAST.MINUS;
				pre_increment = false;
			}
			ExprAST addingOneBinExpr = new BinExprAST(ast.exprAST, opType, null,
					new NullExprAST());
			Object new_value = addingOneBinExpr.visit(this, "increment");
			this.simulation.updateValueOfVariable(varName, new_value);
			if (pre_increment) {
				// increment first then return the new value
				return new_value;
			} else {
				// return the old value and also
				return val;
			}
		} else if (ast.opType == UnaryExprAST.UNARY_MINUS) {
			// -i
			ExprAST zeroMinusBinExpr = new BinExprAST(new NullExprAST(),
					BinExprAST.MINUS, null, ast.exprAST);
			return zeroMinusBinExpr.visit(this, "zero_minus");
		} else if (ast.opType == UnaryExprAST.LOGICAL_NOT) {
			// !bool
			if (val instanceof Boolean) {
				boolean v = ((Boolean) val).booleanValue();
				return !v;
			}
		}

		return val;
	}

	// VarDeclAST
	@Override
	public Object visitVarDeclAST(VarDeclAST ast, Object o)
			throws CompilationException {
		// ast.t.visit(this, o);
		TypeAST type;
		if (ast.typeAST instanceof TypeListAST) {
			type = ((TypeListAST) ast.typeAST).typeAST;
		} else {
			type = ast.typeAST;
		}

		int scope = this.simulation.currentScope;
		if (ast.init != null) {
			this.simulation.addNewVariable(type, ast.op.getText(),
					ast.init.visit(this, o), scope);
			// change in pdg
			int line = ast.line;
			Node node = this.pdg.findNodeAtLine(line);
			node.getDefinedVar().setScope(scope);

			// add executed node
			this.execHistory.addExecNode(new ExecNode(ast.line, 'N'));
		} else {
			this.simulation.addNewVariable(type, ast.op.getText(), scope);
		}

		return null;
	}

	/***********************************************************************************
	 * Handling ExprAST
	 ***********************************************************************************/
	// VarExprAST
	@Override
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		ast.line = ast.parentAST.line;

		String name = ast.op.getText();
		if (o.equals(GET_VARIABLE_NAME)) {
			return name;
		} else if (o.equals("address")) {
			/*
			 * return the record pointed by pointer
			 */
			Node node = this.pdg.findNodeAtLine(ast.line);
			if (node != null) {
				VariableUsed usedVar = node.findUsedVar(name);
				if (usedVar != null) {
					int scope = this.simulation.getScopeOfVariable(name);
					usedVar.setScope(scope);
				}
			}
			Variable record = this.simulation.getAddressOf(name);
			return record;
		} else if (o.equals("get_pointed_address")) {
			/*
			 * return the varName pointed by pointer. if the pointer was not
			 * point to any record, this is a free pointer.
			 */
			Object value = this.simulation.getValueOfVariable(name);
			if (value instanceof Variable) {
				return ((Variable) value).varName;
			} else {
				return name;
			}
		} else if (o.equals("get_pointer_value")) {
			Node node = this.pdg.findNodeAtLine(ast.line);
			if (node != null) {
				VariableUsed usedVar = node.findUsedVar(name);
				if (usedVar != null) {
					int scope = this.simulation.getScopeOfVariable(name);
					usedVar.setScope(scope);
				}
			}
			Object value = this.simulation.getValueOfVariable(name);
			if (value instanceof Variable) {
				return ((Variable) value).value;
			} else {
				return value;
			}
		} else {
			Node node = this.pdg.findNodeAtLine(ast.line);
			if (node != null) {
				VariableUsed usedVar = node.findUsedVar(name);
				if (usedVar != null) {
					int scope = this.simulation.getScopeOfVariable(name);
					usedVar.setScope(scope);
				}
			}
			return this.simulation.getValueOfVariable(name);
		}
	}

	// VarInitializer
	@Override
	public Object visitVarInitializerAST(VarInitializerAST ast, Object o)
			throws CompilationException {
		return ast.exprAST.visit(this, o);
	}

	// VarInitializerList
	@Override
	public Object visitVarInitializerListAST(VarInitializerListAST ast, Object o)
			throws CompilationException {
		ast.varInitializerAST.visit(this, o);
		ast.varInitializerListAST.visit(this, o);
		return null;
	}

	// WhileStmtAST
	@Override
	public Object visitWhileStmtAST(WhileStmtAST wAst, Object o)
			throws CompilationException {
		int count = 0;
		boolean value = ((Boolean) wAst.exprAST.visit(this, o)).booleanValue();
		while (value) {
			count++;
			this.execHistory.addExecNode(new ExecNode(wAst.line, 'T'));
			this.simulation.enterScope();
			wAst.oneStmtAST.visit(this, o);
			value = ((Boolean) wAst.exprAST.visit(this, o)).booleanValue();
			this.simulation.exitScope();
		}
		if (count == 0)
			this.execHistory.addExecNode(new ExecNode(wAst.line, 'F'));
		return null;
	}

}

/***********************************************************************************
 * class SimulationMemory Task: gia lap lai bo nho luu tru gia tri cua cac bien
 * (valueTableOfVariables)
 ***********************************************************************************/
class SimulationMemory {
	Stack<Variable> valueTableOfVariables;
	Stack<Integer> scope = new Stack<Integer>();
	protected int currentScope;

	String newline = "\r\n";

	public SimulationMemory() {
		this.valueTableOfVariables = new Stack<Variable>();
		this.currentScope = -1;
		this.enterScope();
	}

	// them 1 bien moi (xay ra khi gap cau lenh: int a)
	public void addNewVariable(TypeAST type, String name, int scope) {
		this.valueTableOfVariables.push(new Variable(type, name, scope));
		/*
		 * whenever the memory is updated, show the memory
		 */
		// this.memoryChanged();
	}

	// them 1 bien moi co gia tri (xay ra khi gap cau lenh: int a = 1)
	public void addNewVariable(TypeAST type, String name, Object value,
			int scope) {
		this.valueTableOfVariables.push(new Variable(type, name, value, scope));
		/*
		 * whenever the memory is updated, show the memory
		 */
		// this.memoryChanged();
	}

	// invoked when entering a new scope
	public void enterScope() {
		this.currentScope++;
		this.scope.push(this.valueTableOfVariables.size());
	}

	// invoked when exitting a scope
	public void exitScope() {
		if (this.scope.empty()) {
			System.out.println("IllegalOperationException");
			return;
		}
		int top = this.scope.pop();
		if (this.valueTableOfVariables.size() < top) {
			System.out.println("IllegalOperationException");
			return;
		}
		// when exitting a scope, remove every variable declared in this scope.
		this.valueTableOfVariables.setSize(top);
		this.currentScope--;
	}

	/*
	 * this method will return the index of var name in stack memory
	 */
	public Variable getAddressOf(String name) {
		for (int i = this.valueTableOfVariables.size() - 1; i >= 0; i--) {
			if (this.valueTableOfVariables.get(i).varName.equals(name)) {
				return this.valueTableOfVariables.get(i);
			}
		}
		return null;
	}

	/**
	 * return the scope of a variable in memory
	 * 
	 * @param varName
	 * @return
	 */
	public int getScopeOfVariable(String varName) {
		for (int i = this.valueTableOfVariables.size() - 1; i >= 0; i--) {
			if (this.valueTableOfVariables.elementAt(i).varName.equals(varName)) {
				return this.valueTableOfVariables.elementAt(i).scope;
			}
		}
		return -1;
	}

	/**
	 * return the value of a variable in memory
	 * 
	 * @param varName
	 * @return
	 */
	public Object getValueOfVariable(String varName) {
		for (int i = this.valueTableOfVariables.size() - 1; i >= 0; i--) {
			if (this.valueTableOfVariables.elementAt(i).varName.equals(varName)) {
				return this.valueTableOfVariables.elementAt(i).value;
			}
		}
		return null; // show an error dialog
	}

	@SuppressWarnings("unused")
	private void memoryChanged() {
		/*
		 * this method is used for debug, enable the following statement to use.
		 */
		System.out.println(this.toString());
	}

	@Override
	public String toString() {
		String result = this.newline
				+ "Table value of all variables in the program:" + this.newline;
		for (int i = 0; i < this.valueTableOfVariables.size(); i++) {
			result += this.valueTableOfVariables.elementAt(i).toString()
					+ this.newline;
		}
		return result;
	}

	// cap nhat lai gia tri cua bien (xay ra khi gap phep gan =, +=, -=, *=, /=)
	public int updateValueOfVariable(String var, Object newVal) {
		for (int i = this.valueTableOfVariables.size() - 1; i >= 0; i--) {
			Variable element = this.valueTableOfVariables.elementAt(i);
			if (element.varName.equals(var)) {
				element.value = newVal;
				/*
				 * whenever the memory is updated, show the memory
				 */
				// this.memoryChanged();
				return element.scope;
			}
		}
		return -1;
	}
}

/***********************************************************************************
 * Class Variable luu (kieu, ten, gia tri) cua 1 bien duoc tao ra trong chuong
 * trinh
 ***********************************************************************************/
class Variable {
	public TypeAST type;
	public String varName;
	public Object value;
	/**
	 * this attribute is the scope of the declaration of a variable, start from
	 * 0 and always greater than or equal 0.
	 */
	protected int scope;

	public Variable(TypeAST t, String n, int scope) {
		this.type = t;
		this.varName = n;
		this.value = null;
		this.scope = scope;
	}

	public Variable(TypeAST t, String n, Object v, int scope) {
		this.type = t;
		this.varName = n;
		this.value = v;
		this.scope = scope;
	}

	@Override
	public String toString() {
		String result = "";
		if (this.type instanceof BoolTypeAST) {
			result += "bool ";
		} else if (this.type instanceof ShortTypeAST) {
			result += "short ";
		} else if (this.type instanceof IntTypeAST) {
			result += "int ";
		} else if (this.type instanceof LongTypeAST) {
			result += "long ";
		} else if (this.type instanceof FloatTypeAST) {
			result += "float ";
		} else if (this.type instanceof DoubleTypeAST) {
			result += "double ";
		} else if (this.type instanceof CharTypeAST) {
			result += "char ";
		} else if (this.type instanceof PointerTypeAST) {
			result += "*";
		}

		result += this.varName;

		if (this.value != null) {
			result += " = " + this.value.toString();
		}
		return result;
	}
}
