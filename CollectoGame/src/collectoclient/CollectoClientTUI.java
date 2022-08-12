package collectoclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import exceptions.ExitProgram;
import exceptions.ServerUnavailableException;
import protocol.ProtocolMessages;

public class CollectoClientTUI implements CollectoClientView {
	
	/**
	 * @invariant consoleOut = the Stream that sends text to the console
	 * @invariant consoleIn = the Stream that receives the input from the user in the console
	 * @invariant client = the client belonging to this CollectoClientTUI
	 */
	private PrintWriter consoleOut;
	private BufferedReader consoleIn;
	private CollectoClient client;
	private boolean exit = false;
	
	/**
	 * Starts a new CollectoClientTUI.
	 * @param client - the client this TIU belongs to
	 * @ensures a input stream and output stream are created for 
	 * the connection between the console and the program
	 */
	public CollectoClientTUI(CollectoClient client) {
		this.client = client;
		consoleOut = new PrintWriter(System.out, true);
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
	}
	
	// This method is used for testing the userInput
	/**
	 * This method can replace the BufferdReader by a different one.
	 * @param read - The new BufferdReader
	 * @return The old bufferdReader (in case you later want to switch back
	 */
	public BufferedReader replace(BufferedReader read) {
		BufferedReader old = consoleIn;
		consoleIn = read;
		return old;
	}

	@Override
	public void start() {
		// Ask for user input continuously and handle
		// communication accordingly by sending it to handleUserInput
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while (!this.exit) {
			String input = null;
			try {
				input = reader.readLine();
			} catch (IOException e1) {
				showMessage("Oh no, Cannot read input . Please try again");
			}
			try {
				handleUserInput(input);
			} catch (ExitProgram e) {
				this.exit = true;
				if (client.closeConnection(true)) {
					this.exit = true;
				} else {
					this.exit = false;
				}
			} catch (ServerUnavailableException e) {
				showMessage("Server is unavailable");
				client.closeConnection(false);
			}
		}
		this.exit = false;
	}

	@Override
	public void handleUserInput(String input) throws ServerUnavailableException, ExitProgram {
		String[] splitted = input.split("\\s+"); // Split on space
		String commandString = splitted[0]; // Safe since input != empty
		String cmd1 = null;
		String cmd2 = null;
	
		if (splitted.length > 1) {
			cmd1 = splitted[1];
			if (splitted.length > 2) {
				cmd2 = splitted[2];
			}
		}
	
		switch (commandString.toUpperCase()) { //Now user can type lower and uppercase
			case ProtocolMessages.LOGIN:
				if (cmd1 == null) {
					client.doLogin("");
				} else if (cmd2 == null) {
					client.doLogin(cmd1);
				} else {
					client.doLogin(cmd1 + " " + cmd2); 
				}
				break;
			case ProtocolMessages.QUEUE:
				client.doQueue();
				break;
			case ProtocolMessages.LIST:
				client.doList();
				break;
			case ProtocolMessages.MOVE:			
				client.doMove();
				break;
			case "HELP":
				printHelpMenu();
				break;
			case "EXIT":
				throw new ExitProgram("User exited");
			default:
				System.out.println("Unkown command: " + commandString);
				//When unknown command is used, the helpMenu is printed
				printHelpMenu();
		}	
	}

	@Override
	public void showMessage(String message) {
		consoleOut.println(message);
	}

	@Override
	public InetAddress getIp() {
		InetAddress addr = null;
		while (addr == null) {
			String input = getString("Enter the IP or host to connect to");
			try {
				addr = InetAddress.getByName(input);
			} catch (UnknownHostException e) {
				showMessage("ERROR: host " + input + " unknown");
			}
		}	
		return addr;
	}

	@Override
	public String getString(String question) {
		showMessage(question);
		try {
			return consoleIn.readLine();
		} catch (IOException e) {
			showMessage("Oh no, something whent wrong. Please try again");
			return getString(question);
		}
	}

	@Override
	public int getInt(String question) {
		String answer;
		int intAnswer;
		showMessage(question);
		try {
			answer = consoleIn.readLine();
		} catch (IOException e1) {
			showMessage("Oh no, cannot read. Please try again");
			return getInt(question);
		}
		try {
			intAnswer = Integer.parseInt(answer);
		} catch (NumberFormatException e) {
			showMessage("This is not a number!! Please try again");
			return getInt(question);
		}
		return intAnswer;
	}

	@Override
	public boolean getBoolean(String question) {
		String answer;
		showMessage(question);
		try {
			answer = consoleIn.readLine().toUpperCase();
		} catch (IOException e) {
			showMessage("Oh no, it is not boolean. Please try again");
			return getBoolean(question);
		}
		if (answer.equals("YES") || answer.equals("TRUE") || answer.equals("Y")) {
			return true;
		} else if (answer.equals("NO") || answer.equals("FALSE") || answer.equals("N")) {
			return false;
		} else {
			showMessage("You can answer: (yes / true / y / no / false / n)!! Please try again");
			return getBoolean(question);
		}
	}

	@Override
	public void printHelpMenu() {
		showMessage("Available commands are:\n"
				+ "LOGIN (name)         - to login to the server with your name\n"
				+ "QUEUE                - to enter or leave the queue for playing a game\n"
				+ "LIST                 - to get the names of all the online players\n"
				+ "MOVE (move1) (move2) - to play a (double) move during the game\n"
				+ "HELP                 - to print this help-menu\n"
				+ "EXIT                 - to exit the program");
	}

	@Override
	public void shutDown() {
		this.exit = true;	
	}

}
