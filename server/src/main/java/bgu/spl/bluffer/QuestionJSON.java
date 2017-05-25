package bluffer;

/**
 * The Class QuestionJSON.
 * Used for receiving questions to the database using JSON files.
 */
public class QuestionJSON {

	private String questionText;
	private String realAnswer;
	
	/**
	 * Instantiates a new question.
	 *
	 * @param questionText the question text
	 * @param realAnswer the real answer
	 */
	public QuestionJSON(String questionText, String realAnswer) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
	}
	
	/**
	 * Gets the question text.
	 *
	 * @return the question text
	 */
	public String getQuestionText() {
		return questionText;
	}
	
	/**
	 * Gets the real answer.
	 *
	 * @return the real answer
	 */
	public String getRealAnswer() {
		return realAnswer;
	}
}
