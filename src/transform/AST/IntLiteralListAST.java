package transform.AST;

import transform.CodeGeneration.Visitor;

public class IntLiteralListAST extends AST {
	
	public IntLiteralAST intLiteralAST;
	public IntLiteralListAST intLiteralListAST;

	public IntLiteralListAST() {
		intLiteralAST = null;
		intLiteralListAST = null;
	}

	public IntLiteralListAST(IntLiteralAST intLiteralAST, IntLiteralListAST intLiteralListAST) {
		this.intLiteralAST = intLiteralAST;
		this.intLiteralListAST = intLiteralListAST;
		
		this.intLiteralAST.parentAST = this.intLiteralListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitIntLiteralListAST(this, o);
	}
}