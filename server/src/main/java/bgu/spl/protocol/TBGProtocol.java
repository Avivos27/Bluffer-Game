package protocol;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import bluffer.Player;
import mics.Game;
import mics.GameFactory;
import mics.GameRoom;
import mics.SharedDS;
import tokenizer.StringMessage;

/**
 * The Class TBGProtocol.
 * Desribes a text based game protocol, each user gets an instance of this protocol.
 */
public class TBGProtocol implements AsyncServerProtocol<StringMessage> {
	
	private SharedDS ds;
	private String nick;
	private String roomName;
	private final String ACCEPTED = "ACCEPTED";
	private final String REJECTED = "REJECTED";
	private final String UNIDENTIFIED = "UNIDENTIFIED";
	private boolean connectionTerminated = false;
	
	/**
	 * Instantiates a new TBG protocol.
	 *
	 * @param ds the shared data-structure in the server.
	 */
	public TBGProtocol(SharedDS ds){
		this.ds = ds;
	};

/**
 * processing a message from a user.
 * once processed checks if command is in syntax, and allowed due to current state of the {@link Player} and the {@link GameRoom}  
 */
	@Override
	public void processMessage(StringMessage msg, ProtocolCallback<StringMessage> callback) {
			String message = msg.getMessage();
			String cmd = "";
			try{
				cmd = message.substring(0,message.indexOf(' '));
			}
			catch (StringIndexOutOfBoundsException e){
				message+=" ";
				cmd = message.substring(0,message.indexOf(' '));
			}
			if(nick!=null || cmd.equals("NICK")){
			String parameter = message.substring(message.indexOf(' ')+1);
			String prefix = "SYSMSG "+cmd+" ";
			String response = "";
			boolean sendMessage = true;
			try{
				switch(cmd){
						case "NICK":
								response = determineResponse(addNick(parameter,callback));
							break;
						case "JOIN":
							if(roomName == null){
								response = determineResponse(joinRoom(parameter));
							}
							else{
								if(ds.getRoomsMap().containsKey(parameter))
									response = determineResponse(switchRooms(parameter));
								else{ 
									if (leaveRoom())
										response = determineResponse(joinRoom(parameter));
									else
										response = REJECTED;
								}
							}
							break;
						case "MSG":
								response = determineResponse(sendRoomMessage(parameter,roomName,nick));
							break;
						case "LISTGAMES":
							for (String game : ds.getGames())
							callback.sendMessage(new StringMessage(game));
							response = ACCEPTED;
							break;	
						case "STARTGAME":
							sendMessage = false;
							startGame(parameter);
							break;
						case "TXTRESP":
							if (roomName==null || ds.getRoomsMap().get(roomName).getGamePlayed()==null){
								response = REJECTED;
								break;
								}
							sendMessage = false;
							ds.getRoomsMap().get(roomName).getGamePlayed().respond(parameter,nick);
							break;
						case "SELECTRESP":
							if (roomName==null || ds.getRoomsMap().get(roomName).getGamePlayed()==null){
								response = REJECTED;
								break;
								}
							sendMessage = false;
							ds.getRoomsMap().get(roomName).getGamePlayed().select(parameter,nick);
							break;
						case "QUIT":
							if(canQuit()){
								connectionTerminated();
								response = ACCEPTED;
							}
							else
								response = REJECTED;
							break;			
						default: 
							response = UNIDENTIFIED;
							break;
				}
				if (sendMessage)
					callback.sendMessage(new StringMessage(prefix+response));
	
			} catch (IOException e){};
		} else
			try {
				callback.sendMessage(new StringMessage("PLEASE CHOOSE NICK FIRST"));
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Sends a room message (to all users which are int the room).
	 *
	 * @param msg the message to be sent
	 * @param room the {@link GameRoom} to send the message in
	 * @param sender the sender
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean sendRoomMessage(String msg, String room, String sender) throws IOException {
		if (room == null)
			return false;
		ConcurrentLinkedQueue<String> users = ds.getRoomsMap().get(room).getUsers();
		if (users != null){
			for(String user: users){
				if (!user.equals(sender))
					ds.getNetworkMap().get(user).sendMessage(new StringMessage("USRMSG "+sender + " : " + msg));
			}
			return true;
		} 
		else return false;
	}

	/**
	 * Switch a user`s current room to a different room.
	 * 
	 * @param room the {@link GameRoom} to switch to
	 * @return true, if successful
	 */
	private boolean switchRooms(String room){
		GameRoom room1=ds.getRoomsMap().get(roomName);
		GameRoom room2=ds.getRoomsMap().get(room);
		if (room.compareTo(roomName)>=0){
			GameRoom temp = room1;
			room1=room2;
			room2=temp;
		}
			synchronized (room1) {
				synchronized (room2) {
					if(leaveRoom()){
						if (ds.getRoomsMap().get(room).addUser(nick)){
							roomName = room;
							return true;
						}
						else{
							ds.getRoomsMap().get(roomName).addUser(nick);
							return false;
						}
					}
					else return false;
				}
				
			}
	}
	
	/**
	 * Adds a user to a {@link GameRoom}.
	 * creates a new {@link GameRoom} if room doesn't exists
	 *
	 * @param room the room
	 * @return true, if successful
	 */
	private boolean joinRoom(String room) {
			if(!ds.getRoomsMap().containsKey(room)){// room doesnt exist
				ds.getRoomsMap().put(room, new GameRoom(room));
				ds.getRoomsMap().get(room).addUser(nick);
				roomName = room;
				return true;
			}
			else{//room exists
				if(ds.getRoomsMap().get(room).addUser(nick)){
					roomName = room;
					return true;
				}
				else{
					return false;
				}
			}
	}

	/**
	 * Adds a new user`s nick to the shared data structure.
	 *
	 * @param nick the user`s new nick
	 * @param callback an instance of ProtocolCallback unique to the connection from which msg originated .
	 * 
	 * @return true, if successful
	 */
	private boolean addNick(String nick, ProtocolCallback<StringMessage> callback) {
		if(!ds.getNetworkMap().containsKey(nick) && this.nick==null){
			this.nick = nick;
			ds.getNetworkMap().put(nick, callback);
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Starts a game in the {@link TBGProtocol} {@link GameRoom}.
	 *
	 * @param gameString the game name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void startGame(String gameString) throws IOException{
		if (roomName == null)
			ds.getNetworkMap().get(nick).sendMessage(new StringMessage("SYSMSG STARTGAME REJECTED"));
		else{
				synchronized (ds.getRoomsMap().get(roomName)) {
					if(ds.getGames().contains(gameString) && 
							!ds.getRoomsMap().get(roomName).hasGameStarted()){
						ds.getRoomsMap().get(roomName).setGameStarted(true);
						Game game = GameFactory.create(gameString, ds.getRoomsMap().get(roomName));
						ds.getRoomsMap().get(roomName).setGamePlayed(game);
						ds.getNetworkMap().get(nick).sendMessage(new StringMessage("SYSMSG STARTGAME ACCEPTED"));
						ds.getRoomsMap().get(roomName).StartGame();
					}
					else
						ds.getNetworkMap().get(nick).sendMessage(new StringMessage("SYSMSG STARTGAME REJECTED"));
			}
		}
	}
	
	/**
	 * Allows the user to leave the {@link GameRoom}.
	 *
	 * @return true, if successful
	 */
	private boolean leaveRoom(){
		if (roomName == null )
			return true;
		else {
			synchronized (ds.getRoomsMap().get(roomName)) {
			return ds.getRoomsMap().get(roomName).removeUser(nick);
			}
		}
	}

	/* (non-Javadoc)
	 * @see bgu.spl.protocol.AsyncServerProtocol#isEnd(java.lang.Object)
	 */
	@Override
	public boolean isEnd(StringMessage msg) {
		return msg.getMessage().equals("QUIT ");
	}
	
	/**
	 * Used to send the user a System Message.
	 *
	 * @param nick the nick to send the message to
	 * @param command the command the user`s has sent
	 * @param result the state of the command (ACCEPTED/REJECTED).
	 */
	public void sendMessage(String nick,String command,String result){
		try {
			ds.getNetworkMap().get(nick).sendMessage(new StringMessage("SYSMSG "+command+" "+result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Allows the user to quit the network.
	 */
	private void quit(){
		ds.getNetworkMap().remove(nick);
		if(roomName!=null)
			ds.getRoomsMap().get(roomName).removeUser(nick);
	}
	
	/**
	 * CHecks if the user can quit the network.
	 *
	 * @return true, if user is not in an active game.
	 */
	private boolean canQuit(){
		if (roomName == null)
			return true;
		else
			return !ds.getRoomsMap().get(roomName).hasGameStarted();
	}
	
	/**
	 * Used to determine response.
	 *
	 * @param bool the boolean indicator used for determining the response
	 * @return ACCEPTED/REJECTED according to the given parameter
	 */
	private String determineResponse(boolean bool){
		if (bool)
			return ACCEPTED;
		else
			return REJECTED;
	}

	/*
	 * Checks if the protocol can close. When a protocol is in a closing state,
	 *  it ’s handler  writes out all pending data , and close the connection .
	 */
	@Override
	public boolean shouldClose() {
		return connectionTerminated;
	}


	
	/* (non-Javadoc)
	 * @see bgu.spl.protocol.AsyncServerProtocol#connectionTerminated()
	 */
	@Override
	public void connectionTerminated() {
		connectionTerminated = true;
		quit();
		
	}

}
