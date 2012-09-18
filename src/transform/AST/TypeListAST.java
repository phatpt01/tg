package transform.AST;

import transform.CodeGeneration.Visitor;

public class TypeListAST extends TypeAST {
	public TypeAST t;
	public TypeListAST tl;

	public TypeListAST() {
		this.t = null;
		this.tl = null;
	}

	public TypeListAST(TypeAST type, TypeListAST typel) {
		this.t = type;
		this.tl = typel;
		this.t.parentAST = this.tl.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitTypeListAST(this, o);
	}

}
