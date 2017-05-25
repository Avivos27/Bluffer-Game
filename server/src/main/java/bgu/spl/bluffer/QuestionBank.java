package bluffer;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionBank.
 */
public class QuestionBank {
	
	private Question[] questions;
	
	/**
	 * Instantiates a new question bank.
	 *
	 * @param questions the questions to be uploaded to the {@link QuestionBank}
	 */
	public QuestionBank(Question[] questions){
		this.questions = questions;
	}

	/**
	 * Returns the questions.
	 *
	 * @return the questions
	 */
	public Question[] getQuestions() {
		return questions;
	}
}
