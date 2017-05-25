package bluffer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mics.Game;
import mics.GameRoom;
import mics.SharedDS;
import tokenizer.StringMessage;

/**
 * The Class BlufferGame.
 */
public class BlufferGame implements Game{

	private Question[] questions;
	private String[] choices;
	private int choiceCounter = 0;
	private ConcurrentHashMap<String, Player> users;
	private final String PATH="bluffer.json";
	private int currentRound = 0;
	private boolean gameOver = false;
	private GameRoom room;
	SharedDS ds = SharedDS.getInstance();
		
	/**
	 * Instantiates a new bluffer game.
	 *
	 * @param room the room the {@link BlufferGame} is held at
	 */
	public BlufferGame(GameRoom room){
		this.users = new ConcurrentHashMap<String, Player>();
		this.room = room;
		questions = new Question[3];
		Gson gson = new GsonBuilder().create();
		try {
			BufferedReader br = new BufferedReader(new FileReader(PATH));
			QuestionBank bank = gson.fromJson(br, QuestionBank.class);
			Question[] qBank = bank.getQuestions();
			Random r = new Random();
			int questionsPicked = 0;
			while (questionsPicked<3){
				int rand = r.nextInt(qBank.length);
				if (qBank[rand] != null){
					questions[questionsPicked] = new Question(qBank[rand]);
					System.out.println(qBank[rand].getQuestionText());
					qBank[rand] = null;
					questionsPicked++;
				}
			}
			for (String user : room.getUsers()){
				this.users.put(user,new Player(user,0,-1,"",false));
			}
		} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	}
	
		
	/**
	 * Returns a single question.
	 *
	 * @param i the question index
	 * @return the question at the given index
	 */
	public Question getQuestion(int i){
			return questions[i];
	}


	/**
	 * Starts a new round for all the players in the room.
	 */
	@Override
	public void playRound() {
		for (Player player : users.values()){
			player.resetForRound();
		}
		sendMessageToUsers("ASKTXT "+ questions[currentRound].getQuestionText());
	}

	/**
	 * Send message to users.
	 *
	 * @param msg the message to be sent.
	 */
	private void sendMessageToUsers(String msg){
		for(String user: users.keySet()){

			try {
				ds.getNetworkMap().get(user).sendMessage(new StringMessage(msg));
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * A getter method for gameOver.
	 *
	 * @return gameOver field status.
	 */
	@Override
	public boolean gameOver() {
		return gameOver;
	}


	/**
	 * A respond method for the {@link BlufferGame}.
	 * Handles receiving responds from the players to the ASKTXT command.
	 * Once all players submitted their answers, will send all players the Answers for the current round.
	 *
	 * @param text the text
	 * @param nick the nick
	 */
	@Override
	public void respond(String text, String nick) {
		String result;
		System.out.println("got new respond text: "+text);
		text = text.toLowerCase();
		if (!users.get(nick).hasResponded() && 
					!text.contentEquals(questions[currentRound].getRealAnswer())){
			questions[currentRound].addUserAnswer(text);
			users.get(nick).setResponded(true);
			users.get(nick).setBluff(text);
			result = "ACCEPTED";
		}
		else
			result = "REJECTED";
		
		sendMessageToUser(nick, "SYSMSG TXTRESP "+result);
		if (questions[currentRound].getUserAnswers().size() == users.size()){
			sendAnswers();
		}
	}
	
	/**
	 * Send message to user.
	 *
	 * @param nick the users to send the message to.
	 * @param msg the message to be sent.
	 */
	private void sendMessageToUser(String nick, String msg){
		try {
			ds.getNetworkMap().get(nick).sendMessage(new StringMessage(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a randomized list of answers to all players.
	 */
	private void sendAnswers() {
		choiceCounter = 0;
		choices = questions[currentRound].getChoices();
		String str ="";
		int i=0;
		for (String choice : choices){
			str+=i+"."+choice+" ";
			i++;
		}

		sendMessageToUsers("ASKCHOICHES "+str);
	}


	/**
	 * Used to handle the SELECTRESP command from all players.
	 * Once a SELECTRESP has been arrived to the server, will submit the player choice (if submitted in the allowed boundaries).
	 * Once all players submitted their answers, this method will end the round.
	 *
	 * @param choice the choice the player has submitted
	 * @param nick the player that sent the choice
	 */
	@Override
	public void select(String choice, String nick) {
		String result = "REJECTED";
		try{
		int choiceInt = Integer.parseInt(choice);
		System.out.println("got new select text: "+choice);
		if(choiceInt < choices.length && choiceInt >=0 && users.get(nick).getChoice()==-1){
			users.get(nick).setChoice(choiceInt);
			choiceCounter++;
			result = "ACCEPTED";
		}
		}catch (NumberFormatException e){
			
		}

		sendMessageToUser(nick, "SYSMSG SELECTRESP "+result);
		if (choiceCounter == users.size()){
			endRound();
		}
	}


	/**
	 * Ends the round. 
	 * Updating players scores following the {@link BlufferGame} rules, and printing for each player 
	 * the round summary. Once three rounds has passed will end the current game.
	 */
	private void endRound() {
		for (Player player : users.values()){
			if (choices[player.getChoice()].equals(questions[currentRound].getRealAnswer())){
				player.incRoundScoreBy(10);
				player.incScoreBy(10);
				player.setAnsweredCorrectly(true);
			}
			else{
				player.setAnsweredCorrectly(false);
				for (Player bluffPlayer : users.values()){
					if(choices[player.getChoice()].equals(bluffPlayer.getBluff())){
						bluffPlayer.incRoundScoreBy(5);
						bluffPlayer.incScoreBy(5);
						break;
					}
				}
			}
		}
		sendMessageToUsers("GAMEMSG The correct answer is:"+questions[currentRound].getRealAnswer());
		for (Player player : users.values()){
			String result;
			if (player.didAnswerCorrectly())
				result = "correct!";
			else
				result = "wrong!";
			sendMessageToUser(player.getNick(), "GAMEMSG "+result+" +"+player.getRoundScore()+"pts");
		}
		currentRound++;
		endGame();
	}
	
	/**
	 * Once three rounds has passed, sends a game points summary for all players in the game.
	 * In addition will reset all {@link BlufferGame} fields and objects.
	 */
	private void endGame(){
		if (currentRound == 3){
			String summary = "GAMEMSG Summary: ";
			for (Player player : users.values()){
				summary += player.getNick()+": "+player.getScore()+"pts, ";
			}
			summary = summary.substring(0, summary.length()-2);
			sendMessageToUsers(summary);
			room.endGame();
		}
		else
			playRound();
	}
}
