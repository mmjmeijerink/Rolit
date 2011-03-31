package rolit.client.controllers;

import java.util.ArrayList;

import rolit.client.models.AIControllerInterface;
import rolit.sharedModels.Board;
import rolit.sharedModels.Gamer;
import rolit.sharedModels.Slot;

public class SmartAIController implements AIControllerInterface {

	private Board 				board;
	private ArrayList<Gamer>	gamers;

	public SmartAIController(Board aBoard, ArrayList<Gamer> aGamers) {
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
		int lessPoints = 100;
		int mostPointsIndex = -1;
		int leastOpponentPoints = 65; //Hoogste aantal punten is 64
		
		for(int i = 0; i < Board.DIMENSION * Board.DIMENSION && lessPoints != -1; i++) {
			if(boardToCalculate.checkMove(i, color)) {
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

				//Als dit vakje een hoek is (en dus mogelijk is om te pakken), altijd in de hoek gaan zitten
				if (i == 0 || i == 7 || i == 56 || i == 63) {
					lessPoints = -1;
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
				//Als dit vakje aan de rand ligt (en er is nog geen hoek gevonden), pak die dan
				} else if(lessPoints > -1 /*Geen hoek gevonden */
					&& ((i % 8 == 0) /* Linker rand */
					|| ((i + 1) % 8 == 0) /* Rechter rand */
					|| (i > 0 && i < 7) /* Bovenste rand */
					|| (i > 56 && i < 63) /* Onderste rand */
					)) {
					lessPoints = 0;
					mostPointsIndex = i;
				//Anders random vakje kiezen, waarbij eerst zo min mogelijk punten gepakt worden
				} else if(newPoints < lessPoints && opponentPoints < leastOpponentPoints) {
					lessPoints = newPoints;
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

//Check, is het de rand of is het 1 van de rand, of is het een ander vakje?
//Rand: Pakken (Hoek ALTIJD pakken)
//1 hokje van de rand, bij voorkeur niet pakken
//Ander vakje, randomness (minst GREEDY!!!)