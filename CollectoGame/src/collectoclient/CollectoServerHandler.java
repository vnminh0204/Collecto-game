package collectoclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import protocol.ProtocolMessages;

public class CollectoServerHandler implements Runnable {
	
	/**
	 * @invariant in = the BufferedReader that receives text from the server
	 * @invariant out = the BufferdWriter that sends text to the server
	 * @invariant sock = the socket between the server and the client
	 * @invariant client = the client this CollectoServerHandler belongs to
	 * @invariant keepRunning = if client wants to shutdown: false || true
	 */
	private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;
    private CollectoClient client;
    private boolean keepRunning = true;
    
    /**
     * Starts a new server handler.
     * @param sock - the socket created between the server and the client
     * @param client - the collectoClient belonging to this server handler
     * @ensures input stream and output stream created
     * @throws IOException throws when input or output error
     */
    public CollectoServerHandler(Socket sock, CollectoClient client) throws IOException {
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        this.sock = sock;
        this.client = client;
    }

    /**
     * Constantly checks for serverInput.
     * @ensures Input is handled correctly
     */
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null && keepRunning) {
            	handleCommand(msg);
                msg = in.readLine();                
            }
            if (keepRunning) {
            	clearConnection();
            }
        } catch (IOException e) {
        	//Could have been handles differently but for now not notify user
        } catch (ProtocolException e1) {
        	client.showToUser("Closed connection because the server violated Protocol");
        	client.closeConnection(false);
        	
        } catch (ServerUnavailableException e2) {
        	client.showToUser("Closed connection because the server is not available");
        	client.closeConnection(false);
        }

    }
    
    /**
     * Sends a command to the server.
     * @param msg - the command you want to send to the server
     * @ensures the command is sent to the server
     * @throws ServerUnavailableException when the server is unavailable
     */
    public void sendToServer(String msg) throws ServerUnavailableException {
    	if (out != null) {
			try {
				out.write(msg);
				out.newLine();
				out.flush();
				//System.out.println("[client] " + msg);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				throw new ServerUnavailableException("Could not write "
						+ "to server.");
			}
		} else {
			throw new ServerUnavailableException("Could not write "
					+ "to server.");
		}
    }
    
    /**
     * Handle server commands.
     * @param input - the command send by the server
     * @throws ServerUnavailableException - when the server is unavailable 
     * @throws ProtocolException throws when server is not following the protocol communication
     * @ensures that the command is send to the right method in the client
     * @throws IOException when there is an put error
     */
    private void handleCommand(String input) throws ServerUnavailableException, ProtocolException {
    	String[] splitted = input.split(ProtocolMessages.DELIMITER, 2);
		switch (splitted[0]) {
			case ProtocolMessages.HELLO:
				client.handleHello(input);
				break;
			case ProtocolMessages.LOGIN:
			case ProtocolMessages.ALREADYLOGGEDIN:
				client.handleLogin(input);
				break;
			case ProtocolMessages.NEWGAME:
				client.handleNewGame(input);
				break;
			case ProtocolMessages.MOVE:
				client.handleMove(input);
				break;
			case ProtocolMessages.GAMEOVER:
				client.handleGameOver(input);
				break;
			case ProtocolMessages.LIST:
				client.handleList(input);
				break;
            case ProtocolMessages.ERROR:
            	//Print the error message the server sent
                client.showToUser("[!ERROR!] " + splitted[1]);
                break;
			default:
				throw new ProtocolException("Server send a unknow command: " + input);
		}
    }
    
    /**
     * Closes the connection between the server and the server handler.
     * @ensures socked is closed
     * @ensures BufferdWriter and reader are closed
     * @throws IOException when there is an put error
     */
    public void clearConnection() throws IOException {
        sock.close();
        out.close();
        in.close();
        this.keepRunning = false;
    }

}
