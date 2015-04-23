package region;

import java.io.IOException;
import java.io.File;

import static utilities.ExtractWords.*;

public class ExtractRegion {

	// debug
	public static void main(String[] args) throws IOException {
		File file = new File("testDocs/");
		String[] files = file.list();
		
		String out = getMainRegion("testDocs/" + files[7]);
		//System.out.println(out);
		
	}
	
	/*
	 * Returns the first mentioned location name of the input article
	 * e.g. "ROME (Reuters) - [Article text]" as input returns "Rome"
	 */
	public static String getMainRegion(String inputFile) throws IOException {
		String article = returnLine(inputFile, 10);
		int end = article.indexOf('(') - 1;
		String output = article.substring(0, 1);
		output += article.substring(1, end).toLowerCase();
		
		return output;
	}
}


