package transform.AST;

import transform.CodeGeneration.Visitor;

public class ArrayInitializerAST extends InitializerAST {
	public VarInitializerListAST v;

	public ArrayInitializerAST() {
		v = null;
	}

	public ArrayInitializerAST(VarInitializerListAST vx) {
		v = vx;
		v.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitArrayInitializerAST(this, o);
	}

}
