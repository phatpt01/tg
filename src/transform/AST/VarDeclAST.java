package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class VarDeclAST extends DeclarationAST {
	
	public Token op;
	public TypeAST typeAST;
	public InitializerAST init;

	public VarDeclAST() {
	}

	public VarDeclAST(Token tk, TypeAST type, InitializerAST i) {
		this.op = tk;
		this.typeAST = type;
		this.init = i;
		
		this.typeAST.parentAST = this;
		if (this.init != null) {
			this.init.parentAST = this;
		}
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitVarDeclAST(this, o);
	}
}