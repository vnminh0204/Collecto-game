package test;

import static collectogame.Board.DIM;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import collectogame.Ball;
import collectogame.Board;



public class BoardTest {
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    /**
     * test index method.
     * check if the row and col number is converted to correct index value.
     */
    @Test
    public void testIndex() {
        assertEquals(0, board.index(0, 0));
        assertEquals(1, board.index(0, 1));
        assertEquals(7, board.index(1, 0));
        assertEquals(16, board.index(2, 2));
    }

    /**
     * test isField method.
     * check if the index value is correctly checked in range of the board.
     */
    @Test
    public void testIsFieldIndex() {
        assertFalse(board.isField(-1));
        assertTrue(board.isField(0));
        assertTrue(board.isField(8));
        assertFalse(board.isField(49));
    }

    /**
     * test isField method 
     * check if the row and column value is correctly checked in range of the board.
     */
    @Test
    public void testIsFieldRowCol() {
        assertFalse(board.isField(-1, 0));
        assertFalse(board.isField(0, -1));
        assertTrue(board.isField(0, 0));
        assertTrue(board.isField(2, 2));
        assertFalse(board.isField(7, 3));
        assertFalse(board.isField(3, 7));
    }

    /**
     * Test setField and get Field method.
     * Test if the ball is set and get correct field's value.
     */
    @Test
    public void testSetAndGetFieldIndex() {
        board.setField(0, Ball.BLUE);
        assertEquals(Ball.BLUE, board.getField(0));
        assertEquals(null, board.getField(-1));
        assertEquals(null, board.getField(8, 8));
    }

    /**
     * Test setField and get Field method.
     * if the ball is set and get correct field's value.
     */
    @SuppressWarnings("static-access")
	@Test
    public void testSetFieldRowCol() {
        board.setField(0, 0, Ball.RED);
        assertEquals(Ball.RED, board.getField(0));
        board.setField(board.DIM - 1, board.DIM - 1, Ball.RED);
        assertEquals(Ball.RED, board.getField(DIM * board.DIM - 1));
    }
    
    /**
     * Test deepCopyBoard method if copyboard is indead a deep copy version of the board.
     * (Only if when you make change in copyboard is not apllied to current board.)
     */
    @Test
    public void testDeepCopy() {
        board.setField(0, Ball.BLUE);
        Board deepCopyBoard = board.deepCopy();
        deepCopyBoard.setField(0, Ball.RED);

        assertEquals(Ball.BLUE, board.getField(0));
        assertEquals(Ball.RED, deepCopyBoard.getField(0));
    }
    
    /**
     * test isValidPosition method.
     * The position is correct if there are no surrounding balls have same color.
     */
    @Test
    public void testisValidPosition() {
    	assertTrue(board.isValidPosition(3, 3));
    	board.setField(0, Ball.BLUE);
    	board.setField(1, Ball.BLUE);
    	board.setField(2, Ball.BLUE);
    	board.setField(7, Ball.BLUE);
    	board.setField(8, Ball.YELLOW);
        assertFalse(board.isValidPosition(0, 1));
        assertTrue(board.isValidPosition(0, 0, Ball.RED));
        assertFalse(board.isValidPosition(0, 1, Ball.YELLOW));
    }
    
    /**
     * test isCollectableBoard method.
     * True if board have 2 balls with same color are adjacent.
     */
    @Test
    public void testCollectableBoard() {
        assertFalse(board.isCollectableBoard());
    	board.setField(1, Ball.BLUE);
    	board.setField(2, Ball.BLUE);
    	board.setField(7, Ball.BLUE);
        assertTrue(board.isCollectableBoard());
    }
    
    /**
     * test initializeNewBoard method.
     * true if the middle of board is empty
     * and no same colors ball are adjecent.
     */
    //check if board has a possible move will be tested in game
    @Test
    public void testNewInitializedBoard() {
    	for (int test = 0; test < 10000; test++) {
    		board.initializeNewBoard();
    		assertEquals(Ball.EMPTY, board.getField(24));
    		for (int i = 0; i < Board.DIM; i++) {
    			for (int j = 0; j < Board.DIM; j++) {
    				assertTrue(board.isValidPosition(i, j));
    			}
    		}
    	}
    }

