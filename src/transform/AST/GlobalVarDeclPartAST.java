package transform.AST;

import transform.CodeGeneration.Visitor;

public class GlobalVarDeclPartAST extends OneProcDeclAST {
	public VarDeclPartAST varDeclPartAST;

	public GlobalVarDeclPartAST(VarDeclPartAST varPart) {
		varDeclPartAST = varPart;
		varDeclPartAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitGlobalVarDeclPartAST(this, o);
	}

}
