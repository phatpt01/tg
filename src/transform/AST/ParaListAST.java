package transform.AST;

import transform.CodeGeneration.Visitor;

public class ParaListAST extends AST {
	public ParaAST v;
	public ParaListAST p;

	public ParaListAST() {
		v = null;
		p = null;
	}

	public ParaListAST(ParaAST var, ParaListAST par) {
		v = var;
		p = par;
		v.parentAST = p.parentAST = this;
	}

	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitParaListAST(this, o);
	}
}
