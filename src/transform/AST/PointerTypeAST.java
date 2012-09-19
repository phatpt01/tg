package transform.AST;

import transform.CodeGeneration.Visitor;

public class PointerTypeAST extends PrimTypeAST {
	public TypeAST typeAST;

	public PointerTypeAST(TypeAST type) {
		this.typeAST = type;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitPointerTypeAST(this, o);
	}
}
