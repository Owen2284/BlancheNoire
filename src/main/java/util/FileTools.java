package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Static methods that simplify file reading, writing and merging methods.
 */
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
	
	public static String[] getAllFilePathsInDir(String dir) {
		File d = new File(dir);
		File[] allFiles = d.listFiles();
		int fileCount = 0;
		for (File f : allFiles) {
			if (f.isFile()) {++fileCount;}
		}
		String[] allPaths = new String[fileCount];
		int ptr = 0;
		for (File f : allFiles) {
			if (f.isFile()) {
				allPaths[ptr] = f.getPath();
				++ptr;				
			}
		}
		return allPaths;
	}
	
	public static File[] getAllFilesInDir(String dir) {
		File d = new File(dir);
		File[] allFiles = d.listFiles();
		return allFiles;
	}
	
	public static ArrayList<String> readDir(String dir) {
		ArrayList<String> all = new ArrayList<String>();
		for (String path : getAllFilePathsInDir(dir)) {
			all.addAll(readFile(path));
		}
		return all;
	}

	public static void mergeDir(String dir, String newFileName) {
		ArrayList<String> merged = new ArrayList<String>();
		for (File f : getAllFilesInDir(dir)) {
			merged.addAll(readFile(f.getPath()));
			f.delete();
		}
		writeFile(dir + newFileName, merged);
	}

	public static void mergeDirNoDelete(String dir, String newPath) {
		ArrayList<String> merged = new ArrayList<String>();
		for (File f : getAllFilesInDir(dir)) {
			merged.addAll(readFile(f.getPath()));
		}
		writeFile(newPath, merged);
	}

}
