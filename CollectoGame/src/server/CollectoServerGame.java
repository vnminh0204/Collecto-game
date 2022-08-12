package server;

import collectoclient.CollectoClient;
import collectogame.Game;
import collectogame.Player;

public class CollectoServerGame extends Game {
	/**
	 * @invariant ch1 != null - the CollectoClientHandler of first player
	 * @invariant ch2 != null - the CollectoClientHandler of second player
	 */
	private CollectoClientHandler ch1;
	private CollectoClientHandler ch2;

	/**
	 * Creates a new CollectoGame for the CollectoServer.
	 * @param s0 - The first player playing the game
	 * @param s1 - The second player playing the game
	 * @param client - For the output of the game (not used)
	 * @param chp0 - The first clientHandler belonging to this game
	 * @param chp1 - The second clientHandler belonging to this game
	 */
	public CollectoServerGame(Player s0, Player s1, CollectoClient client, 
			CollectoClientHandler chp0, CollectoClientHandler chp1) {
		super(s0, s1, client);
		this.ch1 = chp0;
		this.ch2 = chp1;
	}
	
	/**
	 * Gets the first ClientHandler belonging to this game.
	 * @ensures ch1 != null;
	 * @return this.ch1;
	 */
	public CollectoClientHandler getClientHandler1() {
		return this.ch1;
	}
	
	/**
	 * Gets the second ClientHandler belonging to this game.
	 * @ensures ch2 != null;
	 * @return this.ch2;
	 */
	public CollectoClientHandler getClientHandler2() {
		return this.ch2;
	}

}
