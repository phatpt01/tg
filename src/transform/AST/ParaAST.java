package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class ParaAST extends AST {

	public Token op;
	public TypeAST typeAST;
	public boolean isRef; // true if pass by reference, false otherwise

	public ParaAST(Token op, TypeAST typeAST, boolean isRef) {
		this.op = op;
		this.typeAST = typeAST;
		this.isRef = isRef;
		
		this.typeAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitParaAST(this, o);
	}
}
