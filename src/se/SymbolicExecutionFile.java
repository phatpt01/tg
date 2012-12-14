package se;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import system.Variable;

public class SymbolicExecutionFile {

	public static void appendAtBeginning(String sourceFile,
			ArrayList<Variable> lstNewVariable) {

		File file = new File(sourceFile);

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWritter = new FileWriter(sourceFile, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

			for (Variable variable : lstNewVariable) {
				bufferWritter.write("(declare-const " + variable.getName()
						+ " " + variable.getType() + ") \n");
			}

			bufferWritter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteAllFiles(String directoryPath) {
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		for (File file : files) {
			if (!file.delete()) {
				System.out.println("Failed to delete " + file);
			}
		}
	}

	public static String getAbsolutePathOfSmt2() {
		String z3output = "Z3OUTPUT";
		File z3outFolder = new File(z3output);

		if (!z3outFolder.exists()) {
			z3outFolder.mkdirs();
		}
		return z3outFolder.getAbsolutePath() + File.separatorChar;
	}

	public static String readFile(String filePath) {
		String result = "";
		try {
			FileInputStream fstream = new FileInputStream(filePath);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String strLine;

			while ((strLine = br.readLine()) != null) {
				result += strLine;
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void writeFile(String filename, String content) {
		try {
			FileWriter fileWriter = new FileWriter(filename);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(content);

			bufferedWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}