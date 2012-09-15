package transform.AST;

import transform.CodeGeneration.Visitor;

public class RetStmtAST extends OneStmtAST {
	public ExprAST e;
	public String line_str;

	public RetStmtAST() {
		e = null;
	}

	public RetStmtAST(ExprAST exp) {
		e = exp;
		e.parent = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitRetStmtAST(this, o);
	}
}