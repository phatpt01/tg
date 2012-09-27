package se;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import system.Variable;

public class SymbolicExecutionFile {

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

	public static void createBlankFile(String filename) {
		File file = new File(filename);
		OutputStream outputStream;
		
		try {
			outputStream = new FileOutputStream(file);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void copyfile(String sourceFile, String destinationFile, boolean overwrite) {
		try {
			File source = new File(sourceFile);
			File destination = new File(destinationFile);
			InputStream inputStream = new FileInputStream(source);

			OutputStream outputStream;
			if (overwrite) {
				outputStream = new FileOutputStream(destination);
			} else {
				outputStream = new FileOutputStream(destination, true);
			}

			byte[] buffer = new byte[1024];
			int length;
			
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			
			inputStream.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendAtBeginning(String sourceFile,
			ArrayList<Variable> lstNewVariable) {

		File file = new File(sourceFile);

		try {
			// if file does not exists, then create it
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
}