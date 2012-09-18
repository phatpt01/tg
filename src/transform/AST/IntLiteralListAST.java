package transform.AST;

import transform.CodeGeneration.Visitor;

public class IntLiteralListAST extends AST {
	public IntLiteralAST i;
	public IntLiteralListAST l;

	public IntLiteralListAST() {
		i = null;
		l = null;
	}

	public IntLiteralListAST(IntLiteralAST lit, IntLiteralListAST list) {
		i = lit;
		l = list;
		i.parentAST = l.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitIntLiteralListAST(this, o);
	}
}