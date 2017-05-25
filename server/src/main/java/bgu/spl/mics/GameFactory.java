/*
 * 
 */
package mics;

import bluffer.BlufferGame;

/**
 * A factory for creating Game objects.
 */
public class GameFactory {
	
	/**
	 * Creates a specific game according to the game name passed as a parameter.
	 *
	 * @param gameName the game`s name
	 * @param room the {@link GameRoom} in which the game will be held
	 * @return the the {@link Game} that has been created
	 */
	public static Game create(String gameName, GameRoom room){
		switch(gameName){
			case "BLUFFER":
				return new BlufferGame(room);
			default:
				return null;
		}
	}
}
