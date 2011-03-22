package rolit.sharedModels;

import java.util.*;

public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;
	private ArrayList<Gamer> startedWith;

	@SuppressWarnings("unchecked")
	public Game(ArrayList<Gamer> aGamers) {
		board = new Board();
		gamers = aGamers;
		
		/*
		 * Deze if else structuur staat hier omdat de volgorde van de kleuren soms wat vreemd is.
		 * Bij twee spelers eerst rood dan groen
		 * Bij drie spelers eerst rood dan geel dan groen (in tegenstelling tot rood dan groen dan geel)
		 * Bij vier spelers eerst rood dan geel dan groen dan blauw.
		 */
		if(aGamers.size() == 2) {
			gamers.get(0).setColor(Slot.RED);
			gamers.get(1).setColor(Slot.GREEN);
		} else if (aGamers.size() == 3) {
			gamers.get(0).setColor(Slot.RED);
			gamers.get(1).setColor(Slot.YELLOW);
			gamers.get(2).setColor(Slot.GREEN);
		} else if (aGamers.size() == 4) {
			gamers.get(0).setColor(Slot.RED);
			gamers.get(1).setColor(Slot.YELLOW);
			gamers.get(2).setColor(Slot.GREEN);
			gamers.get(3).setColor(Slot.BLUE);
		}
		
		current = gamers.get(0);
		startedWith = (ArrayList<Gamer>) gamers.clone();
	}
	
	/**
	 * 
	 * @param i
	 * @param aGamer
	 * @return
	 */
	public boolean checkMove(int i, Gamer aGamer) {
		boolean result = false;
		/*
		 * De gamer moet wel aan de beurt zijn voor het positief terug geven van deze methoden 
		 */
		if(aGamer == current && board.checkMove(i, aGamer.getColor())) {
			result = true;
		}
		return result;
	}
	
	/**
	 * 
	 * @param i
	 * @param aGamer
	 */
	public void doMove(int i, Gamer aGamer) {
		if(checkMove(i,aGamer)) {
			board.doMove(i, aGamer.getColor());
			nextTurn();
			setChanged();
			notifyObservers("Move");
		}
	}
	
	/**
	 * 
	 * @param aGamer
	 * @return
	 */
	public int getPointsOf(Gamer aGamer) {
		int result = 0;
		
		for(Gamer check: gamers) {
			if (check == aGamer) {
				result = board.getPointsOfColor(aGamer.getColor());
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEnded() {
		boolean result = false;
		
		if(board.isFull() || gamers.size() < 2) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param toBeRemoved
	 */
	public void removeGamer(Gamer toBeRemoved) {
		if(current == toBeRemoved) {
			nextTurn();
		}
		
		gamers.remove(toBeRemoved);
		setChanged();
		notifyObservers("Gamer Removed");
	}
	
	/**
	 * 
	 */
	private void nextTurn() {
		int indexOfCurrent = gamers.indexOf(current);
		int indexOfNext = indexOfCurrent + 1;
		
		if(indexOfNext >= gamers.size()) {
			indexOfNext = 0;
		}
		current = gamers.get(indexOfNext);
	}
	
	/**
	 * 
	 * @return
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * 
	 * @return
	 */
	public Gamer getCurrent() {
		return current;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Gamer> getGamers() {
		return gamers;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Gamer> getStartedWith() {
		return startedWith;
	}
	
	/**
	 * Maakt een nette string van alle beschrijvingen van de gamers die in deze game zitten.
	 * @return
	 */
	public String toString() {
		String result = "Game with: ";
		
		for(Gamer aGamer: gamers) {
			result = result + aGamer.toString() + " ";
		}
		
		return result;
	}
}