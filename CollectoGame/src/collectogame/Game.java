package collectogame;

import collectoclient.CollectoClient;

public class Game {
    public static final int NUMBER_PLAYERS = 2;

    /**
     * The board.
     * @invariant board is never null
     */
    private Board board;

    /**
     * The 2 players of the game.
     * @invariant the length of the array equals NUMBER_PLAYERS
     * @invariant all array items are never null
     */
    private Player[] players;

    /**
     * Index of the current player.
     * @invariant the index is always between 0 and NUMBER_PLAYERS
     */
    private int current;
    
    private CollectoClient client;

    // -- Constructors -----------------------------------------------

    /**
     * Creates a new Game object.
     * @requires s0 and s1 to be non-null
     * @param s0 the first player
     * @param s1 the second player
     * @param client - CollectoClient
     */
    public Game(Player s0, Player s1, CollectoClient client) {
        this.board = new Board();
        this.players = new Player[NUMBER_PLAYERS];
        this.client = client;
        this.players[0] = s0;
        this.players[1] = s1;
        this.current = 0;
    }

    /**
     * Create a board that has at least 1 possible move.
     * @ensures (this.getBoard().isStillPossibleSingleMove() == true);
     */
    public void createNewBoard() {
    	while (true) {
    		board.initializeNewBoard();
    		if (board.isStillPossibleSingleMove()) {
    			break;
    		}
    		board = new Board();
    	}
    }

    /**
     * Prints the game situation.
     */
    public void update() {
        client.showToUser("\ncurrent game situation: \n\n" + board.toString()
                + "\n");
        client.showToUser("\n Player " + players[0].getName() 
        		+ " has " + players[0].getScore() + " scores and "
        		+ players[0].getNumBall() + " balls" + " with " + players[0].displayBall());
        client.showToUser("\n Player " + players[1].getName() 
        		+ " has " + players[1].getScore() + " scores and " 
        		+ players[1].getNumBall() + " balls" + " with " + players[1].displayBall());
    }
    
    /**
     * Returns player who is winner or return null if no winner. 
     * This is the case when one has more score 
     * or if score is equal, one who has more balls is the winner
     * @ensures winner() != null when there is a winner
     * @return a player who is the winner
     */
    public Player winner() {
    	
    	if ((players[0].getScore() > players[1].getScore()) 
    			|| ((players[0].getScore() == players[1].getScore()) 
    			&& (players[0].getNumBall() > players[1].getNumBall()))) {
    		return players[0];
    	}
    	if ((players[1].getScore() > players[0].getScore()) 
    			|| ((players[1].getScore() == players[0].getScore()) 
    			&& (players[1].getNumBall() > players[0].getNumBall()))) {
    		return players[1];
    	}
        return null;
    }
    
    /**
     * Returns current turn of the game.
     * @ensures (current == 0) || (current == 1);
     * @return this.current;
     */
    public int getCurrentTurn() {
    	return this.current;
    }
    
    /**
     * Update the current turn of the game.
     * @requires (current == 0) || (current == 1);
     * @ensures current == (1 - \old(current));
     */
    public void updateCurrentTurn() {
    	this.current = 1 - this.current;
    }
    
    /**
     * Returns current board of the game.
     * @ensures this.board != null
     * @return a current board
     */
    public Board getBoard() {
    	return this.board;
    }
    
    /**
     * Returns the player of the game.
     * @param role - the turn of the player
     * @requires (role == 1) or (role ==0);
     * @ensures this.player[role] != null;
     * @return a player corresponding with role
     */
    public Player getPlayer(int role) {
    	return this.players[role];
    }
    
    // Online game methods
    /**
     * Update board from server for online game.
     * @param boardInput;
     * @requires (board != null);
     */
    public void updateOnlineBoard(Board boardInput) {
    	this.board = boardInput.deepCopy();
    }
    
    /**
     * Make the move for online players.
     * @param currentTurn - role of online player;
     * @requires (board != null) and (players[currentTurn] != null);
     */
    public void makeOnlineMove(int currentTurn) {
        players[currentTurn].makeMove(board);
    }
    
    //Offline game methods
    /**
     * Starts the Collecto game. <br>
     * Asks after each ended game if the user want to continue. Continues until
     * the user does not want to play anymore.
     */
    public void startOfflineGame() {
        boolean continueGame = true;
        while (continueGame) {
    		reset();
            createNewBoard();
            playOfflineGame();
            continueGame = client.getBoolean("\n> Play another time?");
        }
    }
    
    //We dont write the test for startOfflineGame, 
    //because it just call other method that is already tested 
    
    
    
    /**
     * Plays the Collecto game. <br>
     * The game is played until it is over. 
     * Players can make a move one after the other. 
     * After each move, the changed game situation is printed.
     */
    public void playOfflineGame() {
        while (!board.isEndGame()) {
        	this.update();
            players[current].makeMove(board);
        	this.updateCurrentTurn();
        }
        this.update();
        this.printResult();
    }
    
    /**
     * Resets the game. <br>
     * The new board object is assigned and player[0] becomes the current player.
     */
    public void reset() {
    	players[0].resetPlayer();
    	players[1].resetPlayer();
        current = 0;
        board = new Board();
    }
    
    /**
     * Prints the result of the last game. <br>
     * @requires the game to be over
     */
    private void printResult() {
    	Player winner = winner();
        if (winner != null) {
            client.showToUser("Speler " + winner.getName() + " has won!");
        } else {
            client.showToUser("Draw. There is no winner!");
        }
    }
}
