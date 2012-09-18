package transform.AST;

import transform.CodeGeneration.Visitor;

public class DoStmtAST extends OneStmtAST {
	public ExprAST e;
	public OneStmtAST o;
	public String line_str;

	public DoStmtAST(ExprAST exp, OneStmtAST stmt) {
		e = exp;
		o = stmt;
		e.parentAST = o.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDoStmtAST(this, o);
	}
}