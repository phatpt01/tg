package transform.AST;

import transform.CodeGeneration.Visitor;

public class TernaryExprAST extends ExprAST {
	
	public ExprAST exprAST1;
	public ExprAST exprAST2;
	public ExprAST exprAST3;
	public int l1;
	public int l2;
	public int l3;
	public String s1;
	public String s2;
	public String s3;

	public String line_str = "";
	public int line_else = -1;

	public TernaryExprAST(ExprAST exp1, ExprAST exp2, ExprAST exp3) {
		exprAST1 = exp1;
		exprAST2 = exp2;
		exprAST3 = exp3;
		
		exprAST1.parentAST = exprAST2.parentAST = exprAST3.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitTernaryExprAST(this, o);
	}
}
