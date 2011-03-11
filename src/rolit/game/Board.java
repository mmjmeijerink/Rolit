package rolit.game;

import java.awt.Color;

/**
 * Board for a Rolit game
 */
public class Board {

	public static final int DIM = 8;
	private Place[] places;

	/**
	 * Creates a new board with dimension DIM*DIM
	 */
	public Board() {
		places = new Place[DIM * DIM];
	}

	/**
	 * Creates a copy of the board
	 * 
	 * @ensure color.equals(this)
	 */
	public Board deepCopy() {
		Board copy = new Board();

		for (int i = 0; i < places.length; i++) {
			copy.places[i] = this.places[i];
		}

		return copy;
	}

	/**
	 * Gives the index of the Place with row <code>row</code> and column <code>col</code>
	 * 
	 * @require <code>0 <= row && row < DIM && 0 <= col && col < DIM</code>
	 * @ensure <code>this.isPlace(color) == true</code>
	 */
	public int index(int row, int col) {
		return DIM * row + col;
	}

	/**
	 * Returns true if <code>i</code> is a valid index of this board
	 * 
	 * @ensure <code>color == (0 <= i && i < DIM*DIM)</code>
	 */
	public boolean isPlace(int i) {
		return (0 <= i) && (i < DIM * DIM);
	}

	/**
	 * Returns true if <code>row</code> and <code>col</code> are valid rows and columns on this board
	 * 
	 * @ensure <code>color == (0 <= row && row < DIM && 0 <= col && col < DIM)</code>
	 */
	public boolean isPlace(int row, int col) {
		return (0 <= row) && (row < DIM) && (0 <= col) && (col < DIM);
	}

	/**
	 * Returns the contents of the Place with index <code>i</code>
	 * 
	 * @require <code>this.isPlace(i)</code>
	 * @ensure <code>color == Place.EMPTY || color == Place.RED || color == Place.GREEN || color == Place.YELLOW || color == Place.BLUE</code>
	 */
	public Place getPlace(int i) {
		return places[i];
	}

	/**
	 * Returns the contents of the Place with row <code>row</code> and column <code>col</code>
	 * 
	 * @require <code>this.isPlace(row, col)</code>
	 * @ensure <code>color == Board.EMPTY || color == Board.RED || color == Board.GREEN || color == Board.YELLOW || color == Board.BLUE</code>
	 */
	public Place getPlace(int row, int col) {
		return places[index(row, col)];
	}

	/**
	 * Returns true if Place with index <code>i</code> is empty
	 * 
	 * @require <code>this.isPlace(i)</code>
	 * @ensure <code>color == (this.getPlace(i) == Place.EMPTY)</code>
	 */
	public boolean isEmptyPlace(int i) {
		return getPlace(i).getColor() == Place.EMPTY;
	}

	/**
	 * Return true if Place with row <code>row</code> and column <code>col</code> is empty
	 * 
	 * @require <code>this.isPlace(row, col)</code>
	 * @ensure <code>color == (this.getPlace(row, col) == Place.EMPTY)</code>
	 */
	public boolean isEmptyPlace(int row, int col) {
		return isEmptyPlace(index(row, col));
	}

	/**
	 * Tests whether or not the whole board is filled
	 * 
	 * @ensure <code>color == for all i: 0 <= i && i < DIM*DIM: 
	 *                                 !this.isEmptyPlace(i)</code>
	 */
	public boolean isFull() {
		boolean full = true;

		for(int i = 0; i < places.length && full; i++) {
			if(isEmptyPlace(i))
				full = false;
		}

		return full;
	}

	/**
	 * Sets the contents of Place <code>i</code> on color <code>color</code>
	 * 
	 * @require <code>this.isPlace(i)</code>
	 *          <code>color == Place.EMPTY || color == Place.RED || color == Place.GREEN || color == Place.YELLOW || color == Place.BLUE</code>
	 * @ensure <code>this.getPlace(i) == color</code>
	 */
	public void setPlace(int i, Color color) {
		places[i].setColor(color);
	}

	/**
	 * Sets the contents of Place <code>i</code> with color <code>color</code>
	 * 
	 * @require <code>this.isPlace(row, col)</code><br>
	 *          <code>color == Place.EMPTY || color == Place.RED || color == Place.GREEN || color == Place.YELLOW || color == Place.BLUE</code>
	 * @ensure <code>this.getPlace(i) == color</code>
	 */
	public void setPlace(int row, int col, Color color) {
		setPlace(index(row, col), color);
	}
}