package collectogame;


public interface Strategy {
	
	/**
	 * Return Strategy's name.
	 * @ensures Strategy.name != null;
	 * @return Strategy.name;
	 */
	public String getName();
	
	/**
	 * Return the move based on Strategy.
	 * @requires (board != null) and ((role == 0) or (role == 1))
	 * @ensures (board.isValidSingleMove(choice) == true) 
	 * or (board.isValidDoubleMove(choice) == true);
	 * @param board - current board situation
	 * @param collection - the collection of player
	 * @return SingleMove or DoubleMove
	 */
	public int[] determineMove(Board board, int[] collection);
}

