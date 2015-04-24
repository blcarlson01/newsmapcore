package sentiment;

import static utilities.ExtractWords.returnLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.*;

public class mainSentiment {

	// debug
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file = new File("testDocs/");
		String[] files = file.list();
		
		System.out.println(files[0]);
		sentiment("testDocs/" + files[2]);

		//sentiment("testDocs/1425828330904.txt");
	}
	
	/*
	 * Documentation coming soon; cleanup also coming soon
	 */
	public static void sentiment(String path) throws IOException {
		Properties props = new Properties();
		props.setProperty("annotators",  "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
	
		// TODO: turn this into method in utils
		String article = returnLine(path, 10);
		int dashIdx = 2 + article.indexOf('-');
		int parenIdx = article.lastIndexOf('(');
		if (dashIdx < parenIdx) // band-aid to prevent crash-and-burn
			article = article.substring(dashIdx,  parenIdx - 1);
		
		Annotation document = new Annotation(article);
		
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for (CoreMap sentence: sentences) {
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String pos = token.get(PartOfSpeechAnnotation.class);
				String ne = token.get(NamedEntityTagAnnotation.class);
			}
			
			Tree tree = sentence.get(TreeAnnotation.class);
			
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}
		
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		
		int totalSentiment = 0;
		int count = 0;
		Annotation annotation = pipeline.process(article);
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
			int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			totalSentiment += sentiment;
			count ++;
		}
		
		double avgSentiment = ((double) totalSentiment)/((double) count);
		
		System.out.println("Sentiment = " + avgSentiment);
	}
	

}
