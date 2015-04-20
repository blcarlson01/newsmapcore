package sentiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
	        List<Sentiment> sentimentList = new  ArrayList<Sentiment>();	        
	        
	        if (line != null && line.length() > 0) {
	        	//int longest = 0;
	            Annotation annotation = pipeline.process(line);
	            for (CoreMap sentence : annotation
	                    .get(CoreAnnotations.SentencesAnnotation.class)) {
	                Tree tree = sentence
	                        .get(SentimentCoreAnnotations.AnnotatedTree.class);
	                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
	                String partText = sentence.toString();
	                
	               // String[] sentimentText = { "Very Negative","Negative", "Neutral", "Positive", "Very Positive"};
	               // System.out.println(partText + " = "+ sentimentText[sentiment] + " length: "+ partText.length());
	                
	                sentimentList.add(new Sentiment(sentiment, partText.length(), sentence.toString()));
	             /* 
	              * Keep for now, but should be removed b/c it give too much weight to the longest sentence  
	              * if (partText.length() > longest) {
	                    mainSentiment = sentiment;
	                    longest = partText.length();
	                }*/

	            }
	        }
	 	        
	        return sentimentList;
	 
	    }
	 
	public static void main(String[] args) throws IOException {
		
		String line = "MOSCOW (Reuters) - Russian authorities said on Sunday they were holding five men over the killing of Kremlin critic Boris Nemtsov, one of whom served in a police unit in the Russian region of Chechnya, according to a law enforcement official.";
		
		String line2 = "I love cats. I love every kind of cat. I just want to hug all of them. But I can't. Can't hug every cat. Can't hug every cat. So anyway. I am a cat lover and I love to run";
		String line3 = "Demanding a summit on the issue, Mr Renzi said trafficking was \"a plague in our continent\" and bemoaned the lack of European solidarity.  The 20m (70ft) long boat was believed to be carrying up to 700 migrants, and only 28 survivors have been rescued.  Up to 1,500 migrants are now feared to have drowned this year alone.  Human smugglers are taking advantage of the political crisis in Libya to use it as a launching point for boats carrying migrants who are fleeing violence or economic hardship in Africa and the Middle East.";
		
		String[] sentimentText = { "Very Negative","Negative", "Neutral", "Positive", "Very Positive"};
		
		//System.out.println("Moscow SENT: " + sentimentText[findSentiment(line)]);
		//System.out.println("Cats SENT: " + sentimentText[findSentiment(line2)]);
		//System.out.println("Death SENT: " + sentimentText[findSentiment(line3)]);

		//TODO: average score sum of scores / # of sentences
		
		//TODO: smoothing of the scores based on sentence length
		
		List<Sentiment> sentiments = findSentiment(line3);
		
		int totalScore = 0;
		int longestScore =0 ;
		int longest = 0;	
		
		int totaldoclen = 0;
		for (Sentiment sentiment : sentiments) {
			totaldoclen += sentiment.getSentenceLength();			
		}
		float avgdoclen = totaldoclen/sentiments.size();
		System.out.println("avg lenth: "+avgdoclen);
		
		
		List<Float> weights = new ArrayList<Float>();
		for (Sentiment sentiment : sentiments) {
			
			totalScore += sentiment.getScore();			
			int doclen = sentiment.getSentenceLength();
			int score = sentiment.getScore();
			
			
			float b = 0.5f; 
			float pn = 1-b+b*doclen/avgdoclen;
			int k = 1;
			float weight = 0f;
			if(score != 0)
			{
			 weight = (k+1) * score / score + k * pn;
			}
		
			System.out.println(sentiment.getSentence() + " : " + sentimentText[score] +" : "+score + " : modified weight: "+ weight+ " : " + doclen);
			weights.add(weight);
			
			if (sentiment.getSentenceLength() > longest) {
				longestScore = sentiment.getScore();
                longest =sentiment.getSentenceLength();
            }
		}
		
		System.out.println("Total Score: " + totalScore);
		System.out.println("Longest Sentence Score: "+ longestScore + " => "+sentimentText[longestScore]);
		System.out.println("Average Score: " + totalScore/sentiments.size() + " => "+sentimentText[totalScore/sentiments.size()]);
		
		float totalWeight = 0f;
		float highestWeight = 0f;
		for (Float weight : weights) {
			totalWeight += weight;
			if(weight > highestWeight)
			{
				highestWeight = weight;	
			}
		}
		
		System.out.println("Average Score from Modified Weights: " + totalWeight/sentiments.size() + " : floor = "
				+ sentimentText[(int) Math.floor(totalWeight/sentiments.size())] + " : ceiling = " + sentimentText[(int) Math.ceil(totalWeight/sentiments.size())]);
		System.out.println("Highest Weight: " + highestWeight);
		
	}

}
