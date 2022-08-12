package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.ArrayList;
import java.util.List;
import collectogame.Ball;
import collectogame.Board;
import collectogame.ComputerPlayer;
import collectogame.NaiveStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import collectoclient.CollectoClient;
import collectogame.OnlinePlayer;
import collectogame.Player;
import collectogame.SmartStrategy;
import collectogame.Strategy;

public class PlayerandStrategyTest {
    
    Player p1;
    OnlinePlayer op2;
    Player cSmart;
    Player cNaive;

    @BeforeEach
    public void setUp() {
    	CollectoClient client = new CollectoClient();
        p1 = new OnlinePlayer("Minh", 0, client);
        op2 = new OnlinePlayer("Jesse", 1, client);
        Strategy naive = new NaiveStrategy();
        Strategy smart = new SmartStrategy();
        cSmart = new ComputerPlayer(smart, "BOT1", 0, client);
        cNaive = new ComputerPlayer(naive, "BOT2", 1, client);
    }

    /**
     * test getName method.
     * check if the correct name is saved in the game's data
     */
    @Test
    public void testgetName() {
    	assertEquals("Minh", p1.getName());
    	assertEquals("Jesse", op2.getName());
    	assertEquals("Smart BOT - BOT1", cSmart.getName());
    	assertEquals("Naive BOT - BOT2", cNaive.getName());
    }
    
    /**
     * test getRole method.
     * check if the correct turn(role) of player is saved in the game's data
     */
    @Test
    public void testgetRole() {
    	assertEquals(0, p1.getRole());
    	assertEquals(1, op2.getRole());
    }
    
    /**
     * test updateCollection method.
     * check if player update the correct number of ball each type from the collectList.
     */
    @Test
    public void testupdateCollection() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	p1.updateCollection(collectList);
    	int[] collection = p1.getCollection();
    	assertEquals(2, collection[Ball.BLUE.convertTo()]);
    	assertEquals(1, collection[Ball.RED.convertTo()]);
    	assertEquals(0, collection[Ball.GREEN.convertTo()]);
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.YELLOW);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	p1.updateCollection(collectList);
    	assertEquals(3, collection[Ball.BLUE.convertTo()]);
    	assertEquals(2, collection[Ball.RED.convertTo()]);
    	assertEquals(1, collection[Ball.YELLOW.convertTo()]);
    }
    
    /**
     * test calculateScore method.
     * check if the score of player is correct calculated based on his collection.
     */
    @Test
    public void testcalulateScore() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.YELLOW);
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	assertEquals(2, p1.getScore());
    }
    
    /**
     * test getNumBall method.
     * check if the number of balls that player has is correct.
     */
    @Test
    public void testgetNumBall() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.YELLOW);
    	p1.updateCollection(collectList);
    	assertEquals(7, p1.getNumBall());
    }
    
    /**
     * test resetPlayer method.
     * check if the player's score and collection is reset to 0.
     */
    @Test
    public void testresetPlayer() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.YELLOW);
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	p1.resetPlayer();
    	assertEquals(0, p1.getScore());
    	int[] collection = p1.getCollection();
    	for (int i = 0; i < collection.length; i++) {
    		assertEquals(0, collection[i]);
    	}
    }
    
    /**
     * test displayBall method.
     * check if the collection information of player is displayed correctly
     */
    @Test
    public void testdisplayBall() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	p1.updateCollection(collectList);
    	String ans = p1.displayBall();
    	assertThat(ans, containsString("BLUE: 2"));
    	assertThat(ans, containsString("RED: 1"));
    	assertThat(ans, containsString("YELLOW: 0"));
    }
    
    /**
     * test updateOnlineCollection method.
     * check if player update the correct number of ball each type from the onlinePlayer.
     */
    @Test
    public void testupdateOnlineCollection() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	op2.updateCollection(collectList);
    	p1.updateOnlineCollection(op2);
    	int[] collection = p1.getCollection();
    	assertEquals(2, collection[Ball.BLUE.convertTo()]);
    	assertEquals(1, collection[Ball.RED.convertTo()]);
    	assertEquals(0, collection[Ball.GREEN.convertTo()]);
    }
    
    /**
     * test makeMove method.
     * check if the move is correctly applied on the board
     * and the list of collected ball after the is correctly gained for player  
     */
    @Test
    public void testmakeMove() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}

    	int[] choice = new int[1];
    	choice[0] = 17;
    	op2.updateOnlinveMove(choice);
    	op2.makeMove(board);

    	int[] collection = op2.getCollection();
    	assertEquals(2, collection[Ball.BLUE.convertTo()]);
    	
    	op2.resetPlayer();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.EMPTY);
		}
    	board.setField(8, Ball.BLUE);
    	board.setField(5, Ball.BLUE);
    	
    	choice = new int[2];
    	choice[0] = 15;
    	choice[1] = 0;
    	op2.updateOnlinveMove(choice);
    	op2.makeMove(board);
    	assertEquals(2, collection[Ball.BLUE.convertTo()]);
    }
    
    /**
     * test OnlinePlayer.
     * check if with provided move indice (for single and double move)
     * is correctly applied to the board
     * (in right direction, column/row and make right change in the board)
     */
    @Test
    public void testOnlinePlayer() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}

    	int[] choice = new int[1];
    	choice[0] = 17;
    	op2.updateOnlinveMove(choice);
    	int[] move = op2.determineMove(board);
    	assertEquals(choice.length, move.length);
    	for (int i = 0; i < choice.length; i++) {
    		assertEquals(choice[i], move[i]);
    	}
    	choice = new int[2];
    	choice[0] = 17;
    	choice[1] = 23;
    	op2.updateOnlinveMove(choice);
    	move = op2.determineMove(board);
    	assertEquals(choice.length, move.length);
    	for (int i = 0; i < choice.length; i++) {
    		assertEquals(choice[i], move[i]);
    	}
    }
    
    /**
     * test Naive Strategy.
     * check if return a valid single and double move
     */
    @Test
    public void testNaiveComputerPlayer() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}

    	int[] choice = cNaive.determineMove(board);
    	assertEquals(1, choice.length);
    	int move1 = choice[0];
    	assertTrue(board.isValidSingleMove(move1));
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.EMPTY);
		}
    	choice = cNaive.determineMove(board);
    	assertEquals(0, choice.length);
    	board.setField(8, Ball.BLUE);
    	board.setField(5, Ball.BLUE);

    	choice = cNaive.determineMove(board);
    	assertEquals(2, choice.length);
    	assertTrue(board.isValidDoubleMove(choice[0], choice[1]));
    }
    
    /**
     * test Smart Strategy.
     * check if return a valid single and double move
     * check if return a best single and double move with the game's situation
     */
    @Test
    public void testSmartComputerPlayer() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}

    	int[] choice = cSmart.determineMove(board);
    	assertEquals(1, choice.length);
    	int move1 = choice[0];
    	assertTrue(board.isValidSingleMove(move1));
    	assertEquals(17, move1);
    	
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.EMPTY);
		}
    	choice = cSmart.determineMove(board);
    	assertEquals(0, choice.length);
    	board.setField(8, Ball.BLUE);
    	board.setField(5, Ball.BLUE);

    	choice = cSmart.determineMove(board);
    	assertEquals(2, choice.length);
    }
}
