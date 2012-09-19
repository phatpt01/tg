package transform.AST;

import transform.CodeGeneration.Visitor;

public class ProcDeclPartAST extends AST {
	
	public OneProcDeclAST oneProcDeclAST;
	public ProcDeclPartAST procDeclPartAST;

	public ProcDeclPartAST() {
		this.oneProcDeclAST = null;
		this.procDeclPartAST = null;
	}

	public ProcDeclPartAST(OneProcDeclAST one, ProcDeclPartAST pro) {
		this.oneProcDeclAST = one;
		this.procDeclPartAST = pro;
		
		this.oneProcDeclAST.parentAST = this.procDeclPartAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitProcDeclPartAST(this, o);
	}
}
