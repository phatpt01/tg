package transform.AST;

import transform.CodeGeneration.Visitor;

public class VarDeclPartAST extends AST {
	public VarDeclAST v;
	public VarDeclPartAST vp;

	public VarDeclPartAST() {
		v = null;
		vp = null;
	}

	public VarDeclPartAST(VarDeclAST var, VarDeclPartAST varPart) {
		v = var;
		vp = varPart;
		v.parentAST = vp.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarDeclPartAST(this, o);
	}
}