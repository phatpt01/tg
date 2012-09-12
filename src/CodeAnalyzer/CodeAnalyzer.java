package CodeAnalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import system.*;
import transform.CodeGeneration.AstSimulationVisitor;
import transform.*;
import transform.AST.*;
import transform.AST.CompilationException;
import transform.DependenceGraph.*;
import transform.CodeGeneration.*;

public class CodeAnalyzer 
{
	private AST astree;
	private PDG pdg;
	//Mapping Table
	private MappingTable mapTable;
	//Transform
	private Transform transform;
	
	private ArrayList<ArrayList<AST>> listPath;
	private ArrayList<ArrayList<Integer>> listBranch;
	
	//List Variable
	private ArrayList<Variable> listVar;
	//List Parameter
	private ArrayList<Parameter> listPara;
	//List Condition
	private ArrayList<Condition> listCon;
	
	//private ArrayList<ArrayList<Integer>> testcaseSet;
	
	//private ArrayList<ArrayList<Integer>> slideset;
	
	private int currTestCase = 0;
	
	private String testcase = ""; 
	
	private int numUnsolCon;
	
	int temp1 = 1;
	
	public CodeAnalyzer()
	{
	}
	
	public CodeAnalyzer(String strSourceFile)
	{
		//this.testcaseSet = new ArrayList<ArrayList<Integer>>();
		this.listCon = new ArrayList<Condition>();
		//this.slideset = new ArrayList<ArrayList<Integer>>();
		transform = new Transform(strSourceFile);
        this.mapTable = transform.getMapTable(); // Mapping Table
        this.pdg = transform.getPdg();
        this.astree = transform.getAstree();
        this.listPara = transform.getListParameters(); // list Parameters
        this.listVar = transform.getListVariables(); // list Variables
	}
	
	public void loadFile(String strSourceFile)
	{
		//this.testcaseSet = new ArrayList<ArrayList<Integer>>();
		this.listCon = new ArrayList<Condition>();
		//this.slideset = new ArrayList<ArrayList<Integer>>();
		transform = new Transform(strSourceFile);
        this.mapTable = transform.getMapTable(); // Mapping Table
        this.astree = transform.getAstree();
        this.pdg = transform.getPdg();
        this.listPara = transform.getListParameters(); // list Parameters
        this.listVar = transform.getListVariables(); // list Variables
        this.listPath = transform.getListPath();
        this.listBranch = transform.getListBranch();
	}
	
	public ArrayList<String> getParaNameList()
	{
		ArrayList<String> listParaName = new ArrayList<String>();
		for(int i=0; i<listPara.size(); i++)
		{
			listParaName.add(listPara.get(i).getName());
		}
		return listParaName;
	}
	
	public ArrayList<String> getVarNameList()
	{
		ArrayList<String> listVarName = new ArrayList<String>();
		for(int i=0; i<listVar.size(); i++)
		{
			listVarName.add(listVar.get(i).getName());
		}
		return listVarName;
	}
	
	public String getStandardSource(String filename) 
	{
		this.loadFile(filename);
		return transform.getStandardSourceFile();
	}
	
	public ArrayList<String> getConditionList() throws CompilationException
	{
		ConditionPrintVisitor visitor = new ConditionPrintVisitor(null, true);
		ArrayList<String> listConditionExpr = new ArrayList<String>();
		for(int i=0; i<this.mapTable.size(); i++)
		{
			AST ast = (AST)mapTable.get(i).getStatementAST();
			if(!(ast instanceof RetStmtAST))
			{
				if(ast instanceof IfThenStmtAST)
				{
					ast = ((IfThenStmtAST) ast).e;
				}
				else if(ast instanceof IfThenElseStmtAST)
				{
					ast = ((IfThenElseStmtAST) ast).e;
				}
				else if(ast instanceof WhileStmtAST)
				{
					ast = ((WhileStmtAST) ast).e;
				}
				else if(ast instanceof DoStmtAST)
				{
					ast = ((DoStmtAST) ast).e;
				}
				else
				{
					ast = null;
				}
				if(ast !=null)
				{
					Condition con = new Condition();
					con.setStmtID(ast.line);
					con.setAst(ast);
					String conExp = visitor.print(ast);
					con.setCondition(conExp);
					listCon.add(con);
					listConditionExpr.add(conExp);
				}
			}
		}
		return listConditionExpr;
	}
	
