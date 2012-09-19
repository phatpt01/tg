package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class VarExprAST extends LvalueAST {
	public Token op;

	public VarExprAST(Token op) {
		this.op = op;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarExprAST(this, o);
	}
}
