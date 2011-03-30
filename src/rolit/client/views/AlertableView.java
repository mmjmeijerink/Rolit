package rolit.client.views;
/**
 * Dit is een interface voor een view die een alert kan laten zien, dit is belangrijk voor de ApplicationController zodat hij dringende berichten aan de gebruiker kan tonen.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public interface AlertableView {
	/**
	 * De methode voor het tonenen van een error bericht.
	 * @param message het te tonen bericht.
	 */
	public void alert(String message);
}