	public String GenerateSolvable()
	{
		int count = 0;
		String output = "";
		for(int i=0; i<this.listCon.size(); i++)
		{
			GenNextTestCase(i);
		}
		
		for(int i=0; i<this.listCon.size(); i++)
		{
			Condition temp = this.listCon.get(i);
			if(temp.isHastc())
			{
				count++;
				output += "Condition: " + temp.getCondition() + "\n";
				output += "\tTrue: " + temp.getTruetc() + "\t" + temp.hastruetc + "\n";
				output += "\tFalse: " + temp.getFalsetc() + "\t" + temp.hasfalsetc + "\n";
			}
		}
		output = "Number of solvable condition: " + count + "\n" + output;
		return output;
	}

	/*public ArrayList<String> getFirstTestCase() 
	{
		Random generator = new Random();
		ArrayList<String> testcase = new ArrayList<String>();
		for(int i=0; i<this.listPara.size(); i++)
		{
			switch(listPara.get(i).getType())
			{
			case "Int":
				testcase.add(String.valueOf(generator.nextInt(100)));
				break;
			case "Real":
			case "Double":
				testcase.add(String.valueOf(generator.nextDouble()*100));
				break;
			}
		}
		ArrayList<String> temp = new ArrayList<String>();
			
		StringBuffer tc = new StringBuffer();
        for (int i = 0; i < testcase.size(); i++) {
        	temp.add(testcase.get(i));
            tc.append(testcase.get(i));
            tc.append("\n");
        }
        this.testcaseSet.add(tc.toString());
		return testcase;
	}*/

	

	private void GenNextTestCase(int i) 
	{
		boolean check = false;
		for(int j=0; j < this.listCon.get(i).getTruepath().size(); j++)
		{
			String res = GenTestCase(this.listCon.get(i).getTruepath().get(j));
			if(!res.equals(""))
			{
				this.listCon.get(i).setTruetc(res);
				this.listCon.get(i).hastruetc = true;
				check = true;
				break;
			}
		}
		for(int k=0; k < this.listCon.get(i).getFalsepath().size(); k++)
		{
			String res = GenTestCase(this.listCon.get(i).getFalsepath().get(k));
			if(!res.equals(""))
			{	
				
				this.listCon.get(i).setFalsetc(res);
				this.listCon.get(i).hasfalsetc = true;
				check = true;
				break;		
			}
			
		}
		this.listCon.get(i).setHastc(check);
	}

	private String GenTestCase(String con) 
	{
		String z3output = "Z3OUTPUT";
        File z3outFolder = new File(z3output);
        if (!z3outFolder.exists()) {
            z3outFolder.mkdirs();
        }
        
        String z3FilePath = z3outFolder.getAbsolutePath() + File.separatorChar + "Z3Formula.smt2";
        // Print the parameters, variables, and reindexed variables
        try {
            FileWriter fw = new FileWriter(z3FilePath);
            BufferedWriter out = new BufferedWriter(fw);
            
            // Print the parameters of program
            for (int i = 0; i < this.listPara.size(); i++) {
                Parameter p = this.listPara.get(i);
                out.write("(declare-const ");
                out.write(p.getName() + " ");
                switch(p.getType())
                {
                case "Int":
                	out.write(p.getType() + ")");
                	break;
                case "Float":
                case "Double":
                	out.write("Real)");
                	break;
                }
                out.write("\n");
            }
            // Print variables of program
            for (int i = 0; i < this.listVar.size(); i++) {
                Variable v = this.listVar.get(i);
                out.write("(declare-const ");
                out.write(v.getName() + " ");
                switch(v.getType())
                {
                case "Int":
                	out.write(v.getType() + ")");
                	break;
                case "Float":
                case "Double":
                	out.write("Real)");
                	break;
                }
                out.write("\n");
            }
            out.write(con);
            out.write("(check-sat)\n");
            out.write("(model)\n");
            out.close();
            fw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }   	
    	ArrayList<String> testcase = this.getNewTestcase(z3FilePath);
    	if(testcase != null)
    	{
	    	ArrayList<String> temp = new ArrayList<String>();
	    	StringBuffer tc = new StringBuffer();
	        for (int i = 0; i < testcase.size(); i++) {
	            tc.append(testcase.get(i));
	            tc.append("\n");
	            temp.add(testcase.get(i));
	        }
	    	return temp.toString();
	    }
    	else
    		return "";
	}