    /**
     * test findSwapPosition method.
     * Return the position that the invalid ball can swap
     */
    @Test
    public void testfindSwapPosition() {
    	board.setField(0, Ball.BLUE);
    	board.setField(1, Ball.BLUE);
    	board.setField(2, Ball.BLUE);
    	board.setField(7, Ball.BLUE);
    	board.setField(8, Ball.YELLOW);
        assertEquals(8, board.findSwapPosition(1, 1));
    	board.setField(1, Ball.YELLOW);
    	board.setField(7, Ball.YELLOW);
    	assertEquals(8, board.findSwapPosition(1, 1));
    	board.setField(4, Ball.BLUE);
    	assertEquals(4, board.findSwapPosition(1, 1));
        
    }

    /**
     * test the moveLtoR, moveRtoL, moveUtoD, moveDtoU methods.
     * These method are expected to 
     * return correct column, row number where the move's indice will be applied
     */
    @Test
    public void testConvertMoveIndiceToCorrespondingRowandCol() {
    	assertEquals(4, board.moveLtoR(11));
    	assertEquals(6, board.moveRtoL(6));
    	assertEquals(2, board.moveUtoD(23));
    	assertEquals(3, board.moveDtoU(17));
    	assertEquals(-1, board.moveRtoL(11));
    	assertEquals(-1, board.moveDtoU(6));
    	assertEquals(-1, board.moveLtoR(23));
    	assertEquals(-1, board.moveUtoD(17));
    	assertEquals(-1, board.moveUtoD(29));
    }
    
    /**
     * test makeSingleMove method.
     * test if with the provided indice
     *  method makeSingleMove make correct change in the board 
     *  (all the balls are moved in correct row and direction)
     */
    @Test
    public void testSingleMove() {
    	assertFalse(board.makeSingleMove(0));
    	board.setField(0, Ball.BLUE);
    	board.setField(6, Ball.BLUE);
    	board.setField(5, Ball.BLUE);
    	board.makeSingleMove(0);
    	assertEquals(Ball.BLUE, board.getField(0));
    	assertEquals(Ball.BLUE, board.getField(1));
    	assertEquals(Ball.BLUE, board.getField(2));
    	assertEquals(Ball.EMPTY, board.getField(5));
    	assertEquals(Ball.EMPTY, board.getField(6));
    	assertEquals(false, board.makeSingleMove(-1));
    	for (int i = 0; i < 49; i++) {
    		board.setField(i, Ball.BLUE);
    	}
    	assertFalse(board.makeSingleMove(0));
    	assertFalse(board.makeSingleMove(7));
    	for (int i = 0; i < 49; i++) {
    		board.setField(i, Ball.BLUE);
    	}
    	assertFalse(board.makeSingleMove(7));
    	assertFalse(board.makeSingleMove(4));
    	for (int i = 0; i < 49; i++) {
    		board.setField(i, Ball.BLUE);
    	}
    	assertFalse(board.makeSingleMove(27));
    	assertFalse(board.makeSingleMove(14));
    	for (int i = 0; i < 49; i++) {
    		board.setField(i, Ball.BLUE);
    	}
    	assertFalse(board.makeSingleMove(14));
    	assertFalse(board.makeSingleMove(27));
    }
    
    /**
     * test isValidSingleMove method.
     * check if the move's indice is correct or not.
     * check if after the move if the board can be collected or not.
     */
    @Test
    public void testisValidSingleMove() {
    	board.setField(0, Ball.BLUE);
    	board.setField(6, Ball.BLUE);
    	board.setField(42, Ball.YELLOW);
    	assertTrue(board.isValidSingleMove(0));
    	assertFalse(board.isValidSingleMove(1));
    	assertFalse(board.isValidSingleMove(14));
    	assertFalse(board.isValidSingleMove(28));
    	assertFalse(board.isValidSingleMove(-1));
    }
    
