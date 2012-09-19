package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class EleExprAST extends LvalueAST {
	
	public Token name;
	public ExprListAST exprListAST;

	public EleExprAST(Token t, ExprListAST exprListAST) {
		this.name = t;
		this.exprListAST = exprListAST;
	
		this.exprListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitEleExprAST(this, o);
	}
}
