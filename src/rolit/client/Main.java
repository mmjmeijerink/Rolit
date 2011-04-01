package rolit.client;

import rolit.client.controllers.ApplicationController;

/**
 * De main klasse die alleen de public static methode main bevat waarmee het spel gestart wordt.
 * Eindopdracht Programmeren 2
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class Main {

	/**
     * Creeert een ApplicationController instantie en logt vervolgens versie informatie.
     * @ensure ApplicationController wordt gestart.
     */
	public static void main(String[] args) {
		ApplicationController appController = new ApplicationController();
		appController.log(	"        Rolit Client by Thijs Scheepers and Mart Meijerink" +
									"\n         v1.0beta" +
	        						"\n         Protocol of INF2" +
	        						"\n ----------------------------------------------------------");
	}
}