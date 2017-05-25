package bluffer;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Class Question.
 */
public class Question {

	private String questionText;
	private String realAnswer;
	private ConcurrentLinkedQueue<String> userAnswers;
	
	/**
	 * Instantiates a new question, parameter constructor.
	 *
	 * @param questionText the question text
	 * @param realAnswer the real answer
	 */
	public Question(String questionText, String realAnswer) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
		userAnswers = new ConcurrentLinkedQueue<String>();
	}
	
	/**
	 * Instantiates a new question, copy constructor.
	 *
	 * @param question the question to be copied into the new Question.
	 */
	public Question(Question question) {
		this.questionText = question.getQuestionText();
		this.realAnswer = question.getRealAnswer();
		userAnswers = new ConcurrentLinkedQueue<String>();
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
	
	/**
	 * Gets the user answers.
	 *
	 * @return a queue containing the players answers.
	 */
	public ConcurrentLinkedQueue<String> getUserAnswers(){
		return userAnswers;
	}
	
	/**
	 * Adds a player`s answer.
	 *
	 * @param text the answer
	 */
	public void addUserAnswer(String text){
		userAnswers.add(text);
	}
	
	/**
	 * Returns the player`s answers after shuffling. 
	 *
	 * @return the player`s submitted answers
	 */
	public String[] getChoices(){
		userAnswers.add(realAnswer);
		String[] temp = userAnswers.toArray(new String[0]);
		Random rnd = new Random();
		for (int i = temp.length-1;i>0;i--){
			int index = rnd.nextInt(i + 1);
			String str = temp[index];
			temp[index] = temp[i];
			temp[i] = str;
		}
		return temp;
	}
}
