package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileTools {

	public static ArrayList<String> readFile(String filePath) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			ArrayList<String> data = new ArrayList<String>();
			String s;
			while ((s = br.readLine()) != null) {
				data.add(s.replace("\n", ""));
			}
			br.close();
			return data;
		} catch (Exception e) {
			System.out.println(e.getClass().getCanonicalName() + " on " + filePath + " : " + e.getMessage());
			return null;
		}
	}
	
	public static void writeFile(String filePath, ArrayList<String> fileData) {
		try {
			PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			for (String s : fileData) {
				writer.println(s);
			}
			writer.close();
		} catch(Exception e) {
			System.out.println(e.getClass().getCanonicalName() + " on " + filePath + " : " + e.getMessage());
		}
	}

}
