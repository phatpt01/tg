package transform.AST;

import org.antlr.runtime.*;

import transform.CodeGeneration.Visitor;

public class StringLiteralAST extends LiteralAST {

	public StringLiteralAST(Token op) {
		literalToken = op;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitStringLiteralAST(this, o);
	}
}