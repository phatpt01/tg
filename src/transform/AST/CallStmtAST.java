package transform.AST;

import org.antlr.runtime.*;

import transform.CodeGeneration.Visitor;

public class CallStmtAST extends OneStmtAST {
	
	public Token op;
	public ExprListAST exprListAST;

	public CallStmtAST(Token op, ExprListAST exprListAST) {
		this.op = op;
		this.exprListAST = exprListAST;
	
		exprListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitCallStmtAST(this, o);
	}
}