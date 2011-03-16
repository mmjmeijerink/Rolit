package rolit.client;

import rolit.client.controllers.ApplicationController;

public class Main {

	public static void main(String[] args) {
		ApplicationController appController = new ApplicationController();
		appController.log(	"        Rolit Client by Thijs Scheepers and Mart Meijerink" +
									"\n         v0.1alpha" +
	        						"\n         Protocol of INF2" +
	        						"\n ----------------------------------------------------------");
	}
}