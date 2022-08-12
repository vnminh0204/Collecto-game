package collectogame;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Board for the Collecto game. Team Yellow 1.3's Module2 final project.
 *
 * @author Vo Nhat Minh and Jesse Snoijer
 */
public class Board {
    public static final int DIM = 7;
    /**
     * The DIM by DIM fields of the Collecto board. See NUMBERING for the
     * coding of the fields.
     * @invariant there are always DIM*DIM fields
     * @invariant all fields are either Ball.EMPTY, Ball.BLUE, Ball.YELLOW, 
     * Ball.RED, Ball.ORANGE, Ball.PURPLE, Ball.GREEN;
     */
    private Ball[] fields;

    // -- Constructors -----------------------------------------------

    /**
     * Creates an empty board.
     * @ensures at player can do least 1 possible sing move
     */
    public Board() {
    	fields = new Ball[DIM * DIM];
    	for (int i = 0; i < DIM; i++) {
    		for (int j = 0; j < DIM; j++) {
				setField(index(i, j), Ball.EMPTY);
			}
    	}
    }

    // -- Methods -----------------------------------------------
    
    /**
     * Calculates the index in the linear array of fields from a (row, column)
     * pair.
     * @param row - the row value
     * @param column - the column value
     * @requires row to be between 0 and DIM
     * @requires column to be between 0 and DIM
     * @return the index belonging to the (row,column)-field
     */
    public int index(int row, int column) {
        return column + row * DIM;
    }
    
    /**
     * Returns true if index is a valid index of a field on the board.
     * @param index - the index value of the field 
     * @ensures a positive result when the index is between 0 and DIM*DIM
     * @return true if index is in field
     */
    public boolean isField(int index) {
    	if ((0 <= index) && (index < DIM * DIM)) {
    		return true;
    	}
        return false;
    }

    /**
     * Returns true of the (row,col) pair refers to a valid field on the board.
     * @ensures true when both row and col are within the board's bounds
     * @param row - the row value
     * @param col - the column value
     * @return true if row and col  are in range
     */
    public boolean isField(int row, int col) {
    	if ((row < 0) || (col < 0)) {
    		return false;
    	}
    	if ((row >= DIM) || (col >= DIM)) {
    		return false;
    	}
        return isField(index(row, col));
    }
    
    /**
     * Sets the content of field i to the ball b.
     * @requires i to be a valid field
     * @ensures field i to be set to Ball b
     * @param i the field number (see NUMBERING)
     * @param b the ball to be placed
     */
    public void setField(int i, Ball b) {
    	fields[i] = b;
    }

    /**
     * Sets the content of the field represented by the (row,col) pair to the
     * Ball b.
     * @requires (row, col) to be a valid field
     * @ensures field (row, col) to be set to Ball 
     * @param row the field's row
     * @param col the field's column
     * @param b the Ball to be placed
     */
    public void setField(int row, int col, Ball b) {
    	fields[index(row, col)] = b;
    }
    
    /**
     * Returns the content of the field i.
     * @requires i to be a valid field
     * @ensures the result to be either BLUE, YELLOW, RED, ORANGE, PURPLE, GREEN or EMPTY
     * @param i the number of the field (see NUMBERING)
     * @return the Ball on the field
     */
    public Ball getField(int i) {
    	if (isField(i)) {
    		return fields[i];
    	}
        return null;
    }

    /**
     * Returns the content of the field i, j.
     * @requires i, j to be a valid field
     * @ensures the result to be either BLUE, YELLOW, RED, ORANGE, PURPLE, GREEN or EMPTY
     * @param column the column of the field (see NUMBERING)
     * @param row the row of the field (see NUMBERING)
     * @return the Ball on the field
     */
    public Ball getField(int row, int column) {
    	if (isField(row, column)) {
    		return getField(index(row, column));
    	}
        return null;
    }
    
