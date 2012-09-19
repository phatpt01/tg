package transform.AST;

import transform.CodeGeneration.Visitor;

public class ForStmtAST extends OneStmtAST {

	public ForInitAST forInitAST;
	public ExprAST exprAST;
	public ExprListAST exprListAST;
	public OneStmtAST oneStmtAST;
	public String s2 = "";

	public ForStmtAST(ForInitAST forInitAST, ExprAST exprAST, ExprListAST exprListAST,
			OneStmtAST oneStmtAST) {

		this.forInitAST = forInitAST;
		this.exprAST = exprAST;
		this.exprListAST = exprListAST;
		this.oneStmtAST = oneStmtAST;
		
		if (this.forInitAST != null) {
			this.forInitAST.parentAST = this;
		}
		if (this.exprAST != null) {
			this.exprAST.parentAST = this;
		}
		if (this.exprListAST != null) {
			this.exprListAST.parentAST = this;
		}
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitForStmtAST(this, o);
	}
}
