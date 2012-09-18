package transform.AST;

import transform.CodeGeneration.Visitor;

public class CastExprAST extends ExprAST {

	public TypeListAST typeListAST;
	public ExprAST exprAST;

	public CastExprAST(TypeListAST typeListAST, ExprAST exprAST) {
		this.typeListAST = typeListAST;
		this.exprAST = exprAST;

		this.typeListAST.parentAST = this.exprAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCastExprAST(this, o);
	}

}
