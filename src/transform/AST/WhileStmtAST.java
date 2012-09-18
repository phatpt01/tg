package transform.AST;

import transform.CodeGeneration.Visitor;

public class WhileStmtAST extends OneStmtAST {
	public ExprAST e;
	public OneStmtAST o;
	public String line_str;

	public WhileStmtAST(ExprAST exp, OneStmtAST stmt) {
		this.e = exp;
		this.o = stmt;
		this.e.parentAST = this.o.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitWhileStmtAST(this, o);
	}
}
