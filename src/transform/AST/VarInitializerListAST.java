package transform.AST;

import transform.CodeGeneration.Visitor;

public class VarInitializerListAST extends InitializerAST {
	public VarInitializerAST varInitializerAST;
	public VarInitializerListAST varInitializerListAST;

	public VarInitializerListAST() {
		this.varInitializerAST = null;
		this.varInitializerListAST = null;
	}

	public VarInitializerListAST(VarInitializerAST vx, VarInitializerListAST vlx) {
		this.varInitializerAST = vx;
		this.varInitializerListAST = vlx;

		this.varInitializerAST.parentAST = this.varInitializerListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarInitializerListAST(this, o);
	}
}