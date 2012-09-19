package transform.AST;

import transform.CodeGeneration.Visitor;

public class ForInitAST extends AST {
	
	public int type; // 1:localVarDecl, 2: expressions, 3:null
	public DeclarationListAST declarationListAST;
	public ExprListAST exprListAST;

	public ForInitAST(int type, DeclarationListAST declarationListAST, ExprListAST exprListAST) {
	
		this.type = type;
		this.declarationListAST = declarationListAST;
		this.exprListAST = exprListAST;
		
		if (this.declarationListAST != null) {
			this.declarationListAST.parentAST = this;
		}
		if (this.exprListAST != null) {
			this.exprListAST.parentAST = this;
		}
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitForInitAST(this, o);
	}
}
