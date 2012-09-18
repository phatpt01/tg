package transform.AST;

import transform.CodeGeneration.Visitor;

public class GlobalVarDeclPartAST extends OneProcDeclAST {
	public VarDeclPartAST vp;

	public GlobalVarDeclPartAST(VarDeclPartAST varPart) {
		vp = varPart;
		vp.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitGlobalVarDeclPartAST(this, o);
	}

}
