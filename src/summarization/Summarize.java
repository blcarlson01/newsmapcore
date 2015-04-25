package summarization;

import java.io.IOException;

import net.sf.classifier4J.summariser.SimpleSummariser;

import static utilities.ExtractWords.returnLine;

public class Summarize {

	public static void main(String[] args) throws IOException {
		// test
		System.out.println(summarizeClean("testDocs/1425828330904.txt", 2));
		
	}
	
	/*
	 * Takes an article and removes the reuters info line and the author line, 
	 * then passes it through the SimpleSummariser for summary. numSentences is 
	 * the number of sentences the summary should be. 
	 */
	public static String summarizeClean(String path, int numSentences) throws IOException {
		String article = returnLine(path, 10);
		if (article.equals(""))
			return "n/a";
		int dashIdx = 2 + article.indexOf('-');
		int parenIdx = article.lastIndexOf('(');
		if (dashIdx == -1 || parenIdx == -1)
			return summarizeDirty(path, numSentences);
		try {
			article = article.substring(dashIdx,  parenIdx - 1);
		}
		catch (java.lang.StringIndexOutOfBoundsException e) {
			return summarizeDirty(path, numSentences);
		}
		
		SimpleSummariser summary = new SimpleSummariser();
		return summary.summarise(article, numSentences);
	}
	
	/*
	 * Same as summarizeClean(), but doesn't edit the article
	 */
	public static String summarizeDirty(String path, int numSentences) throws IOException {
		String article = returnLine(path, 10);
		
		SimpleSummariser summary = new SimpleSummariser();
		return summary.summarise(article, numSentences);
	}

}
