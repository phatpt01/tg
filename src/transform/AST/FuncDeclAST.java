package transform.AST;

import org.antlr.runtime.Token;

import transform.CodeGeneration.Visitor;

public class FuncDeclAST extends DeclarationAST {
	public Token name;
	public ParaListAST paraListAST;
	public CompStmtAST compStmtAST;
	public TypeAST typeAST;

	public FuncDeclAST(Token n, ParaListAST pl, TypeAST rt, CompStmtAST comp) {
		this.name = n;
		this.paraListAST = pl;
		this.typeAST = rt;
		this.compStmtAST = comp;

		this.paraListAST.parentAST = this.typeAST.parentAST = this;
	}

	@Override
	public Object visit(Visitor v, Object o) throws CompilationException {
		return v.visitFuncDeclAST(this, o);
	}
}
