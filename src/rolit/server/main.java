package rolit.server;

import rolit.server.controllers.ApplicationController;

public class Main {

	public static void main(String[] args) {
		ApplicationController appController = new ApplicationController();
		appController.view().log(	"        Rolit Server by Mart Meijerink and Thijs Scheepers" +
									"\n         v1.0beta" +
	        						"\n         Protocol of INF2" +
	        						"\n ----------------------------------------------------------");
	}
}