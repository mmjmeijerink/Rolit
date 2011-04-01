package rolit.client.models;

import rolit.sharedModels.Board;

/**
 * Interface voor de AI's.
 * Hierdoor wordt gegarandeerd dat de AI's een calculateBestMove(int color, Board board, int moves) methode hebben.
 * Daardoor hoeft er in de ApplicationController geen onderscheid gemaakt te worden tussen de verschillende AI's.
 * 
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public interface AIControllerInterface {
	
	/**
	 * Laat een AI een zet berekenen
	 * 
	 * @require color != null
	 * @require board != null
	 * @require moves > 0
	 * @ensure Board.checkmove(result, color) == true
	 * @param color kleur waarvoor de AI de zet moet berekenen
	 * @param board het bord waarop de AI de zet moet berekenen
	 * @param moves het aantal zetten dat de AI 'vooruit' moet kijken
	 * @return de zet die de AI heeft berekent
	 */
    public int calculateBestMove(int color, Board board, int moves);
}