package author;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static utilities.ExtractWords.returnLine;

public class ExtractAuthor {

	// debug
	public static void main(String[] args) throws IOException {
		File file = new File("testDocs/");
		String[] files = file.list();

		ArrayList<String> out = findAuthorNames("testDocs/" + files[1]);
		/*
		for (int i = 0; i < out.size(); i++) {
			System.out.println(out.get(i));
		}
		*/
	}
	
	public static ArrayList<String> findAuthor(String path) throws IOException {
		String article = returnLine(path, 10);
		int parenIdx = article.lastIndexOf('(');
		article = article.substring(parenIdx + 1, article.length() - 1);
		String[] creators = article.split(";");
		ArrayList<String> output = new ArrayList<String>(Arrays.asList(creators));
		for (int i = 1; i < output.size(); i++) {
			String modified = output.get(i);
			output.set(i, modified.trim());
		}
		return output;
	}
	
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

}
