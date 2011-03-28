package rolit.client.models;

/**
 * Interface voor de AI's.
 * Hierdoor wordt gegarandeerd dat de AI's een calculateBestMove(int color) methode hebben.
 * Daardoor hoeft er in de ApplicationController geen onderscheid gemaakt te worden tussen de verschillende AI's.
 * 
 * @author Mart Meijerink
 */
public interface AIControllerInterface {
    public int calculateBestMove(int color);
}