package GA;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import CodeAnalyzer.CodeAnalyzer;

public class Testcase 
{
	public int[][] m_aiParams;
	
	public int[] m_CanAcessBranch;
	
	public int m_iParamNum;
	
	public int m_iBranchNum;
	
	public Testcase()
	{
		
	}
	
	public Testcase(CodeAnalyzer codeAnalyzer)
	{
		m_iParamNum = codeAnalyzer.getNumPar();
		m_iBranchNum = codeAnalyzer.getNumCon() * 2;
		m_aiParams = new int[m_iBranchNum][m_iParamNum];
		for (int i = 0; i < m_iBranchNum; i++)
		{
			m_aiParams[i] = Util.Random(m_iParamNum);
		}
		m_CanAcessBranch = codeAnalyzer.check(m_aiParams);
//		if(m_CanAcessBranch[3] == 0)
//		{
//			System.out.println("Abc" + m_aiParams[3][0] + " " + m_aiParams[3][1] + " "+ m_aiParams[3][2]);
//		}
			
	}
	
	public boolean isAcesssAllBranch()
	{
		for (int ok : m_CanAcessBranch)
		{
			if (ok!=0) return false;
		}
		return true;
	}
	
	public int GetAcessedBranchNum()
	{
		int count = 0;
		for (int ok : m_CanAcessBranch)
		{
			if (ok == 0) count++;
		}
		return count;
	}
	
	public Testcase Clone()
	{
		Testcase newTestcase = new Testcase();
		newTestcase.m_iParamNum = m_iParamNum;
		newTestcase.m_iBranchNum = m_iBranchNum;
		
		newTestcase.m_CanAcessBranch = new int[m_iBranchNum];
		System.arraycopy(m_CanAcessBranch, 0, newTestcase.m_CanAcessBranch, 0, m_iBranchNum);
		
		newTestcase.m_aiParams = new int[m_iBranchNum][m_iParamNum];
		for (int i = 0; i < m_iBranchNum; i ++)
			System.arraycopy(m_aiParams[i], 0, newTestcase.m_aiParams[i], 0, m_iParamNum);
		return newTestcase;
	}
	
	public int Hybid(Testcase t)
	{
		int count = 0;
		for (int i = 0; i < m_iBranchNum; i++)
		{
			if (this.m_CanAcessBranch[i] > t.m_CanAcessBranch[i]) 
			{
				System.arraycopy(t.m_aiParams[i], 0, this.m_aiParams[i], 0, m_iParamNum);
				this.m_CanAcessBranch[i] = t.m_CanAcessBranch[i];
			}
			else if(this.m_CanAcessBranch[i] != 0)
			{
				count = i;
			}
		}
		return count;
	}
	
	public void Mutate(CodeAnalyzer codeAnalyzer, int pos)
	{
		Random r = new Random();
		int pos1 = Math.abs(r.nextInt()%(m_iParamNum-1));
		this.m_aiParams[pos][pos1] = r.nextInt()%10;
		this.m_CanAcessBranch = codeAnalyzer.check(m_aiParams);
//		if(m_CanAcessBranch[3] == 0)
//		{
//			System.out.println("Abc" + m_aiParams[3][0] + " " + m_aiParams[3][1] + " "+ m_aiParams[3][2]);
//		}
	}
	
	public void PrintResult()
	{
//		for (int i = 0; i < m_iBranchNum; i++)
//		{
//			String s = "Branch " + i + ": ";
//			for (int j = 0; j < m_iParamNum; j++)
//			{
//				s += m_aiParams[i][j] + " ";
//			}
//			Util.PRINT(s + m_CanAcessBranch[i]);
//		}
				
		try
		{
			FileWriter fstream = new FileWriter("result.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < m_iBranchNum; i++)
			{
				String s = "Branch " + i + ": ";
				for (int j = 0; j < m_iParamNum; j++)
				{
					s += m_aiParams[i][j] + " ";
				}
				out.write(s + m_CanAcessBranch[i]);
				out.newLine();
				Util.PRINT(s + m_CanAcessBranch[i]);
			}
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
