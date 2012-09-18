package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class BoolLiteralAST extends LiteralAST {
	
	public BoolLiteralAST(Token op) {
		this.literalToken = op;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitBoolLiteralAST(this, o);
	}
}
