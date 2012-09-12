package transform.AST;

import org.antlr.runtime.*;


import transform.CodeGeneration.Visitor;

public class DefineDirectiveAST extends DirectiveAST {
	public Token id;
	public ExprAST l;
	
	public DefineDirectiveAST(Token tk, ExprAST e) {
		id = tk;
		l = e;
		l.parent = this;
	}	
	
	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		// TODO Auto-generated method stub
		return v.visitDefineDirectiveAST(this, o);
	}

}
