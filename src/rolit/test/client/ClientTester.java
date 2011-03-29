package rolit.test.client;

import rolit.server.controllers.NetworkController;

public class ClientTester extends NetworkController {
	
	public ClientTester() {
		super(1337, new rolit.server.controllers.ApplicationController());
		super.start();
		
		startTest();
	}
	
	public void startTest() {
		
	}

	public static void main(String[] args) {
		new ClientTester();
	}
}