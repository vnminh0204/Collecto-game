package test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import protocol.ProtocolMessages;

public class CollectoServerTest {
	
	/**
	 * [IMPORTAND] For this test it is necessary you start
	 * the CollectoServer on local host with port number 8080.
	 * 
	 * We needed to put everything in one test because I needed the server 
	 * to save the date from the previous ones,
	 * For example the second client that tries to log in with the same userName.
	 */
	@Test
	void testHandleHello() {
		Socket sock1 = null;
		Socket sock2 = null;
		try {
			sock1 = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(sock1.getInputStream()));
			final BufferedWriter out1 = 
					new BufferedWriter(new OutputStreamWriter(sock1.getOutputStream()));
			
		//Test the hello message
			out1.write(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + "The testerClient");
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.HELLO 
					+ ProtocolMessages.DELIMITER));
			
		//Test the queue (should return a error, because you're not logged in yet
			out1.write(ProtocolMessages.QUEUE);
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.ERROR));
			
		//Test the login method
			out1.write(ProtocolMessages.LOGIN + ProtocolMessages.DELIMITER + "The Tester1");
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.LOGIN));
			
		//Test login again (should return a error, because you're already logged in
			out1.write(ProtocolMessages.LOGIN + ProtocolMessages.DELIMITER + "The Tester again");
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.ERROR));
			
		//Test a wrong command
			out1.write("WRONG");
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.ERROR));
			
		//Connecting an other client
			sock2 = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
			final BufferedWriter out2 = 
					new BufferedWriter(new OutputStreamWriter(sock2.getOutputStream()));
			out2.write(ProtocolMessages.HELLO + ProtocolMessages.DELIMITER + "The testerClient");
			out2.newLine();
			out2.flush();
			assertThat(in2.readLine(), containsString(ProtocolMessages.HELLO 
					+ ProtocolMessages.DELIMITER));
			
		//Test login with a second client with same user name (should return ALREADYLOGGEDIN)
			out2.write(ProtocolMessages.LOGIN + ProtocolMessages.DELIMITER + "The Tester1");
			out2.newLine();
			out2.flush();
			assertThat(in2.readLine(), containsString(ProtocolMessages.ALREADYLOGGEDIN));
			
		//Now actually log in
			out2.write(ProtocolMessages.LOGIN + ProtocolMessages.DELIMITER + "The Tester2");
			out2.newLine();
			out2.flush();
			assertThat(in2.readLine(), containsString(ProtocolMessages.LOGIN));
			
		//Test the list
			out1.write(ProtocolMessages.LIST);
			out1.newLine();
			out1.flush();
			String ans = in1.readLine();
			assertThat(ans, containsString("The Tester1"));
			assertThat(ans, containsString("The Tester2"));
			
		//Test the move before entering a game (should return an error)
			out1.write(ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + "3");
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.ERROR));
			
		//Test the queue
			out1.write(ProtocolMessages.QUEUE);
			out1.newLine();
			out1.flush();
			out2.write(ProtocolMessages.QUEUE);
			out2.newLine();
			out2.flush();
			String queue1 = in1.readLine();
			String queue2 = in2.readLine();
			assertThat(queue1, containsString(queue2));
			assertThat(queue2, containsString(queue1));
			assertThat(queue1, containsString(ProtocolMessages.NEWGAME 
					+ ProtocolMessages.DELIMITER));
			
		//Try during the game to join a queue again (should return an error)
			out1.write(ProtocolMessages.QUEUE);
			out1.newLine();
			out1.flush();
			assertThat(in1.readLine(), containsString(ProtocolMessages.ERROR));
			
		//Test ClientUnavailableException
			in2.close();
			out2.close();
			sock2.close();
			out1.write(ProtocolMessages.LIST);
			assertFalse(in1.readLine().contains("The Tester2"));
			
		} catch (IOException e) {
			System.out.println("[IMPORTANT] Test failed because of "
					+ "no connection with the server!!\n"
					+ "Please start a CollectoServer on localhost on port 8080");
			assert false;
		}	
	}
}
