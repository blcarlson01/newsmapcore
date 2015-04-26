package author;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static utilities.ExtractWords.returnLine;

public class ExtractAuthor {

	// debug
	public static void main(String[] args) throws IOException {
		//File file = new File("testDocs/");
		//String[] files = file.list();


		//System.out.println(getAuthor("testDocs/" + files[0]));
		/*
		for (int i = 0; i < out.size(); i++) {
			System.out.println(out.get(i));
		}
		*/
	}
	
	/*
	 * Parses the last line of the article and returns an ArrayList<String>
	 * where each element is a string starting with worker type (Reporter, Editor, etc.)
	 * and a list of that worker type
	 */
	public static ArrayList<String> findAuthor(String path) throws IOException {
		String article = returnLine(path, 10);
		int parenIdx = article.lastIndexOf('(');
		if (article.length() == 0)
			article = "n/a";
		article = article.substring(parenIdx + 1, article.length() - 1);
		String[] creators = article.split(";");
		ArrayList<String> output = new ArrayList<String>(Arrays.asList(creators));
		for (int i = 1; i < output.size(); i++) {
			String modified = output.get(i);
			output.set(i, modified.trim());
		}
		return output;
	}
	
	/*
	 * Parses the last line using findAuthor() and outputs a raw ArrayList<String> 
	 * of names 
	 * 
	 * TODO: Make it work properly when multiple workers of the same type work on 
	 * an article
	 */
	public static ArrayList<String> findAuthorNames(String path) throws IOException {
		ArrayList<String> authors = findAuthor(path);
		ArrayList<String> output = new ArrayList<String>();
		
		for (int i = 0; i < 3; i++) {
			String currLine = authors.get(i);
			int start = currLine.indexOf(" by ");
			if (start == -1)
				continue;
			start += 3;
			currLine = currLine.substring(start, currLine.length());
			output.add(currLine.trim());
		}

		return output;
	}
	
	public static String getAuthor(String inputFile) throws IOException {
		if (inputFile.contains("/."))
			return "n/a";
		String author = returnLine(inputFile, 6);
		if (!author.equals(""))
			return author;
		else {
			return findAuthor(inputFile).get(0);
		}
	}

}
