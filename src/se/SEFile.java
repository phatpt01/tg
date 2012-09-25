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

public class SEFile {

	public static void writeFile(String filename, String content) {
		try {
			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);

			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void createBlankFile(String filename) {
		File file = new File(filename);
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void copyfile(String srFile, String dtFile, boolean overWrite) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			OutputStream out;
			if (overWrite) {
				// For Overwrite the file.
				out = new FileOutputStream(f2);
			} else {
				// For Append the file.
				out = new FileOutputStream(f2, true);
			}

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendAtBeginning(String srFile,
			ArrayList<Variable> lstNewVariable) {

		File file = new File(srFile);

		try {
			// if file does not exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fileWritter = new FileWriter(srFile, true);
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

	public static String readFile(String z3FilePathSE) {
		String result = "";
		try {
			FileInputStream fstream = new FileInputStream(z3FilePathSE);

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