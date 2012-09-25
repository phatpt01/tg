package program;

import java.util.ArrayList;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import transform.AST.CompilationException;

public class View {

	Control control;
	String sourceFile;
	protected Shell shell;

	private StyledText txtLog;
	private StyledText txtSourceCode;

	private Table tblParameter;
	private Table tblCondition;
	private TableColumn columnPara;
	private TableColumn columnValue;
	private TableColumn columnCondition;
	private TableColumn columnTrue;
	private TableColumn columnFalse;

	private Button btnOpen;
	private Button btnStandard;
	private Button btnScanCondition;
	private Button btnGenerateSolvable;

	private Button btnSE;

	private Button btnGenerateUnsolvable;
	private Button btnShowAllTC;
	private Button btnPrev;
	private Button btnNext;
	private Button btnExit;

	public static void main(String[] args) {
		try {
			View window = new View();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changeSourceText(String source) {
		txtSourceCode.setText(source);
	}

	protected void createContents() {
		shell = new Shell();
		shell.setSize(940, 750);
		shell.setText("Test Case Generation");
		txtLog = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		txtLog.setEditable(false);
		txtLog.setBounds(10, 423, 912, 284);
		Font font = new Font(Display.getCurrent(), "Courier New", 12, SWT.NONE);
		txtLog.setFont(font);

		txtSourceCode = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		txtSourceCode.setEditable(false);
		txtSourceCode.setBounds(10, 10, 580, 407);
		txtSourceCode.setFont(font);

		tblParameter = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		font = new Font(Display.getCurrent(), "Courier New", 10, SWT.NONE);
		tblParameter.setFont(font);
		tblParameter.setBounds(600, 10, 160, 197);
		tblParameter.setHeaderVisible(true);
		tblParameter.setLinesVisible(true);

		columnPara = new TableColumn(tblParameter, SWT.CENTER);
		columnPara.setWidth(100);
		columnPara.setText("Parameters");

		columnValue = new TableColumn(tblParameter, SWT.LEFT);
		columnValue.setWidth(60);
		columnValue.setText("Value");

		tblCondition = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblCondition.setFont(font);
		tblCondition.setBounds(602, 213, 320, 204);
		tblCondition.setHeaderVisible(true);
		tblCondition.setLinesVisible(true);

		columnCondition = new TableColumn(tblCondition, SWT.RIGHT);
		columnCondition.setWidth(180);
		columnCondition.setText("Conditions");

		columnTrue = new TableColumn(tblCondition, SWT.LEFT);
		columnTrue.setWidth(70);
		columnTrue.setText("True");

		columnFalse = new TableColumn(tblCondition, SWT.LEFT);
		columnFalse.setWidth(70);
		columnFalse.setText("False");

		btnOpen = createButton("Open Source", true, 800, 10, 100, 20);
		btnStandard = createButton("Standard Source", false, 800, 35, 100, 20);
		btnScanCondition = createButton("Scan Condition", false, 800, 60, 100,
				20);
		// btnGenerateSolvable = createButton("Generate Solvable", false, 800,
		// 85,
		// 100, 20);
		btnGenerateSolvable = createButton("Generate Solvable", false, 800, 85,
				50, 20);
		btnSE = createButton("SE", true, 850, 85, 50, 20);

		btnGenerateUnsolvable = createButton("Generate Unsolvable", false, 800,
				110, 100, 20);
		btnShowAllTC = createButton("Show all test case", false, 800, 135, 100,
				20);

		btnPrev = createButton("Prev", false, 800, 161, 50, 20);
		btnNext = createButton("Next", false, 850, 161, 50, 20);
		btnExit = createButton("Exit", false, 800, 187, 100, 20);

		addSelectionListener();
	}

	private Button createButton(String buttonCaption, boolean initEnable,
			int x, int y, int w, int h) {
		Button btnTemp = new Button(shell, SWT.NONE);
		btnTemp.setBounds(x, y, w, h);
		btnTemp.setText(buttonCaption);
		btnTemp.setEnabled(initEnable);

		return btnTemp;
	}

	private void addSelectionListener() {

		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog openfile = new FileDialog(shell);
				sourceFile = openfile.open();

				if (sourceFile != null) {
					String source = control.readSourceFile(sourceFile);
					changeSourceText(source);
					txtLog.setText("");
					btnStandard.setEnabled(true);
				}
			}
		});

		btnStandard.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String standardSource = control.standardSource(sourceFile);
				changeSourceText(standardSource);

