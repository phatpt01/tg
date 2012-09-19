package transform.AST;

import transform.CodeGeneration.Visitor;

public class ParaListAST extends AST {
	
	public ParaAST paraAST;
	public ParaListAST paraListAST;

	public ParaListAST() {
		paraAST = null;
		paraListAST = null;
	}

	public ParaListAST(ParaAST var, ParaListAST par) {
		paraAST = var;
		paraListAST = par;
		
		paraAST.parentAST = paraListAST.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitParaListAST(this, o);
	}
}