    /**
     * Creates a deep copy of this field.
     * @ensures the result is a new object, so not this object
     * @ensures the values of all fields of the copy match the ones of this Board
     * @return the copy version of the board
     */
    public Board deepCopy() {
    	Board copy = new Board();
    	for (int i = 0; i < DIM * DIM; i++) {
    		copy.setField(i, this.getField(i));

    	}
        return copy;
    }
    
    /**
     * Check if place ball's color in this place is correct or not.
     * @requires row, col to be a valid field
     * @ensures return false if one of the surrounding balls is not EMPTY but has same color;
     * @param row is the row number of field
     * @param col is the column number of field
     * @return true if it is a valid position
     */
    public boolean isValidPosition(int row, int col) {
    	Ball ball = getField(row, col);
    	
    	return isValidPosition(row, col, ball);
    }

    /**
     * Check if the ball's color can be placed in this position or not.
     * @requires row, col to be a valid field
     * @ensures return false if one of the surrounding balls is not EMPTY but has same color
     * @param row is the row number of field
     * @param col is the column number of field
     * @param ball is the ball to be checked
     * @return true if it is a valid position
     */
    public boolean isValidPosition(int row, int col, Ball ball) {
    	Ball compareBall;
    	if (isField(row - 1, col)) {
    		compareBall = getField(row - 1, col);
    		if ((compareBall != Ball.EMPTY) && (compareBall == ball)) {
    			return false;
    		}
    	}
    	if (isField(row, col - 1)) {
    		compareBall = getField(row, col - 1);
    		if ((compareBall != Ball.EMPTY) && (compareBall == ball)) {
    			return false;
    		}
    	}
    	if (isField(row + 1, col)) {
    		compareBall = getField(row + 1, col);
    		if ((compareBall != Ball.EMPTY) && (compareBall == ball)) {
    			return false;
    		}
    	}
    	if (isField(row, col + 1)) {
    		compareBall = getField(row, col + 1);
    		if ((compareBall != Ball.EMPTY) && (compareBall == ball)) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * Initialize a new random board.
     * @requires \forall(getField(index)) == null;
     * @ensures isValidBoard() == true;
     */
    public void initializeNewBoard() {
	
    	int[] countColor = new int[7];
    	int randomRange = 6;
    	setField(24, Ball.EMPTY);
    	for (int i = 0; i < DIM; i++) {
    		for (int j = 0; j < DIM; j++) {
    			if (index(i, j) == 24) {
    				continue;	
    			}
    			int random = (int) (Math.random() * randomRange) + 1;
    			for (int k = 1; k < countColor.length; k++) {
    				if (countColor[k] < 8) {
    					random--;
    				}	
    				if (random == 0) {
    					countColor[k]++;
    					if (countColor[k] == 8) {
    						randomRange--;
    					}
    					setField(index(i, j), Ball.convertTo(k));  					
    					if (!isValidPosition(i, j)) {
    						int swapPosition = findSwapPosition(i, j);
    						Ball temp = getField(i, j);
    						setField(index(i, j), getField(swapPosition));
    						setField(swapPosition, temp);
    					}  					
    					break;
    				}
    			}   			
    		}   		
    	}
    	for (int i = 0; i < DIM; i++) {
    		for (int j = 0; j < DIM; j++) {
    			if (!isValidPosition(i, j)) {
					int swapPosition = findSwapPosition(i, j);
					Ball temp = getField(i, j);
					setField(index(i, j), getField(swapPosition));
					setField(swapPosition, temp);
				}
    		}
    	}
    }

    /**
     * Check the board after moves has any pairs of ball with same color.
     * @ensures return true if \exist !isValidPosition(i,j); 
     * @return return true if find a pair of ball with same color || otherwise false;
     */
    public boolean isCollectableBoard() {
    	for (int i = 0; i < DIM; i++) {
    		for (int j = 0; j < DIM; j++) {
    			if (!isValidPosition(i, j)) {
    				return true;
    			}	
    		}
    	}
    	return false;
    }
    


    /**
     * Find index position that ball in row, col can swap, if not return the current position.
     * @requires isField(row, col)
     * @ensures isField(i,j)
     * @return the index position that ball in row, col can swap
     * @param row of the position needed to swap
     * @param col of the position needed to swap
     */
    public int findSwapPosition(int row, int col) {
    	Ball ball = getField(row, col); 
    	
    	int newRow, newCol;
    	newRow = row;
    	newCol = col;
    	for (int i = 0; i < DIM; i++) {
    		for (int j = 0; j < DIM; j++) {
    			Ball compareBall = getField(i, j);
    			if (compareBall.equals(Ball.EMPTY)) {
    				continue;
    			}
 			    if ((!isValidPosition(i, j)) && (isValidPosition(row, col, compareBall))) {
     				return index(i, j);
 			    }
 			    if (isValidPosition(i, j, ball) && (isValidPosition(row, col, compareBall))) {
 				    newRow = i;
 				    newCol = j;
 			    }
 		    }
     	}
 	    return index(newRow, newCol);
    }

    /**
     * Makes a move on with the given move number.
     * @requires move is a valid move
     * @ensures the move is successfully completed without losing balls
     * @param indice the move you want to make (see toString)
     * @return false if move is not possible cause of a full column or row || true
     */
    public boolean makeSingleMove(int indice) {
    	int row;
    	int col;
        if (moveRtoL(indice) != -1) {
        	row = moveRtoL(indice);
            Queue<Ball> queue = new LinkedList<>();
            for (int i = 0; i < DIM; i++) {
                if (!getField(row, i).equals(Ball.EMPTY)) {
                    queue.add(getField(row, i));
                    setField(index(row, i), Ball.EMPTY);
                }               
            }
            if ((queue.size() == 7) || (queue.isEmpty())) {
            	return false;
            }
            for (int i = 0; i < DIM; i++) {
                setField(index(row, i), queue.poll());
                if (queue.isEmpty()) {
                	return true;
                }
            }
            
        } else if (moveLtoR(indice) != -1) {
        	row = moveLtoR(indice);
            Queue<Ball> queue = new LinkedList<>();
            for (int i = DIM - 1; i >= 0; i--) {
                if (!getField(row, i).equals(Ball.EMPTY)) {
                    queue.add(getField(row, i));
                    setField(index(row, i), Ball.EMPTY);
                }
            }
            if ((queue.size() == 7) || (queue.isEmpty())) {
            	return false;
            }
            for (int i = DIM - 1; i >= 0; i--) {
                setField(index(row, i), queue.poll());
                if (queue.isEmpty()) {
                	return true;
                }
            }
            
        } else if (moveDtoU(indice) != -1) {
        	col = moveDtoU(indice);
            Queue<Ball> queue = new LinkedList<>();
            for (int i = 0; i < DIM; i++) {
                if (!getField(i, col).equals(Ball.EMPTY)) {
                    queue.add(getField(i, col));
                    setField(index(i, col), Ball.EMPTY);
                }
            }
            if ((queue.size() == 7) || (queue.isEmpty())) {
            	return false;
            }
            for (int i = 0; i < DIM; i++) {
                setField(index(i, col), queue.poll());
                if (queue.isEmpty()) {
                	return true;
                }
            }
            
        } else if (moveUtoD(indice) != -1) {
        	col = moveUtoD(indice);
            Queue<Ball> queue = new LinkedList<>();
            for (int i = DIM - 1; i >= 0; i--) {
                if (!getField(i, col).equals(Ball.EMPTY)) {
                    queue.add(getField(i, col));
                    setField(index(i, col), Ball.EMPTY);
                }
            }
            if ((queue.size() == 7) || (queue.isEmpty())) {
            	return false;
            }
            for (int i = DIM - 1; i >= 0; i--) {
                setField(index(i, col), queue.poll());
                if (queue.isEmpty()) {
                	return true;
                }
            }
            
        }
        return false;
    }
    
    /**
     * Makes a double move on with the given move numbers.
     * @requires the moves are valid moves
     * @ensures the move is successfully completed without losing balls
     * @param indice1 the first move you want to make (see toString)
     * @param indice2 the second move you want to make (see toString)
     * @return false if moves are not possible cause of a full collumn or row || true
     */
    public boolean makeDoubleMove(int indice1, int indice2) {
        if (makeSingleMove(indice1) && makeSingleMove(indice2)) {
            return true;
        } else {
            return false;
        }    
    }
    
    /**
     * Calculate the Row number need to move from Left to Right.
     * @requires indice is between 0 and 27
     * @ensures indice is between 13 and 7
     * @return -1 if not a Left to Right move || row number is corresponding to move
     * @param indice - the move number that you want to make (see toString)
     */
    public int moveLtoR(int indice) {
    	if ((indice <= 13) && (indice >= 7)) {
    		return indice % 7;
    	}
        return -1;
    }

    /**
     * Calculate the Row number need to move from Left to Right.
     * @requires indice is between 0 and 27
     * @ensures indice is between 0 and 6
     * @return -1 if not a Right to Left move || row number is corresponding to move
     * @param indice - the move number that you want to make (see toString)
     */
    public int moveRtoL(int indice) {
    	if ((indice <= 6) && (indice >= 0)) {
    		return indice;
    	}
    	return -1;
    }

    /**
     * Calculate the Column number need to move from Up to Down.
     * @requires indice is between 0 and 27
     * @ensures indice is between 21 and 27
     * @return -1 if not a Up to Down move || column number is corresponding to move
     * @param indice - the move number that you want to make (see toString)
     */
    public int moveUtoD(int indice) {
    	if ((indice >= 21) && (indice <= 27)) {
    		return indice % 7;
    	}
    	return -1;
    }

    /**
     * Calculate the Column number need to move from Down to Up.
     * @requires indice is between 0 and 27
     * @ensures indice is between 14 and 20
     * @return -1 if not a Down to Up move || column number is corresponding to move
     * @param indice - the move number that you want to make (see toString)
     */
    public int moveDtoU(int indice) {
    	if ((indice >= 14) && (indice <= 20)) {
    		return indice % 7;
    	}
    	return -1;
    }

    /**
     * Returns a String representation of this board. In addition to the current
     * situation, the String also shows with arrows which number belongs to which move.
     * @requires the board is initialed, so all the indexes contain a value (can be EMPTY)
     * @return the game situation as String
     */
    public String toString() {
        int moveNumber = 0;
        String s = "      ";
        String line = "      ";
        for (int i = 0; i < DIM; i++) {
            if (i < DIM - 1) {
                line = line + "+--------"; 
            } else {
                line = line + "+--------+";
            }
            s = s + "    " + (i + 21) + "   ";
        }
        for (int i = 0; i < 2; i++) {
            s = s + "\n" + "      ";
            for (int j = 0; j < DIM; j++) {
                if (i < 1) {
                    s = s + "    |    ";
                } else {
                    s = s + "    v    ";
                }
            }
        }
        s = s + "\n" + line + "\n";
        for (int i = 0; i < DIM; i++) {
        	String row = "";
        	if (moveNumber + DIM >= 10) {
        		row = (moveNumber + DIM) + "--> |";
        	} else {
        		row = (moveNumber + DIM) + " --> |";
        	}
            for (int j = 0; j < DIM; j++) {
                String ball = getField(i, j).toString();
                if (ball.equals("EMPTY")) {
                	ball = "     ";
                }
                int length = 6 - ball.length();
                String firstSpaces = " ";
                String lastSpaces = " ";
                for (int k = 0; k < (length / 2); k++) {
                    firstSpaces = firstSpaces + " ";
                }
                if (length % 2 == 0) {
                	lastSpaces = firstSpaces;
                } else {
                	lastSpaces = firstSpaces + " ";
                }
                row = row + firstSpaces + ball + lastSpaces;
                if (j < DIM - 1) {
                    row = row + "|";
                }
            }
            row = row + "| <-- " + moveNumber;
            moveNumber++;
            s = s + row + "\n" + line + "\n";
        }
        for (int i = 0; i < 3; i++) {
            s = s + "      ";
            for (int j = 0; j < DIM; j++) {
                if (i == 0) {
                    s = s + "    ^    ";
                } else if (i == 1) {
                    s = s + "    |    ";
                } else {
                    s = s + "    " + (j + 14) + "   ";
                }
            }
            s = s + "\n";
        }
        return s;
    }

    /**
     * Checks if the singleMove is a valid move, so move returns in colecting balls.
     * @ensures the return is false if indice is out of range
     * @param indice the number of the move that you want to make
     * @return true if the given move is a valid move otherwise will return false
     */
    public boolean isValidSingleMove(int indice) {
    	if ((indice < 0) || (indice > 27)) {
    		return false;
    	}
    	Board copyBoard = deepCopy();
    	if ((copyBoard.makeSingleMove(indice)) && (copyBoard.isCollectableBoard())) {
            return true;
        } else {
            return false;
    	}
    }
    
    /**
     * Checks if the doubleMove is a valid move, so double move returns in collecting balls.
     * @ensures the return is false if indice is out of range
     * @param indice1 the number of the first move that you want to make
     * @param indice2 the number of the second move that you want to make
     * @return true if the given move is a valid move otherwise will return false
     */
    public boolean isValidDoubleMove(int indice1, int indice2) {
    	if ((indice1 < 0) || (indice1 > 27)) {
    		return false;
    	}
    	if ((indice2 < 0) || (indice2 > 27)) {
    		return false;
    	}
    	Board copyBoard = deepCopy();
    	if ((copyBoard.makeDoubleMove(indice1, indice2)) && (copyBoard.isCollectableBoard())) {
            return true;
        } else {
            return false;
    	}
    }
    
    /**
     * Checks if there is a singleMove possible, so a move returns in colecting balls.
     * @ensures the return is false if there is no singleMove possible
     * @return true if there is a singleMove possible otherwise false
     */
    public boolean isStillPossibleSingleMove() {
    	for (int indice = 0; indice <= 27; indice++) {
    		if (isValidSingleMove(indice)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Checks if there is a doubleMove possible, so a move returns in collecting balls.
     * @ensures the return is false if there is no doubleMove possible
     * @return true if there is a doubleMove possible otherwise false
     */
    public boolean isStillPossibleDoubleMove() {
    	for (int indice1 = 0; indice1 <= 27; indice1++) {
    		for (int indice2 = 0; indice2 <= 27; indice2++) {
    			if (isValidDoubleMove(indice1, indice2)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    /**
     * Collect all the scored balls after a move.
     * @ensures (isCollectableBoard() == true);
     * @requires \forall(isValidPosition(getField(index)) == true);
     * @return a collectedList of all balls in Invalid Position;
     */
    public List<Ball> collectBall() {
        List<Ball> collectedList = new ArrayList<Ball>();
        boolean[] collectable = new boolean[DIM * DIM];
        for (int i = 0; i < DIM; i++) {
        	for (int j = 0; j < DIM; j++) {
        		if (!isValidPosition(i, j)) {
        			collectable[index(i, j)] = true;
                }
        	}
        }
        
        for (int index = 0; index < DIM * DIM; index++) {
        	if (collectable[index]) {
        		collectedList.add(getField(index));
        		setField(index, Ball.EMPTY);
        	}
        }
        return collectedList;
    }
    
    /**
     * GameOver when there is no possible move.
     * @requires (isStillPossibleSingleMove() == false) and (isStillPossibleDoubleMove() == false)
     * @return true if game is over otherwise false
     */
    public boolean isEndGame() {
    	if (isStillPossibleSingleMove()) {
    		return false;
    	}
    	if (isStillPossibleDoubleMove()) {
    		return false;
    	}
    	return true;
    }

}
