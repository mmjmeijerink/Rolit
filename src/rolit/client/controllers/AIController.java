package rolit.client.controllers;

import java.util.*;
import rolit.sharedModels.*;

public class AIController {
	
	private Board board;
	private ArrayList<Gamer> gamers;
	
	public AIController(Board aBoard,ArrayList<Gamer> aGamers) {
		board = aBoard;
		gamers = aGamers;
	}
	
	public int calculateBestMove(int color) {
		int result = -1;
		
		// Rekent uit welke zet de meeste punten oplevert binnen 1 zet, 
		//als er meerdere de zelfde punten opleveren neemt deze funtie de eerste die hij tegenkomt
		int mostPoints = -1;
		int mostPointsIndex = -1;
		for(int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			if(board.checkMove(i, color)) {
				int oldPoints = board.getPointsOfColor(color);
				Board newBoard = board.copy();
				newBoard.doMove(i, color);
				int newPoints = newBoard.getPointsOfColor(color);
				if(newPoints-oldPoints > mostPoints) {
					mostPoints = newPoints - oldPoints;
					mostPointsIndex = i;
				}
			}
		}
		
		result = mostPointsIndex;
		
		return result;
	}
	
	private int nextColor(int color) {
		int result = Slot.EMPTY;
		if(gamers.size() == 2) {
			if(color == Slot.RED) {
				result = Slot.GREEN;
			} else if(color == Slot.GREEN) {
				result = Slot.RED;
			}
		} else if(gamers.size() == 3) {
			if(color == Slot.RED) {
				result = Slot.YELLOW;
			} else if(color == Slot.YELLOW) {
				result = Slot.GREEN;
			} else if(color == Slot.GREEN) {
				result = Slot.RED;
			} 
		} else if(gamers.size() == 4) {
			if(color == Slot.RED) {
				result = Slot.YELLOW;
			} else if(color == Slot.YELLOW) {
				result = Slot.GREEN;
			} else if(color == Slot.GREEN) {
				result = Slot.BLUE;
			} else if(color == Slot.BLUE) {
				result = Slot.RED;
			} 
		}
		
		return result;
	}
	
}