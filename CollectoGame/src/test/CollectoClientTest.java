package test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import collectoclient.CollectoClient;
import collectoclient.CollectoClientTUI;
import collectogame.Ball;
import collectogame.Board;
import exceptions.ProtocolException;
import exceptions.ServerUnavailableException;
import protocol.ProtocolMessages;

public class CollectoClientTest {
	
	private final static ByteArrayOutputStream OUTCONTENT = new ByteArrayOutputStream();
	private final static PrintStream ORIGINALOUT = System.out;
	
	CollectoClient client = new CollectoClient();
	
	/**
	 * Set the System.out from the client to outcontent for easy cheking of right text is printed.
	 */
	@BeforeAll
	static public void setUpStream() {
	    System.setOut(new PrintStream(OUTCONTENT));
	}
	
	/**
	 * Tests if the name of the server is shown to the user and the method handle hello works.
	 * (unit test)
	 * @throws ProtocolException
	 */
	@Test
	void testHandleHello() throws ProtocolException {
		this.client.handleHello(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + "The name");
		assertThat(OUTCONTENT.toString(), containsString("The name"));
		OUTCONTENT.reset();
	}
	
	/**
	 * Tests if after sending the command "LOGIN", it is shown to the user the login is successful.
	 * (unit test)
	 */
	@Test
	void testHandleLogin() {
		this.client.handleLogin(ProtocolMessages.LOGIN);
		assertThat(OUTCONTENT.toString(), containsString("succesfully logged in"));
		OUTCONTENT.reset();
		
		this.client.handleLogin(ProtocolMessages.ALREADYLOGGEDIN);
		assertThat(OUTCONTENT.toString(), containsString("please try a different one"));
		OUTCONTENT.reset();
	}
	
	/**
	 * Test if handle list correctly prints the list.
	 * (unit test)
	 */
	@Test
	void testHandleList() {
		this.client.handleList(ProtocolMessages.LIST + ProtocolMessages.DELIMITER 
				+ "first name" + ProtocolMessages.DELIMITER + "seccond" 
				+ ProtocolMessages.DELIMITER + "This&is*a@long+name=with strange tokens!@#$%^");
		assertThat(OUTCONTENT.toString(), containsString("1. first name"));
		assertThat(OUTCONTENT.toString(), containsString("2. seccond"));
		assertThat(OUTCONTENT.toString(), 
				containsString("This&is*a@long+name=with strange tokens!@#$%^"));
		OUTCONTENT.reset();
	}
	
