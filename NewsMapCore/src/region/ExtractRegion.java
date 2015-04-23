package region;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import static utilities.ExtractWords.*;

public class ExtractRegion {

	// debug
	public static void main(String[] args) throws IOException {
		File file = new File("testDocs/");
		String[] files = file.list();
		
		ArrayList<String> out = getAllCaps("testDocs/" + files[7]);
		for (int i = 0; i < out.size(); i++) {
			System.out.println(out.get(i));
		}
		
	}
	
	/*
	 * Many Reuters articles we crawled contain the main city location 
	 * in all caps, so this method extracts words in all caps (by checking
	 * if the first two characters are caps) from an ArrayList<String> and
	 * returns a new ArrayList<String> containing only those words
	 */
	public static ArrayList<String> getAllCaps(String inputFile) throws IOException {
		ArrayList<String> properNouns = findProperNouns(inputFile);
		ArrayList<String> output = new ArrayList<String>();
		
		for (int i = 0; i < properNouns.size(); i++) {
			String currentWord = properNouns.get(i);
			char secondLetter;
			
			if (currentWord.length() > 3)
				secondLetter = currentWord.charAt(1);
			else
				secondLetter = 100;
			
			// same comparison in findProperNouns(), but
			// with the second letter
			if (secondLetter >= 'A' && secondLetter <= 'Z') {
				output.add(currentWord);
			}
		// TODO: Sanitize strings thoroughly 
			
		}
		
		return output;
	}
}


