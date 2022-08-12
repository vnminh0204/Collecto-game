package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class CollectoServerTUI implements CollectoServerView {
	/** The PrintWriter to write messages to.*/
	private PrintWriter consoleOut;
	
	/** The BufferdReader to receive messages from. */
	private BufferedReader consoleIn;

	/**
	 * Constructs a new HotelServerTUI. Initializes the console.
	 */
	public CollectoServerTUI() {
		consoleOut = new PrintWriter(System.out, true);
		consoleIn = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void showMessage(String message) {
		consoleOut.println(message);
	}
	
	@Override
	public String getString(String question) {
		showMessage(question);
		String ans = null;
		try {
			ans = consoleIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ans;
	}

	@Override
	public int getInt(String question) {
		showMessage(question);
		String ans = null;
		try {
			ans = consoleIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int number;
		try {
			number = Integer.parseInt(ans);
		} catch (NumberFormatException e) {
			showMessage("This is not a number, please try again");
			return getInt(question);
		}
		return number;
	}

	@Override
	public boolean getBoolean(String question) {
		showMessage(question);
		String ans = null;
		try {
			ans = consoleIn.readLine().toUpperCase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ans.equals("YES") || ans.equals("TRUE") || ans.equals("Y")) {
			return true;
		} else if (ans.equals("NO") || ans.equals("FALSE") || ans.equals("N")) {
			return false;
		} else {
			showMessage("You can answer: (yes / true / y / no / false / n)!! Please try again");
			return getBoolean(question);
		}
	}
}
