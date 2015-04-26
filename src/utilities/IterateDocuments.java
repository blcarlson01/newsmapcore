package utilities;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static region.ExtractRegion.*;
import static utilities.ExtractWords.returnLine;
import static author.ExtractAuthor.*;
import static summarization.Summarize.*;

public class IterateDocuments {

	public static void main(String[] args) throws IOException {
		File file = new File("text");
		String[] files = file.list();
		FileWriter fw = new FileWriter("output.txt");
		Map<String, Point2D> cityMap = makeMap("cities.txt");
		
		System.out.println("Iterating through docs...");
		for (int i = 0; i < files.length; i++) {
			if (files[i].charAt(0) == '.') // not an article file
				continue;
			
			String articleText = returnLine("text/" + files[i], 10);
			if (articleText.length() == 0) // skip if article is empty
				continue;
			
			String location = getMainRegionAlt("text/" + files[i]);
			location = location.substring(0, 1) + location.substring(1, location.length()).toLowerCase(); // properly capitalize
			
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
			String url = returnLine("text/" + files[i], 1);
			String title = returnLine("text/" + files[i], 2);
			String publishDate = returnLine("text/" + files[i], 3);
			String category = returnLine("text/" + files[i], 4);
			String keywords = returnLine("text/" + files[i], 7);
			
			
			Point2D coord = cityMap.get(location.toLowerCase());
			String lat, lon;
			if (coord != null) {
				Double latitude = coord.getX();
				Double longitude = coord.getY();
				lat = latitude.toString();
				lon = longitude.toString();
			}
			else {
				lat = "-1.0";
				lon = "-1.0";
			}
			
			fw.write("{ \"docID\":" + articleID + 
					 ",\"url\":\"" + url + 
					 "\",\"title\":\"" + title + 
					 "\",\"pubDate\":\"" + publishDate + 
					 "\",\"category\":\"" + category +
					 "\",\"keywords\":\"" + keywords + 
					 "\",\"summary\":\"" + summary + 
					 "\",\"location\":\"" + location + 
					 "\",\"latitude\":" + lat + 
					 ",\"longitude\":" + lon +
					 "}\n"
					 );
		
			//System.out.println((i * 100)/(files.length));
		}
		fw.close();
		System.out.println("Done!");
	}
	
	public static Map<String, Point2D> makeMap(String path) throws IOException {
		System.out.println("Building city map...");
		Map<String, Point2D> output = new HashMap<String, Point2D>();
		FileReader file = new FileReader(path);
		Scanner sFile = new Scanner(file);
		sFile.nextLine();
		
		while (sFile.hasNext()) {
			String line = sFile.nextLine();
			String[] brokenLine = line.split(",");
			if (output.get(brokenLine[1]) == null) {
				double lat, lon;
				lat = Double.parseDouble(brokenLine[5]);
				lon = Double.parseDouble(brokenLine[6]);
				Point2D coord = new Point2D.Double(lat, lon);
				output.put(brokenLine[1], coord);
			}
		}
		
		
		sFile.close();
		file.close();
		return output;
	}
 
}
