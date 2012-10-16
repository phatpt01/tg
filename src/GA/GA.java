package GA;

import java.util.ArrayList;

import CodeAnalyzer.CodeAnalyzer;

public class GA implements Cons {
	public static final int POPULATION_SIZE = 10;
	public static final int MAX_RUN_TIMES = 1000;
	
	public ArrayList<Testcase> lstPopulation;
	public ArrayList<Testcase> lstChildPopulation;

	public Testcase m_AllTimeBestTestcase;

	private void generateRandomPopulation(CodeAnalyzer codeAnalyzer) {
		lstPopulation = new ArrayList<Testcase>();
		for (int i = 0; i < POPULATION_SIZE; i++) {
			lstPopulation.add(new Testcase(codeAnalyzer));
		}
	}

	private Testcase getBestTestCase() {
		Testcase bestTestCase = lstPopulation.get(0);
		int maxAccessedBranchNum = bestTestCase.getAcessedBranchNum();
		for (Testcase t : lstPopulation) {
			if (t.getAcessedBranchNum() > maxAccessedBranchNum) {
				bestTestCase = t;
				maxAccessedBranchNum = bestTestCase.getAcessedBranchNum();
			}
		}
		return bestTestCase;
	}

	public int[][] getResult() {
		if (m_AllTimeBestTestcase != null) {
			int[][] res = new int[m_AllTimeBestTestcase.m_iBranchNum][m_AllTimeBestTestcase.m_iParamNum + 1];
			for (int i = 0; i < m_AllTimeBestTestcase.m_iBranchNum; i++) {
				int j;
				for (j = 0; j < m_AllTimeBestTestcase.m_iParamNum; j++) {
					res[i][j] = m_AllTimeBestTestcase.m_aiParams[i][j];
				}
				if (m_AllTimeBestTestcase.m_CanAcessBranch[i] == 0) {
					res[i][j] = 0;
				} else {
					res[i][j] = 1;
				}
			}
			return res;
		} else
			return null;
	}

	private boolean isDone() {
		if (m_AllTimeBestTestcase.isAcesssAllBranch()) {
			for (int i = 0; i < m_AllTimeBestTestcase.m_iBranchNum; i += 2) {
				System.out.println("BBBBBBBB " + m_AllTimeBestTestcase.m_CanAcessBranch[i]);
				System.out
						.println("CCCCCCCCC " + m_AllTimeBestTestcase.m_CanAcessBranch[i + 1]);
				int j;
				
				String trueTestcase = "[";
				String falseTestcase = "[";
				
				for (j = 0; j < m_AllTimeBestTestcase.m_iParamNum; j++) {
					trueTestcase += m_AllTimeBestTestcase.m_aiParams[i][j];
					falseTestcase += m_AllTimeBestTestcase.m_aiParams[i + 1][j];
				
					if (j < m_AllTimeBestTestcase.m_iParamNum - 1) {
						trueTestcase += ", ";
						falseTestcase += ", ";
					}
				}
				trueTestcase += "]";
				falseTestcase += "]";
				System.out.println("True testcase: " + trueTestcase);
				System.out.println("False testcase: " + falseTestcase);
			}

			return true;
		}
		return false;
	}

	public void reset() {
		if (lstPopulation != null) {
			lstPopulation.clear();
			lstChildPopulation.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void run(CodeAnalyzer codeAnalyzer) {
		if (codeAnalyzer.getNumUnSolvableCondition() != 0) {
			generateRandomPopulation(codeAnalyzer);

			lstChildPopulation = new ArrayList<Testcase>();
			m_AllTimeBestTestcase = getBestTestCase();

			int count = 0;

			while (!isDone() && count < MAX_RUN_TIMES) {
				Testcase bestTestCase = getBestTestCase();
				
				if (m_AllTimeBestTestcase.getAcessedBranchNum() < bestTestCase
						.getAcessedBranchNum()) {
					m_AllTimeBestTestcase = bestTestCase;
				}

				for (int i = 0; i < POPULATION_SIZE; i++) {
					Testcase t = lstPopulation.get(i);
					if (t != bestTestCase) {
						Testcase child = bestTestCase.Clone();
						int result = child.hybrid(t);
						if (result != 0) {
							child.mutate(codeAnalyzer, result);
						}
						lstChildPopulation.add(child);
					}
				}
				lstChildPopulation.add(m_AllTimeBestTestcase);

				lstPopulation = (ArrayList<Testcase>) lstChildPopulation.clone();
				lstChildPopulation = new ArrayList<Testcase>();
				count++;
			}
		}
	}
}
