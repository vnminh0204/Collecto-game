package collectoclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import collectogame.Ball;
import collectogame.Board;
import collectogame.ComputerPlayer;
import collectogame.Game;
import collectogame.HumanPlayer;
import collectogame.NaiveStrategy;
import collectogame.OnlinePlayer;
import collectogame.Player;
import collectogame.SmartStrategy;
import collectogame.Strategy;
import exceptions.ExitProgram;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import protocol.ProtocolMessages;

public class CollectoClient {
	
	//The name of this clientHandler (NOT THE USERNAME)
	private static final String CLIENT_NAME = "Yellow-1.3 Minh&Jesse";
	
	/**
	 * @invariant view != null
	 * @invariant handler = the handler connected to the server
	 * @invariant serverSock = the socket between a CollectoServer and the client
	 * @invariant loggedInAs = the username the user uses
	 * @invariant inQueue = false if the user is not in the queue || true if the user is in a queue
	 * @invariant inGame = false if the user is not playing || true if the user is playing a game
	 * @invariant ourTurn = 0 || 1
	 * @invariant game = the game the user is playing
	 */
	private CollectoClientView view;
	public CollectoServerHandler handler;
	private Socket serverSock;
	public boolean loggedIn = false;
	private String loggedInAs;
	private boolean inQueue = false;
	private boolean inGame = false;
	private int ourTurn;
	private Game game;
	private Player ourPlayer;
	/**
	 * Starts a new CollectoClient and creates a view for the user.
	 */
	public CollectoClient() {
		view = new CollectoClientTUI(this);
	}
	
	//This method is needed for the testing of the username
	/**
	 * Sets the name of the client (for testing).
	 * @param name - The username
	 */
	public void setLoggedInAs(String name) {
		this.loggedInAs = name;
	}
	
	//This method is needed for testing the methods that require userInput
	/**
	 * Returns the view that is connected to this client.
	 * @return The view
	 */
	public CollectoClientView getView() {
		return this.view;
	}
	
