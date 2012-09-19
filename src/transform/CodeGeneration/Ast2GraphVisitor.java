package transform.CodeGeneration;

import system.VariableUsed;
import transform.AST.*;
import transform.DependenceGraph.*;
import java.util.*;

/***********************************************************************************
 * Visitor duyet qua cay AST de tao ra Program Dependence Graph
 ***********************************************************************************/
public class Ast2GraphVisitor extends DoNothingVisitor {
	TableAssignedVariable tableAssignedVariable; // <=== table luu bien duoc gan tai
											// moi dong
	PDG graph;
	ControlDependence currentLevelNode; // <=== dung de tinh toan phu thuoc dieu khien

	// bien constant bao hieu dang duyet cac lenh trong vong loop de tim nhung
	// bien duoc gan
	// khong tao ra node hay tim DataDependence trong luc duyet
	static final String FIND_ASSIGN_STMT_IN_LOOP = "findAssiInLoop";

	// bien constant bao hieu 1 statement dang o trong 1 loop
	static final String STMT_IN_LOOP = "stmtInLoop";

	public Ast2GraphVisitor() {
		tableAssignedVariable = new TableAssignedVariable();
		graph = new PDG();
		currentLevelNode = new ControlDependence();
	}

	public void add2ListDataDep(ArrayList<DataDependence> list1,
			ArrayList<DataDependence> list2) {
		int size1 = list1.size();
		for (int i = 0; i < list2.size(); i++) {
			boolean existsInList1 = false;
			for (int j = 0; j < size1; j++) {
				if (list1.get(j).getID() == list2.get(i).getID()
						&& list1.get(j).getVarName()
								.equals(list2.get(i).getVarName())) {
					existsInList1 = true;
					break;
				}
			}
			if (!existsInList1)
				list1.add(list2.get(i));
		}
	}

	public boolean checkEquals(Object o, String s) {
		String str = (String) o;
		if (s.equals(str))
			return true;
		return false;
	}

	public int getLineFromNode(OneStmtAST ast) {
		int line = 0;
		if (ast instanceof ExprStmtAST)
			line = getLineOfExpr(((ExprStmtAST) ast).exprAST);
		else if (ast instanceof IfThenElseStmtAST)
			line = getLineOfExpr(((IfThenElseStmtAST) ast).exprAST);
		else if (ast instanceof IfThenStmtAST)
			line = getLineOfExpr(((IfThenStmtAST) ast).exprAST);
		else if (ast instanceof WhileStmtAST)
			line = getLineOfExpr(((WhileStmtAST) ast).exprAST);
		else if (ast instanceof ForStmtAST)
			line = getLineOfExpr(((ForStmtAST) ast).exprAST);
		else if (ast instanceof SwitchStmtAST)
			line = getLineOfExpr(((SwitchStmtAST) ast).exprAST);
		else if (ast instanceof CaseStmtAST)
			line = getLineOfExpr(((CaseStmtAST) ast).exprAST);
		else if (ast instanceof BreakStmtAST)
			line = ((BreakStmtAST) ast).op.getLine();
		else if (ast instanceof ContStmtAST)
			line = ((ContStmtAST) ast).t.getLine();
		else if (ast instanceof RetStmtAST)
			line = getLineOfExpr(((RetStmtAST) ast).exprAST);

		return line;
	}

	public int getLineOfExpr(ExprAST exprAST) {
		int line = 0;
		if (exprAST instanceof BinExprAST)
			line = ((BinExprAST) exprAST).op.getLine();
		if (exprAST instanceof UnaryExprAST)
			line = ((UnaryExprAST) exprAST).op.getLine();
		if (exprAST instanceof CallExprAST)
			line = ((CallExprAST) exprAST).op.getLine();
		if (exprAST instanceof VarExprAST)
			line = ((VarExprAST) exprAST).op.getLine();
		if (exprAST instanceof LiteralAST)
			line = ((LiteralAST) exprAST).literalToken.getLine();
		if (exprAST instanceof EleExprAST)
			line = ((EleExprAST) exprAST).name.getLine();
		return line;
	}