				printParameterList();

				try {
					printConditionsList();
				} catch (CompilationException e) {
					e.printStackTrace();
				}

				btnStandard.setEnabled(false);
				btnScanCondition.setEnabled(true);
			}
		});

		btnScanCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				txtLog.setText(control.scanCondition());
				btnScanCondition.setEnabled(false);
				btnGenerateSolvable.setEnabled(true);
			}
		});

		btnGenerateSolvable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				txtLog.setText(control.generateSolvable());
				btnGenerateSolvable.setEnabled(false);
				btnGenerateUnsolvable.setEnabled(true);
			}

		});

		btnSE.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				control.runSE(txtSourceCode.getText());
			}
		});

		btnGenerateUnsolvable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				txtLog.setText(control.runGA());
				btnGenerateUnsolvable.setEnabled(false);
				btnShowAllTC.setEnabled(true);
			}

		});

		btnShowAllTC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				txtLog.setText(control.showAllTestCase());
				updateConditionList();
				btnShowAllTC.setEnabled(false);
			}
		});

		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.dispose();
			}
		});

		btnPrev.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ArrayList<Integer> testcase = control.getPrevTestCase();
				updateTestCase(testcase);
			}
		});

		btnNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ArrayList<Integer> testcase = control.getNextTestCase();
				updateTestCase(testcase);
			}
		});

	}

	public void open() {
		Display display = Display.getDefault();
		createContents();
		control = new Control();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void printConditionsList() throws CompilationException {
		tblCondition.removeAll();

		ArrayList<String> lstCondition = control.getConditionList();

		for (int i = 0; i < lstCondition.size(); i++) {
			TableItem tblItem = new TableItem(tblCondition, SWT.CENTER);
			tblItem.setText(lstCondition.get(i));
		}
	}

	protected void printParameterList() {
		tblParameter.removeAll();

		ArrayList<String> lstParameter = control.getParameterList();

		for (int i = 0; i < lstParameter.size(); i++) {
			TableItem tblItem = new TableItem(tblParameter, SWT.CENTER);

			tblItem.setText(lstParameter.get(i));
		}
	}

	// protected void setValue(ArrayList<String> testcase) {
	// int c = 1;
	// TableItem[] items = tblParameter.getItems();
	// for (int i = 0; i < testcase.size(); i++) {
	// items[i].setText(c, testcase.get(i));
	// }
	// }

	protected void updateConditionList() {
		ArrayList<Boolean> trueList = control.getTrueList();
		ArrayList<Boolean> falseList = control.getFalseList();
		int c = 1;
		TableItem[] items = tblCondition.getItems();
		for (int i = 0; i < trueList.size(); i++) {
			if (trueList.get(i) == true)
				items[i].setText(c, "X");
			else
				items[i].setText(c, "O");
		}
		c = 2;
		for (int i = 0; i < falseList.size(); i++) {
			if (falseList.get(i) == true)
				items[i].setText(c, "X");
			else
				items[i].setText(c, "O");
		}
	}

	protected void updateSlide() {
		// Color criteriaColor = new Color(Display.getCurrent(), 255, 166, 107);
		Color highlightColor = Display.getCurrent().getSystemColor(
				SWT.COLOR_CYAN);

		ArrayList<Integer> slide = control.getSlide();
		if (slide != null) {
			int size = slide.size();
			StyleRange[] ranges = new StyleRange[size];
			for (int i = 0; i < size; i++) {
				int line = slide.get(i) - 1;
				// find the offset of the beginning of the line
				int offsetLine = this.txtSourceCode.getOffsetAtLine(line);
				while (this.txtSourceCode.getTextRange(offsetLine, 1).equals(
						"\t")
						|| this.txtSourceCode.getTextRange(offsetLine, 1)
								.equals(" ")) {
					offsetLine++;
				}
				// the lenght of text need to be highlight
				int length = this.txtSourceCode.getOffsetAtLine(line + 1)
						- offsetLine;

				// create new StyleRange

				ranges[i] = new StyleRange(offsetLine, length, null,
						highlightColor);

			}
			this.txtSourceCode.setStyleRanges(ranges);
		}
	}

	protected void updateTestCase(ArrayList<Integer> testcase) {
		int c = 1;
		TableItem[] items = tblParameter.getItems();
		for (int i = 0; i < testcase.size(); i++) {
			items[i].setText(c, testcase.get(i).toString());
		}
	}
}
