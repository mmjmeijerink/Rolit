package rolit.client.controllers;

import rolit.client.models.AIControllerInterface;
import rolit.sharedModels.Board;

public class SmartAIController implements AIControllerInterface {

	private Board board;

	public SmartAIController(Board aBoard) {
		board = aBoard;
	}

	public int calculateBestMove(int color) {
		int result = -1;

		// Rekent uit welke zet de meeste punten oplevert binnen 1 zet,
		//als er meerdere de zelfde punten opleveren neemt deze funtie de eerste die hij tegenkomt
		int lessPoints = 100;
		int mostPointsIndex = -1;
		for(int i = 0; i < Board.DIMENSION * Board.DIMENSION && lessPoints != -1; i++) {
			if(board.checkMove(i, color)) {
				//int oldPoints = board.getPointsOfColor(color);
				Board newBoard = board.copy();
				newBoard.doMove(i, color);
				int newPoints = newBoard.getPointsOfColor(color);

				//Als dit vakje een hoek is (en dus mogelijk is om te pakken), altijd in de hoek gaan zitten
				if (i == 0 || i == 7 || i == 56 || i == 63) {
				    lessPoints = -1;
				    mostPointsIndex = i;
				//Als dit vakje direct aan één van de hoeken grenst, pak het dan niet (bij voorkeur)
				} else if(lessPoints > 95
					&& ((i == 1)
					|| (i == 8)
					|| (i == 9)
					|| (i == 6)
					|| (i == 14)
					|| (i == 15)
					|| (i == 48)
					|| (i == 49)
					|| (i == 57)
					|| (i == 54)
					|| (i == 55)
					|| (i == 62)
					)) {
				    lessPoints = 95;
				    mostPointsIndex = i;
				//Zit het vakje 1 rij of kolom van de rand, pak het dan niet (bij voorkeur)
				} else if(lessPoints > 90
					&& (((i - 1)%8 != 0) /* 1 rechts van de linker rand */
					|| ((i + 2)%8 != 0) /* 1 links van de rechter rand */
					|| (i >= 8 && i <= 15) /* 1 onder de bovenste rand */
					|| (i >= 48 && i <= 55) /* 1 boven de onderste rand */
					)) {
				    lessPoints = 90;
				    mostPointsIndex = i;
				//Anders random vakje kiezen, waarbij eerst zo min mogelijk punten gepakt worden
				} else if(newPoints < lessPoints) {
				    lessPoints = newPoints;
				    mostPointsIndex = i;
				}
			}
		}

		result = mostPointsIndex;

		return result;
	}
}

//Check, is het de rand of is het 1 van de rand, of is het een ander vakje?
//Rand: Pakken (Hoek ALTIJD pakken)
//1 hokje van de rand, bij voorkeur niet pakken
//Ander vakje, randomness (minst GREEDY!!!)