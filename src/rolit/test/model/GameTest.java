package rolit.test.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rolit.client.controllers.SmartAIController;
import rolit.sharedModels.*;

/**
 * Tests a full game.
 * Sets up a game, board for a 2 player, 3 player and 4 player test.
 * The moves are being set by AI's
 * 
 * @author Mart
 */
public class GameTest {
	
	public GameTest() {
		Game twoPlayerGame = new Game(getTwoPlayers());
		Game threePlayerGame = new Game(getThreePlayers());
		Game fourPlayerGame = new Game(getFourPlayers());
		
		playGame(twoPlayerGame);
		playGame(threePlayerGame);
		playGame(fourPlayerGame);
	}
	
	public void playGame(Game game) {
		String result = "";
		
		while(!game.isEnded()) {
			Gamer current = game.getCurrent();
			int i = (new SmartAIController(game.getBoard())).calculateBestMove(current.getColor());
			game.doMove(i, current);
			
			//Visualize
			result += "Move "+i+" done by "+current.getName()+":\n";
			result += game.getBoard().toString()+"\n";
			System.out.println("Move "+i+" done by "+current.getName()+":");
			System.out.println(game.getBoard().toString());
		}
		result += game.toString() + "has ended!";
		System.out.println(game.toString() + "has ended!");
		
		try {
			//Log game
			BufferedWriter file = new BufferedWriter(new FileWriter(game.getGamers().size()+"playerGame.txt"));
			file.write(result);
			file.close();
		} catch (IOException e) {
			e.printStackTrace(); //Test class
		}
	}

	//Gamerlists
	public ArrayList<Gamer> getTwoPlayers() {
		ArrayList<Gamer> twoPlayers = new ArrayList<Gamer>();
		
		Gamer first = new Gamer();
		first.setName("Red");
		
		Gamer second = new Gamer();
		second.setName("Green");
		
		twoPlayers.add(first);
		twoPlayers.add(second);
		
		return twoPlayers;
	}
	
	public ArrayList<Gamer> getThreePlayers() {
		ArrayList<Gamer> threePlayers = new ArrayList<Gamer>();
		
		Gamer first = new Gamer();
		first.setName("Red");
		
		Gamer second = new Gamer();
		second.setName("Yellow");
		
		Gamer third = new Gamer();
		third.setName("Green");
		
		threePlayers.add(first);
		threePlayers.add(second);
		threePlayers.add(third);
		
		return threePlayers;
	}
	
	public ArrayList<Gamer> getFourPlayers() {
		ArrayList<Gamer> fourPlayers = new ArrayList<Gamer>();
		
		Gamer first = new Gamer();
		first.setName("Red");
		
		Gamer second = new Gamer();
		second.setName("Yellow");
		
		Gamer third = new Gamer();
		third.setName("Green");
		
		Gamer fourth = new Gamer();
		fourth.setName("Blue");
		
		fourPlayers.add(first);
		fourPlayers.add(second);
		fourPlayers.add(third);
		fourPlayers.add(fourth);
		
		return fourPlayers;
	}
	
	public static void main(String[] args) {
		new GameTest();
	}
}