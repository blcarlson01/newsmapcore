package author;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static utilities.ExtractWords.articleToArray;

public class ExtractAuthor {

	// debug
	public static void main(String[] args) throws IOException {
		File file = new File("testDocs/");
		String[] files = file.list();

		ArrayList<String> out = findAuthor("testDocs/" + files[1]);
		for (int i = 0; i < out.size(); i++) {
			System.out.println(out.get(i));
		}
	}
	
	public static ArrayList<String> findAuthor(String inputfile) throws IOException {
		ArrayList<String> article = new ArrayList<String>(articleToArray(inputfile));
		ArrayList<String> output = new ArrayList<String>();
		

		return output;
	}

}
