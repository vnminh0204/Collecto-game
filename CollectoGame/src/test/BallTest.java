package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import collectogame.Ball;

public class BallTest {
	/**
	 * The Ball Test test if the ball is correct converted to its defined integer and vice versa.
	 */
    @Test
    public void testConvertTo() {
        assertEquals(Ball.EMPTY, Ball.convertTo(0));
        assertEquals(Ball.BLUE, Ball.convertTo(1));
        assertEquals(Ball.YELLOW, Ball.convertTo(2));
        assertEquals(Ball.RED, Ball.convertTo(3));
        assertEquals(Ball.ORANGE, Ball.convertTo(4));
        assertEquals(Ball.PURPLE, Ball.convertTo(5));
        assertEquals(Ball.GREEN, Ball.convertTo(6));
        assertEquals(0, Ball.EMPTY.convertTo());
        assertEquals(1, Ball.BLUE.convertTo());
        assertEquals(2, Ball.YELLOW.convertTo());
        assertEquals(3, Ball.RED.convertTo());
        assertEquals(4, Ball.ORANGE.convertTo());
        assertEquals(5, Ball.PURPLE.convertTo());
        assertEquals(6, Ball.GREEN.convertTo());
    }
}
