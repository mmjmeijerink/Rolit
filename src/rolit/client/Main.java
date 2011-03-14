package rolit.client;

import rolit.server.controllers.ApplicationController;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationController appController = new ApplicationController();
		appController.view().log(	"        Rolit Client by Thijs Scheepers and Mart Meijerink" +
									"\n         v0.1alpha" +
	        						"\n         Protocol of INF2" +
	        						"\n ----------------------------------------------------------");
	}
}