package transform.AST;

import transform.CodeGeneration.Visitor;

public class WhileStmtAST extends OneStmtAST {
	public ExprAST exprAST;
	public OneStmtAST oneStmtAST;
	public String line_str;

	public WhileStmtAST(ExprAST exprAST, OneStmtAST oneStmtAST) {
		
		this.exprAST = exprAST;
		this.oneStmtAST = oneStmtAST;
		
		this.exprAST.parentAST = this.oneStmtAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitWhileStmtAST(this, o);
	}
}
