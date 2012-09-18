package transform.AST;

import org.antlr.runtime.*;

import transform.CodeGeneration.Visitor;

public class CallExprAST extends ExprAST {
	
	public Token op;
	public ExprListAST exprListAST;

	public CallExprAST(Token op, ExprListAST exprListAST) {
		this.op = op;
		this.exprListAST = exprListAST;
		exprListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCallExprAST(this, o);
	}
}