	public PDG getProgramDependenceGraph() {
		return graph;
	}

	public String getTableAssignedVar() {
		return tableAssignedVariable.toString();
	}

	@SuppressWarnings("rawtypes")
	public void printArrayList(ArrayList list) {
		System.out.println("ArrayList:");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("EndArrayList:");
	}

	/***********************************************************************/
	public void println(Object o) {
		System.out.println(o);
	}

	// ArrayInitializer
	public Object visitArrayInitializerAST(ArrayInitializerAST ast, Object o)
			throws CompilationException {
		ast.varInitializerListAST.visit(this, o);
		return null;
	}

	// ArrayInitializerList
	public Object visitArrayInitializerListAST(ArrayInitializerListAST ast,
			Object o) throws CompilationException {
		ast.arrayInitializerAST.visit(this, o);
		ast.arrayInitializerAST1.visit(this, o);
		return null;
	}

	//
	// /////////////////////////////////////////////////////////////////////////////////

	// ArrayTypeAST
	public Object visitArrayTypeAST(ArrayTypeAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		ast.exprListAST.visit(this, o);
		return null;
	}

	// BinExprAST
	@SuppressWarnings("unchecked")
	public Object visitBinExprAST(BinExprAST ast, Object o)
			throws CompilationException {
		ast.line = ast.op.getLine();
		ArrayList<DataDependence> list1 = (ArrayList<DataDependence>) ast.exprAST1.visit(this, o);
		ArrayList<DataDependence> list2 = (ArrayList<DataDependence>) ast.exprAST2.visit(this, o);

		// cac loai phep gan =, +=, -=, *=, /=, %=
		if (ast.opType >= BinExprAST.ASSIGN
				&& ast.opType <= BinExprAST.MOD_ASSIGN) {
			// day la phep gan
			String varNameAssigned = list1.get(0).getVarName();
			// luu bien duoc gan vao danh sach
			tableAssignedVariable.addAssignedVar(ast.line, varNameAssigned);
			if (ast.parentAST instanceof ExprStmtAST) {
				// neu return true <=> dang tim kiem lenh gan trong vong lap
				if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP))
					return "skip_adding_node";
			}
		}

		// truong hop phep gan thi DataDep = list2
		if (ast.opType == BinExprAST.ASSIGN
				&& (ast.parentAST instanceof ExprStmtAST || ast.parentAST instanceof ExprListAST))
			return list2;

		// cac truong hop khac thi add 2 danh sach du lieu phu thuoc
		if (list1 != null && list2 != null) {
			add2ListDataDep(list1, list2);
			return list1;
		}
		if (list1 == null && list2 != null)
			return list2;
		if (list1 != null && list2 == null)
			return list1;
		return null;
	}

	// BoolLiteralAST
	public Object visitBoolLiteralAST(BoolLiteralAST ast, Object o)
			throws CompilationException {
		// print(indentString() + ast.literal.getText());
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// chua xu ly cac cau lenh ben duoi
	// BreakStmtAST
	public Object visitBreakStmtAST(BreakStmtAST ast, Object o)
			throws CompilationException {
		ast.line = ast.op.getLine();
		println("BreakStmtAST: " + ast.line);
		return null;
	}

	// CallExprAST
	@SuppressWarnings("unchecked")
	public Object visitCallExprAST(CallExprAST ast, Object o)
			throws CompilationException {
		ArrayList<DataDependence> list = (ArrayList<DataDependence>) ast.exprListAST.visit(this, o);
		return list;
	}

	// CaseStmtAST
	public Object visitCaseStmtAST(CaseStmtAST cAst, Object o)
			throws CompilationException {
		cAst.line = getLineOfExpr(cAst.exprAST);
		println("CaseStmtAST: " + cAst.line);
		cAst.exprAST.visit(this, o);
		cAst.stmtListAST.visit(this, o);
		return null;
	}

	// CharLiteralAST
	public Object visitCharLiteralAST(CharLiteralAST ast, Object o)
			throws CompilationException {
		// print(indentString() + ast.literal.getText());
		return null;
	}

	// CompStmtAST
	public Object visitCompStmtAST(CompStmtAST cAst, Object o)
			throws CompilationException {
		cAst.stmtListAST.visit(this, o);
		return null;
	}

	// ContStmtAST
	public Object visitContStmtAST(ContStmtAST ast, Object o)
			throws CompilationException {
		ast.line = ast.t.getLine();
		println("ContStmtAST: " + ast.line);
		return null;
	}

	// DeclarationListAST
	public Object visitDeclarationListAST(DeclarationListAST ast, Object o)
			throws CompilationException {
		ast.declarationAST.visit(this, o);
		ast.declarationListAST.visit(this, o);
		return null;
	}

	// DeclarationStmtAST
	public Object visitDeclarationStmtAST(DeclarationStmtAST ast, Object o)
			throws CompilationException {
		ast.declarationListAST.visit(this, o);
		return null;
	}

	// DefaultStmtAST
	public Object visitDefaultStmtAST(DefaultStmtAST dAst, Object o)
			throws CompilationException {
		dAst.line = getLineFromNode(dAst.stmtListAST.oneStmtAST) - 1;
		println("DefaultStmtAST: " + dAst.line);
		dAst.stmtListAST.visit(this, o);
		return null;
	}

	//
	// /////////////////////////////////////////////////////////////////////////////////

	// DoStmtAST
	@SuppressWarnings("unchecked")
	public Object visitDoStmtAST(DoStmtAST wAst, Object o)
			throws CompilationException {
		wAst.line = getLineOfExpr(wAst.exprAST);
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) {
			wAst.oneStmtAST.visit(this, o);
			return null;
		}

		// duyet lan dau so qua de tim cac phep gan trong than vong lap
		wAst.oneStmtAST.visit(this, FIND_ASSIGN_STMT_IN_LOOP);

		// bat dau duyet chinh thuc
		ArrayList<DataDependence> data = (ArrayList<DataDependence>) wAst.exprAST.visit(this, o);
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node doNode = new Node(wAst.line, TYPE.CONDITION, data, conDep, null);

		// backup lai node o phia tren cua lenh loop
		ControlDependence backupLevelNode = new ControlDependence(currentLevelNode);
		currentLevelNode.set(doNode, true);
		wAst.oneStmtAST.visit(this, STMT_IN_LOOP);
		currentLevelNode.set(backupLevelNode);

		graph.addNode(doNode);
		return null;
	}

	// EleExprAST
	// Bieu thuc truy xuat mang: array[...][...][...]
	@SuppressWarnings("unchecked")
	public Object visitEleExprAST(EleExprAST ast, Object o)
			throws CompilationException {
		String varName = ast.name.getText();
		ArrayList<Integer> linesOfVar = tableAssignedVariable
				.getLinesOfAssignedVar(varName);
		ArrayList<DataDependence> result = new ArrayList<DataDependence>();
		for (int i = 0; i < linesOfVar.size(); i++)
			result.add(new DataDependence(linesOfVar.get(i).intValue(), varName));
		if (result.size() == 0)
			result.add(new DataDependence(0, varName));

		ArrayList<DataDependence> resExprList = (ArrayList<DataDependence>) ast.exprListAST.visit(this,
				o);
		if (resExprList != null)
			add2ListDataDep(result, resExprList);

		return result;
	}

	// ExprListAST
	@SuppressWarnings("unchecked")
	public Object visitExprListAST(ExprListAST ast, Object o)
			throws CompilationException {
		ArrayList<DataDependence> list = (ArrayList<DataDependence>) ast.exprAST.visit(this, o);
		ArrayList<DataDependence> list2 = (ArrayList<DataDependence>) ast.exprListAST.visit(this, o);
		if (list != null && list2 != null) {
			add2ListDataDep(list, list2);
			return list;
		}
		if (list == null && list2 != null)
			return list2;
		if (list != null && list2 == null)
			return list;
		return null;
	}

	// ExprStmtAST
	@SuppressWarnings("unchecked")
	public Object visitExprStmtAST(ExprStmtAST ast, Object o)
			throws CompilationException {
		ast.line = getLineOfExpr(ast.exprAST);
		ast.exprAST.line = ast.line;
		Object res = ast.exprAST.visit(this, o);
		if (res instanceof String)
			if (((String) res).equals("skip_adding_node"))
				return null;

		ArrayList<DataDependence> data = (ArrayList<DataDependence>) res;
		String varNameAssigned = "";
		// lay thong tin cua variable duoc gan
		if (ast.exprAST instanceof BinExprAST) {
			ExprAST leftSide = ((BinExprAST) ast.exprAST).exprAST1;
			if (leftSide instanceof VarExprAST)
				varNameAssigned = ((VarExprAST) leftSide).op.getText();
			else if (leftSide instanceof EleExprAST)
				varNameAssigned = ((EleExprAST) leftSide).name.getText();
		} else if (ast.exprAST instanceof UnaryExprAST)
			varNameAssigned = ((VarExprAST) ((UnaryExprAST) ast.exprAST).exprAST).op
					.getText();
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node assignNode = new Node(ast.line, TYPE.ASSIGN, data, conDep, null);
		assignNode.setDefinedVar(new VariableUsed(varNameAssigned, -1));
		graph.addNode(assignNode);
		return null;
	}

	// FloatLiteralAST
	public Object visitFloatLiteralAST(FloatLiteralAST ast, Object o)
			throws CompilationException {
		// print(indentString() + ast.literal.getText());
		return null;
	}

	// ForInitAST
	public Object visitForInitAST(ForInitAST ast, Object o)
			throws CompilationException {
		if (ast.declarationListAST != null)
			ast.declarationListAST.visit(this, o);
		if (ast.exprListAST != null)
			return ast.exprListAST.visit(this, o);
		return null;
	}

	// ForStmtAST
	@SuppressWarnings("unchecked")
	public Object visitForStmtAST(ForStmtAST fAst, Object o)
			throws CompilationException {
		fAst.line = getLineOfExpr(fAst.exprAST);
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) {
			if (fAst.forInitAST != null)
				fAst.forInitAST.visit(this, o);
			if (fAst.exprListAST != null)
				fAst.exprListAST.visit(this, o);
			fAst.oneStmtAST.visit(this, o);
			return null;
		}

		// duyet lan dau so qua de tim cac phep gan trong than vong lap
		// khong add node trong lan duyet nay
		if (fAst.forInitAST != null)
			fAst.forInitAST.visit(this, FIND_ASSIGN_STMT_IN_LOOP);
		if (fAst.exprListAST != null)
			fAst.exprListAST.visit(this, FIND_ASSIGN_STMT_IN_LOOP);
		fAst.oneStmtAST.visit(this, FIND_ASSIGN_STMT_IN_LOOP);

		// duyet lan thu 2 de tim ra data dependence
		ArrayList<DataDependence> data1 = null;
		ArrayList<DataDependence> data2 = null;
		ArrayList<DataDependence> data3 = null;
		if (fAst.forInitAST != null)
			data1 = (ArrayList<DataDependence>) fAst.forInitAST.visit(this, "");
		if (fAst.exprAST != null)
			data2 = (ArrayList<DataDependence>) fAst.exprAST.visit(this, "");
		if (fAst.exprListAST != null)
			data3 = (ArrayList<DataDependence>) fAst.exprListAST.visit(this, "");

		// tong hop lai cac du lieu phu thuoc nay vo bien data
		ArrayList<DataDependence> data = new ArrayList<DataDependence>();
		if (data1 != null)
			add2ListDataDep(data, data1);
		if (data2 != null)
			add2ListDataDep(data, data2);
		if (data3 != null)
			add2ListDataDep(data, data3);
		if (data.size() == 0)
			data = null;
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node forNode = new Node(fAst.line, TYPE.FORLOOP, data, conDep, null);
		graph.addNode(forNode);

		// backup lai node o phia tren cua lenh loop
		ControlDependence backupLevelNode = new ControlDependence(currentLevelNode);
		currentLevelNode.set(forNode, true);
		fAst.oneStmtAST.visit(this, STMT_IN_LOOP);
		currentLevelNode.set(backupLevelNode);
		return null;
	}

	// FuncDeclAST
	public Object visitFuncDeclAST(FuncDeclAST fAst, Object o)
			throws CompilationException {
		fAst.line = fAst.name.getLine();
		fAst.paraListAST.visit(this, o);
		fAst.typeAST.visit(this, o);
		Node funcNode = new Node(fAst.line, TYPE.ENTRANCE, null, null, null);
		graph.addNode(funcNode);
		currentLevelNode.set(funcNode, true);
		fAst.compStmtAST.visit(this, o);
		return null;
	}

	// IfThenElseStmtAST
	@SuppressWarnings("unchecked")
	public Object visitIfThenElseStmtAST(IfThenElseStmtAST iAst, Object o)
			throws CompilationException {
		iAst.line = getLineOfExpr(iAst.exprAST);
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) {
			iAst.oneStmtAST1.visit(this, o);
			iAst.oneStmtAST2.visit(this, o);
			return null;
		}

		ArrayList<DataDependence> data = (ArrayList<DataDependence>) iAst.exprAST.visit(this, o);
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node ifNode = new Node(iAst.line, TYPE.CONDITION, data, conDep, null);
		graph.addNode(ifNode);

		// backup lai node o phia tren cua lenh if
		ControlDependence backupLevelNode = new ControlDependence(currentLevelNode);
		if (!checkEquals(o, STMT_IN_LOOP)) {
			// neu lenh IfElse nay khong nam trong bat ky lenh loop nao
			// su dung 1 backup table de luu cac bien duoc gan trong nhanh IF
			tableAssignedVariable.useBackUpTable();
			currentLevelNode.set(ifNode, true);
			iAst.oneStmtAST1.visit(this, o);

			// ket thuc viec su dung backup table
			// luu lai trong 1 stack, quay tro ve voi main table.
			tableAssignedVariable.finishUsingBackUpTable();
			currentLevelNode.set(ifNode, false);
			iAst.oneStmtAST2.visit(this, o);

			// copy lai backup table vo main table
			tableAssignedVariable.restoreBackUpTable();
		} else {
			currentLevelNode.set(ifNode, true);
			iAst.oneStmtAST1.visit(this, o);
			currentLevelNode.set(ifNode, false);
			iAst.oneStmtAST2.visit(this, o);
		}
		currentLevelNode.set(backupLevelNode);
		return null;
	}

	// IfThenStmtAST
	@SuppressWarnings("unchecked")
	public Object visitIfThenStmtAST(IfThenStmtAST iAst, Object o)
			throws CompilationException {
		iAst.line = getLineOfExpr(iAst.exprAST);
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) {
			iAst.oneStmtAST.visit(this, o);
			return null;
		}

		ArrayList<DataDependence> data = (ArrayList<DataDependence>) iAst.exprAST.visit(this, o);
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node ifNode = new Node(iAst.line, TYPE.CONDITION, data, conDep, null);
		graph.addNode(ifNode);

		// backup lai node o phia tren cua lenh if
		ControlDependence backupLevelNode = new ControlDependence(currentLevelNode);

		// gan ifNode hien tai la node dieu khien cha
		currentLevelNode.set(ifNode, true);
		iAst.oneStmtAST.visit(this, o);

		// restore lai node da duoc backup
		currentLevelNode.set(backupLevelNode);
		return null;
	}

	// IntLiteralAST
	public Object visitIntLiteralAST(IntLiteralAST ast, Object o)
			throws CompilationException {
		// print(indentString() + ast.literal.getText());
		return null;
	}

	// ParaAST
	public Object visitParaAST(ParaAST pAst, Object o)
			throws CompilationException {
		pAst.line = pAst.op.getLine();
		// bien khai bao tai para trong ham duoc luu vao table
		String varNameAssigned = pAst.op.getText();
		// luu bien duoc gan vao danh sach
		tableAssignedVariable.addAssignedVar(pAst.line, varNameAssigned);
		pAst.typeAST.visit(this, o);
		return null;
	}

	// ParaListAST
	public Object visitParaListAST(ParaListAST pAst, Object o)
			throws CompilationException {
		pAst.paraAST.visit(this, o);
		pAst.paraListAST.visit(this, o);
		return null;
	}

	// PointerTypeAST
	public Object visitPointerTypeAST(PointerTypeAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		return null;
	}

	/***********************************************************************/

	// ProgramAST
	public Object visitProgramAST(ProgramAST ast, Object o)
			throws CompilationException {
		ast.declarationListAST.visit(this, o);

		// gan gia tri bien node trong DataDep,refer den dung Node ma:
		// Node.StatementID == DataDep.lineID
		graph.changeLineIdAtDataDepPointToNode();
		return null;
	}

	// RetStmtAST
	@SuppressWarnings("unchecked")
	public Object visitRetStmtAST(RetStmtAST rAst, Object o)
			throws CompilationException {
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP))
			return null;
		if (rAst.exprAST != null) {
			rAst.line = getLineOfExpr(rAst.exprAST);
			ArrayList<DataDependence> data = (ArrayList<DataDependence>) rAst.exprAST
					.visit(this, o);
			ControlDependence conDep = null;
			if (!currentLevelNode.isEmpty())
				conDep = new ControlDependence(currentLevelNode);
			graph.addNode(new Node(rAst.line, TYPE.RETURN, data, conDep, null));
		}
		return null;
	}

	// StmtListAST
	public Object visitStmtListAST(StmtListAST sAst, Object o)
			throws CompilationException {
		sAst.oneStmtAST.visit(this, o);
		sAst.stmtListAST.visit(this, o);
		return null;
	}

	// StringLiteralAST
	public Object visitStringLiteralAST(StringLiteralAST ast, Object o)
			throws CompilationException {
		// print(indentString() + ast.literal.getText());
		return null;
	}

	// SwitchStmtAST
	public Object visitSwitchStmtAST(SwitchStmtAST sAst, Object o)
			throws CompilationException {
		sAst.line = getLineOfExpr(sAst.exprAST);
		println("SwitchStmtAST: " + sAst.line);
		sAst.exprAST.visit(this, o);
		sAst.oneStmtAST.visit(this, o);
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////////
	// chua can xet cac ham ben duoi
	// TernaryExprAST
	public Object visitTernaryExprAST(TernaryExprAST ast, Object o)
			throws CompilationException {
		ast.exprAST1.visit(this, o);
		ast.exprAST2.visit(this, o);
		ast.exprAST3.visit(this, o);
		return null;
	}

	// TypeListAST
	public Object visitTypeListAST(TypeListAST ast, Object o)
			throws CompilationException {
		ast.typeAST.visit(this, o);
		ast.typeListAST.visit(this, o);
		return null;
	}

	// UnaryExprAST
	@SuppressWarnings("unchecked")
	public Object visitUnaryExprAST(UnaryExprAST ast, Object o)
			throws CompilationException {
		ast.line = ast.op.getLine();
		ArrayList<DataDependence> list = (ArrayList<DataDependence>) ast.exprAST.visit(this, o);
		if (ast.op.getText().equals("++") || ast.op.getText().equals("--")) {
			// day la phep gan tang hoac giam 1 don vi
			String varNameAssigned = ((VarExprAST) ast.exprAST).op.getText();
			// luu bien duoc gan vao danh sach
			tableAssignedVariable.addAssignedVar(ast.line, varNameAssigned);
			if (ast.parentAST instanceof ExprStmtAST) {
				// neu return true <=> dang tim kiem lenh gan trong vong lap
				if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP))
					return "skip_adding_node";
			}
		}
		return list;
	}

	// VarDeclAST
	@SuppressWarnings("unchecked")
	public Object visitVarDeclAST(VarDeclAST ast, Object o)
			throws CompilationException {
		ast.line = ast.op.getLine();
		if (ast.parentAST.parentAST instanceof DeclarationStmtAST)
			ast.parentAST.parentAST.line = ast.line;

		ast.typeAST.visit(this, o);
		if (ast.init != null) {
			// day la phep gan
			String varNameAssigned = ast.op.getText();
			// luu bien duoc gan vao danh sach
			tableAssignedVariable.addAssignedVar(ast.line, varNameAssigned);
			// neu return true <=> dang tim kiem lenh gan trong vong lap
			if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP))
				return null;
			ArrayList<DataDependence> data = (ArrayList<DataDependence>) ast.init.visit(this,
					o);
			ControlDependence conDep = null;
			if (!currentLevelNode.isEmpty())
				conDep = new ControlDependence(currentLevelNode);
			Node assignNode = new Node(ast.line, TYPE.ASSIGN, data, conDep,
					null);
			assignNode.setDefinedVar(new VariableUsed(varNameAssigned, -1));
			graph.addNode(assignNode);
		}
		/*
		 * else { // neu return true <=> dang tim kiem lenh gan trong vong lap
		 * if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) return null; ControlDep
		 * conDep = null; if (!currentLevelNode.isEmpty()) conDep = new
		 * ControlDep(currentLevelNode); graph.addNode(new Node(ast.line,
		 * TYPE.DECLARATION, null, conDep, null)); }
		 */
		return null;
	}

	// VarExprAST
	public Object visitVarExprAST(VarExprAST ast, Object o)
			throws CompilationException {
		String varName = ast.op.getText();
		ArrayList<Integer> linesOfVar = tableAssignedVariable
				.getLinesOfAssignedVar(varName);
		ArrayList<DataDependence> result = new ArrayList<DataDependence>();
		for (int i = 0; i < linesOfVar.size(); i++)
			result.add(new DataDependence(linesOfVar.get(i).intValue(), varName));
		if (result.size() == 0)
			result.add(new DataDependence(0, varName));
		return result;
	}

	// VarInitializer
	public Object visitVarInitializerAST(VarInitializerAST ast, Object o)
			throws CompilationException {
		return ast.exprAST.visit(this, o);
	}

	// VarInitializerList
	public Object visitVarInitializerListAST(VarInitializerListAST ast, Object o)
			throws CompilationException {
		ast.varInitializerAST.visit(this, o);
		ast.varInitializerListAST.visit(this, o);
		return null;
	}

	// WhileStmtAST
	@SuppressWarnings("unchecked")
	public Object visitWhileStmtAST(WhileStmtAST wAst, Object o)
			throws CompilationException {
		wAst.line = getLineOfExpr(wAst.exprAST);
		// neu return true <=> dang tim kiem lenh gan trong vong lap
		if (checkEquals(o, FIND_ASSIGN_STMT_IN_LOOP)) {
			wAst.oneStmtAST.visit(this, o);
			return null;
		}

		// duyet lan dau so qua de tim cac phep gan trong than vong lap
		wAst.oneStmtAST.visit(this, FIND_ASSIGN_STMT_IN_LOOP);

		// bat dau duyet chinh thuc
		ArrayList<DataDependence> data = (ArrayList<DataDependence>) wAst.exprAST.visit(this, o);
		ControlDependence conDep = null;
		if (!currentLevelNode.isEmpty())
			conDep = new ControlDependence(currentLevelNode);
		Node whileNode = new Node(wAst.line, TYPE.CONDITION, data, conDep, null);
		graph.addNode(whileNode);

		// backup lai node o phia tren cua lenh loop
		ControlDependence backupLevelNode = new ControlDependence(currentLevelNode);
		currentLevelNode.set(whileNode, true);
		wAst.oneStmtAST.visit(this, STMT_IN_LOOP);
		currentLevelNode.set(backupLevelNode);
		return null;
	}

}

