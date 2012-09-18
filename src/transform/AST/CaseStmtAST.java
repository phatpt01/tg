package transform.AST;

import transform.CodeGeneration.Visitor;

public class CaseStmtAST extends OneStmtAST {
	public ExprAST exprAST;
	public StmtListAST stmtListAST;

	public CaseStmtAST(ExprAST exprAST, StmtListAST stmtListAST) {
		this.exprAST = exprAST;
		this.stmtListAST = stmtListAST;
		
		exprAST.parentAST = this;
		stmtListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCaseStmtAST(this, o);
	}
}
