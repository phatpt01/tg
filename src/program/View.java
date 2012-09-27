package program;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import se.MappingRecord;
import system.Variable;
import transform.AST.CompilationException;

public class View {

	private static int BUTTON_WIDTH = 120;
	private static int BUTTON_HEIGHT = 30;

	public static void main(String[] args) {
		try {
			View window = new View();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Control control;
	String sourceFile;

	protected Shell shell;
	private StyledText txtLog;

	private StyledText txtSourceCode;

	private Table tblParameter;
	private TableColumn colPara;
	private TableColumn colValue;

	private Table tblCondition;
	private TableColumn colCondition;
	private TableColumn colTrue;
	private TableColumn colFalse;

	private Table tblMappingTable;
	private TableColumn colExpression;
	private TableColumn colSymbol;
	private TableColumn colReturnType;

	private Table tblVariable;
	private TableColumn colVarName;
	private TableColumn colVarType;

	private Button btnOpen;
	private Button btnStandard;
	private Button btnScanCondition;
	private Button btnGenerateSolvableBeforeSE;
	private Button btnSymbolicExecution;

	private Button btnGenerateSolvableAfterSE;
	private Button btnGenerateUnsolvableByGA;
	private Button btnShowAllTC;

	private Button btnPrev;
	private Button btnNext;
	private Button btnExit;

	String oldText = "";

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
			}
		});

		btnScanCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				oldText = txtLog.getText();
				txtLog.setText(oldText + "\n" + control.scanCondition());
			}
		});

		btnGenerateSolvableBeforeSE
				.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						txtLog.setText(txtLog.getText() + "\n"
								+ control.generateSolvable());
						
						txtLog.setText(txtLog.getText() + "\n"
								+ "Number of unsolvable by Z3: " 
								+ control.getNumUnSolvableCondition());
					}

				});

		btnSymbolicExecution.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			
				control.runSE(txtSourceCode.getText());
				printMappingTable();
				printVariableTable();
			}
		});

		btnGenerateSolvableAfterSE.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// Use Z3 to solve code after SE
				oldText = txtLog.getText();
				txtLog.setText(oldText + "\n"
						+ control.generateTestCaseWithSE());
			}

		});

		btnGenerateUnsolvableByGA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				oldText = txtLog.getText();
				txtLog.setText(oldText + "\n" + control.runGA());
			}

		});

		btnShowAllTC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				txtLog.setText(control.showAllTestCase());
				updateConditionList();
			}
		});

		btnExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (JOptionPane.showConfirmDialog(null, "Do you want to exit?",
						"Confirm to exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					shell.dispose();
					System.exit(0);
				}
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

	protected void printVariableTable() {
		tblVariable.removeAll();

		ArrayList<Variable> lstVariable = control.getVariableList();

		TableItem tblItem;
		for (int i = 0; i < lstVariable.size(); i++) {
			tblItem = new TableItem(tblVariable, SWT.CENTER);

			tblItem.setText(0, lstVariable.get(i).getName());
			tblItem.setText(1, lstVariable.get(i).getType());
		}
	}

	private void changeSourceText(String source) {
		txtSourceCode.setText(source);
	}

	private Button createButton(String buttonCaption, boolean initEnable,
			int x, int y, int w, int h) {
		Button btnTemp = new Button(shell, SWT.NONE);
		btnTemp.setBounds(x, y, w, h);
		btnTemp.setText(buttonCaption);
		btnTemp.setEnabled(initEnable);

		return btnTemp;
	}

	protected void createContents() {
		Font font = new Font(Display.getCurrent(), "Arial", 11, SWT.NONE);

		shell = new Shell();
		shell.setText("Test Case Generation");
		shell.setMaximized(true);

		// int screenWidth = shell.getSize().x;
		// int screenHeight = shell.getSize().y;

		txtSourceCode = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		txtSourceCode.setEditable(false);
		txtSourceCode.setBounds(10, 10, 480, 366);
		txtSourceCode.setFont(font);

		txtLog = new StyledText(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		txtLog.setEditable(false);
		txtLog.setBounds(10, 387, 960, 300);
		txtLog.setFont(font);

		createTableParameter(font, 500, 10, 300, 90);
		createTableCondition(font, 500, 110, 300, 90);
		createTableMapping(font, 500, 210, 300, 90);
		createTableVariable(font, 500, 310, 300, 90);

		setButtonLocation();

		addSelectionListener();
	}

	private void setButtonLocation() {
		btnOpen = createButton("Open Source", true, 980, 10, BUTTON_WIDTH,
				BUTTON_HEIGHT);
		btnStandard = createButton("Standard Source", true, 980, 45,
				BUTTON_WIDTH, BUTTON_HEIGHT);
		btnScanCondition = createButton("Scan Condition", true, 980, 80,
				BUTTON_WIDTH, BUTTON_HEIGHT);
		btnGenerateSolvableBeforeSE = createButton("Generate Solvable", true,
				980, 115, BUTTON_WIDTH, BUTTON_HEIGHT);
		btnSymbolicExecution = createButton("Symbolic execution", true, 980,
				150, BUTTON_WIDTH, BUTTON_HEIGHT);

		btnGenerateSolvableAfterSE = createButton("Generate TC after SE", true,
				1110, 10, BUTTON_WIDTH, BUTTON_HEIGHT);
		btnGenerateUnsolvableByGA = createButton("Generate Unsolvable", true,
				1110, 45, BUTTON_WIDTH, BUTTON_HEIGHT);
		btnShowAllTC = createButton("Show all test case", true, 1110, 80,
				BUTTON_WIDTH, BUTTON_HEIGHT);

		btnPrev = createButton("Prev", true, 1110, 115, BUTTON_WIDTH / 2,
				BUTTON_HEIGHT);
		btnNext = createButton("Next", true, 1170, 115, BUTTON_WIDTH / 2,
				BUTTON_HEIGHT);

		btnExit = createButton("Exit", true, 1110, 150, BUTTON_WIDTH,
				BUTTON_HEIGHT);

		btnOpen.setToolTipText("Open source file");
	}

	private void createTableVariable(Font font, int x, int y, int width,
			int height) {
		tblVariable = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblVariable.setFont(font);
		tblVariable.setBounds(x, y, width, height);
		tblVariable.setHeaderVisible(true);
		tblVariable.setLinesVisible(true);

		colVarName = new TableColumn(tblVariable, SWT.LEFT);
		colVarName.setWidth(150);
		colVarName.setText("Variable name");

		colVarType = new TableColumn(tblVariable, SWT.LEFT);
		colVarType.setWidth(110);
		colVarType.setText("Variable type");
	}

	private void createTableMapping(Font font, int x, int y, int width,
			int height) {
		tblMappingTable = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblMappingTable.setFont(font);
		tblMappingTable.setBounds(x, y, width, height);
		tblMappingTable.setHeaderVisible(true);
		tblMappingTable.setLinesVisible(true);

		colExpression = new TableColumn(tblMappingTable, SWT.LEFT);
		colExpression.setWidth(120);
		colExpression.setText("Expression");

		colSymbol = new TableColumn(tblMappingTable, SWT.LEFT);
		colSymbol.setWidth(70);
		colSymbol.setText("Symbol");

		colReturnType = new TableColumn(tblMappingTable, SWT.LEFT);
		colReturnType.setWidth(90);
		colReturnType.setText("Return type");
	}

	private void createTableCondition(Font font, int x, int y, int width,
			int height) {
		tblCondition = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblCondition.setFont(font);
		tblCondition.setBounds(x, y, width, height);
		tblCondition.setHeaderVisible(true);
		tblCondition.setLinesVisible(true);

		colCondition = new TableColumn(tblCondition, SWT.RIGHT);
		colCondition.setWidth(150);
		colCondition.setText("Conditions");

		colTrue = new TableColumn(tblCondition, SWT.LEFT);
		colTrue.setWidth(70);
		colTrue.setText("True");

		colFalse = new TableColumn(tblCondition, SWT.LEFT);
		colFalse.setWidth(70);
		colFalse.setText("False");
	}

	private void createTableParameter(Font font, int x, int y, int width,
			int height) {
		tblParameter = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);
		tblParameter.setFont(font);
		tblParameter.setBounds(x, y, width, height);
		tblParameter.setHeaderVisible(true);
		tblParameter.setLinesVisible(true);

		colPara = new TableColumn(tblParameter, SWT.CENTER);
		colPara.setWidth(100);
		colPara.setText("Parameters");

		colValue = new TableColumn(tblParameter, SWT.LEFT);
		colValue.setWidth(60);
		colValue.setText("Value");
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

	protected void printMappingTable() {
		tblMappingTable.removeAll();

		ArrayList<MappingRecord> lstMappingRecord = control
				.getMappingRecordList();

		TableItem tblItem;
		for (int i = 0; i < lstMappingRecord.size(); i++) {
			tblItem = new TableItem(tblMappingTable, SWT.CENTER);

			tblItem.setText(0, lstMappingRecord.get(i).getExpression());
			tblItem.setText(1, lstMappingRecord.get(i).getSymbol());
			tblItem.setText(2, lstMappingRecord.get(i).getReturnType());
		}
	}

	protected void printParameterList() {
		tblParameter.removeAll();

		ArrayList<String> lstParameter = control.getParameterList();

		TableItem tblItem;
		for (int i = 0; i < lstParameter.size(); i++) {
			tblItem = new TableItem(tblParameter, SWT.CENTER);
			tblItem.setText(lstParameter.get(i));
		}
	}

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
				int offsetLine = txtSourceCode.getOffsetAtLine(line);
				while (txtSourceCode.getTextRange(offsetLine, 1).equals("\t")
						|| txtSourceCode.getTextRange(offsetLine, 1)
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
			txtSourceCode.setStyleRanges(ranges);
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