/***********************************************************************************
 * Class chua 1 table luu danh sach cac bien duoc gan tai moi dong. Vi du: Line
 * 1: int x = 2; Line 2: y = x + 1; ==> Table se luu thong tin: (1,
 * ArrayList(x)), (2, ArrayList(y)) Luu y: 1 dong co the co nhieu bien duoc gan
 ***********************************************************************************/
class TableAssignedVariable {
	Map<Integer, ArrayList<String>> tableAssignedVar;

	// cac bien duoc gan trong nhanh if cua IfElseStmt se duoc luu trong bien
	// nay.
	Map<Integer, ArrayList<String>> backupListVarInIfBranch;
	boolean isUsingBackupTableFlag = false;

	// sau khi duyet het nhanh if cua IfElseStmt, bien backup se duoc push vo
	// Stack.
	Stack<Map<Integer, ArrayList<String>>> backupMultiListInIfBranch;

	public TableAssignedVariable() {
		tableAssignedVar = new HashMap<Integer, ArrayList<String>>();
		backupListVarInIfBranch = new HashMap<Integer, ArrayList<String>>();
		backupMultiListInIfBranch = new Stack<Map<Integer, ArrayList<String>>>();
	}

	// them 1 cap (dong va bien duoc gan)
	public void addAssignedVar(int line, String varname) {
		Map<Integer, ArrayList<String>> listVar;
		if (!isUsingBackupTableFlag)
			listVar = tableAssignedVar;
		else
			listVar = backupListVarInIfBranch;
		if (listVar.containsKey(line)) {
			ArrayList<String> listVarNames = (ArrayList<String>) listVar
					.get(line);
			if (!listVarNames.contains(varname))
				listVarNames.add(varname);
		} else {
			ArrayList<String> vars = new ArrayList<String>();
			vars.add(varname);
			listVar.put(new Integer(line), vars);
		}
	}

