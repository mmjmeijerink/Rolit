package rolit.server.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.Game;
import rolit.sharedModels.Gamer;

public class NetworkController extends Thread implements Observer {
	private ApplicationController				appController;
	private int									port;
	private ArrayList<ConnectionController>  	connections;
	private ArrayList<ConnectionController>		waitingForGame;
	private ArrayList<Game>						games;

	public NetworkController(int aPort, ApplicationController controller) {
		super();
		connections 	= new ArrayList<ConnectionController>();
		waitingForGame 	= new ArrayList<ConnectionController>();
		games			= new ArrayList<Game>();
		appController = controller;
		port = aPort;
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
				/* Execute command "dmove" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					if(splitCommand.size() == 2) {

						Game participatingGame = null;
						for(Game aGame: games) {
							//appController.log(games.toString());
							for(Gamer aGamer: aGame.getGamers()) {
								if(aGamer == sender.getGamer()) {
									participatingGame = aGame;
								}
							}
						}
						if(participatingGame != null && sender.getGamer().isTakingPart()) {
							if(participatingGame.getCurrent() == sender.getGamer()) {
								try {
									int slotToSet = Integer.parseInt(splitCommand.get(1));
									if(participatingGame.checkMove(slotToSet, sender.getGamer())) {
										moveDone(participatingGame,sender.getGamer(),slotToSet);
										participatingGame.doMove(slotToSet, sender.getGamer());
										appController.log(sender.toString() + " has set " + splitCommand.get(1) + " in his game.");
									} else {
										appController.log("Domove command from " + sender.toString() + " FAILED, move is impossible " + msg);
										kickGamer(sender.getGamer());
									}
								} catch(NumberFormatException e) {
									appController.log("Domove command from " + sender.toString() + " FAILED, 1st parameter needs to be a int: " + msg);
								}
							} else {
								appController.log("Domove command from " + sender.toString() + " FAILED, does not have the turn.");
								kickGamer(sender.getGamer());
							}
						} else {
							appController.log("Domove command from " + sender.toString() + " FAILED, player is not in game.");
						}

					} else {
						appController.log("Domove command from " + sender.toString() + " FAILED, has more than 1 parameter: " + msg);
					}
				} else {
					appController.log("Domove command from " + sender.toString() + " FAILED, not identified.");
				}
			} else if(splitCommand.get(0).equals("chat")) {
				/* Execute command "chat" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					sendChat(msg.substring(5),sender);
				} else {
					appController.log("Chat command from " + sender.toString() + " FAILED, not identified.");
				}
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

	private void sendChat(String message, ConnectionController sender) {
		Game participatingGame = null;
		for(Game aGame: games) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == sender.getGamer()) {
					participatingGame = aGame;
				}
			}
		}

		if(participatingGame == null) {
			for(ConnectionController aConnection: connections) {
				if(!aConnection.getGamer().isTakingPart()) {
					aConnection.sendCommand("message "+sender.getGamer().getName()+" "+message);
				}
			}
		} else {
			for(ConnectionController aConnection: connections) {
				for(Gamer aGamer: participatingGame.getGamers()) {
					if(aConnection.getGamer() == aGamer) {
						aConnection.sendCommand("message "+sender.getGamer().getName()+" "+message);
					}
				}
			}
		}


	}

	private boolean checkName(String name) {
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

	private void checkForGameStart() {
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

	private void startGame(ArrayList<ConnectionController> players) {
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
				gamers.add(player.getGamer());
			}

			Game aGame = new Game(gamers);
			aGame.addObserver(this);
			games.add(aGame);
			nextTurn(aGame);
		}
	}

	private void kickGamer(Gamer toBeKicked) {
		appController.log("Kicking " + toBeKicked.getName() + " ...");
		Game participatingGame = null;
		for(Game aGame: games) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == toBeKicked) {
					participatingGame = aGame;
				}
			}
		}	
		if(participatingGame != null) {

			for(ConnectionController connection: connections) {
				for(Gamer aGamer: participatingGame.getGamers()) {
					if(aGamer == connection.getGamer()) {
						connection.sendCommand("kick " + toBeKicked.getName());
					}
				}
			}
			participatingGame.removeGamer(toBeKicked);
			toBeKicked.setColor(0);
		}
	}

	private void nextTurn(Game aGame) {
		for(ConnectionController connection: connections) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					connection.sendCommand("turn " + aGame.getCurrent().getName());
				}
			}
		}
	}

	private void endGame(Game aGame) { 
		String command = "endgame";
		String logEntry = "Ending a game between";
		for(Gamer aGamer: aGame.getStartedWith()) {
			if(aGame.getGamers().contains(aGamer)) {
				command = command + " " + aGame.getPointsOf(aGamer);
			} else {
				command = command + " 0";
			}
			logEntry = logEntry + ", " + aGamer.getName() + " (" + aGame.getPointsOf(aGamer) + ")";

		}
		appController.log(logEntry);

		for(ConnectionController connection: connections) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					connection.sendCommand(command);
				}
			}
		}
	}

	private void moveDone(Game aGame, Gamer mover, int slot) {
		for(ConnectionController connection: connections) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					connection.sendCommand("movedone " + mover.getName() + " " + slot);
				}
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0.getClass().equals(Game.class)) {
			Game game = (Game) arg0;
			appController.log(game.getBoard().toString());
			if(game.isEnded()) {
				endGame(game);
			} else {
				nextTurn(game);
			}
		}
	}

}
