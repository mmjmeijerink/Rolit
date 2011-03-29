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
	 * Zorgt er voor dat de volgend gamer aan de beurt is.
	 * Dit alles volgens de volgende volgorde:
	 * Bij twee spelers eerst rood dan groen
	 * Bij drie spelers eerst rood dan geel dan groen (in tegenstelling tot rood dan groen dan geel)
	 * Bij vier spelers eerst rood dan geel dan groen dan blauw.
	 */
	private void nextTurn() {
		/*
		 * Gebruikt de index van de gamers array, zoals toegwezen in de constructor van deze classe
		 */
		int indexOfCurrent = gamers.indexOf(current);
		int indexOfNext = indexOfCurrent + 1;
		
		if(indexOfNext >= gamers.size()) {
			indexOfNext = 0;
		}
		current = gamers.get(indexOfNext);
	}
	
	/**
	 * Geeft de board instantie terug die bij deze game hoort.
	 * Een bord wordt toegewezen aan het begin van de game en verandert niet meer.
	 * Wel kan de situatie op het bord veranderen.
	 * @return board insantie die bij deze game hoort.
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Geeft de gamer terug die nu aan de beurt is.
	 * @return gamer die aan de beurt is
	 */
	public Gamer getCurrent() {
		return current;
	}
	
	/**
	 * Geeft een lijst terug van de Gamer's die nu nog in de game zitten.
	 * Alle gamers die dus gekickt zijn zullen niet voorkomen in deze lijst.
	 * Elke gamer in deze lijst moet ook in de lijst this.getStartedWith() zitten.
	 * @ensure this.getStartedWith().size() >= this.getGamers().size()
	 * @return lijst met gamers die in de game zitten
	 */
	public ArrayList<Gamer> getGamers() {
		return gamers;
	}
	
	/**
	 * Geeft een lijst terug van de Gamer's waarmee de game gestart is. Dus als zij gekickt zijn komen die gamers nog steeds
	 * voor in deze lijst. Dit is nodig voor het bepalen van de eind punten in een game.
	 * Deze lijst is geschikt om de kleuren van de gamers te bepalen omdat gamers op volgorde toegevoegd worden.
	 * @ensure this.getStartedWith().size() >= this.getGamers().size()
	 * @return lijst met gamers die aan deze game zijn begonnen
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