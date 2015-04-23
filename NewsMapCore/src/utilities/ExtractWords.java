package utilities;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ExtractWords {

	// only used for debug
	public static void main(String[] args) throws IOException {
		File file = new File("testDocs/");
		String[] files = file.list();

		ArrayList<String> out = findProperNouns("testDocs/" + files[1]);
		for (int i = 0; i < out.size(); i++) {
			System.out.println(out.get(i));
		}
	}

	/*
	 * findProperNouns takes a text document as input and outputs
	 * an ArrayList<String> containing all words starting with a 
	 * capital letter
	 */
	public static ArrayList<String> findProperNouns(String inputFile) throws IOException {
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<String> wordArray = new ArrayList<String>(articleToArray(inputFile));
		
		// checks if the word starts with a capital letter
		for (int i = 0; i < wordArray.size(); i++) {
			if (wordArray.get(i).charAt(0) >= 'A' && wordArray.get(i).charAt(0) <= 'Z')
				output.add(wordArray.get(i));
		}
	
		return output;
	}
	
	public static ArrayList<String> articleToArray(String inputFile) throws IOException {
		FileReader file = new FileReader(inputFile);
		Scanner article = new Scanner(file);
		ArrayList<String> output = new ArrayList<String>();
		
		while (article.hasNext()) {
			String nextWord = article.next();
			output.add(nextWord);
		}
		
		article.close();
		file.close();
		return output;
	}
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static String returnLine(String inputFile, int lineNum) throws IOException {
		String output;
		FileReader file = new FileReader(inputFile);
		Scanner article = new Scanner(file);
		
		for (int i = 0; i < lineNum - 1; ++i) {
			article.nextLine();
		}
		
		output = article.nextLine();
		article.close();
		file.close();
		return output;
	}

}
