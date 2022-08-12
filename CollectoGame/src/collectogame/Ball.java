package collectogame;

public enum Ball {
    
    BLUE, YELLOW, RED, ORANGE, PURPLE, GREEN, EMPTY;

    /**
     * Converts Ball color to the integer corresponding to it.
     * @ensures The right integer is returned for the ball color. 
     * @return The number value of Ball corresponding to its color
     */
    public int convertTo() {
        if (this == BLUE) {
            return 1;
        } else if (this == YELLOW) {
            return 2;
        } else if (this == RED) {
            return 3;
        } else if (this == ORANGE) {
            return 4;
        } else if (this == PURPLE) {
            return 5;
        } else if (this == GREEN) {
            return 6;
        } else {
            return 0;
        }
    }
    
    /**
     * Converts the integer number to Ball color corresponding to it.
     * @ensures The right ball color is returned for the integer. 
     * @param x - the integer number will be converted to Ball
     * @return The number of its color
     */
    public static Ball convertTo(int x) {
        if (x == 1) {
            return Ball.BLUE;
        } else if (x == 2) {
            return Ball.YELLOW;
        } else if (x == 3) {
            return Ball.RED;
        } else if (x == 4) {
            return Ball.ORANGE;
        } else if (x == 5) {
            return Ball.PURPLE;
        } else if (x == 6) {
            return Ball.GREEN;
        } else {
            return Ball.EMPTY;
        }
    }
}
