package transform.AST;

import transform.CodeGeneration.Visitor;

public class LongTypeAST extends PrimTypeAST {

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitLongTypeAST(this, o);
	}

}