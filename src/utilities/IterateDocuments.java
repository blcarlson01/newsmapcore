package utilities;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import static region.ExtractRegion.*;
import static utilities.ExtractWords.returnLine;
import static author.ExtractAuthor.*;
import static summarization.Summarize.*;

public class IterateDocuments {

	public static void main(String[] args) throws IOException {
		File file = new File("text");
		String[] files = file.list();
		FileWriter fw = new FileWriter("output.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		Map<String, Point2D> cityMap = makeMap("allCountries.txt");
		
		int emptyArticles = 0;
		int emptyAuthors = 0;
		int emptyCoords = 0;
		int totalSkips = 0;
		
		System.out.println("Iterating through docs...");
		for (int i = 0; i < files.length; i++) {
			if (files[i].charAt(0) == '.') // not an article file
				continue;
			
			
			String articleText = returnLine("text/" + files[i], 10);
			if (articleText.length() == 0) { // skip if article is empty
				++ emptyArticles;
				++ totalSkips;
				continue;
			}
			
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
			else {
				++ emptyAuthors;
				++ totalSkips;
				continue;
			}
			

			
			
			Point2D coord = cityMap.get(location.toLowerCase());
			double lat = -1.0, lon = -1.0;
			if (coord != null) {
				lat = coord.getX();
				lon = coord.getY();
			}
			else {
				++ emptyCoords;
				++ totalSkips;
				//System.out.println(articleID + " - " + location);
				continue;
				//lat = "-1.0";
				//lon = "-1.0";
			}
			
			
			String summary = summarizeClean("text/" + files[i], 1);
			String url = returnLine("text/" + files[i], 1);
			String title = returnLine("text/" + files[i], 2);
			String publishDate = returnLine("text/" + files[i], 3);
			String category = returnLine("text/" + files[i], 4);
			String keywords = returnLine("text/" + files[i], 7);
			
			Gson gson = new Gson();
			JsonObject textDocument = new JsonObject();
			JsonObject locationInfo = new JsonObject();
			locationInfo.addProperty("name", location);
			locationInfo.addProperty("lat", lat);
			locationInfo.addProperty("lon", lon);
			
			textDocument.addProperty("articleId",  articleID);
			textDocument.addProperty("date", publishDate);
			textDocument.addProperty("provider", "Reuters");
			textDocument.addProperty("cluster_url", "");
			textDocument.addProperty("summary", summary);
			textDocument.addProperty("title", title);
			textDocument.add("location", locationInfo);
			textDocument.addProperty("url", url);
			textDocument.addProperty("imageUrl", returnLine("text/" + files[i], 8));
			textDocument.addProperty("category", category);
			textDocument.addProperty("keywords", keywords);
			
			bw.write(gson.toJson(textDocument));
			System.out.println(i);
			
			/*
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
			*/
		}
		bw.close();
		fw.close();
		System.out.println("Finished:\n" + emptyArticles + " empty articles"
				+ "\n" + emptyAuthors + " missing authors"
				+ "\n" + emptyCoords + " missing coordinates"
				+ "\n" + totalSkips + " total articles skipped"
				+ "\n" + (files.length - totalSkips) + " articles included");
	}
	
	public static Map<String, Point2D> makeMap(String path) throws IOException {
		System.out.println("Building city map...");
		Map<String, Point2D> output = new HashMap<String, Point2D>();
		FileReader file = new FileReader(path);
		Scanner sFile = new Scanner(file);
		sFile.nextLine();
		
		while (sFile.hasNext()) {
			String line = sFile.nextLine();
			String[] brokenLine = line.split("\t");
			try {
				if (output.get(brokenLine[2]) == null && 
				   (Integer.parseInt(brokenLine[14]) > 5000) &&
				   (brokenLine[6].equals("A") || brokenLine[6].equals("P") &&
						   !brokenLine[7].contains("PPLA"))) {
					System.out.println(brokenLine[2] + " - " + brokenLine[14]);
					double lat, lon;
					lat = Double.parseDouble(brokenLine[4]);
					lon = Double.parseDouble(brokenLine[5]);
					Point2D coord = new Point2D.Double(lat, lon);
					output.put(brokenLine[2].toLowerCase(), coord);
				}
			}
			catch (java.lang.NumberFormatException e) {
				System.out.println("String exception for " + brokenLine[14]);
				continue;
			};
		}
			
		sFile.close();
		file.close();
		return output;
	}
 
}
