package transform.AST;

import transform.CodeGeneration.Visitor;

public class DoStmtAST extends OneStmtAST {
	public ExprAST exprAST;
	public OneStmtAST oneStmtAST;
	public String line_str;

	public DoStmtAST(ExprAST exprAST, OneStmtAST oneStmtAST) {
		this.exprAST = exprAST;
		this.oneStmtAST = oneStmtAST;
		
		this.exprAST.parentAST = this.oneStmtAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDoStmtAST(this, o);
	}
}