package rolit.client.controllers;

import rolit.client.models.AIControllerInterface;
import rolit.sharedModels.Board;

public class AIController implements AIControllerInterface {
	
	private Board board;
	
	public AIController(Board aBoard) {
		board = aBoard;
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
}