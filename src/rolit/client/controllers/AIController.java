package rolit.client.controllers;

import rolit.sharedModels.Board;

public class AIController {
	private Board board;
	
	public AIController(Board aBoard) {
		board = aBoard;
	}
	
	public int calculateBestMove(int color) {
		int result = -1;
		
		Board newBoard = board.copy();
		
		return result;
	}
}
