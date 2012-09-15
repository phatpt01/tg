package transform.AST;

import transform.CodeGeneration.Visitor;

public class DeclarationListAST extends AST {
	public DeclarationAST d;
	public DeclarationListAST dl;

	public DeclarationListAST() {
		this.d = null;
		this.dl = null;
	}

	public DeclarationListAST(DeclarationAST decl, DeclarationListAST decll) {
		this.d = decl;
		this.dl = decll;
		this.d.parent = this.dl.parent = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitDeclarationListAST(this, o);
	}

}
