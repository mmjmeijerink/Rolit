package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;

	public Game(ArrayList<Gamer> aGamers) {
		board = new Board();
		gamers = aGamers;
		for(int i = 0; i > gamers.size(); i++) {
			gamers.get(i).setColor(i+1);
		}
		current = gamers.get(0);
	}
	
	public boolean doMove(int i, Gamer aGamer) {
		boolean result = false;
		if(aGamer == current &&
		   board.checkMove(i, aGamer.getColor())) {
			board.doMove(i, aGamer.getColor());
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
