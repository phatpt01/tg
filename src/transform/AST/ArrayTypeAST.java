package transform.AST;

import transform.CodeGeneration.Visitor;

public class ArrayTypeAST extends TypeAST {
	
	public TypeAST typeAST;
	public ExprListAST exprListAST;

	public ArrayTypeAST(TypeAST prim, ExprListAST l) {
		this.typeAST = prim;
		this.exprListAST = l;
		this.typeAST.parentAST = this.exprListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitArrayTypeAST(this, o);
	}
}