	// ham se duoc kich hoat sau khi duyet xong nhanh if cua IfElseStmt
	public void finishUsingBackUpTable() {
		backupMultiListInIfBranch.push(backupListVarInIfBranch);
		backupListVarInIfBranch = new HashMap<Integer, ArrayList<String>>();
		isUsingBackupTableFlag = false;
	}

	// tim tat ca cac dong ma bien varname duoc gan
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Integer> getLinesOfAssignedVar(String varname) {
		// Get Map in Set interface to get key and value
		Set s = tableAssignedVar.entrySet();
		// Move next key and value of Map by iterator
		Iterator it = s.iterator();
		ArrayList<Integer> lines = new ArrayList<Integer>();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			int line = (Integer) m.getKey();
			ArrayList<String> listVar = (ArrayList<String>) m.getValue();
			if (listVar.contains(varname))
				lines.add(line);
		}
		if (isUsingBackupTableFlag) {
			Iterator iter = backupListVarInIfBranch.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry m = (Map.Entry) iter.next();
				int line = (Integer) m.getKey();
				ArrayList<String> listVar = (ArrayList<String>) m.getValue();
				if (listVar.contains(varname))
					lines.add(line);
			}
		}
		return lines;
	}

	@SuppressWarnings("rawtypes")
	public void printArrayList(ArrayList list) {
		System.out.println("ArrayList:");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		System.out.println("EndArrayList:");
	}

	// ham se duoc kich hoat sau khi duyet het nhanh else cua IfElseStmt
	public void restoreBackUpTable() {
		Map<Integer, ArrayList<String>> oneList = backupMultiListInIfBranch
				.pop();
		tableAssignedVar.putAll(oneList);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String toString() {
		String result = "";
		String newline = "\r\n";
		result += newline + newline
				+ "List of assigned variable in the source code:" + newline;
		// Get Map in Set interface to get key and value
		Set s = tableAssignedVar.entrySet();
		// Move next key and value of Map by iterator
		Iterator it = s.iterator();
		while (it.hasNext()) {
			Map.Entry m = (Map.Entry) it.next();
			int line = (Integer) m.getKey();
			ArrayList<String> listVarName = (ArrayList<String>) m.getValue();
			result += "Line: " + line + "       Assigned Variable: ";
			for (int i = 0; i < listVarName.size(); i++)
				result += listVarName.get(i) + " ";
			result += newline;
		}
		result += newline;
		return result;
	}

	// ham se duoc kich hoat truoc khi bat dau duyet nhanh if cua IfElseStmt
	public void useBackUpTable() {
		isUsingBackupTableFlag = true;
	}
}
