package transform.AST;

import transform.CodeGeneration.Visitor;

public class IfThenStmtAST extends OneStmtAST {

	public ExprAST exprAST;
	public OneStmtAST oneStmtAST;
	public String line_str = "";

	public IfThenStmtAST(ExprAST exprAST, OneStmtAST oneStmtAST) {
		this.exprAST = exprAST;
		this.oneStmtAST = oneStmtAST;

		this.exprAST.parentAST = this.oneStmtAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitIfThenStmtAST(this, o);
	}
}