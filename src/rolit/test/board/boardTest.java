package rolit.test.board;

import rolit.sharedModels.Board;

public class BoardTest {
	private Board board;
	
	public BoardTest() {
		board = new Board();
		System.out.println(board.layoutToString());
		System.out.println(board.toString());
		board.doMove(45, 1);
		System.out.println(board.toString());
		board.doMove(34, 2);
		System.out.println(board.toString());
		board.doMove(42, 3);
		System.out.println(board.toString());
	}
	
	public static void main(String[] args) {
		BoardTest boardTest = new BoardTest();
	}
}
