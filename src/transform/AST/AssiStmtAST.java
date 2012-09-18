package transform.AST;

import transform.CodeGeneration.Visitor;

public class AssiStmtAST extends OneStmtAST {

	public LvalueAST lValueAST;
	public ExprAST exprAST;

	public AssiStmtAST(LvalueAST lValueAST, ExprAST exprAST) {

		this.lValueAST = lValueAST;
		this.exprAST = exprAST;

		exprAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitAssiStmtAST(this, o);
	}
}