	private ArrayList<String> getNewTestcase(String z3FormulaFilename) {
		ArrayList<String> testcase = new ArrayList<String>();
        ArrayList<String> z3result = new ArrayList<String>();
        Runtime run = Runtime.getRuntime();
        try {
            String runZ3 = "./Z3/z3.exe";
            String config = " /m ";
            String formulaFile = z3FormulaFilename;
            Process pp = run.exec(runZ3 + config + formulaFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(pp.getInputStream()));
            String line = in.readLine();
            if (line.contains("sat") && !line.contains("unsat")) {
                while ((line = in.readLine()) != null) {
                    if (line.contains("define")) {
                        String sub;
                        if (!line.contains(")\")")) { // not contain
                            sub = line.substring(8, line.length() - 1);
                        }
                        else {
                            sub = line.substring(8, line.length() - 3);
                        }
                        // System.out.println(sub);
                        Scanner sc = new Scanner(sub);
                        sc.useDelimiter(" ");
                        z3result.add(sc.next());
                        z3result.add(sc.next());
                        sc.close();
                    }
                }
                // process z3result
                for (int i = 0; i < this.listPara.size(); i++) {
                    for (int j = 0; j < z3result.size(); j += 2) {
                        if (z3result.get(j).equals(this.listPara.get(i).getName())) {
                            testcase.add(z3result.get(j + 1)); // add new value to testcase
                            break;
                        }
                    }
                    if (testcase.size()< i) {
                    	Object result = 0;
                        Random ran1 = new Random();
                        double n = ran1.nextDouble()*100;
                        switch(this.listPara.get(i).getType())
                        {
                        case "Int":
                        	result = (int)n;
                        	break;
                        case "Double":
                        	result = (double)n;
                        	break;
                        case "Float":
                        	result = (float)n;
                        	break;
                        }
                        testcase.add(result.toString());
                    }
                }
                if (testcase.size() < this.listPara.size()) 
                {
                	int num = this.listPara.size() - testcase.size();
                    Random ran1 = new Random();
                    for (int i = 0; i < num; i++) {
                    	Object result = 0;
                        double n = ran1.nextDouble()*100;
                        switch(this.listPara.get(i).getType())
                        {
                        case "Int":
                        	result = (int)n;
                        	break;
                        case "Double":
                        	result = (double)n;
                        	break;
                        case "Float":
                        	result = (float)n;
                        	break;
                        }
                        testcase.add(result.toString());
                    }
                }
            }
            else
            {
            	return null;
            }
            // if Z3 doesn't generate all values then randomly generate value for the rest
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return testcase;
	}

	/*private void printSMT2(String z3FormulaFilename, Path pathCondition) throws CompilationException {
		// Print the parameters, variables, and reindexed variables
        try {
            FileWriter fw = new FileWriter(z3FormulaFilename);
            BufferedWriter out = new BufferedWriter(fw);
            
            // Print the parameters of program
            for (int i = 0; i < this.listPara.size(); i++) {
                Parameter p = this.listPara.get(i);
                out.write("(declare-const ");
                out.write(p.getName() + " ");
                out.write(p.getType() + ")");
                out.write("\n");
            }
            // Print variables of program
            for (int i = 0; i < this.listVar.size(); i++) {
                Variable v = this.listVar.get(i);
                out.write("(declare-const ");
                out.write(v.getName() + " ");
                out.write(v.getType() + ")");
                out.write("\n");
            }
            // Print variables after reindexing
            for (int i = 0; i < pathCondition.getListVariableReIndexed().size(); i++) {
                Variable v = pathCondition.getListVariableReIndexed().get(i);
                out.write("(declare-const ");
                out.write(v.getName() + " ");
                out.write(v.getType() + ")");
                out.write("\n");
            }
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        // Print the negated path condition in the SMT2 form
        Z3PathPrintVisitor z3Visitor = new Z3PathPrintVisitor(z3FormulaFilename, false);
        z3Visitor.printSMT2(pathCondition);		
	}
	*/

	public ArrayList<Integer> getSlide()
	{
		ArrayList<String> input = new ArrayList<String>();
		StringTokenizer st= new StringTokenizer(testcase, "[, ]");
		while(st.hasMoreTokens())
		{
			input.add(st.nextToken());
		}
		System.out.println("ABC:" + input);
		AstSimulationVisitor simulationAST = new AstSimulationVisitor(this.pdg, input);
        try {
            this.astree.visit(simulationAST, "");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        ExecutionHistory eh = simulationAST.getExecutionHistory();
        eh.changeLineIdAtExecNodePointToNode(this.pdg);
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<eh.size(); i++)
        {
        	result.add(eh.get(i).getNode().getID());
        }
        return result;
	}

	
	public int getNumCon()
	{
		numUnsolCon = 0;
		for(int i=0; i<this.listCon.size(); i++)
		{
			if(this.listCon.get(i).isHastc() == false)
				numUnsolCon++;
		}
		return numUnsolCon;
	}
	
	public int getNumPar()
	{
		return this.listPara.size();
	}
	
	public int[] check(int[][] testcase)
	{
		int numCon = this.listCon.size();
		int numPar = this.listPara.size();
		int[] result = new int[numUnsolCon*2];
		int count = 0;
		
		for(int i=0; i<numCon; i++)
		{
			if(this.listCon.get(i).isHastc() == false)
			{
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp1 = new ArrayList<String>();
				for(int j=0; j< numPar; j++)
				{
					temp.add(String.valueOf(testcase[count][j]));
					temp1.add(String.valueOf(testcase[count+1][j]));
				}
				result[count] = checkCon(temp, i , 0);
				result[count+1] = checkCon(temp1, i , 1);
				count += 2;
			}
		}
//		if(result[3] == 0)
//		{
//			System.out.println(testcase[3][0]+" "+ testcase[3][1]+" "+ testcase[3][2]);
//		}
		return result;
	}
	
	private int checkCon(ArrayList<String> testcase, int con, int branch)
	{
		int result = 100;
		int temp;
		int count = 0;
		Temp2Visitor visitor = new Temp2Visitor(this.listPara, this.listVar, testcase);
		try 
		{
			boolean check;
			if(branch == 0)
			{
				for(int i=0; i<this.listCon.get(con).getTruecon().size(); i++)
				{
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.listPath.get(this.listCon.get(con).getTruecon().get(i));
					ArrayList<Integer> lbranch = this.listBranch.get(this.listCon.get(con).getTruecon().get(i));
					for(j=0; path.get(j).line<this.listCon.get(con).getStmtID(); j++)
					{
						count++;
						int res = (Integer)path.get(j).visit(visitor,lbranch.get(j));
						if(check == true)
						{
							temp += 100;
						}
						if(res != 0)
						{
							temp += res;
							check = true;
						}
					}
					temp += (Integer)path.get(j).visit(visitor,branch);
					count++;
					if(temp != 0)
						temp = temp/count+1;
					else
						temp = 0;
					if(temp<result)
						result = temp;
					visitor.clear();
				}
			}
			else
			{
				for(int i=0; i<this.listCon.get(con).getFalsecon().size(); i++)
				{
					temp = 0;
					check = false;
					int j;
					ArrayList<AST> path = this.listPath.get(this.listCon.get(con).getFalsecon().get(i));
					ArrayList<Integer> lbranch = this.listBranch.get(this.listCon.get(con).getFalsecon().get(i));
					for(j=0; path.get(j).line<this.listCon.get(con).getStmtID(); j++)
					{
						count++;
						int res = (Integer)path.get(j).visit(visitor,lbranch.get(j));
						if(check == true)
						{
							temp += 100;
						}
						if(res != 0)
						{
							temp += res;
							check = true;
						}
					}
					temp += (Integer)path.get(j).visit(visitor, branch);
					count ++;
					if(temp != 0)
						temp = temp/count+1;
					else
						temp = 0;
					if(temp<result)
						result = temp;
					visitor.clear();
				}
			}
		}
        catch (NullPointerException e) {
        	throw new NullPointerException("Invalid Input!!!"); // if the input is invalid
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException("Type is not correct!!!");
        }
		catch (CompilationException e){
			e.printStackTrace();
		}
		return result;
	}
	public String update(int[][] res)
	{
		String output = "";
		int count = 0;
		int numCon = this.listCon.size();
		int numPar = this.listPara.size();
		for(int i=0; i<numCon; i++)
		{
			if(this.listCon.get(i).isHastc() == false)
			{
				int j;
				String truetc = "[";
				String falsetc = "[";
				for(j=0; j< numPar; j++)
				{
					truetc += res[count][j];
					falsetc += res[count+1][j];
					if(j < numPar -1)
					{
						truetc += ", ";
						falsetc += ", ";
					}
				}
				truetc += "]";
				this.listCon.get(i).setTruetc(truetc);
				falsetc += "]";
				this.listCon.get(i).setFalsetc(falsetc);
				if(res[count][j] == 0)
				{
					this.listCon.get(i).hastruetc = true;
				}
				else
				{
					this.listCon.get(i).hastruetc = false;
				}
				
				if(res[count+1][j] == 0)
				{
					this.listCon.get(i).hasfalsetc = true;
				}
				else
				{
					this.listCon.get(i).hasfalsetc = false;
				}
				
				output += "Condition: " + this.listCon.get(i).getCondition() + "\n";
				output += "\tTrue: " + this.listCon.get(i).getTruetc() + "\t" + this.listCon.get(i).hastruetc + "\n";
				output += "\tFalse: " + this.listCon.get(i).getFalsetc() + "\t" + this.listCon.get(i).hasfalsetc + "\n";
				count+=2;
			}
		}
		output = "Number of unsolvable condition: " + this.numUnsolCon + "\n" + output;
		return output;
	}
	public String showAllTestCase()
	{
		String output = "";
		for(int i=0; i<this.listCon.size(); i++)
		{
			Condition temp = this.listCon.get(i);
			output += "Condition " + (i+1) +": "+ temp.getCondition() + "\n";
			output += "\tTrue: " + temp.getTruetc()+ "\t" + (temp.hastruetc) + "\n";
			output += "\tFalse: " + temp.getFalsetc()+ "\t" + (temp.hasfalsetc) + "\n";
		}
		return output;
	}

	public String scanCondition() {
		String output = "";
		listCon = transform.updateConList(listCon);
		for(int i=0; i<this.listCon.size(); i++)
    	{
    		output += "Condition "+ (i+1) + ":" + this.listCon.get(i).getCondition() + "\n";
    		output += "True:\n";
    		for(int j = 0; j<this.listCon.get(i).getTruepath().size(); j++)
    		{
    			output += this.listCon.get(i).getTruepath().get(j) + "\n";
    		}
    		output += "False:\n";
    		for(int j = 0; j<this.listCon.get(i).getFalsepath().size(); j++)
    		{
    			output += this.listCon.get(i).getFalsepath().get(j) +"\n";
    		}
    	}
		return output;
	}

	public ArrayList<Boolean> getTrueList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for(int i=0; i<this.listCon.size(); i++)
		{
			if(this.listCon.get(i).hastruetc == true)
			{
				result.add(true);
			}
			else
			{
				result.add(false);
			}
		}
		return result;
	}

