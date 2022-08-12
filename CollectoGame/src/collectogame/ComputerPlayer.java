package collectogame;

import collectoclient.CollectoClient;

public class ComputerPlayer extends Player {
	
	/**
	 * @invariant strategy = the strategy this computerPlayer is using
	 */
	Strategy strategy;

	/**
	 *Creates a computer player with a given mark and a given strategy.
	 *@param role - the turn of the player
	 *@param name - the name of user
	 *@param client - CollectoClient
	 *@param strategy - the strategy of the computer player
	 */
	public ComputerPlayer(Strategy strategy, String name, int role,  CollectoClient client) {
		super(strategy.getName() + " BOT - " + name, role, client);
		this.strategy = strategy;
	}
	
	/**
	 * Return the move based on Strategy.
	 * @requires (board != null)
	 * @ensures (board.isValidSingleMove(choice) == true) 
	 * || (board.isValidDoubleMove(choice) == true);
	 * @param board - current board situation
	 * @return SingleMove or DoubleMove
	 */
	@Override
	public int[] determineMove(Board board) {
		return this.strategy.determineMove(board, super.collection);
	}	
}

