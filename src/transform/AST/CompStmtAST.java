package transform.AST;

import transform.CodeGeneration.Visitor;

public class CompStmtAST extends OneStmtAST {

	public StmtListAST stmtListAST;

	public CompStmtAST(StmtListAST stmt) {
		stmtListAST = stmt;
		stmtListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCompStmtAST(this, o);
	}
}