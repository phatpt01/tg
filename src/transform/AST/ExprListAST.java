package transform.AST;

import transform.CodeGeneration.Visitor;

public class ExprListAST extends AST {

	public ExprAST exprAST;
	public ExprListAST exprListAST;
	public String line_str;

	public ExprListAST() {
		exprAST = null;
		exprListAST = null;
	}

	public ExprListAST(ExprAST exprAST, ExprListAST exprListAST) {
		this.exprAST = exprAST;
		this.exprListAST = exprListAST;
		
		exprAST.parentAST = exprListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitExprListAST(this, o);
	}
}
