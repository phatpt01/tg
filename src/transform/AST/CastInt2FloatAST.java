package transform.AST;

import transform.CodeGeneration.Visitor;

public class CastInt2FloatAST extends ExprAST {
	
	public ExprAST exprAST;

	public CastInt2FloatAST(ExprAST exprAST) {
		this.exprAST = exprAST;
		
		this.exprAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCastInt2FloatAST(this, o);
	}
}
