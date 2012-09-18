package transform.AST;

import transform.CodeGeneration.Visitor;

public class SwitchStmtAST extends OneStmtAST {
	public ExprAST exprAST;
	public OneStmtAST oneStmtAST;

	public SwitchStmtAST(ExprAST expr, OneStmtAST one) {
		exprAST = expr;
		oneStmtAST = one;
		exprAST.parentAST = this;
		one.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitSwitchStmtAST(this, o);
	}
}
