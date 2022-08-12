package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import collectoclient.CollectoClient;
import collectogame.Board;
import collectogame.OnlinePlayer;
import collectogame.Player;
import exceptions.ClientUnavailableException;
import exceptions.ExitProgram;
import protocol.ProtocolMessages;

public class CollectoServer implements Runnable {
	
	/**
	 * @invariant userNames - Contains the userNames of the clients that logged in
	 * @invariant queue - Contains the client handlers of the clients in the queue for a game
	 * @invariant port - The port number the server is working with
	 */
	private List<String> userNames = new ArrayList<String>();
	private LinkedList<CollectoClientHandler> queue = new LinkedList<CollectoClientHandler>();
	private CollectoServerView view;
	private ServerSocket ssock;

	// The name of this server
	private static final String SERVER_NAME = "Yellow-1.3 Minh&Jesse Server";
    
	/**
	 * Creates a new object CollectoServer with the given port number.
	 */
    public CollectoServer() {
    	this.view = new CollectoServerTUI();
    }
    
    /**
     * Starts the server and creates new client handlers for new clients.
     * @ensures Server is started with given port number as a local host
     * @ensures new client hander is started on a new thread for each new client
     */
    public void run() {
    	try {
			setup();
		} catch (ExitProgram e2) {
			view.showMessage("The program is closed");
			return;
		}
	    while (true) {
	    	Socket sock;
			try {
				sock = ssock.accept();
				CollectoClientHandler handler = new CollectoClientHandler(sock, this);
				new Thread(handler).start(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
	/**
	 * Sets up a Internet connection on local host with a port number thats being asked.
	 * @ensures A connection is created or the client wants to shutdown
	 * @throws ExitProgram - when the server wants to exit
	 */
    public void setup() throws ExitProgram {
    	ssock = null;
		while (ssock == null) {
			int port = view.getInt("Please enter the server port.");

			// try to open a new ServerSocket
			try {
				view.showMessage("Attempting to open a socket at port " + port + "...");
				ssock = new ServerSocket(port);
				view.showMessage("Server started at port " + port);
			} catch (IOException e) {
				view.showMessage("ERROR: could not create a socket on port " + port + ".");

				if (!view.getBoolean("Do you want to try again?")) {
					throw new ExitProgram("User indicated to exit the "
							+ "program.");
				}
			}
		}
    }
    
    /**
     * If client disconnects it should be removed from the lists.
     * @param handler - the ClientHandler that should be removed
     * @requires handler != null;
     * @ensures That the client is removed everywhere
     */
    public void removeClient(CollectoClientHandler handler) {
    	synchronized (this.userNames) {
    		if (this.userNames.contains(handler.getUserName())) {
        		userNames.remove(handler.getUserName());
        	}
    	}
    	synchronized (this.queue) {
        	if (this.queue.contains(handler)) {
        		this.queue.remove(handler);
        	}
    	}	
    }
    
    /**
     * Handles the hello send by a client by replying: HELLO~"serverName".
     * @param handler - The client handler that sent the hello to the server
     * @ensures the hello send back is to the same client that send hello in the first place
     * @ensures message is sent to client according the protocol
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void handleHello(CollectoClientHandler handler) throws ClientUnavailableException {
    	handler.sendToClient(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + SERVER_NAME);
    }
    
    /**
     * Handles the login send by a client by adding it to all the online users and to the client.
     * @param clientInput - the message after the login command (thus the loginName)
     * @param handler - The client handler that sent the login request to the server
     * @ensures if user name already exists, ALREADYLOGGEDIN is sent to client
     * @ensures if user name is unique, LOGIN is sent to client and new user name is added
     * 		and handler.userName = user name
     * @ensures message is sent to client according the protocol
     * @throws ClientUnavailableException - if connection between server and client failed
     */

    public void handleLogin(String clientInput, CollectoClientHandler handler) 
    		throws ClientUnavailableException {
    	if (handler.getUserName() != null) {
    		doError("You are already logged in as: " + handler.getUserName(), handler);
    		return;
    	}
    	synchronized (this.userNames) {
    		if (userNames.contains(clientInput)) {
        		handler.sendToClient(ProtocolMessages.ALREADYLOGGEDIN);
        	} else {
        		handler.sendToClient(ProtocolMessages.LOGIN);
        		userNames.add(clientInput);
        		handler.setUserName(clientInput);
        	}
    	}	
    }
    
    /**
     * Handles the queue send by the client by adding 
     * or removing it from the queue, also checks if game can be played.
     * @param handler - The client handler that sent the login request to the server
     * @ensures if client is already in queue, it is removed from it.
     * @ensures if client is not in queue, it is added to it, 
     * also is checked if there are now 2 players in queue so a game can be played
     * @ensures the role of player is picked randomly.
	 * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void handleQueue(CollectoClientHandler handler) throws ClientUnavailableException {
    	if (handler.getUserName() == null) {
			doError("You should first login, before you enter a queue", handler);
			return;
		}
		if (handler.getGame() != null) {
			doError("You can not use this command while in a game", handler);
			return;
		}
    	synchronized (this.queue) {
        	if (queue.contains(handler)) {
        		queue.remove(handler);
        	} else {
        		queue.add(handler);
        		if (queue.size() >= 2) {
       			
        			CollectoClientHandler clientHandler1 = queue.removeFirst();
        			CollectoClientHandler clientHandler2 = queue.removeFirst();
        			    			
        			int random = (int) (Math.random() * 2);
    				if (random == 0) {
    					doNewGame(clientHandler1, clientHandler2);
    				} else {
    					doNewGame(clientHandler2, clientHandler1);
    				}		
        		}
        	}
    	}
    }
    
    /**
     * Starts a new Collecto game with the clients given in the parameters.
     * @param hdler1 - The first client waiting in the queue
     * @param hdler2 - The second client waiting in the queue
     * @ensures the created board is sent to both clients and both names are added
     * @ensures message is sent to client according the protocol
	 * @throws ClientUnavailableException - if connection between server and client failed 
     */
    public void doNewGame(CollectoClientHandler hdler1, CollectoClientHandler hdler2)
    		throws ClientUnavailableException {
		
		CollectoClient cc = new CollectoClient();
    	
		
		OnlinePlayer p1 = new OnlinePlayer(hdler1.getUserName(), 0, cc);
		OnlinePlayer p2 = new OnlinePlayer(hdler2.getUserName(), 1, cc);
		
		CollectoServerGame game = new CollectoServerGame(p1, p2, cc, hdler1, hdler2);
		
		hdler1.setNewGame(game);
		hdler2.setNewGame(game);
		hdler1.setNewPlayerRole(0);
		hdler2.setNewPlayerRole(1);
		
		view.showMessage("[" + hdler1.getUserName() + " + " 
				+ hdler2.getUserName() + "] Started a new Game");
		
		game.createNewBoard();
		Board board = game.getBoard().deepCopy();
		
		String reply = ProtocolMessages.NEWGAME;
		
		for (int index = 0; index < 49; index++) {
			reply = reply + ProtocolMessages.DELIMITER + board.getField(index).convertTo();
		}
		
		reply = reply + ProtocolMessages.DELIMITER + p1.getName() 
							+ ProtocolMessages.DELIMITER + p2.getName();
		
		hdler1.sendToClient(reply);
		hdler2.sendToClient(reply);
    }
    
    /**
     * Handles the move sent by the client.
     * @param clientInput - the move(s) the client wants to make separated with the delimiter
     * @param handler - The client that made the move request
     * @requires clientInput != null;
     * @requires handler != null;
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void handleMove(String clientInput, CollectoClientHandler handler)
    		throws ClientUnavailableException {

    	CollectoServerGame game = handler.getGame();
    	
    	if (game == null) {
    		doError("You are currently not playing a game", handler);
    		return;
    	}
    	if (game.getCurrentTurn() != handler.getPlayerRole()) {
    		doError("This is not your turn you", handler);
			return;
    	}
    	
    	String[] splitted = clientInput.split(ProtocolMessages.DELIMITER);
    	int[] choice;
		
		Board copyBoard = game.getBoard().deepCopy();
    	boolean isStillPossibleSingleMove = copyBoard.isStillPossibleSingleMove();
    	
    	if (isStillPossibleSingleMove && (splitted.length == 3)) {
    		doError("There is still possible single move", handler);
			return;
    	}
    	
    	if (!isStillPossibleSingleMove && (splitted.length == 2)) {
    		doError("You need to make a double move", handler);
			return;
    	}
    	
    	if (isStillPossibleSingleMove && (splitted.length == 2)) {
    		choice = new int[1];
    		choice[0] = Integer.valueOf(splitted[1]);
    		if (!copyBoard.isValidSingleMove(choice[0])) {
    			doError("Move " + choice[0] + " is not a valid move", handler);
    			return;
    		} else {
    			// Handle correct move
    			handleCorrectMove(choice, game);    			
    		}
    	} else if (!isStillPossibleSingleMove && (splitted.length == 3)) {
    		choice = new int[2];
    		choice[0] = Integer.valueOf(splitted[1]);
    		choice[1] = Integer.valueOf(splitted[2]);
    		if (!copyBoard.isValidDoubleMove(choice[0], choice[1])) {
    			doError("Move " + choice[0] + " and " + choice[1] + " are not valid", handler);
    			return;
    		} else {
    			handleCorrectMove(choice, game);    			
    		}    		
    	}		
    }
    
    /**
     * Handles the correct choice of client and apply to the game.
     * @param choice - the move(s) the client
     * @param game - The client's current game
     * @requires choice != null;
     * @requires game != null;
     * @ensures doGameOver if (game.getBoard.isEndGame() == true);
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void handleCorrectMove(int[] choice, CollectoServerGame game)
    			throws ClientUnavailableException {
    	OnlinePlayer player = (OnlinePlayer) game.getPlayer(game.getCurrentTurn());
    	player.updateOnlinveMove(choice);
    	game.makeOnlineMove(game.getCurrentTurn());
    	game.updateCurrentTurn();
    	Board copyBoard = game.getBoard().deepCopy();
    	
    	if (choice.length == 1) {
			doMove(choice[0], -1, game.getClientHandler1(), game.getClientHandler2());
		} else {
			doMove(choice[0], choice[1], game.getClientHandler1(), game.getClientHandler2());
		}
    	
    	if (copyBoard.isEndGame()) {
    		Player winner = game.winner();
    		String reason = "";
    		if (winner != null) {
    			reason = reason + ProtocolMessages.VICTORY;
    			doGameOver(game.getClientHandler1(), game.getClientHandler2(), 
    					reason, winner.getName());
    		} else {
    			reason = reason + ProtocolMessages.DRAW;
    			doGameOver(game.getClientHandler1(), game.getClientHandler2(), reason, null);
    		}
    	}   		
    }
    
    /**
     * When a game is ended, this method should be called to notify the clients.
     * @param handler1 - The handler you want to send the message to
     * @param handler2 - The handler you want to send the message to
     * @param reason - The reason why the game is over
     * @param name - The name of the player that won the game (empty in case of draw)
     * @requires handler1 is the one who is still connecting
     * @requires reason.equals(ProtocolMessages.DRAW) || 
     * reason.equals(ProtocolMessages.DISCONECTED) || reason.equals(ProtocolMessages.VICTORY)
     * @ensures message is sent to client according the protocol
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void doGameOver(CollectoClientHandler handler1, CollectoClientHandler handler2, 
    							String reason, String name) throws ClientUnavailableException {
    	String reply = ProtocolMessages.GAMEOVER;
    	if (reason.equals(ProtocolMessages.DRAW)) {
    		reply = reply + ProtocolMessages.DELIMITER + reason;
    	} else if (reason.equals(ProtocolMessages.VICTORY)) {
    		reply = reply + ProtocolMessages.DELIMITER + reason + ProtocolMessages.DELIMITER + name;
    	}

		if (reason.equals(ProtocolMessages.DISCONNECT)) {
			reply = reply + ProtocolMessages.DELIMITER + reason + ProtocolMessages.DELIMITER + name;
			
			//handler1 is the one who still online
			handler1.sendToClient(reply);
		    handler1.setNewGame(null);
		    handler1.setNewPlayerRole(-1);
		}

		if (!reason.equals(ProtocolMessages.DISCONNECT)) {
			handler1.sendToClient(reply);
	    	handler1.setNewGame(null);
	    	handler1.setNewPlayerRole(-1);
			handler2.sendToClient(reply);
    		handler2.setNewGame(null);
    		handler2.setNewPlayerRole(-1);
		}
		view.showMessage("[" + handler1.getUserName() + " + " 
				+ handler2.getUserName() + "] Game has ended");
    }
    
    /**
     * Sends the updated move(s) in the game to both clients in the game.
     * @param move1 - The first move made
     * @param move2 - The second move made (-1 if it was a single move)
     * @param handler1 - client 1 of the game
     * @param handler2 - client 2 of the game
     * @requires move1 to be a valid move and move2 to be -1 or a valid move
     * @ensures message is sent to client according the protocol
     * @ensures the move made is sent to both clients
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void doMove(int move1, int move2, 
    		CollectoClientHandler handler1, CollectoClientHandler handler2) 
    				throws ClientUnavailableException {
    	String reply = ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + move1;
    	if (move2 != -1) {
    		reply = reply + ProtocolMessages.DELIMITER + move2;
    	}
    	handler1.sendToClient(reply);
    	handler2.sendToClient(reply);
    }
    
    /**
     * Handles the list request from the client.
     * @param handler - The ClientHandler that did the request
     * @ensures all clients that are logged in are included in the list
     * @ensures message is sent to client according the protocol
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void handleList(CollectoClientHandler handler) throws ClientUnavailableException {  	
    	String reply = ProtocolMessages.LIST;
    	for (String name : this.userNames) {
    		reply = reply + ProtocolMessages.DELIMITER + name;
    	}
    	handler.sendToClient(reply);
    }
    
    /**
     * Sends a error message to the client.
     * @param errorMessage - the message that you want the client to know
     * @param handler - The clientHandler you want to send the message to
     * @requires errorMessage != null;
     * @requires handle != null;
     * @ensures message is sent to client according the protocol
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void doError(String errorMessage, CollectoClientHandler handler)
    		throws ClientUnavailableException {
    	handler.sendToClient(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + errorMessage);
    }
    
    /**
     * Starts a new server with the port number given in the arguments.
     * @param args - the Arguments added when running
     */
    public static void main(String[] args) {
        CollectoServer s = new CollectoServer();
        new Thread(s).start();
    }
}
