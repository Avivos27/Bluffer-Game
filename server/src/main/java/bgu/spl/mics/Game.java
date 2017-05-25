package mics;

/**
 * The Interface Game.
 */
public interface Game {
	
	/**
	 * Play`s a round in a game.
	 */
	public void playRound();
	
	/**
	 * A method to check if the game is over.
	 *
	 * @return true, if the game is over
	 */
	public boolean gameOver();

	/**
	 * A method used to handle a respond from a player.
	 *
	 * @param text the respond text
	 * @param nick the player which responded nick`s
	 */
	public void respond(String text, String nick);
	
	/**
	 *  A method used to handle a select respond from a player.
	 *
	 * @param choice the choice the player has made
	 * @param nick the player which made the choice nick`s
	 */
	public void select(String choice, String nick);
}
