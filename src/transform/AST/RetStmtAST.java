package transform.AST;

import transform.CodeGeneration.Visitor;

public class RetStmtAST extends OneStmtAST {
	
	public ExprAST exprAST;
	public String line_str;

	public RetStmtAST() {
		exprAST = null;
	}

	public RetStmtAST(ExprAST exp) {
		exprAST = exp;
	
		exprAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitRetStmtAST(this, o);
	}
}