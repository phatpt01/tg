package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class FloatLiteralAST extends LiteralAST {

	public FloatLiteralAST(Token t) {
		this.literalToken = t;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitFloatLiteralAST(this, o);
	}
}
