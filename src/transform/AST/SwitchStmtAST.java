package transform.AST;

import transform.CodeGeneration.Visitor;

public class SwitchStmtAST extends OneStmtAST {
	public ExprAST e;
	public OneStmtAST o;
	public SwitchStmtAST(ExprAST expr,OneStmtAST one){
		//name=t;
		e=expr;
		o=one;
		e.parent= this;
		one.parent=this;
	}
	public Object visit(Visitor v, Object o) throws CompilationException{
		return v.visitSwitchStmtAST(this,o);
	}
}
