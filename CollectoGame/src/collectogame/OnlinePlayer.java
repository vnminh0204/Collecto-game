package collectogame;

import collectoclient.CollectoClient;

public class OnlinePlayer extends Player {

	/**
	 * @invariant 0 <= indice1 <= 27 || -1 if no move possible
	 * @invariant 0 <= indice2 <= 27 || -1 if no second move possible
	 */
	private int indice1;
	private int indice2;
	
	/**
	 * Constructs a new OnlinePlayer object.
	 * @param name - the name of the online player
	 * @param role - the role of the online player
	 * @param client - a random client (design mistake)
	 */
	public OnlinePlayer(String name, int role, CollectoClient client) {
		super(name, role, client);
		indice1 = -1;
		indice2 = -1;
	}

	@Override
	public int[] determineMove(Board board) {
		int[] onlineMove = new int[0];
		if (indice2 == -1) {
			onlineMove = new int[1];
			onlineMove[0] = indice1;
		}
		if (indice2 != -1) {
			onlineMove = new int[2];
			onlineMove[0] = indice1;
			onlineMove[1] = indice2;
		}
		return onlineMove;
	}

    /**
     * Update the move of online game.
     * @param choice - the moves were read from server
     * @requires (choice != null);
     */
	public void updateOnlinveMove(int[] choice) {
		if (choice.length == 1) {
			indice1 = choice[0];
			indice2 = -1;
		}
		
		if (choice.length == 2) {
			indice1 = choice[0];
			indice2 = choice[1];
		}
	}
}
