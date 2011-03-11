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
	 * Levert de Mark op die aan de beurt is.
	 */
	public Mark getHuidig() {
		return huidig;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Reset dit spel. <br>
	 * Het speelbord wordt weer leeggemaakt en Mark.XX wordt de huidige Mark.
	 */
	public void reset() {
		huidig = Mark.XX;

		bord.reset();
		setChanged();
		notifyObservers();
	}

	/**
	 * Zet de huidige mark in het vakje i. Geef de beurt aan de andere Mark.
	 * 
	 * @require 0<=i && i <Bord.DIM*Bord.DIM && this.getBord().isLeegVakje(i)
	 * @param i
	 *            de index waar de mark zal worden gezet.
	 */
	public void doeZet(int i) {
		bord.setVakje(i, huidig);
		setChanged();
		notifyObservers();
		huidig = huidig.other();
	}
}
