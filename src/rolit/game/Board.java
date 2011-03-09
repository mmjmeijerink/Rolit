package rolit.game;

/**
 * Board for a Rolit game
 */
public class Board {

	public static final int DIM = 8;
	private Ball[] places;

	/**
	 * Creates a new board with dimension DIM*DIM
	 */
	public Board() {
		places = new Ball[DIM * DIM];
	}

	/**
	 * Creates a copy of the board
	 * 
	 * @ensure result.equals(this)
	 */
	public Board deepCopy() {
		Board copy = new Board();

		for (int i = 0; i < places.length; i++) {
			copy.places[i] = this.places[i];
		}

		return copy;
	}

	/**
	 * Gives the index of a mark
	 * 
	 * @require <code>0 <= row && row < DIM && 0 <= col && col < DIM</code>
	 * @return the index of the mark with row <code>row</code> and col <code>col</code>
	 */
	public int index(int row, int col) {
		return DIM * row + col;
	}

	/**
	 * Returns true if <code>i</code> is a valid index of this board
	 * 
	 * @ensure <code>result == (0 <= i && i < DIM*DIM)</code>
	 */
	public boolean isMark(int i) {
		return (0 <= i) && (i < DIM * DIM);
	}

	/**
	 * Returns true if <code>row</code> and <code>col</code> are valid rows and columns on this board
	 * 
	 * @ensure <code>result == (0 <= row && row < DIM && 0 <= col && col < DIM)</code>
	 */
	public boolean isMark(int row, int col) {
		return (0 <= row) && (row < DIM) && (0 <= col) && (col < DIM);
	}

	/**
	 * Returns the contents of the mark with index <code>i</code>
	 * 
	 * @require <code>this.isMark(i)</code>
	 * @ensure <code>result == Ball.EMPTY || result == Ball.RED || result == Ball.GREEN || result == Ball.YELLOW || result == Ball.BLUE</code>
	 */
	public Ball getPlace(int i) {
		return places[i];
	}

	/**
	 * Returns the contents of the mark with row <code>row</code> and column <code>col</code>
	 * 
	 * @require <code>this.isMark(row, col)</code>
	 * @ensure <code>result == Ball.EMPTY || result == Ball.RED || result == Ball.GREEN || result == Ball.YELLOW || result == Ball.BLUE</code>
	 */
	public Ball getPlace(int row, int col) {
		return places[index(row, col)];
	}

	/**
	 * Returns true if mark with index <code>i</code> is empty
	 * 
	 * @require <code>this.isMark(i)</code>
	 * @ensure <code>result == (this.getMark(i) == LEEG)</code>
	 */
	public boolean isEmptyPlace(int i) {
		return getPlace(i) == Ball.EMPTY;
	}

	/**
	 * Return true if mark with row <code>row</code> and column <code>col</code>
	 * is empty
	 * 
	 * @require <code>this.isMark(row, col)</code>
	 * @ensure <code>result == (this.getMark(row, col) == LEEG)</code>
	 */
	public boolean isEmptyPlace(int row, int col) {
		return isEmptyPlace(index(row, col));
	}

	/**
	 * Tests whether or not the whole board is filled
	 * 
	 * @ensure <code>result == for all i: 0 <= i && i < DIM*DIM: 
	 *                                 this.getMark(i) != EMPTY</code>
	 */
	public boolean isFull() {
		boolean full = true;

		for(int i = 0; i < places.length && full; i++) {
			if(!isEmptyPlace(i))
				full = false;
		}

		return full;
	}

	/**
	 * Sets the contents of mark <code>i</code> on color <code>m</code>
	 * 
	 * @require <code>this.isMark(i)</code>
	 *          <code>result == Place.EMPTY || result == Place.RED || result == Place.GREEN || result == Place.YELLOW || result == Place.BLUE</code>
	 * @ensure <code>this.getMark(i) == m</code>
	 */
	public void setPlace(int i, Ball color) {
		places[i] = color;
	}

	/**
	 * Place a Ball in place <code>i</code> with color <code>color</code>
	 * 
	 * @require <code>this.isMark(i)</code>
	 *          <code>result == Place.EMPTY || result == Place.RED || result == Place.GREEN || result == Place.YELLOW || result == Place.BLUE</code>
	 * @ensure <code>this.getPlace(i) == color</code>
	 */
	public void setPlace(int row, int col, Ball color) {
		setPlace(index(row, col), color);
	}
}