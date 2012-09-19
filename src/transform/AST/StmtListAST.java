package transform.AST;

import transform.CodeGeneration.Visitor;

public class StmtListAST extends AST {
	
	public OneStmtAST oneStmtAST;
	public StmtListAST stmtListAST;

	public StmtListAST() {
		oneStmtAST = null;
		stmtListAST = null;
	}

	public StmtListAST(OneStmtAST one, StmtListAST stmt) {
		oneStmtAST = one;
		stmtListAST = stmt;
		
		stmtListAST.parentAST = oneStmtAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitStmtListAST(this, o);
	}
}