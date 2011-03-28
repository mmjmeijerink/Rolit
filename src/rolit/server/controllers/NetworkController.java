package rolit.server.controllers;

import java.io.*;
import java.net.*;
import java.util.*;
import rolit.sharedModels.*;

/**
 * De NetworkController beheert alle ConnectionController instanties en handelt alle binnengekomen commando's juist af volgens het protocol met behulp van insanties van Game en lijsten met Gamers.
 * De NetworkController heeft zijn eigen thread zodat hij onafhankelijk van de interface dingen kan berekenen en kan luisteren naar nieuwe clients die zich aanmelden.
 * Een RolitServer heeft maar 1 instantie van een NetworkController nodig.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class NetworkController extends Thread implements Observer {

	private ApplicationController				appController;
	private int									port;
	private ArrayList<ConnectionController>  	connections;
	private ArrayList<ConnectionController>		waitingForGame;
	private ArrayList<Gamer>					challengerList;
	private ArrayList<Gamer>					challengedList;
	private ArrayList<Game>						games;

	/**
	 * In de constructor van de network controller worden alle instantie variable gevult.
	 * Het is nodig dat de network controller een verwijzing heeft naar de application controller zodat hij mogelijke fouten en log berichten door kan geven.
	 * Een logging interface is hier niet genoeg omdat de network controller ook connectionfailed aan moet roepen als er iets niet goed is gegaan met het opzetten van de socket.
	 * 
	 * @require controller != null && aPort > 0
	 * 
	 * @param aPort De poort waarop de server moet gaan draaien.
	 * @param controller de ApplicationController van de server zodat de NetworkController hier berichten aan door kan geven.
	 */
	public NetworkController(int aPort, ApplicationController controller) throws NullPointerException {
		super();
		
		connections 	= new ArrayList<ConnectionController>();
		waitingForGame 	= new ArrayList<ConnectionController>();
		games			= new ArrayList<Game>();
		challengedList	= new ArrayList<Gamer>();
		challengerList	= new ArrayList<Gamer>();
		
		if(controller != null && aPort > 0) {
			appController = controller;
			port = aPort;
		} else {
			/*
			 * Als een gebruiker niet voldoet aan de preconditie wordt er een exception getrowt.
			 */
			throw new NullPointerException();
		}
		
	}
	
	/**
	 * Dit is de methode die gedurende het uitvoeren van de Thread gaat lopen. Deze methode dient niet direct aangeroepen te worden maar kan worden gestart via de methode this.start();
	 */
	public void run() {
		ServerSocket server = null;

		try {
			/*
			 * Start de server door het openen van een nieuwe socket.
			 */
			server = new ServerSocket(port);
			appController.log("Server started on port: " + port);
			while (true) {
				/*
				 * Binnen deze loop wordt er geluistert naar mogelijke niewe connecties van clients.
				 * Als er een conenctie binnen komt wordt daarvoor een nieuwe ConnectionController thread aangemaakt.
				 * Deze thread kan dan vervolgens gaan luisteren naar commando's die binnen komen van deze thread.
				 */
				Socket socket = server.accept();
				ConnectionController client = new ConnectionController(this, socket, appController);
				appController.log("New connection from IP: " + socket.getInetAddress());
				addConnection(client);
				client.start();
			}
		} catch (IOException e){
			/*
			 * Deze IOException wordt aangeroepen als er geen socket kan worden aangemaakt op de gedefineerde poort.
			 * 
			 * Daarnaast kan deze methode ook aangeroepen worden als de ConnectionController constructor een IOException throwt,
			 * maar dat zal niet gebeuren omdat de socket nooit een verkeerde. Stel dat de socket een verkeerde was dan zou deze al
			 * vastlopen bij het instancieren van die socket.
			 * 
			 */
			appController.log("Server can't start because port " + port + " is already being used.");
			appController.connectionFailed();
		}
	}
	
	/**
	 * Deze methode kan worden gebruikt om een commando naar alle verbonden clients te sturen.
	 * @require aCommand != null
	 * @param aCommand het te sturen commando, dient conform te zijn met het protocol van INF2.
	 */
	private void broadcastCommand(String aCommand) {
		if(aCommand != null) {
			for(ConnectionController client: connections){
				client.sendCommand(aCommand);
			}
			appController.log("Broadcasted: " + aCommand);
		}
	}
	
	/**
	 * 
	 * @param msg
	 * @param sender
	 */
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
							possibleName = splitCommand.get(1)+Integer.toString(i);
							i++;
						}
						sender.getGamer().setName(possibleName);
						appController.log("Connection from " + sender.getSocket().getInetAddress() + " has identified itself as: " + possibleName);
						sender.sendCommand("ackconnect "+possibleName);
						broadcastLobby();
					} else {
						appController.log(sender.toString() + " tries to but is already identified.");
					}
				} else {
					appController.log("Connect command from " + sender.toString() + " FAILED, has more than 1 parameter: " + msg);
				}
			} else if(splitCommand.get(0).equals("join")) {
				/* Execute command "join" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					if(!isInGame(sender.getGamer())) {
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
						appController.log("Join command from " + sender.toString() + " gamers is already ingame. " + msg);
					}
				} else {
					appController.log("Join command from " + sender.toString() + " FAILED, not identified.");
				}
			} else if(splitCommand.get(0).equals("domove")) {
				/* Execute command "domove" */
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
					appController.log("Chat command from " + sender.toString() + " send: " + msg.substring(5));
					sendChat(msg.substring(5),sender);
				} else {
					appController.log("Chat command from " + sender.toString() + " FAILED, not identified.");
				}
			} else if(splitCommand.get(0).equals("challenge")) {
				/* Execute command "challenge" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					if(gamersInLobby().contains(sender.getGamer())) {
						if(!sender.getGamer().getName().equals(splitCommand.get(1))) {

							for(Gamer aGamer: gamersInLobby()) {
								if(aGamer.getName().equals(splitCommand.get(1))) {
									appController.log("Gamer " + aGamer.getName() + " challenged by " + sender.getGamer().getName());
									challenge(aGamer,sender.getGamer());
								}
							}
						} else {
							appController.log("Challenge command from " + sender.toString() + " FAILED, tries to challenge itself.");
						}
					} else {
						appController.log("Challenge command from " + sender.toString() + " FAILED, gamer is not in the lobby.");
					}
				} else {
					appController.log("Challenge command from " + sender.toString() + " FAILED, not identified.");
				}
			} else if(splitCommand.get(0).equals("challengeresponse")) {
				/* Execute command "challengeresponse" */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					if(gamersInLobby().contains(sender.getGamer())) {
						for(Gamer aGamer : gamersInLobby()) {
							if(aGamer != sender.getGamer() && aGamer.getName().equals(splitCommand.get(1))) {
								challengerList.remove(aGamer);
								challengedList.remove(sender.getGamer());
								if(splitCommand.get(2).equals("true")) {
									for(ConnectionController aConnection: connections) {
										if(aConnection.getGamer() == aGamer) {
											ArrayList<ConnectionController> toStartWith = new ArrayList<ConnectionController>();
											toStartWith.add(sender);
											toStartWith.add(aConnection);
											startGame(toStartWith);
										}
									}
									
								}
							}
						}	
					} else {
						appController.log("Challengeresponse command from " + sender.toString() + " FAILED, gamer is not in the lobby.");
					}
				} else {
					appController.log("Challengeresponse command from " + sender.toString() + " FAILED, not identified.");
				}
			} else {
				appController.log("Command from " + sender.toString() + " misunderstood: " + msg);
			}
		}
	}

	/**
	 * 
	 * @param connection
	 */
	public void addConnection(ConnectionController connection) {
		connections.add(connection);
	}

	/**
	 * 
	 * @param connection
	 */
	public void removeConnection(ConnectionController connection) {
		if(connections.contains(connection)) {
			kickGamer(connection.getGamer());
			appController.log(connection.toString() + " disconnects");
			if(waitingForGame.contains(connection)){
				waitingForGame.remove(connection);
			}
			connections.remove(connection);
			broadcastLobby();
		} else {
			appController.log("Tries to remove connection that does not exist");
		}
	}

	/**
	 * 
	 * @param challenged
	 * @param challenger
	 */
	private void challenge(Gamer challenged, Gamer challenger) {
		challengedList.add(challenged);
		challengerList.add(challenger);
		for(ConnectionController aConnection: connections) {
			if(aConnection.getGamer() == challenged) {
				aConnection.sendCommand("challenged " + challenger.getName());
			}
		}
	}

	/**
	 * 
	 * @param message
	 * @param sender
	 */
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

	/**
	 * 
	 * @param name
	 * @return
	 */
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

	/**
	 * 
	 */
	private void broadcastLobby() {

		String command = "lobby";
		for(Gamer aGamer: gamersInLobby()) {
			command = command + " " + aGamer.getName();
		}

		broadcastCommand(command);
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Gamer> gamersInLobby() {
		ArrayList<Gamer> gamersConnected = new ArrayList<Gamer>();
		for(ConnectionController aConnection: connections) {
			if(!aConnection.getGamer().getName().equals("[NOT CONNECTED]")) {
				gamersConnected.add(aConnection.getGamer());
			}
		}

		ArrayList<Gamer> gamersInGame = new ArrayList<Gamer>();
		for(Game aGame: games) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(!aGamer.getName().equals("[NOT CONNECTED]")) {
					gamersInGame.add(aGamer);
				}
			}
		}

		ArrayList<Gamer> gamersInLobby = (ArrayList<Gamer>) gamersConnected.clone();
		for(Gamer aGamer: gamersInGame) {
			if(gamersInLobby.contains(aGamer)) {
				gamersInLobby.remove(aGamer);
			}
		}
		return gamersInLobby;
	}

	/**
	 * 
	 * @param aGamer
	 * @return
	 */
	private boolean isInGame(Gamer aGamer) {
		boolean result = false;

		for(Game aGame: games) {
			for(Gamer inGameGamer: aGame.getGamers()) {
				if(aGamer == inGameGamer) {
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * 
	 */
	private void checkForGameStart() {
		appController.log("Checks if games can be started...");
		ArrayList<ConnectionController> startingWith = null;
		ArrayList<ConnectionController> with2min = new ArrayList<ConnectionController>();
		ArrayList<ConnectionController> with3min = new ArrayList<ConnectionController>();
		ArrayList<ConnectionController> with4min = new ArrayList<ConnectionController>();

		for(ConnectionController aConnection: waitingForGame) {
			if(aConnection.getGamer().getRequestedGameSize() <= 2) {
				with2min.add(aConnection);
			}
		}

		for(ConnectionController aConnection: waitingForGame) {
			if(aConnection.getGamer().getRequestedGameSize() <= 3) {
				with3min.add(aConnection);
			}
		}

		for(ConnectionController aConnection: waitingForGame) {
			if(aConnection.getGamer().getRequestedGameSize() <= 4) {
				with4min.add(aConnection);
			}
		}

		if(with4min.size() >= 4) {
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 4; i++) {
				startingWith.add(with4min.get(i));
			}
		} else if(with3min.size() >= 3) {
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 3; i++) {
				startingWith.add(with3min.get(i));
			}
		}  else if(with2min.size() >= 2) {
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 2; i++) {
				startingWith.add(with2min.get(i));
			}
		}

		if(startingWith != null) {
			startGame(startingWith);
		} else {
			appController.log("Not enough players to start a game");
		}
	}

	/**
	 * 
	 * @param players
	 */
	private void startGame(ArrayList<ConnectionController> players) {
		
		for(ConnectionController starting: players) {
			if(waitingForGame.contains(starting)) {
				waitingForGame.remove(starting);
			} else {
				appController.log("Server tries to start game with someone who does not want to start with join");
			}
		}
		
		if(players.size() > 1 && players.size() < 5) {
			String command = "startgame";
			String logEntry = "Starting a game between";
			int i = 0;

			for(ConnectionController player: players) {

				if(i < 4) {
					command = command + " " + player.getGamer().getName();
					logEntry = logEntry + ", " + player.toString();
				} else {
					players.remove(player);
				}
				i++;
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

		broadcastLobby();
	}

	/**
	 * 
	 * @param toBeKicked
	 */
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

		broadcastLobby();
	}

	/**
	 * 
	 * @param aGame
	 */
	private void nextTurn(Game aGame) {
		for(ConnectionController connection: connections) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					connection.sendCommand("turn " + aGame.getCurrent().getName());
				}
			}
		}
	}

	/**
	 * 
	 * @param aGame
	 */
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

		games.remove(aGame);
		broadcastLobby();
	}

	/**
	 * 
	 * @param aGame
	 * @param mover
	 * @param slot
	 */
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
	/**
	 * 
	 */
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