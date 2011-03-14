package rolit.client.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.Game;
import rolit.sharedModels.Gamer;

public class NetworkController extends Thread implements Observer {
	
	private ApplicationController				appController;
	private int									port;
	private InetAddress							host;
	private ArrayList<ConnectionController>		connections;
	private ArrayList<ConnectionController>		waitingForGame;
	private ArrayList<Game>						games;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		//connections 	= new ArrayList<ConnectionController>();
		//waitingForGame 	= new ArrayList<ConnectionController>();
		//games			= new ArrayList<Game>();
		appController = controller;
		port = aPort;
		host = aHost;
	}

	public void run() {
		Socket socket = null;
		
		try {
			socket = new Socket(host, port);
			appController.log("Connecting to server on ip, " + host + ", and port, " + port + ".");
			
			while (true) {
				ConnectionController client = new ConnectionController(this, socket, appController);
				appController.log("New connection from IP: " + socket.getInetAddress());
				addConnection(client);
				client.start();
			}
		} catch (IOException e){
			appController.log("Cannot connect to server!");
			appController.connectionFailed();
		}
	}

	public void executeCommand(String msg, ConnectionController sender) {
		
	}
	
	public void checkForGameStart() {
		appController.log("Checks if games can be started...");
		ArrayList<ConnectionController> startingWith = null;
		
		for(ConnectionController masterClient: waitingForGame){
			int minimalSize = masterClient.getGamer().getRequestedGameSize();
			ArrayList<ConnectionController> readyToStart = new ArrayList<ConnectionController>();
			readyToStart.add(masterClient);
			for(ConnectionController slaveClient: waitingForGame) {
				if(masterClient != slaveClient) {
					if(minimalSize < slaveClient.getGamer().getRequestedGameSize()) {
						minimalSize = slaveClient.getGamer().getRequestedGameSize();
					}
					if(minimalSize > readyToStart.size()) {
						readyToStart.add(slaveClient);
					} else {
						startingWith = readyToStart;
					}
				}
			}
			if (readyToStart.size() == minimalSize) {
				startingWith = readyToStart;
			}
		}
		
		if(startingWith != null) {
			for(ConnectionController starting: startingWith) {
				if(waitingForGame.contains(starting)) {
					waitingForGame.remove(starting);
				} else {
					appController.log("ERROR: Server tries to start game with someone who does not want to start");
				}
			}
			startGame(startingWith);
		} else {
			appController.log("Not enough players to start a game");
		}
	}
	
	public void startGame(ArrayList<ConnectionController> players) {
		if(players.size() > 1 && players.size() < 5) {
			String command = "startgame";
			String logEntry = "Starting a game between";
			int i = 0;
			for(ConnectionController player: players) {
				i++;
				if(i < 4) {
					command = command + " " + player.getGamer().getName();
					logEntry = logEntry + ", " + player.toString();
				} else {
					players.remove(player);
				}
			}
			appController.log(logEntry);
			ArrayList<Gamer> gamers = new ArrayList<Gamer>();
			
			for(ConnectionController player: players) {
				player.sendCommand(command);
				waitingForGame.remove(player);
				gamers.add(player.getGamer());
			}
			
			Game aGame = new Game(gamers);
			aGame.addObserver(this);
			games.add(aGame);
			
			
			String turnCommand = "turn " + players.get(0).getGamer().getName();
			
			for(ConnectionController player: players) {
				player.sendCommand(turnCommand);
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0.getClass().equals(Game.class)) {
			appController.log(((Game) arg0).getBoard().toString());
		}
	}
}