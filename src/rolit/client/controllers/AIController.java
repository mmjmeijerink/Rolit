package rolit.client.controllers;

import rolit.client.models.AIControllerInterface;
import java.util.*;
import rolit.sharedModels.*;


public class AIController implements AIControllerInterface {
	
	private Board board;
	private ArrayList<Gamer> gamers;
	
	public AIController(Board aBoard, ArrayList<Gamer> aGamers) {
		board = aBoard;
		gamers = aGamers;
	}
	
	public int calculateBestMove(int color) {
		return calculateBestMove(color, board, 2);
	}
	
	public int calculateBestMove(int color, Board boardToCalculate, int moves) {
		int result = -1;
		
		//Rekent uit welke zet de meeste punten oplevert binnen 1 zet, 
		//als er meerdere de zelfde punten opleveren neemt deze funtie de eerste die hij tegenkomt
		int mostPoints = -1;
		int mostPointsIndex = -1;
		int leastOpponentPoints = 65; //Hoogste aantal punten is 64
		
		for(int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			if(boardToCalculate.checkMove(i, color)) {
				int oldPoints = boardToCalculate.getPointsOfColor(color);
				Board newBoard = boardToCalculate.copy();
				newBoard.doMove(i, color);
				int newPoints = newBoard.getPointsOfColor(color);
				
				int opponentPoints = newBoard.getPointsOfColor(nextColor(color));
				if(moves > 0 && calculateBestMove(nextColor(color), newBoard, 0) != -1) {
					moves--;
					Board nextBoard = newBoard.copy();
					nextBoard.doMove(calculateBestMove(nextColor(color), newBoard, moves), nextColor(color));
					opponentPoints = nextBoard.getPointsOfColor(nextColor(color));
				}
				
				if(newPoints - oldPoints > mostPoints && opponentPoints < leastOpponentPoints) {
					leastOpponentPoints = opponentPoints;
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