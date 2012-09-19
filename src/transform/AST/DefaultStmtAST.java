package transform.AST;

import transform.CodeGeneration.Visitor;

public class DefaultStmtAST extends OneStmtAST {
	public StmtListAST stmtListAST;

	public DefaultStmtAST(StmtListAST stmtListAST) {
		this.stmtListAST = stmtListAST;
		this.stmtListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDefaultStmtAST(this, o);
	}
}
