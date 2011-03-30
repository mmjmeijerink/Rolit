package rolit.test.client;

import rolit.client.controllers.ApplicationController;
import rolit.client.views.GameView;

public class GameViewTest {
	
	public GameViewTest() {
		new GameView(new ApplicationController());
	}

	public static void main(String[] args) {
		new GameViewTest();
	}
}