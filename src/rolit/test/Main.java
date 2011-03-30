package rolit.test;

public class Main {

	/**
	 * Start een server en 2 clients, zodat je direct kan spelen/testen
	 */
	public static void main(String[] args) {
		new rolit.server.controllers.ApplicationController();
		new rolit.client.controllers.ApplicationController();
		new rolit.client.controllers.ApplicationController();
	}

}