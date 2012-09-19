package transform.AST;

import org.antlr.runtime.*;

import transform.CodeGeneration.Visitor;

public class DefineDirectiveAST extends DirectiveAST {
	public Token id;
	public ExprAST exprAST;

	public DefineDirectiveAST(Token tk, ExprAST exprAST) {
		this.id = tk;
		this.exprAST = exprAST;
		
		this.exprAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDefineDirectiveAST(this, o);
	}

}
