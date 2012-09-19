package transform.AST;

import transform.CodeGeneration.Visitor;

public class RepeatStmtAST extends OneStmtAST {
	
	public StmtListAST stmtListAST;
	public ExprAST exprAST;

	public RepeatStmtAST(StmtListAST stmt, ExprAST exp) {
		stmtListAST = stmt;
		exprAST = exp;
		
		stmtListAST.parentAST = exprAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitRepeatStmtAST(this, o);
	}
}