	/**
	 * Test if the handleBoard method correctly prints out the board.
	 * (unit test)
	 */
	@Test
	void testHandleBoard() {
		this.client.setLoggedInAs("first");
		String command = "NEWGAME~6~4~3~6~2~4~6~3~6~4~5~4~6~1~4~5~1~4~3~5~6~6~3~4~0~6~2~4~1~2~1~2"
				+ "~5~1~5~5~3~5~1~3~5~1~2~1~2~3~2~3~2~first~second";
		this.client.handleNewGame(command); 
		String[] splitted = command.split(ProtocolMessages.DELIMITER);
		Board board = new Board();
		for (int i = 1; i <= 49; i++) {
			board.setField(i - 1, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
		assertThat(OUTCONTENT.toString(), containsString(board.toString()));
		OUTCONTENT.reset();
	}
	
	/**
	 * Tests if a move is correctly made and the new board is printed.
	 * (unit test)
	 */
	@Test
	void testHandleMove() {
		this.client.setLoggedInAs("first");
		String command = "NEWGAME~6~4~3~6~2~4~6~3~6~4~5~4~6~1~4~5~1~"
				+ "4~3~5~6~6~3~4~0~6~2~4~1~2~1~2~5~1~5~5~3~5~1~3~5~1~2~1~2~3~2~3~2~first~second";
		this.client.handleNewGame(command);
		try {
			this.client.handleMove(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + 3);
		} catch (ServerUnavailableException e) {

		}
		Board board = new Board();
		String[] splitted = command.split(ProtocolMessages.DELIMITER);
		for (int i = 1; i <= 49; i++) {
			board.setField(i - 1, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
		board.makeSingleMove(3);
		assertThat(OUTCONTENT.toString(), containsString(board.toString()));
		OUTCONTENT.reset();		
	}
	
	/**
	 * Tests if all the functions of the gameOver method works.
	 * (unit test)
	 */
	@Test
	void testGameOver() {
		this.client.setLoggedInAs("Einstein");
		this.client.handleGameOver(ProtocolMessages.GAMEOVER 
				+ ProtocolMessages.DELIMITER + ProtocolMessages.DRAW);
		assertThat(OUTCONTENT.toString(), containsString("The game ended in a draw"));
		OUTCONTENT.reset();
		this.client.handleGameOver(ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER 
				+ ProtocolMessages.VICTORY + ProtocolMessages.DELIMITER + "Einstein");
		assertThat(OUTCONTENT.toString(), containsString("The game ended, the winner is you"));
		OUTCONTENT.reset();
		this.client.handleGameOver(ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER 
				+ ProtocolMessages.VICTORY + ProtocolMessages.DELIMITER + "00*TIMMITHY*00");
		assertThat(OUTCONTENT.toString(), containsString("The game ended, the winner"
				+ " is 00*TIMMITHY*00"));
		OUTCONTENT.reset();
		this.client.handleGameOver(ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER 
				+ ProtocolMessages.DISCONNECT + ProtocolMessages.DELIMITER + "Einstein");
		assertThat(OUTCONTENT.toString(), 
				containsString("The other player has disconected, so the game is won by you"));
		OUTCONTENT.reset();
		
	}
	
	/**
	 * This test will check all the do methods in the client and most of their sub-methods 
	 * by pretending to be the server and sending commands to the client.
	 * 
	 * (System test)
	 * @throws ServerUnavailableException
	 */
	@Test
	void testDoMethots() throws ServerUnavailableException {
		try {
		// First create and connect a client and a serverSocket
			client = new CollectoClient();
			ServerSocket ssock = new ServerSocket(8888);
			Socket sock = new Socket("localhost", 8888);
			client.setServerSock(sock);
			Socket sockServer = ssock.accept();
			BufferedReader in = new BufferedReader(
					new InputStreamReader(sockServer.getInputStream()));
			BufferedWriter out = new BufferedWriter(
					new OutputStreamWriter(sockServer.getOutputStream()));
			
		// Start with the hello method, check if server receives correct thing
			client.doHello();
			assertThat(in.readLine(), containsString(ProtocolMessages.HELLO));
			
		// Send hello back to start things
			out.write(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + "testServer");
			out.newLine();
			out.flush();
			
		// Test if client tries to queue withoud logging in first
			client.doQueue();
			assertThat(OUTCONTENT.toString(), containsString(
					"You need LOGIN to perform this action"));
			OUTCONTENT.reset();
			
		// Test if client can request list without loggin in first
			client.doList();
			assertThat(OUTCONTENT.toString(), containsString(
					"You need to be logged in to perform this action"));
			OUTCONTENT.reset();
			
		// Test the login by answering with LOGIN and ALREADYLOGGEDIN
			client.doLogin("Minh&Jesse");
			assertThat(in.readLine(), containsString(ProtocolMessages.LOGIN 
					+ ProtocolMessages.DELIMITER + "Minh&Jesse"));
			
			out.write(ProtocolMessages.ALREADYLOGGEDIN);
			out.newLine();
			out.flush();
			
			client.doLogin("Minh&Jesse-New");
			assertThat(in.readLine(), containsString(ProtocolMessages.LOGIN 
					+ ProtocolMessages.DELIMITER + "Minh&Jesse-New"));
			
			out.write(ProtocolMessages.LOGIN);
			out.newLine();
			out.flush();
			
			for (int i = 0; i < 10000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			client.doLogin("error");
			assertThat(OUTCONTENT.toString(), containsString("You are already logged in"));
			OUTCONTENT.reset();
			
		// Test the list
			client.doList();
			assertThat(in.readLine(), containsString(ProtocolMessages.LIST));
			
			out.write(ProtocolMessages.LIST + ProtocolMessages.DELIMITER + "Minh&Jesse-New");
			out.newLine();
			out.flush();
			
		// Test the queue before and after creating a new game
			client.doQueue();
			assertThat(in.readLine(), containsString(ProtocolMessages.QUEUE));
			
			String command = "NEWGAME~6~4~3~6~2~4~6~3~6~4~5~4~6~1~4~5~1~"
					+ "4~3~5~6~6~3~4~0~6~2~4~1~2~1~2~5~1~5~5~3~5~1~3~"
					+ "5~1~2~1~2~3~2~3~2~Minh&Jesse-New~first";
			out.write(command);
			out.newLine();
			out.flush();
			
			for (int i = 0; i < 100000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			client.doQueue();
			assertThat(OUTCONTENT.toString(), containsString(
					"You cannot join QUEUE because you are in a game"));
			OUTCONTENT.reset();
			
		// Creates sets the System.in to a predefined text 
		// (used for multiple parts of the systemTest)
			String testInput = "y" + System.lineSeparator() + "y" 
					+ System.lineSeparator() + "y" + System.lineSeparator() + "y" 
					+ System.lineSeparator() + "n" + System.lineSeparator();		
			BufferedReader testReader = new BufferedReader(new StringReader(testInput));
			((CollectoClientTUI) client.getView()).replace(testReader);
			
		// Tries to do a move with the previouslily created board with the smart strategy
			client.doMove();
			assertThat(OUTCONTENT.toString(), containsString(
					"Best choice is10"));
			OUTCONTENT.reset();
			
			assertThat(in.readLine(), containsString("10"));
			
			for (int i = 0; i < 10000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			out.write(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + "10");
			out.newLine();
			out.flush();
			
			out.write(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + "2");
			out.newLine();
			out.flush();
			
			assertThat(in.readLine(), containsString(ProtocolMessages.MOVE 
					+ ProtocolMessages.DELIMITER + "8"));
		
		// Test the gameOver method
			out.write(ProtocolMessages.GAMEOVER 
					+ ProtocolMessages.DELIMITER + ProtocolMessages.DISCONNECT + "first");
			out.newLine();
			out.flush();
		
		// Test the error method
			out.write(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER 
					+ "Test test error blieb blub");
			out.newLine();
			out.flush();
			
			for (int i = 0; i < 10000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			assertThat(OUTCONTENT.toString(), containsString(
					"[!ERROR!] Test test error blieb blub"));
			OUTCONTENT.reset();
		
		// Some more test for the queue (first enter, then leave the queue and enter again)
			client.doQueue();
			assertThat(in.readLine(), containsString(ProtocolMessages.QUEUE));
			client.doQueue();
			assertThat(OUTCONTENT.toString(), containsString(
					"You succesfully left the queue"));
			client.doQueue();
			OUTCONTENT.reset();
			
		//Create a new game and test the singlemove, doublemove from the Naive Strategy
			command = "NEWGAME~1~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~"
					+ "0~0~0~2~0~0~0~0~0~0~0~0~0~0~0~0~0~0~0~3~0~0~3~"
					+ "0~0~2~0~0~0~0~0~1~Minh&Jesse-New~first";
			client.handleNewGame(command);
			
			for (int i = 0; i < 100000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			client.doMove();
			
			out.write(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER 
					+ 2 + ProtocolMessages.DELIMITER + 14);
			out.newLine();
			out.flush();
			
		// Send a unknown command to the client (should close connection)
			out.write("This is not a valid command, should shutdown");
			out.newLine();
			out.flush();
			
			for (int i = 0; i < 10000; i++) {
				//Wait a little bit because otherwise the program runs to fast for receiving data
				System.out.print("");
			}
			
			assertThat(OUTCONTENT.toString(), containsString(
					"Closed connection because the server violated Protocol"));
			OUTCONTENT.reset();
			
		//Create New client
			client = new CollectoClient();
			sock = new Socket("localhost", 8888);
			client.setServerSock(sock);
			sockServer = ssock.accept();
			in = new BufferedReader(
					new InputStreamReader(sockServer.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(sockServer.getOutputStream()));
			
		// Test the ServerUnavailableException
			client.doHello();
			assertThat(in.readLine(), containsString(ProtocolMessages.HELLO));
			
			out.write(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + "testServer");
			out.newLine();
			out.flush();
			
			for (int i = 0; i < 10000; i++) {
				//Wait a little bit
				System.out.print("");
			}
			
			sockServer.close();
			ssock.close();
			
			testInput = "LIST" + System.lineSeparator();		
			testReader = new BufferedReader(new StringReader(testInput));
			((CollectoClientTUI) client.getView()).replace(testReader);
			
			Assertions.assertThrows(ServerUnavailableException.class, () -> {
			    client.doHello();
			});
			
		} catch (IOException e) {
			assert false : "portnumber 8888 already in use";
		}			
	}
	
	@AfterAll
	static void restoreStream() {
	    System.setOut(ORIGINALOUT);
	}
}
