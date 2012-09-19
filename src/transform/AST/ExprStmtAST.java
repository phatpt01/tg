package transform.AST;

import transform.CodeGeneration.Visitor;

public class ExprStmtAST extends OneStmtAST {
	public ExprAST exprAST;
	public String line_str;

	public ExprStmtAST(ExprAST ex) {
		this.exprAST = ex;
		this.exprAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitExprStmtAST(this, o);
	}
}
