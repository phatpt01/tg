package transform.AST;

import transform.CodeGeneration.Visitor;

public class ArrayInitializerListAST extends InitializerAST {

	public ArrayInitializerAST arrayInitializerAST;
	public ArrayInitializerListAST arrayInitializerAST1;

	public ArrayInitializerListAST() {
		arrayInitializerAST = null;
		arrayInitializerAST1 = null;
	}

	public ArrayInitializerListAST(ArrayInitializerAST arrayInitializerAST,
			ArrayInitializerListAST arrayInitializerAST1) {
		this.arrayInitializerAST = arrayInitializerAST;
		this.arrayInitializerAST1 = arrayInitializerAST1;

		arrayInitializerAST.parentAST = arrayInitializerAST1.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitArrayInitializerListAST(this, o);
	}
}