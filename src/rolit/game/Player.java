package rolit.game;

import java.awt.Color;

public class Player {
	
	private String name;
	private Color color;
	
	/**
	 * Creates a color
	 * 
	 * @require <code>name != null</code>
	 * 			<code>color == Place.RED || color == Place.GREEN || color == Place.BLUE || color == Place.YELLOW</code>
	 */
	public Player(String name, Color color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
}