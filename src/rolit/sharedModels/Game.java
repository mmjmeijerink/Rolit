package rolit.sharedModels;

import java.util.*;

/**
 * Het Game model houdt alle facetten van een game bij, er wordt bijgehouden wie er aan de beurt is,
 * met wie de game gestart is, wie er in de game zit en de situatie op het bord van de game.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class Game extends Observable {

	private Board board;
	private ArrayList<Gamer> gamers;
	private Gamer current;
	private ArrayList<Gamer> startedWith;

	@SuppressWarnings("unchecked")
	/**
	 * Maakt een nieuw Game object aan en zorgt er voor dat elke gamer zijn goede kleur toegewezen krijgt.
	 * @require aGamers != null && aGamer.size() > 1 && aGamers.size() < 5
	 * @ensure this.startedWith().equals(aGamers)
	 */
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
		/*
		 * Deze cast zorgt voor een unchecked warning, maar gamers is een instantie van ArrayList<Gamer> dus 
		 * we hoeven ons geen zorgen te maken om deze warning.
		 */
		startedWith = (ArrayList<Gamer>) gamers.clone();
	}
	
	/**
	 * Deze methode checkt een move van een gamer mogelijk is op dit moment in de game.
	 * 
	 * @require i >= 0 && i < Board.DIMENSION*Board.DIMENSION && this.getGamers().contains(aGamer)
	 * @param i het te zetten vakje op het bord
	 * @param aGamer de gamer waarvoor de move gecheckt moet worden.
	 * @return true als de zet mogelijk is volgens de spel regels en de gamer aan de beurt is anders false
	 */
	public boolean checkMove(int i, Gamer aGamer) {
		boolean result = false;
		/*
		 * De gamer moet wel aan de beurt zijn voor het positief terug geven van deze methoden,
		 * daarnaast wordt er op het bord gecheckt of de zet mogelijk is volgens de spelregels.
		 */
		if(aGamer == current && board.checkMove(i, aGamer.getColor())) {
			result = true;
		}
		return result;
	}
	
	/**
	 * Voert een bepaalde move van een bepaalde gamer uit. Er wordt op het bord gecheckt of de move mogelijk is door middel van de checkMove methode.
	 * Als de move gedaan kan worden wordt de move uitgevoerdt op het bord en worden de observers geinformeerd van de veranderde status van de game.
	 * @require i >= 0 && i < Board.DIMENSION*Board.DIMENSION && this.getGamers().contains(aGamer)
	 * @param i het te zetten vakje.
	 * @param aGamer de gamer die dit vakje wilt zetten.
	 */
	public void doMove(int i, Gamer aGamer) {
		/*
		 * Checkt eerst intern of de move mogelijk is.
		 */
		if(checkMove(i,aGamer)) {
			/*
			 * De move wordt daadwerkelijk op het bord uitgevoerd.
			 */
			board.doMove(i, aGamer.getColor());
			/*
			 * Er wordt naar de volgende persoon gegaan voor de beurt, dit moet eerst gebeuren voordat de observers ge notified worden omdat de observers
			 * de this.getCurrent() methode kunnen gebruiken.
			 */
			nextTurn();
			setChanged();
			notifyObservers("Move");
		}
	}
	
	/**
	 * Vraagt de punten op van een bepaalde gamer door de kleur te bepalen den vervolgens het aantal
	 * slots met die kleur te tellen.
	 * 
	 * @param aGamer de speler waarvan de punten opgevraagt worden.
	 * @return aantal punten van die speler.
	 */
	public int getPointsOf(Gamer aGamer) {
		int result = 0;
		
		for(Gamer check: gamers) {
			if (check == aGamer) {
				/*
				 * Als de gamer aanwezig is pakt hij de kleur van die gamer en gaat hij tellen.
				 * Dit met behulp van de board methode getPointsOfColor();
				 */
				result = board.getPointsOfColor(aGamer.getColor());
			}
		}
		
		return result;
	}
	
	/**
	 * Checkt of het board vol is of dat er minder dan 2 spelers in de game zitten, zo ja
	 * dan is de game dus afgelopen.
	 * 
	 * @return true als board.isFull() of this.getGamers().size() < 2 anders false
	 */
	public boolean isEnded() {
		boolean result = false;
		
		if(board.isFull() || gamers.size() < 2) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Met deze methode verwijder je een gamer uit de game.
	 * Als een gamer verwijdert is zal deze nooit meer aan de beurt zijn.
	 * 
	 * @require this.getGamers().contains(toBeRemoved);
	 * @ensure !this.getGamers().contains(toBeRemoved); && this.getStartedWith().contains(toBeRemoved); 
	 * @param toBeRemoved de te verwijderen gamer
	 */
	public void removeGamer(Gamer toBeRemoved) {
		if(current == toBeRemoved) {
			/*
			 * Als de gamer die geremoved wordt aan de beurt is zal de volgende aan de beurt zijn.
			 */
			nextTurn();
		}
		/*
		 * Notified opservers want als er nog maar 1 gamer in de game zit kan de game worden afgsloten.
		 */
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
	 * Voorbeeld: "Game with: Sjaak Klaas Henk"
	 * @return een nette string van welke gamers er in de game zitten.
	 */
	public String toString() {
		String result = "Game with: ";
		
		for(Gamer aGamer: gamers) {
			result = result + aGamer.toString() + " ";
		}
		
		return result;
	}
}