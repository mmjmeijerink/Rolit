package rolit.test.AITest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Observable;

import rolit.client.controllers.*;
import rolit.client.models.AIControllerInterface;
import rolit.sharedModels.*;

public class AITestApplicationController extends ApplicationController {
	
	public boolean						isEnded = false;
	private NetworkController			network;
	private Game						game = null;
	private AIControllerInterface		ai;
	private AITestApplicationController	opponent;
	
	private int smartWinnings = 0, greedyWinnings = 0;
	private int numberOfGamesToPlay = 100, numberOfGamesPlayed = 0;
	
	public AITestApplicationController(InetAddress host, int port, String nick) {
		network = new NetworkController(host, port, this, "connect " + nick);
		network.start();
	}
	
	public int getSmartWinnings() {
		return smartWinnings;
	}
	
	public int getGreedyWinnings() {
		return greedyWinnings;
	}
	
	public void challenge(AITestApplicationController client) {
		opponent = client;		
		network.sendCommand("challenge " + client.getGamer().getName());
	}
	
	//Overided methodes
	public void myTurn() {
		int bestMove = ai.calculateBestMove(super.getGamer().getColor());
		game.doMove(bestMove, super.getGamer());
		network.sendCommand("domove "+bestMove);
	}
	
	public void startGame(ArrayList<String> players) {
		int i = 1;
		ArrayList<Gamer> gamers = new ArrayList<Gamer>();
		for(String name: players) {
			Gamer participant;
			if(name.equals(super.getGamer().getName())) {
				participant = super.getGamer();
			} else {
				participant = new Gamer();
			}
			participant.setName(name);
			participant.setColor(i);
			gamers.add(participant);
			i++;
		}

		game = new Game(gamers);
		if(super.getGamer().getName().contains("smart")) {
		    ai = new SmartAIController(game.getBoard());
		} else {
		    ai = new AIController(game.getBoard(), gamers);
		}
	}
	
	public void endGame(String message) {
		numberOfGamesPlayed++;
		
		if(message.contains("smart")) smartWinnings++;
		else greedyWinnings++;
		
		System.out.println(smartWinnings + " smartWinnings. \n" + greedyWinnings + " greedyWinnings.");
		
		if(numberOfGamesPlayed < numberOfGamesToPlay) challenge(opponent);
		else isEnded = true;
	}
	
	public void handleMove(Gamer aGamer, int index) {
		game.doMove(index, aGamer);
	}
	
	public Game getGame() {
		return game;
	}
	
	public void challenged(String challenger) {
		network.sendCommand("challengeresponse " + challenger + " true");
	}
	
	public void update(Observable o, Object arg) {
		if(((String) arg).equals("move") && o.getClass().equals(game)) {
			//Don't update GameView, cause it doesn't exist...
		}
	}
}