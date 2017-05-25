package bluffer;

/**
 * The Class Player.
 */
public class Player {
	
	private String nick;
	private int score;
	private int choice;
	private String bluff;
	private boolean responded;
	private int roundScore = 0;
	private boolean answeredCorrectly = true;
	
	/**
	 * Instantiates a new player.
	 *
	 * @param nick the player`s nick
	 * @param score the player`s score 
	 * @param choice the player`s choice
	 * @param bluff the player`s bluff
	 * @param responded a field representing weather the user has responded or not
	 */
	public Player(String nick, int score, int choice, String bluff, boolean responded) {
		this.nick = nick;
		this.score = score;
		this.choice = choice;
		this.responded = responded;
		this.bluff = bluff;
	}

	/**
	 * Gets the nick.
	 *
	 * @return the player`s nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Gets the score.
	 *
	 * @return the player`s score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Increase a player`s score by a specific value.
	 *
	 * @param score the score
	 */
	public void incScoreBy(int score) {
		this.score += score;
	}

	/**
	 * Gets the choice.
	 *
	 * @return the player`s choice
	 */
	public int getChoice() {
		return choice;
	}

	/**
	 * Sets the choice.
	 *
	 * @param choice the new player`s choice
	 */
	public void setChoice(int choice) {
		this.choice = choice;
	}

	/**
	 * Checks if the user has responded.
	 *
	 * @return true, if responded
	 */
	public boolean hasResponded() {
		return responded;
	}

	/**
	 * Sets the player`s responded status.
	 *
	 * @param responded the player`s new responded status.
	 */
	public void setResponded(boolean responded) {
		this.responded = responded;
	}

	/**
	 * Gets the bluff.
	 *
	 * @return the player`s bluff
	 */
	public String getBluff() {
		return bluff;
	}

	/**
	 * Sets the bluff.
	 *
	 * @param bluff the player`s new bluff
	 */
	public void setBluff(String bluff) {
		this.bluff = bluff;
	}

	/**
	 * Gets the round score.
	 *
	 * @return the player`s round score
	 */
	public int getRoundScore() {
		return roundScore;
	}

	/**
	 * Sets the round score.
	 *
	 * @param roundScore the player`s new round score
	 */
	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
	}

	/**
	 * Increase round score by a specific value.
	 *
	 * @param i the value to increase the player`s round score by
	 */
	public void incRoundScoreBy(int i) {
		this.roundScore += i;
		
	}

	/**
	 * Checks if the user answered correctly.
	 *
	 * @return true, if answered correctly.
	 */
	public boolean didAnswerCorrectly() {
		return answeredCorrectly;
	}

	/**
	 * Sets the player`s answered correctly status
	 *
	 * @param answeredCorrectly the new answered correctly status
	 */
	public void setAnsweredCorrectly(boolean answeredCorrectly) {
		this.answeredCorrectly = answeredCorrectly;
	}
	
	/**
	 * Reset the player`s parameters of a specific round.
	 */
	public void resetForRound(){
		roundScore = 0;
		choice = -1;
		responded = false;
		bluff = "";
	}
}