	public ArrayList<Boolean> getFalseList() {
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for(int i=0; i<this.listCon.size(); i++)
		{
			if(this.listCon.get(i).hasfalsetc == true)
			{
				result.add(true);
			}
			else
			{
				result.add(false);
			}
		}
		return result;
	}

	public ArrayList<Integer> getPrevTestCase() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(currTestCase > 0)
			currTestCase --;
		if(currTestCase % 2 == 0)
		{
			testcase = this.listCon.get(currTestCase/2).getTruetc();
		}
		else
		{
			testcase = this.listCon.get(currTestCase/2).getFalsetc();
		}
		StringTokenizer st= new StringTokenizer(testcase, "[, ]");
		while(st.hasMoreTokens())
		{
			int temp = Integer.parseInt(st.nextToken());
			result.add(temp);
		}
		return result;
	}
	
	public ArrayList<Integer> getNextTestCase()
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(currTestCase < this.listCon.size()*2-1)
			currTestCase ++;
		if(currTestCase % 2 == 0)
		{
			testcase = this.listCon.get(currTestCase/2).getTruetc();
		}
		else
		{
			testcase = this.listCon.get(currTestCase/2).getFalsetc();
		}
		StringTokenizer st= new StringTokenizer(testcase, "[, ]");
		while(st.hasMoreTokens())
		{
			result.add(Integer.parseInt(st.nextToken()));
		}
		return result;
	}
}