	//This method is needed for the testing of the connection
	/**
	 * Sets the socket and creates new CollectoSorverHandler (for testing).
	 * @param sock - The socket
	 */
	public void setServerSock(Socket sock) {
		try {
			this.handler = new CollectoServerHandler(sock, this);
			new Thread(handler).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if it is client's turn.
	 * @ensures (ourturn == 1) || (ourturn == 0);
	 * @return (ourTurn == currentTurn);
	 */
	public boolean isOurTurn() {
		return this.ourTurn == this.game.getCurrentTurn();
	}
	
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * Creates a new connection to a server or starts an offline game.
	 */
	public void start() {
		if (view.getBoolean("Do you want to play the game online? "
				+ "(Type y/n for online/offline option)")) {
			boolean connectToNewServer = true;
			while (connectToNewServer) {
				// Ask for IP and Port, attempt to connect, try again if
				// unsuccessful
				try {
					createConnection();
					doHello();
					// Show the available commands
					view.printHelpMenu();
					view.start();
					
					
				} catch (ExitProgram e1) {

				} catch (ServerUnavailableException e2) {
					view.showMessage("Cannot connect to server");
				}
				connectToNewServer = view.getBoolean("Do you want to "
						+ "connect to a new server? (type y/n)");
			}
			view.showMessage("See you later!");
			view.shutDown();
		} else {
			String namePlayer1;
			String namePlayer2;
			Player p1;
			Player p2;
			namePlayer1 = view.getString("What is first player's name?");
			namePlayer2 = view.getString("What is second player's name?");
			if	(view.getBoolean("Do you want bot to play the game ? " + namePlayer1
					+ "(Type y/n for Bot/Human option)")) {
				if	(view.getBoolean("Do you want SMART or NAIVE strategy to play the game ? "
						+ "(Type y/n for SMART/NAIVE option)")) {
					Strategy smart = new SmartStrategy();
				    p1 = new ComputerPlayer(smart, namePlayer1, 0, this);
				} else {
					Strategy naive = new NaiveStrategy();
				    p1 = new ComputerPlayer(naive, namePlayer1, 0, this);
				}
			} else {
				p1 = new HumanPlayer(namePlayer1, 0, this);
			}
			
			if	(view.getBoolean("Do you want bot to play the game ? " + namePlayer2
					+ "(Type y/n for Bot/Human option)")) {
				if	(view.getBoolean("Do you want SMART or NAIVE strategy to play the game ? "
						+ "(Type y/n for SMART/NAIVE option)")) {
					Strategy smart = new SmartStrategy();
				    p2 = new ComputerPlayer(smart, namePlayer2, 1, this);
				} else {
					Strategy naive = new NaiveStrategy();
				    p2 = new ComputerPlayer(naive, namePlayer2, 1, this);
				}
			} else {
				p2 = new HumanPlayer(namePlayer1, 1, this);
			}

		    Game g = new Game(p1, p2, this);
		    g.startOfflineGame();
		    view.showMessage("See you later!");
		}
		
	}
	
	/**
	 * Creates a new connection to a server by getting the IP and the port from the user.
	 * @ensures connection is created or exception is thrown
	 * @throws ExitProgram - if user wants to exit
	 */
	public void createConnection() throws ExitProgram {
		while (this.serverSock == null) {
			InetAddress addr = view.getIp();
			int port = view.getInt("What is the portNumber?");
			try {
				view.showMessage("Attempting to connect to " + addr + ":" 
					+ port + "...");
				this.serverSock = new Socket(addr, port);
				this.handler = new CollectoServerHandler(this.serverSock, this);
				new Thread(handler).start();
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on " 
					+ addr + " and port " + port + ".");
				throw new ExitProgram("User indicated to exit.");
			}
		}
	}
	
	/**
	 * For a showing a message in the view to the user.
	 * @param msg - The message
	 */
	public void showToUser(String msg) {
		view.showMessage(msg);
	}
	
	/**
	 * For asking a boolean from the user.
	 * @param question - The question
	 * @return boolean - The boolean answer
	 */
	public boolean getBoolean(String question) {
		return view.getBoolean(question);
	}
	
	/**
	 * For asking a int from the user.
	 * @param question - The question
	 * @return int - The int answer from the user
	 */
	public int getInt(String question) {
		return view.getInt(question);
	}
	
	/**
	 * For properly closing the connection with the server and resetting the variables.
	 * @param canCancel - If the user is able to cancel, it should be asked to
	 * @ensures variables are reset
	 * @ensures connection with server is closed
	 * @ensures ServerHandler equals null
	 * @return true if the connection is successfully closed
	 */
	public boolean closeConnection(boolean canCancel) {
		boolean closeConnection = true;
		if (canCancel) {
			closeConnection = view.getBoolean("Are you sure you want to "
					+ "close the connection with the server? (type y/n)");
		}
		if (closeConnection) {
			try {
				this.handler.clearConnection();
				//resetting all the variables to their starting value in case 
				//the user wants to connect to a different server
				this.handler = null;
				this.loggedIn = false;
				this.loggedInAs = null;
				this.serverSock = null;
				this.inQueue = false;
				this.game = null;
				this.inGame = false;
				this.ourPlayer = null;
				this.ourTurn = -1;
				if (!canCancel) {
					view.shutDown();
				}
				return true;
			} catch (IOException e) {
				view.showMessage("Could somehow not close the connection, "
						+ "please try again in a few secconds");
				return false;
			}
		} else {
			view.showMessage("Exiting is canceled");
			return false;
		}
	}
	
	/**
	 * Sends the Hello command to the server.
	 * @ensures Hello is send to the server with the name of this client
	 * @throws ServerUnavailableException is thrown when cannot find the server
	 */
	public void doHello() throws ServerUnavailableException {
		this.handler.sendToServer(ProtocolMessages.HELLO 
				+ ProtocolMessages.DELIMITER + CLIENT_NAME);
	}
	
	/**
	 * Handles the Hello command sent by the server correctly.
	 * @param serverReply - the message the server sent to the serverhandler
	 * @throws ProtocolException - if the protocol is not respected
	 * @ensures the protocol is checked
	 */
	public void handleHello(String serverReply) throws ProtocolException {
		String[] split = serverReply.split(ProtocolMessages.DELIMITER);
		if (split.length < 2) {
			throw new ProtocolException("The protocol was not respected. "
					+ "Try connecting to a new server.");
		} else {
			view.showMessage("Succcesfully connected to server: " + split[1]);
			view.showMessage("To start a game first login. "
					+ "Type \"login\" followed by a space and you name");
		}
	}
	
	/**
	 * Sends the Login command to the server with the user name.
	 * @param userName - The name of the user (can be "")
	 * @ensures if name.equals("") the name of the user is asked
	 * @throws ServerUnavailableException is thrown when cannot find the server
	 */
	public void doLogin(String userName) throws ServerUnavailableException {
		if (this.loggedIn) {
			view.showMessage("You are already logged in");
		} else {
			if (userName.equals("")) {
				this.loggedInAs = view.getString("What is your user name?");
			} else {
				this.loggedInAs = userName;
			}
			
			handler.sendToServer(ProtocolMessages.LOGIN 
					+ ProtocolMessages.DELIMITER + this.loggedInAs);
			
		}
	}
	
	/**
	 * Handles the Login command and the ALREADYLOGGGEDIN command sent by the server.
	 * @param serverReply - The message the server sent to the server handler
	 * @ensures if login successfully boolean loggedIn = true
	 * @ensures if login unsuccessfully (name is already in use) 
	 * the user is asked to try again with a different one
	 */
	public void handleLogin(String serverReply) {
		if (serverReply.equals(ProtocolMessages.LOGIN)) {
			view.showMessage("You are succesfully logged in as: " + this.loggedInAs);
			this.loggedIn = true;
		} else {
			view.showMessage("The name \"" + this.loggedInAs 
					+ "\" is already in use, please try a different one");
		}
	}
	
	/**
	 * Sends the server the List command.
	 * @requires the user is logged in at a server
	 * @throws ServerUnavailableException is thrown when cannot find the server
	 */
	public void doList() throws ServerUnavailableException {
		if (this.loggedIn) {
			handler.sendToServer(ProtocolMessages.LIST);
		} else {
			view.showMessage("You need to be logged in to perform this action");
		}
	}
	
	/**
	 * Handles the List with user names send by the server.
	 * @param serverReply - the text the server sent to the client handler
	 * @ensures every name is printed out for the user
	 */
	public void handleList(String serverReply) {
		String[] names = serverReply.split(ProtocolMessages.DELIMITER);
		String ans = "The names of the clients currently online are:";
		for (int i = 1; i < names.length; i++) {
			ans = ans + "\n" + i + ". " + names[i];
		}
		view.showMessage(ans);
	}
	
	/**
	 * Sends the queue command to the server.
	 * @requires the user is logged in at a server
	 * @ensures if the user is already in the queue, it is asked if the user wants to leave
	 * @throws ServerUnavailableException is thrown when cannot find the server
	 */
	public void doQueue() throws ServerUnavailableException {
		if ((this.loggedIn) && !this.inGame) {
			boolean leave = false;
			if (this.inQueue) {
				leave = view.getBoolean("You are already in the queue. Do you want to leave it?");
			}
			if ((!this.inQueue && !leave) || (this.inQueue && leave)) {
				handler.sendToServer(ProtocolMessages.QUEUE);
				if (inQueue) {
					inQueue = false;
					view.showMessage("You succesfully left the queue");
				} else {
					this.inQueue = true;
					view.showMessage("You succesfully enterd the queue. "
							+ "To leave it, type \"queue\" again.");
				}
			}
		} else if (!this.loggedIn) {
			view.showMessage("You need LOGIN to perform this action");
		} else if (this.inGame) {
			view.showMessage("You cannot join QUEUE because you are in a game");
		}
	}
	
	/**
	 * A new game is started with the given board.
	 * @param serverReply - the command + board created by the server
	 * @ensures a board is created with the given numbers
	 */
	public void handleNewGame(String serverReply) {
		String[] splitted = serverReply.split(ProtocolMessages.DELIMITER);
		
		Board board = new Board();
		Player[] players = new OnlinePlayer[2];
		
		this.inQueue = false;
		this.inGame = true;
		this.ourPlayer = null;
		for (int i = 1; i <= 49; i++) {
			board.setField(i - 1, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
		if (splitted[splitted.length - 2].equals(this.loggedInAs)) {
			//We may do the first move
			players[0] = new OnlinePlayer(this.loggedInAs, 0, this);
			ourTurn = 0;
			players[1] = new OnlinePlayer(splitted[splitted.length - 1], 1, this);
		}
		if (splitted[splitted.length - 1].equals(this.loggedInAs)) {
			//The opponent may do the first move
			players[1] = new OnlinePlayer(this.loggedInAs, 1, this);
			ourTurn = 1;
			players[0] = new OnlinePlayer(splitted[splitted.length - 2], 0, this);
		}
		game = new Game(players[0], players[1], this);
		game.updateOnlineBoard(board);
		game.update();
		if (this.isOurTurn()) {
			view.showMessage("ALRIGHT This is our TURN");
		} else {
			view.showMessage("Waiting for other player's move ....");
		}
	}
	
	/**
	 * The move chosen is send to the server.
	 * @throws ServerUnavailableException is thrown when cannot find the server
	 * @ensures the move(s) is/are sent to the server.
	 */
	public void doMove() throws ServerUnavailableException {
		//check if it is NOT your turn you cannot make a move
		if (this.isOurTurn() && this.inGame) {
			if (ourPlayer == null) {
				if	(view.getBoolean("Do you want bot to play the game?"
						//For the first move the user is asked if 
						//it wants to play this game with a bot
						+ "(Type y/n for Bot/Human option)")) {
					if	(view.getBoolean("Do you want SMART or NAIVE strategy to play the game ? "
							+ "(Type y/n for SMART/NAIVE option)")) {
						Strategy smart = new SmartStrategy();
					    ourPlayer = new ComputerPlayer(smart, this.loggedInAs, this.ourTurn, this);
					} else {
						Strategy naive = new NaiveStrategy();
					    ourPlayer = new ComputerPlayer(naive, this.loggedInAs, this.ourTurn, this);
					}
				} else {
					ourPlayer = new HumanPlayer(this.loggedInAs, this.ourTurn, this);
				}
			}
			
			ourPlayer.updateOnlineCollection(game.getPlayer(this.ourTurn));
			ourPlayer.calulateScore();
			
			int[] choice = ourPlayer.determineMove(this.game.getBoard().deepCopy());
			int intMove1 = choice[0];
			int intMove2 = -1;
			String ans = null;
			if (choice.length == 2) {
				intMove2 = choice[1];
			}
			
			try {
				//Send to chosen move to the server
				ans = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + intMove1;
				if (intMove2 != -1) {
					ans = ans + ProtocolMessages.DELIMITER + intMove2;
				}
				handler.sendToServer(ans);

			} catch (NumberFormatException e) {
				view.showMessage("This is not a number!! Please try again");
			}
		} else if (!this.isOurTurn()) {
			view.showMessage("This is NOT our TURN, you cannot make a move");
		} else if (!this.inGame) {
			view.showMessage("You are not in a game, you cannot make a move");
		}
	}
	
	/**
	 * Handle the move send by the server.
	 * @param serverReply - the command + move sent to the server handler
	 * @throws ServerUnavailableException - when the server disconnected
	 * @ensures the game is updated with this move
	 */
	public void handleMove(String serverReply) throws ServerUnavailableException {
		String[] splitted = serverReply.split(ProtocolMessages.DELIMITER);
		int[] choice;
		if (splitted.length == 2) {
			choice = new int[1];
			choice[0] = Integer.valueOf(splitted[1]);
			((OnlinePlayer) this.game.getPlayer(game.getCurrentTurn())).updateOnlinveMove(choice);
		}
		if (splitted.length == 3) {
			choice = new int[2];
			choice[0] = Integer.valueOf(splitted[1]);
			choice[1] = Integer.valueOf(splitted[2]);
			((OnlinePlayer) this.game.getPlayer(game.getCurrentTurn())).updateOnlinveMove(choice);
		}
		game.makeOnlineMove(game.getCurrentTurn());
		game.update();
		game.updateCurrentTurn();
		
		if (this.isOurTurn()) {
			//If it is a bot the next move will be made immediately
			if ((this.ourPlayer instanceof ComputerPlayer) 
					&& (!this.game.getBoard().deepCopy().isEndGame())) {
				doMove();
			} else {
				view.showMessage("ALRIGHT This is our TURN");
			}
		} else {
			view.showMessage("Waiting for other player's move ....");
		}
	}
	
	/**
	 * Handle the GAMEOVER send by the server.
	 * @param serverReply - the command + reason send to the server handler
	 * @requires serverReply != null;
	 */
	public void handleGameOver(String serverReply) {
		String[] names = serverReply.split(ProtocolMessages.DELIMITER);
		this.inGame = false;
		ourPlayer = null;
		switch (names[1]) {
			case ProtocolMessages.DISCONNECT:
				view.showMessage("The other player has disconected, so the game is won by you");
				break;
			case ProtocolMessages.DRAW:
				view.showMessage("The game ended in a draw");
				break;
			case ProtocolMessages.VICTORY:
				String name = names[2];
				if (name.equals(this.loggedInAs)) {
					name = "you";
				}
				view.showMessage("The game ended, the winner is " + name);
		}		
	}
	
	/**
	 * This method starts a new CollectoClient.
	 * @param args - input argument
	 */
	public static void main(String[] args) {
		(new CollectoClient()).start();
	}
}
