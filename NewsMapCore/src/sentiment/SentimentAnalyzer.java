package sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import news.Article;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author blcarlson
 *
 */
public class SentimentAnalyzer {

	public static List<Sentiment> findSentiment(String line) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		List<Sentiment> sentimentList = new ArrayList<Sentiment>();

		if (line != null && line.length() > 0) {
			Annotation annotation = pipeline.process(line);
			for (CoreMap sentence : annotation
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentCoreAnnotations.AnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);

				// String[] sentimentText = { "Very Negative","Negative",
				// "Neutral", "Positive", "Very Positive"};
				// System.out.println(partText + " = "+ sentimentText[sentiment]
				// + " length: "+ partText.length());

				sentimentList.add(new Sentiment(sentiment, sentence.toString().length(),
						sentence.toString()));
			}
		}

		return sentimentList;
	}
	
	private static void writeToTextFileList(String extfileName, String articleFileName)
	{
		final String path = "/Users/Shared/CrawlerData/sentiment/";		
		String fileName = path + extfileName+ ".txt";		
		try {
			final File file = new File(fileName);			
			if (file.exists()) {					
				// Add to File
				addToProcessedFile(file, articleFileName);				
			} else {
				// Create File
				file.createNewFile();
				addToProcessedFile(file, articleFileName);
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void addToProcessedFile(File file, String articleFileName)
	{
	// Append to file
	FileWriter fileWritter;
	try {
		fileWritter = new FileWriter(file, true);
		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		bufferWritter.write(articleFileName);
		bufferWritter.newLine();
		bufferWritter.flush();
		bufferWritter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	@SuppressWarnings("resource")
	private static List<String> processedTextFiles()
	{
		List<String> files = new ArrayList<String>();
		final String path = "/Users/Shared/CrawlerData/sentiment/";		
		String fileName = path + "processedFiles.txt";	
		try {
		File textFile = new File(fileName);
		if(textFile.exists())
		{
		FileReader fr = new FileReader(textFile);		
		BufferedReader br = new BufferedReader(fr);
		String line;
		while ((line = br.readLine()) != null) {
			files.add(line);
		}
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return files;
	}
	
	@SuppressWarnings("resource")
	private static List<Article> loadTextFiles(String path)
	{
		//int xx = 0;
		final File folder = new File(path);
		List<Article> articles = new ArrayList<Article>();
		List<String> processedFiles = processedTextFiles();
		try {
			List<List<String>> files = new ArrayList<List<String>>();
			List<String> fileContents = new ArrayList<String>();
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.getName().endsWith("txt") 
						&& !processedFiles.contains(fileEntry.getName())) {
					File textFile = new File(path + fileEntry.getName());
					FileReader fr = new FileReader(textFile);
					BufferedReader br = new BufferedReader(fr);
					String line;
					fileContents.add(textFile.getName());
					while ((line = br.readLine()) != null) {
						fileContents.add(line);
					}

					files.add(fileContents);
					fileContents = new ArrayList<String>();
					writeToTextFileList("processedFiles", textFile.getName());
					/*
					if (xx == 1) {
						break;
					}
					xx++;
					*/
				}
			}
						
			for (int x = 0; x < files.size(); x++) {
				List<String> contents = files.get(x);
				Article article = new Article();
				article.setArticleId(contents.get(0));
				article.setLink(contents.get(1));
				article.setTitle(contents.get(2));
				article.setPubdate(contents.get(3));
				article.setCategory(contents.get(4));
				article.setDescription(contents.get(5));
				article.setAuthor(contents.get(6));
				article.setKeywords(contents.get(7));
				article.setImagePath(contents.get(8));
				article.setLocation(contents.get(9));
				article.setBody(contents.get(10));
				
				if(article.getBody().length() <= 0)
				{
					// Don't process empty body files
					// Mark them in processed, but also mark them in a separate file for review
					writeToTextFileList("emptyBodyIds", article.getArticleId());
				}
				else
				{
					articles.add(article);	
				}				
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return articles;
	}

	public static void main(String[] args) throws IOException {	
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();		
		
		Gson gson = new Gson();
		String path = "/Users/Shared/CrawlerData/text/";				
		List<Article> articles = loadTextFiles(path);
		JsonArray articleInfoList = new JsonArray();	
		
		//TODO: comeback and clean up
		int jsonFileNameNumber = 0;
		int numberOfJsonArticlesPerFile = 1000;
		int outputToSmallerFilesCount = 0;
		for (int i = 0; i < articles.size(); i++) {
			Article article = articles.get(i);
			JsonObject articleInfo = new JsonObject();
			List<Sentiment> sentiments = findSentiment(articles.get(i).getBody());
			articleInfo.addProperty("articleId", article.getArticleId());					
			int totalScore = 0;
			int longestScore = 0;
			int longest = 0;
			JsonArray sentences = new JsonArray();
						
			for (Sentiment sentiment : sentiments) 
			{				
				JsonObject sentence = new JsonObject();
				totalScore += sentiment.getScore();						
				
				sentence.addProperty("sentence", sentiment.getSentence());
				sentence.addProperty("sentenceLength", sentiment.getSentenceLength());
				sentence.addProperty("score", sentiment.getScore());		
								
				if (sentiment.getSentenceLength() > longest) {
					longestScore = sentiment.getScore();
					longest = sentiment.getSentenceLength();
				}
				
				sentences.add(sentence);
			}
			/*String[] sentimentText = { "Very Negative", "Negative", "Neutral",
					"Positive", "Very Positive" };
			System.out.println("Total Score: " + totalScore
					+ " | numOfSentences: " + sentiments.size());
			System.out.println("Longest Sentence Score: " + longestScore
					+ " => " + sentimentText[longestScore]);
			System.out.println("Average Score: "
					+ (float) totalScore / sentiments.size()
					+ " => "
					+ sentimentText[Math.round((float) totalScore / sentiments.size())]); */
			articleInfo.addProperty("averageScore", (float) totalScore / sentiments.size());
			articleInfo.addProperty("longestSentenceScore", longestScore);
			articleInfo.addProperty("descriptionScore", 0);
			articleInfo.add("sentences", sentences);			
			articleInfoList.add(articleInfo);
			
			if(outputToSmallerFilesCount == numberOfJsonArticlesPerFile)
			{
				writeToTextFileList("json"+ jsonFileNameNumber, gson.toJson(articleInfoList));
				articleInfoList = new JsonArray();	
				outputToSmallerFilesCount = 0;
				jsonFileNameNumber++;
			}
			
			outputToSmallerFilesCount++;
			System.out.println("Completed - FileName: " + article.getArticleId());
			System.out.println("");
		}
		
		if(articleInfoList.size() > 0)
		{
		writeToTextFileList("json"+ jsonFileNameNumber, gson.toJson(articleInfoList));
		//System.out.println(gson.toJson(articleInfoList));
		}
		
		stopWatch.stop();
		System.out.println("Total Files: "+ articles.size());
	    System.out.println("Time to Complete (Minutes): "+TimeUnit.MILLISECONDS.toMinutes(stopWatch.getTime()));
	}

}
