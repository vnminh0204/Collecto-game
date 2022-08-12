package test;

import org.junit.jupiter.api.BeforeEach;
import collectoclient.CollectoClient;
import collectogame.Ball;
import collectogame.Board;
import collectogame.ComputerPlayer;
import collectogame.Game;
import collectogame.NaiveStrategy;
import collectogame.OnlinePlayer;
import collectogame.Player;
import collectogame.SmartStrategy;
import collectogame.Strategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;


public class GameTest {
	
    Player p1;
    OnlinePlayer op2;
    Player cSmart;
    Player cNaive;
    Game game1;
    Game game2;

    @BeforeEach
    public void setUp() {
    	CollectoClient client = new CollectoClient();
        p1 = new OnlinePlayer("Minh", 0, client);
        op2 = new OnlinePlayer("Jesse", 1, client);
        game1 = new Game(p1, op2, client);
        Strategy naive = new NaiveStrategy();
        Strategy smart = new SmartStrategy();
        cSmart = new ComputerPlayer(smart, "BOT1", 0, client);
        cNaive = new ComputerPlayer(naive, "BOT2", 1, client);
        game2 = new Game(cSmart, cNaive, client);
    }
    
    /**
     * test createNewBoard method.
     * check if the created board is valid or not
     * middle position need to be empty
     * there is no invalid position
     * there is at least one single move
     */
    @Test
    public void testcreateNewBoard() {
    	game1.createNewBoard();
    	Board board = game1.getBoard();
    	assertEquals(Ball.EMPTY, board.getField(24));
		for (int i = 0; i < Board.DIM; i++) {
			for (int j = 0; j < Board.DIM; j++) {
				assertTrue(board.isValidPosition(i, j));
			}
		}
		assertTrue(board.isStillPossibleSingleMove());
    }
    
    /**
     * test winner method.
     * check if return null if result is draw
     * check if return correct winner
     * (have more point if same point it will count balls)
     */
    @Test
    public void testWinner() {
    	List<Ball> collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	op2.updateCollection(collectList);
    	op2.calulateScore();
    	assertEquals(null, game1.winner());
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	assertEquals(p1, game1.winner());
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	op2.resetPlayer();
    	op2.updateCollection(collectList);
    	op2.calulateScore();
    	assertEquals(op2, game1.winner());
    	
    	p1.resetPlayer();
    	op2.resetPlayer();
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	op2.updateCollection(collectList);
    	op2.calulateScore();
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	op2.updateCollection(collectList);
    	op2.calulateScore();
    	assertEquals(op2, game1.winner());
    	collectList = new ArrayList<Ball>();
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.BLUE);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	collectList.add(Ball.RED);
    	p1.resetPlayer();
    	p1.updateCollection(collectList);
    	p1.calulateScore();
    	assertEquals(p1, game1.winner());
    }
    
    /**
     * test getCurrentTurn and updateCurrentTurn methods.
     * current turn need to be 0 at the start of the game
     * current turn will be updated to 1 if call updateCurrentTurn.
     */
    @Test
    public void testTurn() {
    	assertEquals(p1, game1.getPlayer(0));
    	assertEquals(op2, game1.getPlayer(1));
    	assertEquals(0, game1.getCurrentTurn());
    	game1.updateCurrentTurn();
    	assertEquals(1, game1.getCurrentTurn());
    }
    
    /**
     * test reset method (for the offline game).
     * check if both player's score and collection are reset to 0
     * check if the currentTurn is reset to 0
     */
    @Test
    public void testReset() {
    	game1.updateCurrentTurn();
    	game1.reset();
    	assertEquals(0, game1.getCurrentTurn());
    	assertEquals(0, game1.getPlayer(0).getScore());
    	assertEquals(0, game1.getPlayer(1).getScore());
    	int[] collection = game1.getPlayer(0).getCollection();
    	for (int i = 0; i < collection.length; i++) {
    		assertEquals(0, collection[i]);
    	}
    	collection = game1.getPlayer(1).getCollection();
    	for (int i = 0; i < collection.length; i++) {
    		assertEquals(0, collection[i]);
    	}
    }
    
    /**
     * test the playOfflinGame method.
     * check if the winner is smart computer at the end game
     */
    @Test
    public void testOfflineGame() {
    	game2.createNewBoard();
    	game2.playOfflineGame();
    	assertTrue(game2.getBoard().isEndGame());
    	assertEquals(cSmart, game2.winner());
    }
    
    /**
     * test updateOnlineBoard method.
     * check if the input board is correctly copied to the game's board
     */
    @Test
    public void testUpdateOnlineBoard() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
    	game1.updateOnlineBoard(board);
    	for (int i = 0; i < 49; i++) {
    		assertEquals(board.getField(i), game1.getBoard().getField(i));
		}
    }
    
    /**
     * check updateOnlineMove method.
     * check if the make online move is correctly applied in the board
     * (in right direct, right row/col and make a change)
     */
    @Test
    public void testmakeOnlineMove() {
    	String testBoard = "5~3~4~2~5~3~6~4~6~3~4~3~1~2~5~3~2~1~2~6~5~4~1~4~0~4~1~4"
    			+ "~5~6~2~1~5~6~2~3~1~5~4~6~5~3~6~3~6~2~1~2~1";
    	String[] splitted = testBoard.split("~");
    	Board board = new Board();
    	for (int i = 0; i < 49; i++) {
			board.setField(i, Ball.convertTo(Integer.valueOf(splitted[i])));
		}
    	game1.updateOnlineBoard(board);
    	int[] choice = new int[1];
    	choice[0] = 17;
    	op2.updateOnlinveMove(choice);
    	game1.makeOnlineMove(1);
    	assertEquals(Ball.EMPTY, game1.getBoard().getField(17));
    	assertEquals(Ball.EMPTY, game1.getBoard().getField(45));
    }
}
