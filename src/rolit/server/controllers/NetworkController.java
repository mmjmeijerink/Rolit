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
	 * Dit is de methode die er voor zorgt dat een binnen gekomen commando van een connectie op de juiste manier wordt afgehandeld.
	 * Dit alles volgens het afgesproken protocol. Deze methode maakt veel gebruik van de private functies die de NetworkController bevat.
	 * 
	 * executeCommand kan commando's uitvoeren die beginnen met: "connect, join, domove, chat, challenge, challengeresponse".
	 * 
	 * @require aCommand != null && sender != null
	 * 
	 * @param aCommand het uit te voeren commando, dient conform te zijn met het protocol van INF2
	 * @param sender de connectie die het commando binnen heeft gekregen.
	 */
	public void executeCommand(String aCommand, ConnectionController sender) {
		/*
		 * Wordt gecheckt of de precondities kloppen, anders gebeurt er niets. Omdat deze methode afhandelijk is van client input zal de applicatie niet crashen in deze methode.
		 * Wel zullen alle fouten gelogt worden zodat er goed gedebugt kan worden met onze server.
		 */
		if(aCommand != null && sender != null) {
			/*
			 * Het commando wordt met behulp van RegEx gesplits bij de spaties en in een array gestopt zodat
			 * het commando gemakkelijk op te delen is. Dit maakt het verwerken van het commando gemakkelijk voor de programmeur.
			 */
			String[] regexCommand = (aCommand.split("\\s+"));
			ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));

			/*
			 * Hier wort het commando connect afgehandeld.
			 * Dit gebeurt door een gamer van een connectie een unieke naam te geven.
			 * Als de naam die is mee gestuurd met connect niet uniek is wordt hier een cijfer achter geplakt.
			 * 
			 * Er wordt aan het eind van de afhandeling een commando ackconnect naar de sender gestuurd.
			 */
			if(splitCommand.get(0).equals("connect")) {
				/*
				 * Als er teveel parameters mee worden gegeven wordt het commando niet verder afgehandeld omdat er niet is gehandeld volgens het protocol.
				 */
				if(splitCommand.size() == 2) {
					/*
					 * Als er een nieuwe gamer aangemaakt wordt krijgt deze standaar de naam NOT CONNECTED en er wordt hier op gecheckt.
					 * Als de gamer al een naam toegewezen heeft gekregen wordt het verzoek tot connect niet afgehandeld.
					 * 
					 * Als een gamer de naam NOT CONNECTED mee geeft zou het niet afgehandeld worden omdat er een spatie in die naam zit en dit is niet conform met het protocol.
					 * Zo wordt deze mogelijke bug onmogelijk gemaakt.
					 */
					if(sender.getGamer().getName().equals("[NOT CONNECTED]")) {
						String possibleName = splitCommand.get(1);
						int i = 1;
						while(!checkName(possibleName)) {
							/*
							 * Als de naam die is mee gegeven met connect al bestaat bij een specifieke gamer dan wordt er een nieuwe naam gecreerd
							 * met een extra cijfer er achter en vervolgens wordt deze opnieuw gecheckt.
							 */
							possibleName = splitCommand.get(1)+Integer.toString(i);
							i++;
						}
						/*
						 * Als de naam uniek blijkt te zijn wordt er het commando ackconnect plus die naam naar de connectie gestuurt.
						 * Vervolgens wordt de naam ook toegewezen aan de instantie van de gamer aan der server kant
						 * En er wordt een broadcastLobby() command gestuurt omdat er iets in de lobby is veranderd. Er is namelijk een nieuwe gamer in de lobby gekomen.
						 */
						sender.getGamer().setName(possibleName);
						appController.log("Connection from " + sender.getSocket().getInetAddress() + " has identified itself as: " + possibleName);
						sender.sendCommand("ackconnect "+possibleName);
						broadcastLobby();
					} else {
						appController.log(sender.toString() + " tries to but is already identified.");
					}
				} else {
					appController.log("Connect command from " + sender.toString() + " FAILED, has more than 1 parameter: " + aCommand);
				}
				
			/*
			 * Hier wordt het commando join afgehandeld.
			 * Er wordt gekeken of de sender zich al heeft geidentificeerd dmv connect commando.
			 * Er wordt gekeken of de sender niet al toevallig in een game zit.
			 * En er wordt gecheckt of het commando conform is met het protocol.
			 * Als alles klopt wordt de gebruiker in een lijstje gezet met gamers die aan het wachten zijn op een game.
			 * Er wordt elke keer als een join commano uitgevoerd wordt gecheckt of een game gestart kan worden met de private methode checkForGameStart();
			 */
			} else if(splitCommand.get(0).equals("join")) {
				/*
				 * Checkt of de gamer zich al heeft geidentificeerd, als dit niet zo is wordt het commando genegeerd.
				 */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					/*
					 * Wordt gecheckt of de gamer al in game zit, als dit zo is wordt dit commando genegeerd.
					 * Er kan namelijk alleen een join commando worden uitgevoerd als de gamer in de lobby zit.
					 * Als een gamer 2 games te gelijk wil spelen zal hij 2 keer een client op moeten starten.
					 */
					if(!isInGame(sender.getGamer())) {
						/*
						 * Er wordt gecheckt of het aantal argumenten van het join commando klopt.
						 * Dit hoort 1 artument te zijn namelijk een getal tussen de 2 en de 4.
						 */
						if(splitCommand.size() == 2) {
							try {
								int requestedSize = Integer.parseInt(splitCommand.get(1));
								/*
								 * Er wordt hier de requestedGameSize property geset op de gamer van de sender.
								 * Deze methode stuurt false terug als het meegegeven getal niet tussen de 2 en 4 zit en dus niet uitgevoerd kan worden.
								 * Als dit het geval is wordt dit commando genegeerd.
								 */
								if(sender.getGamer().setRequestedGameSize(requestedSize)) {
									/*
									 * Als deze gebruiker in de lobby zit en al een keer een join commando had verstuurd kan de gebruiker dit gewoon nog een keer doen.
									 * Hij wordt dan alleen niet opnieuw toegevoegd aan de waitingForGame lijst. Zijn requested game size wordt daarentegen wel veranderd.
									 */
									if(!waitingForGame.contains(sender)) {
										waitingForGame.add(sender);
									}
									appController.log(sender.toString() + " wants to join with " + splitCommand.get(1) + " players");
									/*
									 * Er wordt met deze private metode gecheckt of er een nieuwe game gestart kan worden.
									 */
									checkForGameStart();
								} else {
									appController.log("Join command from " + sender.toString() + " FAILED, 1st parameter between [2-4]: " + aCommand);
								}
							} catch(NumberFormatException e) {
								appController.log("Join command from " + sender.toString() + " FAILED, 1st parameter needs to be a int: " + aCommand);
							}
						} else {
							appController.log("Join command from " + sender.toString() + " FAILED, has more than 1 parameter: " + aCommand);
						}
					} else {
						appController.log("Join command from " + sender.toString() + " gamers is already ingame. " + aCommand);
					}
				} else {
					appController.log("Join command from " + sender.toString() + " FAILED, not identified.");
				}
				
			/*
			 * In dit stuk code wordt het commando domove afgehandeld.
			 * Er wordt checkekt of de verstuurde wel geidentificeerd is, in een game zit en
			 * aan de beurt is, de zet juist is en of het commando conform met het protocol verstuurd wordt.
			 * 
			 * Als de verstuurder een van de voorwaarde overschreid wordt hij in sommige gevallen gekickt,
			 * zoals het protocol voorschrijft.
			 * 
			 */	
			} else if(splitCommand.get(0).equals("domove")) {
				/*
				 * Er wordt gecheckt of de gamer geidentificeerd is met het commando connect [naam]
				 * Als dit niet zo is wordt het commando genegeerd.
				 */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					/*
					 * Er wordt gecheckt of het commando domove het juiste aantal parametes heeft.
					 * Dit zou 1 paramet moeten zijn met de plaatst op het bord waar de gamer zijn bal wil zetten.
					 */
					if(splitCommand.size() == 2) {
						Game participatingGame = null;
						for(Game aGame: games) {
							/*
							 * Er wordt hier in alle games gekeken of de speler wel in een game zit
							 * en zo ja, in welke. Die game wordt opgeslagen in de variabele participatingGame
							 */
							for(Gamer aGamer: aGame.getGamers()) {
								if(aGamer == sender.getGamer()) {
									participatingGame = aGame;
								}
							}
						}
						/*
						 * Als een gamer in een game zit volgens zich zelf en volgens de lijst met gamen die de network controller bijhoud wordt er verder gekeken.
						 * Ander wordt het commando genegeerd.
						 */
						if(participatingGame != null && sender.getGamer().isTakingPart()) {
							/*
							 * Als een gamer aan de beurt is wordt er verder gekeken, als dit niet het geval is wordt hij onmiddelijk gekickt.
							 * Dit wordt allemaal netjes gelogt en de server beheerder kan dit goed bijhouden.
							 */
							if(participatingGame.getCurrent() == sender.getGamer()) {
								try {
									int slotToSet = Integer.parseInt(splitCommand.get(1));
									/*
									 * Checkt de move met de zojuist geparste int. Als de zet onmogelijk blijkt te zijn zal de gamer gekickt worden,
									 * conform met het protocol.
									 */
									if(participatingGame.checkMove(slotToSet, sender.getGamer())) {
										/*
										 * De zet wordt regeristreerd in de game, vervolgens wordt met behulp van de private methode moveDone de juiste commando's
										 * gestuurd naar alle deelnemende spelers.
										 */
										moveDone(participatingGame,sender.getGamer(),slotToSet);
										participatingGame.doMove(slotToSet, sender.getGamer());
										appController.log(sender.toString() + " has set " + splitCommand.get(1) + " in his game.");
									} else {
										appController.log("Domove command from " + sender.toString() + " FAILED, move is impossible " + aCommand);
										kickGamer(sender.getGamer());
									}
								} catch(NumberFormatException e) {
									appController.log("Domove command from " + sender.toString() + " FAILED, 1st parameter needs to be a int: " + aCommand);
								}
							} else {
								appController.log("Domove command from " + sender.toString() + " FAILED, does not have the turn.");
								kickGamer(sender.getGamer());
							}
						} else {
							appController.log("Domove command from " + sender.toString() + " FAILED, player is not in game.");
						}
					} else {
						appController.log("Domove command from " + sender.toString() + " FAILED, has more than 1 parameter: " + aCommand);
					}
				} else {
					appController.log("Domove command from " + sender.toString() + " FAILED, not identified.");
				}
				
			/*
			 * Hier wordt het commando chat afgehandeld.
			 * 
			 * Er wordt alleen maar gecheckt of de sender zich heeft geidentificeerd.
			 * 
			 */
			} else if(splitCommand.get(0).equals("chat")) {
				/*
				 * Als de sender zich niet heeft geidentificeerd zal het commando simpel weg niet worden afgehandeld.
				 */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					appController.log("Chat command from " + sender.toString() + " send: " + aCommand.substring(5));
					/*
					 * Met behulp van de private methode sendChat(); zal het chat bericht naar de juiste personen worden gestuurd,
					 * als de persoon in game is gaat de chat naar de deelnemers en als de persoon in de lobby zit gaat de chat naar de lobby gasten.
					 */
					sendChat(aCommand.substring(5),sender);
				} else {
					appController.log("Chat command from " + sender.toString() + " FAILED, not identified.");
				}
			
				
			/*
			 * Hieronder staat de code voor het afhandelen van het challenge commando.
			 * 
			 * Er wordt gecheckt of de sender zich heeft geidentificeerd, of hij in de lobby zit en of hij niet zichzelf challengd.
			 */
			} else if(splitCommand.get(0).equals("challenge")) {
				/*
				 * Als de sender zich niet heeft geidentificeerd zal het commando simpel weg niet worden afgehandeld.
				 */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					/*
					 * Als de sender zich in de lobby bevind gaan we verder, als dit niet zo is wordt dit commando niet afgehandeld.
					 */
					if(gamersInLobby().contains(sender.getGamer())) {
						/*
						 * Als de sender zichzelf probeerd te challengen zal er niets gebeuren.
						 */
						if(!sender.getGamer().getName().equals(splitCommand.get(1))) {
							for(Gamer aGamer: gamersInLobby()) {
								/*
								 * Er wordt gecheckt of de techallenge speler in de lobby zit, zo ja wordt dit afgehandel door de private methode challenge();
								 * zo nee? gebeurt er helemaal niets.
								 */
								if(aGamer.getName().equals(splitCommand.get(1))) {
									appController.log("Gamer " + aGamer.getName() + " challenged by " + sender.getGamer().getName());
									/*
									 * Zie de beschrijving van de private methode challange voor verdere beschrijving.
									 */
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
			/*
			 * Hieronder wordt het challengeresponse commando afgehandeld.
			 */
			} else if(splitCommand.get(0).equals("challengeresponse")) {
				/*
				 * Als de sender zich niet heeft geidentificeerd zal het commando simpel weg niet worden afgehandeld.
				 */
				if(!sender.getGamer().getName().equals("[NOT CONNECTED]")) {
					/*
					 * Er wordt gecheckt of de sender zich wel in de lobby bevind.
					 */
					if(gamersInLobby().contains(sender.getGamer())) {
						for(Gamer aGamer : gamersInLobby()) {
							/*
							 * Er wordt door alle gamers in de lobby geloopt
							 * als de challenger gevonden is wordt het antwoord geprossest.
							 */
							if(aGamer != sender.getGamer() && aGamer.getName().equals(splitCommand.get(1))) {
								challengerList.remove(aGamer);
								challengedList.remove(sender.getGamer());
								/*
								 * Als het antwoord true is zal er een game gestart worden.
								 * de aanvrager en de antwoorder zullen samen in een game 1 tegen 1 beginnen.
								 */
								if(splitCommand.get(2).equals("true")) {
									for(ConnectionController aConnection: connections) {
										if(aConnection.getGamer() == aGamer) {
											/*
											 * Er wordt netjes door alle connections heen geloopt om op deze manier een array 
											 * van deelnemende connections op te bouwen om vervolgend mee te geven aan de private methode
											 * startGame();
											 * 
											 * Voor verdere beschrijving zie startGame();
											 */
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
				
			/*
			 * Als een commando niet wordt begrepen wordt dit netjes gelogt, er gebeurt verder niets om crashes te voorkomen. 
			 */
			} else {
				appController.log("Command from " + sender.toString() + " misunderstood: " + aCommand);
			}
		}
	}

	/**
	 * 
	 * @param connection
	 */
	private void addConnection(ConnectionController connection) {
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