    /**
     * test toString method.
     * check if the current situation of the board is correctly present to user.
     */
    @Test
    public void testtoString() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
    	String ans = board.toString();
    	assertThat(ans, containsString("10--> | ORANGE |  BLUE  | ORANGE |      "
    			+ "  | ORANGE |  BLUE  | ORANGE | <-- 3"));
    }
    
    /**
     * test makeDoubleMove method.
     * test if with the provided move indices
     *  method makeDoubleMove make correct change in the board 
     *  (all the balls are moved in correct row and direction)
     */
    @Test
    public void testDoubleMove() {
    	board.setField(0, Ball.BLUE);
    	board.setField(6, Ball.BLUE);
    	board.setField(42, Ball.BLUE);
    	board.makeDoubleMove(0, 14);
    	assertEquals(Ball.BLUE, board.getField(0));
    	assertEquals(Ball.BLUE, board.getField(1));
    	assertEquals(Ball.BLUE, board.getField(7));
    	assertEquals(Ball.EMPTY, board.getField(42));
    	assertEquals(Ball.EMPTY, board.getField(6));
    	assertEquals(false, board.makeDoubleMove(-1, -1));
    }
    
    /**
     * test isValidDoubleMove method.
     * check if the move's indice is correct or not.
     * check if after the move if the board can be collected or not.
     */
    @Test
    public void testisValidDoubleMove() {
    	board.setField(0, Ball.BLUE);
    	board.setField(47, Ball.BLUE);
    	assertTrue(board.isValidDoubleMove(7, 19));
    	assertFalse(board.isValidDoubleMove(0, 6));
    	assertFalse(board.isValidDoubleMove(28, 28));
    	assertFalse(board.isValidDoubleMove(20, 28));
    	assertFalse(board.isValidDoubleMove(-1, -1));
    	assertFalse(board.isValidDoubleMove(20, -1));
    }

    /**
     * test isStillPossibleSingleMove method.
     * check if the method is cover all the possible single moves in board
     * if no possible single it will return false 
     */
    @Test
    public void testisStillPossibleSingleMove() {
    	board.setField(0, Ball.BLUE);
    	board.setField(47, Ball.BLUE);
        assertFalse(board.isStillPossibleSingleMove());
        board.setField(14, Ball.BLUE);
        assertTrue(board.isStillPossibleSingleMove());
    }
    
    /**
     * test isStillPossibleDoubleMove method.
     * check if the method is cover all the possible double moves in board
     * if no possible single it will return false 
     */
    @Test
    public void testisStillPossibleDoubleMove() {
    	board.setField(0, Ball.BLUE);
    	board.setField(5, Ball.YELLOW);
    	assertFalse(board.isStillPossibleDoubleMove());
    	board.setField(47, Ball.BLUE);
    	assertTrue(board.isStillPossibleDoubleMove());        
    }
    
    /**
     * test collectBall method.
     * check if all invalid balls is actually removed from board or not.
     */
    @Test
    public void testcollectBall() {
    	board.setField(0, Ball.BLUE);
    	board.setField(1, Ball.YELLOW);
    	board.setField(2, Ball.BLUE);
    	board.setField(3, Ball.BLUE);
    	board.setField(7, Ball.YELLOW);
    	board.setField(8, Ball.YELLOW);
    	board.collectBall();
    	assertEquals(Ball.EMPTY, board.getField(1));     
    	assertEquals(Ball.EMPTY, board.getField(2)); 
    	assertEquals(Ball.EMPTY, board.getField(3)); 
    	assertEquals(Ball.EMPTY, board.getField(7)); 
    	assertEquals(Ball.EMPTY, board.getField(8)); 
    	assertEquals(Ball.BLUE, board.getField(0));
    }
    
    /**
     * test isEndGame method.
     * test if are there any possible single moves or double moves in board
     * if no it will return true
     */
    @Test
    public void testisEndGame() {
    	board.setField(0, Ball.BLUE);
    	board.setField(5, Ball.YELLOW);
    	assertTrue(board.isEndGame()); 
    	board.setField(47, Ball.BLUE);
    	assertFalse(board.isEndGame());
    	board.setField(2, Ball.BLUE);
    	assertFalse(board.isEndGame());
    }

}

