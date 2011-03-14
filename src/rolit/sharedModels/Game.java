package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;

	public Game(ArrayList<Gamer> gamers) {
		board = new Board();
	}
	
	public void doMove(int i, Gamer aGamer) {
		//board.setColor(i, current.getColor());
		setChanged();
		notifyObservers("Move");
	}
	
	public void checkMove(int i, Gamer aGamer) {
		
	}
	
	// Setters en getters
	
	public Board getBoard() {
		return board;
	}

	public Gamer getCurrent() {
		return current;
	}
}
