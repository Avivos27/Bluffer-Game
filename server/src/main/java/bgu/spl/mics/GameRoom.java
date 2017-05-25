package mics;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The Class GameRoom. Used to hold different text based games, and active chats between the users of the room.
 */
public class GameRoom {

	private ConcurrentLinkedQueue<String> users;
	private boolean gameStarted = false;
	private final String name;
	private Game gamePlayed;

	/**
	 * Instantiates a new game room.
	 *
	 * @param name the game room name
	 */
	public GameRoom(String name) {
		users = new ConcurrentLinkedQueue<String>();
		this.name = name;
	}
	
	/**
	 * Sets the game played.
	 *
	 * @param game the new game that will be played at this room.
	 */
	public void setGamePlayed(Game game){
		gamePlayed = game;
	}
	
	/**
	 * Gets the game played.
	 *
	 * @return the game played
	 */
	public Game getGamePlayed(){
		return gamePlayed;
	}
	
	
	/**
	 * Gets the {@link GameRoom} name.
	 *
	 * @return the name of the {@link GameRoom}
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Adds a user to the room as long as there is no game in progress.
	 *
	 * @param nick the user who wishes to join nick`s
	 * @return true, if successful
	 */
	public synchronized boolean addUser(String nick){
		if(gameStarted) {
			System.out.println(nick +" tried to join but failed");
			return false;
		}
		else{
			users.add(nick);
			System.out.println(nick+" joined "+users.size());
			return true;
		}
	}
	
	/**
	 * Removes the user from the game room as long as there is no active game at the room.
	 *
	 * @param nick the user`s nick
	 * @return true, if successful
	 */
	public synchronized boolean removeUser(String nick){
		if(gameStarted) return false;
		else{
			users.remove(nick);
			System.out.println(nick+" left "+users.size());
			if(users.size()==0)
				SharedDS.getInstance().getRoomsMap().remove(name);
			return true;
		}
	}
	
	/**
	 * Checks for weather a game has started.
	 *
	 * @return true, if a game has started
	 */
	public synchronized boolean hasGameStarted(){
		return gameStarted;
	}
	
	/**
	 * Sets the status wheather a game has started or not.
	 *
	 * @param b the new game started status
	 */
	public void setGameStarted(boolean b){
		gameStarted = b;
	}
	
	/**
	 * End`s the current active game.
	 */
	public void endGame(){
		gameStarted = false;
		gamePlayed = null;
	}
	
	/**
	 * Start a new game in the room.
	 */
	public void StartGame(){
		gameStarted = true;
		gamePlayed.playRound();
	}
	
	/**
	 * Gets the users which are in the {@link GameRoom}.
	 *
	 * @return the users which are in the {@link GameRoom}.
	 */
	public ConcurrentLinkedQueue<String> getUsers(){
		return users;
	}
}
