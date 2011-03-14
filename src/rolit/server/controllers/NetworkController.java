package rolit.server.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.Game;
import rolit.sharedModels.Gamer;

public class NetworkController extends Thread implements Observer {
	private ApplicationController				appController;
	private int									port;
	private InetAddress							host;
	private ArrayList<ConnectionController>  	connections;
	private ArrayList<ConnectionController>		waitingForGame;
	private ArrayList<Game>						games;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		connections 	= new ArrayList<ConnectionController>();
		waitingForGame 	= new ArrayList<ConnectionController>();
		games			= new ArrayList<Game>();
		appController = controller;
		port = aPort;
		host = aHost;
	}

	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			appController.log("Server started on port: " + port);
			while (true) {
				Socket socket = server.accept();
				ConnectionController client = new ConnectionController(this, socket, appController);
				appController.log("New connection from IP: " + socket.getInetAddress());
				addConnection(client);
				client.start();
			}
		} catch (IOException e){
			appController.log("Server can't start because port " + port + " is already being used");
			appController.connectionFailed();
		}
	}

	public void broadcastCommand(String msg) {
		if(msg != null) {
			for(ConnectionController client: connections){
				client.sendCommand(msg);
			}
			appController.log("Broadcasted: " + msg);
		}
	}

	public void executeCommand(String msg, ConnectionController sender) {
		if(msg != null && sender != null) {
			String[] regexCommand = (msg.split("\\s+"));
			ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));

			if(splitCommand.get(0).equals("connect")) {
				/* Execute command "connect" */
				
				if(splitCommand.size() == 2) {
					if(sender.getGamer().getName().equals("[NOT CONNECTED]")) {
						String possibleName = splitCommand.get(1);
						int i = 1;
						while(!checkName(possibleName)) {
							possibleName = possibleName+Integer.toString(i);
							i++;
						}
						sender.getGamer().setName(possibleName);
						appController.log("Connection from " + sender.getSocket().getInetAddress() + " has identified itself as: " + possibleName);
						sender.sendCommand("ackconnect "+possibleName);
					} else {
						appController.log(sender.toString() + " tries to but is already identified.");
					}
				} else {
					appController.log("Connect command from " + sender.toString() + " FAILED, has more than 1 parameter: " + msg);
				}
				
			} else if(splitCommand.get(0).equals("join")) {
				/* Execute command "join" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					if(splitCommand.size() == 2) {
						try {
							int requestedSize = Integer.parseInt(splitCommand.get(1));
							if(sender.getGamer().setRequestedGameSize(requestedSize)) {
								if(!waitingForGame.contains(sender)) {
									waitingForGame.add(sender);
								}
								appController.log(sender.toString() + " wants to join with " + splitCommand.get(1) + " players");
								checkForGameStart();
							} else {
								appController.log("Join command from " + sender.toString() + " FAILED, 1st parameter between [2-4]: " + msg);
							}
						} catch(NumberFormatException e) {
							appController.log("Join command from " + sender.toString() + " FAILED, 1st parameter needs to be a int: " + msg);
						}
					} else {
						appController.log("Join command from " + sender.toString() + " FAILED, has more than 1 parameter: " + msg);
					}
				} else {
					appController.log("Join command from " + sender.toString() + " FAILED, not identified.");
				}
				
			} else if(splitCommand.get(0).equals("domove")) {

			} else if(splitCommand.get(0).equals("chat")) {

			} else {
				appController.log("Command from " + sender.toString() + " misunderstoud: " + msg);
			}
		}
	}

	public void addConnection(ConnectionController connection) {
		connections.add(connection);
	}

	public void removeConnection(ConnectionController connection) {
		if(connections.contains(connection)) {
			appController.log(connection.toString() + " disconnects");
			connections.remove(connection);
		} else {
			appController.log("Tries to remove connection that does not exist");
		}
	}

	public boolean checkName(String name) {
		boolean result = true;
		if(name != null) {
			for(ConnectionController client: connections){
				if(client.getGamer().getName().equals(name)) {
					result = false;
				}
			}
		}
		return result;
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
