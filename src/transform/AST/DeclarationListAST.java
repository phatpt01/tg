package transform.AST;

import transform.CodeGeneration.Visitor;

public class DeclarationListAST extends AST {
	
	public DeclarationAST declarationAST;
	public DeclarationListAST declarationListAST;

	public DeclarationListAST() {
		this.declarationAST = null;
		this.declarationListAST = null;
	}

	public DeclarationListAST(DeclarationAST decl, DeclarationListAST decll) {
		this.declarationAST = decl;
		this.declarationListAST = decll;
		this.declarationAST.parentAST = this.declarationListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDeclarationListAST(this, o);
	}

}
