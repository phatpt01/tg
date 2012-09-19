package transform.AST;

import transform.CodeGeneration.Visitor;

public class VarDeclPartAST extends AST {
	
	public VarDeclAST varDeclAST;
	public VarDeclPartAST varDeclPartAST;

	public VarDeclPartAST() {
		varDeclAST = null;
		varDeclPartAST = null;
	}

	public VarDeclPartAST(VarDeclAST var, VarDeclPartAST varPart) {
		varDeclAST = var;
		varDeclPartAST = varPart;
		
		varDeclAST.parentAST = varDeclPartAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarDeclPartAST(this, o);
	}
}