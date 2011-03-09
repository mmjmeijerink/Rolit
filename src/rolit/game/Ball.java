package rolit.game;

/**
 * Represents a Ball for a game of Rolit
 */
public class Ball {
	
	public static final int EMPTY = 0;
	public static final int RED = 1;
	public static final int GREEN = 2;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	private int color;
	
	/**
	 * Create a new Ball with color <code>color</code>
	 */
	private Ball(int color) { 
		this.color = color;
	}
	
	public int getColor() {
		return this.color;
	}
}