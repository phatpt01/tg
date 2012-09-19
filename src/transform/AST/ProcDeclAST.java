package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class ProcDeclAST extends OneProcDeclAST {
	
	public Token op;
	public ParaListAST paraListAST;
	public CompStmtAST compStmtAST;

	public ProcDeclAST(Token op, ParaListAST pl, CompStmtAST comp) {
		this.op = op;
		this.paraListAST = pl;
		this.compStmtAST = comp;
		
		this.paraListAST.parentAST = this.compStmtAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitProcDeclAST(this, o);
	}
}
