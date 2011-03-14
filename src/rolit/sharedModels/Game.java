package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;
	private boolean ended;

	public Game(ArrayList<Gamer> aGamers) {
		board = new Board();
		gamers = aGamers;
		for(int i = 0; i < gamers.size(); i++) {
			gamers.get(i).setColor(i+1);
		}
		current = gamers.get(0);
	}
	
	public boolean doMove(int i, Gamer aGamer) {
		boolean result = false;
		if(aGamer == current && board.checkMove(i, aGamer.getColor())) {
			board.doMove(i, aGamer.getColor());
			checkIfEnded();
			nextTurn();
			setChanged();
			notifyObservers("Move");
			result = true;
		}
		return result;
	}
	
	private void checkIfEnded() {
		if(board.isFull() || gamers.size() < 2) {
			ended = true;
		}
	}
	
	public boolean isEnded() {
		return ended;
	}
	
	public void removeGamer(Gamer toBeRemoved) {
		if(current == toBeRemoved) {
			nextTurn();
		}
		gamers.remove(toBeRemoved);
		checkIfEnded();
		setChanged();
		notifyObservers("Gamer Removed");
	}
	
	private void nextTurn() {
		int indexOfCurrent = gamers.indexOf(current);
		int indexOfNext = indexOfCurrent + 1;
		if(indexOfNext >= gamers.size()) {
			indexOfNext = 0;
		}
		current = gamers.get(indexOfNext);
	}
	
	
	
	// Setters en getters
	
	public Board getBoard() {
		return board;
	}

	public Gamer getCurrent() {
		return current;
	}
	
	public ArrayList<Gamer> getGamers() {
		return gamers;
	}
	
	public String toString() {
		String result = "Game with: ";
		for(Gamer aGamer: gamers) {
			result = result + aGamer.toString() + " ";
		}
		return result;
	}

}
