package rolit.server;

import rolit.server.controllers.ApplicationController;

public class Main {

	public static void main(String[] args) {
		ApplicationController appController = new ApplicationController();
		appController.view().log(	"        Rolit Client by Mart Meijerink and Thijs Scheepers" +
									"\n         v0.1alpha" +
	        						"\n         Protocol of INF2" +
	        						"\n ----------------------------------------------------------");
	}
}