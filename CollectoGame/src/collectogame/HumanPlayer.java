package collectogame;

import collectoclient.CollectoClient;

public class HumanPlayer extends Player {

    // -- Constructors -----------------------------------------------

    /**
     * Creates a new human player object.
     * @param name - the name of user
     * @param role - the turn of player
     * @param client - CollectoClient
     * @requires name is not null
     * @requires role is either 0 or 1
     * @ensures the Name of this player will be name
     * @ensures the Role of this player will be assigned
     */
    public HumanPlayer(String name, int role, CollectoClient client) {
        super(name, role, client);
    }

    // -- Commands ---------------------------------------------------

    /**
     * Asks the user to input the field where to place the next mark. This is
     * done using the standard input/output.
     * @requires board is not null
     * @ensures the returned integer is a valid field index and the corresponding field is empty 
     * @param board the game board
     * @return the player's chosen field
     */
    public int[] determineMove(Board board) {
    	int[] choice = null;
    	String prompt;
    	if (board.isStillPossibleSingleMove()) {
    		// ask if user wants a hint -> returns all possible moves
    		if (client.getBoolean("Do you want to get hint ? (Type y/n)")) {
    			String reply = "Possible single moves are | ";
    			for (int indice = 0; indice <= 27; indice++) {
    				if (board.isValidSingleMove(indice)) {
    					reply = reply + indice + "| ";
    				}
    			}
    			client.showToUser(reply);
    		}
    		
    		choice = new int[1];
    		prompt = "> " + getName() + ", what is your single move? ";
    		choice[0] = client.getInt(prompt);
        
    		boolean valid = board.isValidSingleMove(choice[0]);
    		while (!valid) {
    			client.showToUser("ERROR: field " + choice[0]
    					+ " is no valid choice.");
    			choice[0] = client.getInt(prompt);
    			valid = board.isValidSingleMove(choice[0]);
    		}
        } else {
        	// ask if user wants a hint -> returns all possible moves
    		if (client.getBoolean("Do you want to get hint ? (Type y/n)")) {
    			String reply = "Possible double moves are | ";
    			for (int indice1 = 0; indice1 <= 27; indice1++) {
    				for (int indice2 = 0; indice2 <= 27; indice2++) {
    					if (board.isValidDoubleMove(indice1, indice2)) {
    						reply = reply + indice1 + "->" + indice2 + "| ";
    					}
    				}
    			}
    			client.showToUser(reply);
    		}
        	choice = new int[2];
        	prompt = "> " + getName() + ", what is your first double move? ";
   		 	choice[0] = client.getInt(prompt);
   		 	
   		 	prompt = "> " + getName() + ", what is your second double move? ";
		 	choice[1] = client.getInt(prompt);
    		
		 	boolean valid = board.isValidDoubleMove(choice[0], choice[1]);
    		while (!valid) {
    			client.showToUser("ERROR: field " + choice[0] + " " + choice[1]
   					+ " is no valid choice.");
    			client.showToUser(prompt);
    			
            	prompt = "> " + getName() + ", what is your first double move? ";
       		 	choice[0] = client.getInt(prompt);
       		 	
       		 	prompt = "> " + getName() + ", what is your second double move? ";
    		 	choice[1] = client.getInt(prompt);
    			valid = board.isValidDoubleMove(choice[0], choice[1]);
    		}
        }
        return choice;
    }

}