package transform.AST;

import transform.CodeGeneration.Visitor;

public class DeclarationStmtAST extends OneStmtAST {
	public DeclarationListAST declarationListAST;

	public DeclarationStmtAST(DeclarationListAST decl) {
		this.declarationListAST = decl;
		this.declarationListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDeclarationStmtAST(this, o);
	}
}
