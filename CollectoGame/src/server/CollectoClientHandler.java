package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import exceptions.ClientUnavailableException;
import protocol.ProtocolMessages;

public class CollectoClientHandler implements Runnable {

	/**
	 * @invariant in != null | The input stream for this socket
	 * @invariant out != null | The output stream for this socket
	 * @invariant sock != null | The socket belonging to the server and the client
	 * @invariant server != null | The server this client handler belongs to
	 * @invariant userName | The name the user uses for this server
	 * @invariant playerRole >= -1 && playerRole <= 1 | Saves which client is player one or two
	 * @invariant game | The game the user is playing if not playing a game, game == null
	 */
	private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private CollectoServer server;
    private String userName = null;
    private int playerRole = -1;
    private CollectoServerGame game;
    
	/**
     * Starts a new server handler.
     * @param sock - the socket created between the server and the client
     * @param server - the collectoClient belonging to this server handler
     * @ensures input stream and output stream created
     * @throws IOException - This is thrown when the in, out, or sock are closed
     */
    public CollectoClientHandler(Socket sock, CollectoServer server) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        this.sock = sock;
        this.server = server;
        this.game = null;
        this.playerRole = -1;
    }
    
	/**
	 * Sets the userName belonging to this client.
	 * @param userName - The userName of the client
	 * @requires userName != null;
	 */
    public void setUserName(String userName) {
    	this.userName = userName;
    }
    
	/**
	 * Gets the userName belonging to this client.
	 * @return this.username
	 */
    public String getUserName() {
    	return this.userName;
    }
    
	/**.
	 * Gets the playerRole of this client
	 * @return this.playerRole
	 */
    public int getPlayerRole() {
    	return this.playerRole;
    }
	
 	/**
	 * Sets the PlayerRole of this client.
	 * @param role - The assigned playerRole of the client
	 * @requires role is a value from -1 to 1
	 */
    public void setNewPlayerRole(int role) {
    	this.playerRole = role;
	}
    
	/**
	 * Sets the variable game to the game this client is playing.
	 * @param gameInput;
	 * @requires gameInput != null;
	 */
    public void setNewGame(CollectoServerGame gameInput) {
    	this.game = gameInput;
    }
	
	/**
	 * Gets the game this client is playing.
	 * @return the game the client is playing
	 */
    public CollectoServerGame getGame() {
    	return this.game;
    }
    
	/**
     * Constantly checks for serverInput.
     * @ensures Input is handled correctly
     */
    public void run() {
        String msg;
		try {
			msg = in.readLine();
			while (msg != null) {
				System.out.println("> [" + userName + "] Incoming: " + msg);
				handleCommand(msg);
				msg = in.readLine();
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		} catch (ClientUnavailableException e) {
			shutdown();
		}
    }
    
    /**
     * Sends a command to the client.
     * @param msg - the command you want to send to the client
     * @requires msg to be a message according the protocol
     * @ensures the command is sent to the client
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    public void sendToClient(String msg) throws ClientUnavailableException {
    	if (out != null) {			
			try {
				out.write(msg);
				out.newLine();
				out.flush();
			} catch (IOException e) {
				throw new ClientUnavailableException("The client is Unavailable");
			}
		}
    }
    
    /**
     * Handle client commands.
     * @param input - the command send by the client
     * @ensures that the command is send to the right method in the server
     * @throws ClientUnavailableException - if connection between server and client failed
     */
    private void handleCommand(String input) throws ClientUnavailableException {
    	String[] splitted = input.split(ProtocolMessages.DELIMITER, 2);
		switch (splitted[0]) {
			case ProtocolMessages.HELLO:
				server.handleHello(this);
				break;
			case ProtocolMessages.LOGIN:
				server.handleLogin(splitted[1], this);
				break;
			case ProtocolMessages.QUEUE:
				server.handleQueue(this);
				break;
			case ProtocolMessages.LIST:
				server.handleList(this);
				break;
			case ProtocolMessages.MOVE:
				server.handleMove(input, this);
				break;
			default:
				server.doError("unknow command: " + input, this);
		}
    }
    
    /**
     * Handle the disconnection between the client and this client handler.
     * @ensures socked is closed
     * @ensures BufferdWriter and reader are closed
     * @ensures doGameOver if the disconnected client in a game and the other is still online
     */
	protected void shutdown() {
		System.out.println("> [" + userName + "] Shutting down.");
		try {
			in.close();
			out.close();
			sock.close();			
			if (this.game != null) {
				if (this.game.getClientHandler1().equals(this)) {
					server.doGameOver(this.game.getClientHandler2(), this, 
							ProtocolMessages.DISCONNECT, 
									this.game.getClientHandler2().getUserName());
				} else if (this.game.getClientHandler2().equals(this)) {
					server.doGameOver(this.game.getClientHandler1(), this, 
							ProtocolMessages.DISCONNECT, 
							this.game.getClientHandler1().getUserName());
				}
			}			
		} catch (IOException e1) {
			// not necessary to print anything
		} catch (ClientUnavailableException e2) {
			// not necessary to print anything
		}					
		server.removeClient(this);
	}
}
