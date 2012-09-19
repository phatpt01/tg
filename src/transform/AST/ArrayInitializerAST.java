package transform.AST;

import transform.CodeGeneration.Visitor;

public class ArrayInitializerAST extends InitializerAST {
	public VarInitializerListAST varInitializerListAST;

	public ArrayInitializerAST() {
		varInitializerListAST = null;
	}

	public ArrayInitializerAST(VarInitializerListAST vx) {
		varInitializerListAST = vx;
		varInitializerListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitArrayInitializerAST(this, o);
	}

}
