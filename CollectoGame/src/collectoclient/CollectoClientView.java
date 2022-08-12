package collectoclient;

import java.net.InetAddress;

import exceptions.ExitProgram;
import exceptions.ServerUnavailableException;

public interface CollectoClientView {
	
	/**
	 * Asks for user input continuously and handles communication accordingly using
	 * the {@link #handleUserInput(String input)} method.
	 */
	public void start();

	/**
	 * Split the user input on a space and handle it accordingly. 
	 * - If the input is valid, take the corresponding action 
	 * - If the input is invalid, show a message to the user and print the help menu.
	 * 
	 * @param input The user input.
	 * @throws ExitProgram when user wants to exit the program
	 * @throws ServerUnavailableException when the server is unvailable
	 */
	public void handleUserInput(String input) throws ExitProgram, ServerUnavailableException;

	/**
	 * Writes the given message to standard output.
	 * 
	 * @param message - the message to write to the standard output.
	 */
	public void showMessage(String message);

	/**
	 * Ask the user to input a valid IP. If it is not valid, show a message and ask
	 * again.
	 * 
	 * @return a valid IP
	 */
	public InetAddress getIp();

	/**
	 * Prints the question and asks the user to input a String.
	 * 
	 * @param question The question to show to the user
	 * @return The user input as a String
	 */
	public String getString(String question);

	/**
	 * Prints the question and asks the user to input an Integer.
	 * 
	 * @param question The question to show to the user
	 * @return The written Integer.
	 */
	public int getInt(String question);

	/**
	 * Prints the question and asks the user for a yes/no answer.
	 * 
	 * @param question The question to show to the user
	 * @return The user input as boolean.
	 */
	public boolean getBoolean(String question);

	/**
	 * Prints the help menu with available input options.
	 */
	public void printHelpMenu();
	
	/**
	 * Shuts the view correctly down in the case the client did not type exit to shutdown.
	 */
	public void shutDown();

}
