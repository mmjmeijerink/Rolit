package rolit.game;

import java.util.*;

/**
 * A game of rolit
 */
public class Game extends Observable {

	private Board board;
	private Player current;

	public Game() {
		board = new Board();
	}

	public Board getBoard() {
		return board;
	}

	/**
	 * Returns the player who's turn it is
	 */
	public Player getCurrent() {
		return current;
	}
	
	/**
	 * Place a ball on the board
	 * 
	 * @require 0 <= i && i < Board.DIM * Board.DIM && this.getBoard().isEmptyPlace(i)
	 */
	public void doMove(int i) {
		board.setColor(i, current.getColor());
		setChanged();
		notifyObservers("Move");
	}
}
