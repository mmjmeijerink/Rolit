package rolit.game;

import java.awt.Color;

public class Place {
	
	public static final Color EMPTY = new Color(238, 238, 238);
	public static final Color RED = new Color(255, 0, 0);
	public static final Color GREEN = new Color(0, 255, 0);
	public static final Color BLUE = new Color(0, 0, 255);
	public static final Color YELLOW = new Color(255, 255, 0);
	
	private int index;
	private Color color;
	
	public Place(int index) {
		this.index = index;
		this.color = Place.EMPTY;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}