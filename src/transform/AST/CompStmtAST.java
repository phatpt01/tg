package transform.AST;

import transform.CodeGeneration.Visitor;

public class CompStmtAST extends OneStmtAST {
	/*
	 * public VarDeclPartAST v; public StmtListAST s; public
	 * CompStmtAST(VarDeclPartAST var, StmtListAST stmt){ v=var; s=stmt;
	 * v.parent=s.parent=this; } public Object visit(Visitor v, Object o) throws
	 * CompilationException{ return v.visitCompStmtAST(this,o); }
	 */
	public StmtListAST stmtListAST;

	public CompStmtAST(StmtListAST stmt) {
		stmtListAST = stmt;
		stmtListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCompStmtAST(this, o);
	}
}