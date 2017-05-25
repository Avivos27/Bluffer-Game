package mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import protocol.ProtocolCallback;
import tokenizer.StringMessage;

/**
 * The Class SharedDS.
 * A singleton class that describes a shared data-structure containing information
 * on rooms, network status and games available.
 */
public class SharedDS {
	
	private ConcurrentHashMap<String, GameRoom> roomsMap;
	private ConcurrentHashMap<String, ProtocolCallback<StringMessage>> networkMap;
	private ConcurrentLinkedQueue<String> games;

	/**
	 * Instantiates a new singleton shared data-structure.
	 */
	private SharedDS(){
		
		roomsMap = new ConcurrentHashMap<String,GameRoom>();
		networkMap = new ConcurrentHashMap<String, ProtocolCallback<StringMessage>>();
		games = new ConcurrentLinkedQueue<String>();
		games.add("BLUFFER");
	}
	
	/**
	 * The Class SingletonHolder.
	 * An inner class used for holding the singleton SharedDS.
	 */
	private static class SingletonHolder {
		
		private static SharedDS instance = new SharedDS();
	}
	
	/**
	 * Gets the network map.
	 *
	 * @return the network map
	 */
	public ConcurrentHashMap<String, ProtocolCallback<StringMessage>> getNetworkMap() {
		return networkMap;
	}

	/**
	 * Gets the rooms map.
	 *
	 * @return the rooms map
	 */
	public ConcurrentHashMap<String,GameRoom> getRoomsMap() {
		return roomsMap;
	}

	/**
	 * Gets the games available.
	 *
	 * @return the games available
	 */
	public ConcurrentLinkedQueue<String> getGames() {
		return games;
	}
	
	/**
	 * return the instance of the singleton SharedDS.
	 *
	 * @return single instance of SharedDS
	 */
	public static SharedDS getInstance(){
		return SingletonHolder.instance;
	}

}
