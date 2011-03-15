package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;

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
			nextTurn();
			setChanged();
			notifyObservers("Move");
			result = true;
		}
		return result;
	}
	
	public int getPointsOf(Gamer aGamer) {
		int result = 0;
		for(Gamer check: gamers) {
			if (check == aGamer) {
				result = board.getPointsOfColor(aGamer.getColor());
			}
		}
		return result;
	}
	
	public boolean isEnded() {
		boolean result = false;
		if(board.isFull() || gamers.size() < 2) {
			result = true;
		}
		return result;
	}
	
	public void removeGamer(Gamer toBeRemoved) {
		if(current == toBeRemoved) {
			nextTurn();
		}
		gamers.remove(toBeRemoved);
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
	
	//Getters and Setters
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