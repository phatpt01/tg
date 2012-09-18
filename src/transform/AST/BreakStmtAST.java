package transform.AST;

import org.antlr.runtime.*;

import transform.CodeGeneration.Visitor;

public class BreakStmtAST extends OneStmtAST {
	public Token op;

	public BreakStmtAST(Token op) {
		this.op = op;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitBreakStmtAST(this, o);
	}
}