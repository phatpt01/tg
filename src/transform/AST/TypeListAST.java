package transform.AST;

import transform.CodeGeneration.Visitor;

public class TypeListAST extends TypeAST {
	public TypeAST typeAST;
	public TypeListAST typeListAST;

	public TypeListAST() {
		this.typeAST = null;
		this.typeListAST = null;
	}

	public TypeListAST(TypeAST type, TypeListAST typel) {
		this.typeAST = type;
		this.typeListAST = typel;
	
		this.typeAST.parentAST = this.typeListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitTypeListAST(this, o);
	}

}
