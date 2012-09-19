package transform.AST;

import transform.CodeGeneration.Visitor;

public class VarInitializerAST extends InitializerAST {
	public ExprAST exprAST;

	public VarInitializerAST(ExprAST ex) {
		this.exprAST = ex;
		
		this.exprAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarInitializerAST(this, o);
	}

}
