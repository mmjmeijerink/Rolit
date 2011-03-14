package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;

	public Game(ArrayList<Gamer> gamers) {
		board = new Board();
	}
	
	public boolean doMove(int i, Gamer aGamer) {
		boolean result = false;
		if(aGamer == current &&
		   board.checkMove(i, aGamer.getColor())) {
			
			setChanged();
			notifyObservers("Move");
		}
		return result;
		//board.setColor(i, current.getColor());
		
	}
	
	
	
	// Setters en getters
	
	public Board getBoard() {
		return board;
	}

	public Gamer getCurrent() {
		return current;
	}
}
