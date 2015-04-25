package utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static region.ExtractRegion.*;
import static utilities.ExtractWords.returnLine;
import static author.ExtractAuthor.*;
import static summarization.Summarize.*;

public class IterateDocuments {

	public static void main(String[] args) throws IOException {
		File file = new File("text");
		String[] files = file.list();
		FileWriter fw = new FileWriter("output.txt");
		
		for (int i = 0; i < files.length; i++) {
			if (files[i].charAt(0) == '.')
				continue;
			String location = getMainRegionAlt("text/" + files[i]);
			location = location.substring(0, 1) + location.substring(1, location.length()).toLowerCase();
			
			String articleID = files[i].substring(0, files[i].length() - 4);
			
			String author = getAuthor("text/" + files[i]);
			int newStart = author.indexOf("By ");
			if (newStart == -1) {
				newStart = author.indexOf(" by ");
			}
			if (newStart != -1) {
				newStart ++;
				author = author.substring(newStart + 2,  author.length()).trim();
			}
			else
				continue;
			
			String summary = summarizeClean("text/" + files[i], 1);
			
			String url = returnLine("text/" + files[i], 0);
			
			//System.out.println(url + " - " + articleID + " - " + location + " - " + author + " - " + summary);
			fw.write("{ \"url\": \"" + url + "\", \"articleID\": " + articleID + ", \"location\": " + "\"" + location + "\"" +
					   ", \"author\": " + "\"" + author + "\"" + ", \"summary\": " + "\"" + summary +"\" }\n");
			
			System.out.println((i * 100)/(files.length));
		}
		fw.close();
	}
	
	
}
