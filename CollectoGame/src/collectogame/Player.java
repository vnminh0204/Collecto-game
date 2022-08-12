package collectogame;

import java.util.List;

import collectoclient.CollectoClient;

public abstract class Player {
	
    /**
     * @invariant score >= 0;
     * @invariant name != null;
     * @invariant (role == 0) || (role == 1);
     * @invariant collection.length == 7;
     * Collection is the number of each balls that player collect after a move
     * with the index is the corresponding color
     */
	private String name;
	protected int[] collection;
	private int score;
	private int role;
	protected CollectoClient client;
	
    /**
     * Creates a new Player object.
     * @requires name != null;
     * @requires (role == 0) || (role ==1);
     * @ensures collection.length == 7; 
     * @ensures (score == 0);
     * @ensures the role of this player will be assigned a role
     * @ensures the name of this player will be name
     * @param name - the name of the player
     * @param role - the turn of the player
     * @param client - CollectoClient
     */
    public Player(String name, int role, CollectoClient client) {
        this.name = name;
        this.role = role;
        this.collection = new int[7];
        this.score = 0;
        this.client = client;
    }
    
    /**
     * Calculate player's score.
     * @ensures score = \old(score + (collection[i] / 3)) 
     * for i from 1 to 6 corresponding to 6 color in collection
     */
    public void calulateScore() {
    	int newScore = 0;
    	for (int i = 1; i < collection.length; i++) {
    		newScore = newScore + (collection[i] / 3);
    	}
    	this.score = newScore;
    }
    
    /**
     * Returns the number of ball in collection.
     * @ensures numBall is positive;
     * @return the number of ball in collection.
     */
    public int getNumBall() {
    	int numBall = 0;
    	for (int i = 1; i < collection.length; i++) {
    		numBall = numBall + collection[i];
    	}
    	return numBall;
    }
    
    /**
     * Returns the name of the player.
     * @return the name of player
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the role of the player.
     * @return the turn of player
     */
    public int getRole() {
        return role;
    }
    
    /**
     * Returns the score of the player.
     * @ensures score is positive;
     * @return the score of player
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Determines the indices for the next move.
     * @requires board is not null and still has possible move
     * @ensures the returned in is a array of length 1 for SingleMove or length 2 for DoubleMove
     * @param board the current game board
     * @return the player's choice
     */
    public abstract int[] determineMove(Board board);

    /**
     * Makes a move on the board. <br>
     * @requires board is not null and not full
     * @param board the current board
     */
    public void makeMove(Board board) {
        int[] choice = determineMove(board);
        if (choice.length == 1) {
        	board.makeSingleMove(choice[0]);
        	this.updateCollection(board.collectBall());
        	this.calulateScore();
        }
        
        if (choice.length == 2) {
        	board.makeDoubleMove(choice[0], choice[1]);
        	this.updateCollection(board.collectBall());
        	this.calulateScore();
        }
    }
    /**
     * Update the player's collection of balls after a move.
     * @requires collectedList != null
     * @param collectedList is the list of ball after Board.collectBall();
     */
    public void updateCollection(List<Ball> collectedList) {
    	for (Ball ball : collectedList) {
    		collection[ball.convertTo()]++;
    	}
    }
    /**
     * Reset the player's collection and score after a game over.
     * @ensures (this.collection == new int[7]) and (this.score == 0)
     */
    public void resetPlayer() {
        this.collection = new int[7];
        this.score = 0;
    }
    
    /**
     * Return the collection with the number of each ball player has.
     * @return the Collection balls of Player
     */
    public String displayBall() {
    	String out = "";
    	for (int i = 1; i < collection.length; i++) {
    		out = out + Ball.convertTo(i) + ": "  + collection[i] + "| ";
    	}
    	return out;
    }
    
    /**
     * Return the collection of Player.
     * @return the collection of player
     * @ensures collection != null;
     */
	public int[] getCollection() {
		return this.collection;
	}
	
	/**
	 * Updates player collection from the online player.
	 * @param player - the online player in question
	 */
    public void updateOnlineCollection(Player player) {
    	int[] copyCollection = player.getCollection();
    	for (int i = 1; i < this.collection.length; i++) {
    		this.collection[i] = copyCollection[i] + 0;
    	}
    }
}
