package transform.AST;

import transform.CodeGeneration.Visitor;

public class IfThenElseStmtAST extends OneStmtAST {
	
	public ExprAST exprAST;
	public OneStmtAST oneStmtAST1;
	public OneStmtAST oneStmtAST2;
	public String line_str = "";
	public int line_else = -1;

	public IfThenElseStmtAST(ExprAST exprAST, OneStmtAST oneStmtAST1, OneStmtAST oneStmtAST2) {
		this.exprAST = exprAST;
		this.oneStmtAST1 = oneStmtAST1;
		this.oneStmtAST2 = oneStmtAST2;
		
		this.exprAST.parentAST = this.oneStmtAST1.parentAST = this.oneStmtAST2.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitIfThenElseStmtAST(this, o);
	}
}