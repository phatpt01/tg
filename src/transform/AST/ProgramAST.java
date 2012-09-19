package transform.AST;

import transform.CodeGeneration.Visitor;

public class ProgramAST extends AST {
	
	public DeclarationListAST declarationListAST;

	public ProgramAST(DeclarationListAST declarationListAST) {
		this.declarationListAST = declarationListAST;
	
		this.declarationListAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitProgramAST(this, o);
	}
}
