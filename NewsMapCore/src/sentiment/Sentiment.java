package sentiment;

public class Sentiment {
	public Sentiment(int score, int sentenceLength, String sentence) {
		super();
		this.score = score;
		this.sentenceLength = sentenceLength;
		this.sentence = sentence;
	}

	private int score;
	private int sentenceLength;
	private String sentence;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getSentenceLength() {
		return sentenceLength;
	}

	public void setSentenceLength(int sentenceLength) {
		this.sentenceLength = sentenceLength;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
}
