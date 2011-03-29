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
	 * Met deze private methode wordt een connectie toegevoegd aan het lijstje dat de NetworkController bijhoudt.
	 * @param connection Toe te voegen ConnectionController
	 */
	private void addConnection(ConnectionController connection) {
		connections.add(connection);
	}

	/**
	 * Met deze public methode wordt een connectie verbroken en verwijdert en op de juiste manier afgehandeld.
	 * Als een connectie nog in een game zit wordt de gamer bijvoorbeeld gekickt zodat de rest in de game weet
	 * dat de gamer niet meer deelneemt aan de game.
	 * Aan het eind van deze functie wordt ook de lobby gebroadcast.
	 * 
	 * Deze metode kan goed aangeroepen worden vanaf de ConnectionController zelf.
	 * 
	 * @require connection != null && this.connections.contains(connection);
	 * @ensure !this.connections.contains(connection);
	 * @param connection Te verwijderen connectie.
	 */
	public void removeConnection(ConnectionController connection) {
		if(connections.contains(connection)) {
			/*
			 * Kick de gamer als hij nog in een game zit.
			 */
			kickGamer(connection.getGamer());
			appController.log(connection.toString() + " disconnects");
			/*
			 * Als een gamer het join commando heeft gebruikt moet hij uit de lijst
			 * met wachtende gamers gehaalt worden.
			 */
			if(waitingForGame.contains(connection)){
				waitingForGame.remove(connection);
			}
			connections.remove(connection);
			/*
			 * Nadat een gamer weg is veranderd mogelijk de inhoud van de lobby en moet deze
			 * geupdate worden voor alle clients.
			 */
			broadcastLobby();
		} else {
			appController.log("Tries to remove connection that does not exist");
		}
	}

	/**
	 * Met deze methode kan een specifieke gamer gechallenged worden.
	 * 
	 * @require challenged != null && challenger != null
	 * 
	 * @param challenged De te challange gamer
	 * @param challenger De gamer die gechallenged heeft
	 */
	private void challenge(Gamer challenged, Gamer challenger) {
		/*
		 * Deze lijsten houden bij welke gamers er challenge en gechallenged worden,
		 */
		challengedList.add(challenged);
		challengerList.add(challenger);
		for(ConnectionController aConnection: connections) {
			/*
			 * Zoek in alle connections naar voor de challenged gamer.
			 */
			if(aConnection.getGamer() == challenged && gamersInLobby().contains(challenged) && gamersInLobby().contains(challenger)) {
				aConnection.sendCommand("challenged " + challenger.getName());
				/*
				 * Commando wordt netjes verstuur naar de te challenge speler
				 */
			}
		}
		/*
		 * Als de challenged gamer niet bestaat of niet in de lobby zit
		 * wordt de aanroep genegeerd en gebeurt er niets.
		 */
	}

	/**
	 * Met deze method wordt een chat bericht verstuud naar de juiste gamers.
	 * Als de verstuurder in game zit wordt het chat bericht verstuurd naar alle gamers in die game,
	 * Als de verstuurder in de lobby zit wordt het chat bericht verstuurd naar iedereen in de lobby.
	 * 
	 * @param message Het te versturen bericht
	 * @param sender De verzender van het bericht
	 */
	private void sendChat(String message, ConnectionController sender) {
		Game participatingGame = null;
		for(Game aGame: games) {
			/*
			 * Checkt of de gamer onderdeel is van een game
			 * En zoja welke game.
			 */
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == sender.getGamer()) {
					participatingGame = aGame;
				}
			}
		}

		if(participatingGame == null) {
			/*
			 * Als en gamer niet in een game zit wordt het bericht
			 * naar alle connections gestuurd die niet in een game zitten.
			 * Dit wordt gecheckt mbv de hulp ivar isTakingPart van Gamer.
			 */
			for(ConnectionController aConnection: connections) {
				if(!aConnection.getGamer().isTakingPart()) {
					aConnection.sendCommand("message "+sender.getGamer().getName()+" "+message);
				}
			}
		} else {
			/*
			 * Als een gamer wel in een game zit
			 * wordt het bericht versrtuurd naar iedereen die ook in de game zitten.
			 * 
			 * De dubbele loops zijn constant nodig om de connectie bij een specifieke gamer te zoeken.
			 */
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
	 * Een private methode voor het checken of een bepaalde naam uniek is of niet.
	 * @param name De te checken naam
	 * @return true als naam uniek is en false als de naam al bestaat
	 */
	private boolean checkName(String name) {
		boolean result = true;

		if(name != null) {
			for(ConnectionController client: connections){
				/*
				 * Loopt door alle connections om te checken of de namen niet van de
				 * gekoppelde gamers toevallig gelijk zijn of niet.
				 * 
				 */
				if(client.getGamer().getName().equals(name)) {
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Een methode die er voor zorgt dat het juiste lobby commando naar iedereen gestuurd wordt.
	 * Deze methode dient aangeroepen te worden als er iets in de lobby veranderdt.
	 */
	private void broadcastLobby() {

		String command = "lobby";
		for(Gamer aGamer: gamersInLobby()) {
			/*
			 * Elke gamer in de Lobby opvragen dmv de hulp lijst
			 * met alle gamers in de lobby.
			 */
			command = command + " " + aGamer.getName();
		}
		/*
		 * Roept het commando broadcastCommand aan omdat
		 * alle gamers die in game zitten ook moeten weten wie er allemaal in de lobby zitten.
		 */
		broadcastCommand(command);
	}

	/**
	 * Deze methode geeft een lijst terug met alle gamers die in de Lobby zitten.
	 * @return ArrayList met alle Gamers die in de Lobby zitten en geidentificeerd zijn.
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<Gamer> gamersInLobby() {
		ArrayList<Gamer> gamersConnected = new ArrayList<Gamer>();
		for(ConnectionController aConnection: connections) {
			/*
			 * Maak eerst een lijstje met geidentificeerde connecties.
			 */
			if(!aConnection.getGamer().getName().equals("[NOT CONNECTED]")) {
				gamersConnected.add(aConnection.getGamer());
			}
		}

		ArrayList<Gamer> gamersInGame = new ArrayList<Gamer>();
		for(Game aGame: games) {
			/*
			 * Vervolgens is het gemakkleijk om te checken welke gamers er eigenlijk in game zitten.
			 */
			for(Gamer aGamer: aGame.getGamers()) {
				if(!aGamer.getName().equals("[NOT CONNECTED]")) {
					/*
					 * Beetje overbodig maat toch veilig, checken of gamers in de game
					 * niet toevallig niet ge identificieer zijn.
					 */
					gamersInGame.add(aGamer);
				}
			}
		}

		ArrayList<Gamer> gamersInLobby = (ArrayList<Gamer>) gamersConnected.clone(); // Schijnbaar doe java hier moeilijk over, surpress warning toegevoegd.
		for(Gamer aGamer: gamersInGame) {
			/*
			 * Nu pakken we de eerste lijst en gaan alle persoonen die ook in de tweede lijst staan
			 * er uit halen. Op deze manier houden we een lijst met gamers in de Lobby over.
			 */
			if(gamersInLobby.contains(aGamer)) {
				gamersInLobby.remove(aGamer);
			}
		}
		return gamersInLobby;
	}

	/**
	 * Deze methode checkt of een gamer toevallig in een game zit of niet.
	 * @param aGamer de gamer te checken.
	 * @return geeft true als een gamer in game zit en geeft false als een gamer neit in game zit.
	 */
	private boolean isInGame(Gamer aGamer) {
		boolean result = false;

		for(Game aGame: games) {
			/*
			 * Loopt door alle games heen en checkt of de gamer er in zit.
			 */
			for(Gamer inGameGamer: aGame.getGamers()) {
				if(aGamer == inGameGamer) {
					/*
					 * Als de gamer gevonden is zet hij het result op true.
					 */
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Deze methode checkt of een game gestart kan worden,
	 * hij kijkt naar alle mensen die join hebben gestuurd en zorgt er voor
	 * dat den juiste combinatie gemaakt wordt. De parameter van het join commando
	 * is een minimum aantal.
	 * Dus als Sjaak Join 2 stuurt
	 * en Truus Join 3 stuurt
	 * en Miep Join 3 stuurt
	 * wordt een game gestart met deze 3 spelers ondanks dat Sjaak met 2 spelers wilde starten.
	 */
	private void checkForGameStart() {
		appController.log("Checks if games can be started...");
		/*
		 * Maakt 3 lijsten aan voor mogelijke 2 spelers games, 3 spelergames en 4 spelergames.
		 */
		ArrayList<ConnectionController> startingWith = null;
		ArrayList<ConnectionController> with2min = new ArrayList<ConnectionController>();
		ArrayList<ConnectionController> with3min = new ArrayList<ConnectionController>();
		ArrayList<ConnectionController> with4min = new ArrayList<ConnectionController>();

		for(ConnectionController aConnection: waitingForGame) {
			/*
			 * Stelt een lijst samen met mogelijke 2 speler games.
			 */
			if(aConnection.getGamer().getRequestedGameSize() <= 2) {
				with2min.add(aConnection);
			}
		}

		for(ConnectionController aConnection: waitingForGame) {
			/*
			 * Stelt een lijst samen met mogelijke 3 speler games.
			 */
			if(aConnection.getGamer().getRequestedGameSize() <= 3) {
				with3min.add(aConnection);
			}
		}

		for(ConnectionController aConnection: waitingForGame) {
			/*
			 * Stelt een lijst samen met mogelijke 4 speler games.
			 */
			if(aConnection.getGamer().getRequestedGameSize() <= 4) {
				with4min.add(aConnection);
			}
		}

		if(with4min.size() >= 4) {
			/*
			 * Kijkt eerst of er een 4 speler game gestart kan worden.
			 * En stelt een startingWith lijst samen voor de startGame methode.
			 */
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 4; i++) {
				startingWith.add(with4min.get(i));
			}
		} else if(with3min.size() >= 3) {
			/*
			 * Kijkt of er een 3 speler game gestart kan worden.
			 * En stelt een startingWith lijst samen voor de startGame methode.
			 */
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 3; i++) {
				startingWith.add(with3min.get(i));
			}
		}  else if(with2min.size() >= 2) {
			/*
			 * Kijkt of er een 2 speler game gestart kan worden.
			 * En stelt een startingWith lijst samen voor de startGame methode.
			 */
			startingWith = new ArrayList<ConnectionController>();
			for(int i = 0; i < 2; i++) {
				startingWith.add(with2min.get(i));
			}
		}

		if(startingWith != null) {
			/*
			 * Als er een mogelijke game gestart kan worden doet hij dat met startGame();
			 */
			startGame(startingWith);
		} else {
			appController.log("Not enough players to start a game");
		}
	}

	/**
	 * Deze methode start een game tussen de ingevoerde connecties.
	 * 
	 * De methode zorgt er voor dat de connecties en gamers netjes uit de wachtende lijstjes verwijdert worden.
	 * Zodat er geen conflicten ontstaan.
	 * 
	 * @param players de connecties waarmee een game moet starten.
	 */
	private void startGame(ArrayList<ConnectionController> players) {
		
		
		/*
		 * Checkt of er niet meer dan 4 players me gegeven worden.
		 * Zo ja wordt er geen game gestart.
		 */
		if(players.size() > 1 && players.size() < 5) {
			/*
			 * Stelt een startgame commando
			 * en een mooie log entry samen.
			 */
			String command = "startgame";
			String logEntry = "Starting a game between";

			for(ConnectionController player: players) {
				command = command + " " + player.getGamer().getName();
				logEntry = logEntry + ", " + player.toString();
			}
			
			for(ConnectionController starting: players) {
				/*
				 * Gooit de gamers allemaal uit de waitingForGame list als ze het join commando gebruikt hebben.
				 */
				if(waitingForGame.contains(starting)) {
					waitingForGame.remove(starting);
				} else {
					/*
					 * Dit is natuurlijk mogelijk als een gamer de challenge functie heeft gebruikt.
					 */
					appController.log("Server tries to start game with someone who does not want to start with join");
				}
			}
			
			appController.log(logEntry);
			ArrayList<Gamer> gamers = new ArrayList<Gamer>();

			for(ConnectionController player: players) {
				/*
				 * Stel een mooie lijst samen voor het Game object dat bij de Game moet horen.
				 */
				player.sendCommand(command);
				gamers.add(player.getGamer());
			}

			Game aGame = new Game(gamers);
			/*
			 * Maakt gebruik van het Observer patroon binnen Game zodat NetworkController het weet als er iets in Game vetandert.
			 */
			aGame.addObserver(this);
			games.add(aGame);
			/*
			 * Zorgt er voor dat iemand de beurt krijgt.
			 */
			nextTurn(aGame);
		}
		/*
		 * Aangezien er iets is veranderd in de samenstelling van de lobby moet er een nieuwe lobby gebroadcast worden.
		 */
		broadcastLobby();
	}

	/**
	 * Met dit commando kan er gemakkelijk een gamer uit zijn spel gekickt worden.
	 * De methode viegeliert zelf uit in welke game de gamer zich bevindt.
	 * @param toBeKicked de te kicken gamer.
	 */
	private void kickGamer(Gamer toBeKicked) {
		appController.log("Kicking " + toBeKicked.getName() + " ...");
		/*
		 * Kijkt in welke game de gamer zit.
		 */
		Game participatingGame = null;
		for(Game aGame: games) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == toBeKicked) {
					/*
					 * Als de gamer gevonden is wordt de betreffende game opgeslagen
					 * voor later gebruik.
					 */
					participatingGame = aGame;
				}
			}
		}	

		if(participatingGame != null) {
			/*
			 * Als er een game gevonden is wordt een commando samengestelt en naar
			 * alle gamers in die game gestuurd zodat zij hun game model aan kunnen passen.
			 * 
			 */
			for(ConnectionController connection: connections) {
				for(Gamer aGamer: participatingGame.getGamers()) {
					if(aGamer == connection.getGamer()) {
						connection.sendCommand("kick " + toBeKicked.getName());
					}
				}
			}
			/*
			 * Zorgt er voor dat het Game model van de server geen verwijzing meer heeft naar de gekickte speler.
			 * Het game model houdt ook een lijst met beginspelers bij zodat het endgame commando nog wel goed
			 * doorgegeven kan worden.
			 */
			participatingGame.removeGamer(toBeKicked);
			/*
			 * Geeft de gamer een kleur van Slot.EMPTY,
			 * dit zorgt er voor dat de gamer.isTakingPart() == false
			 */
			toBeKicked.setColor(Slot.EMPTY);
		}
		/*
		 * Omdat er mogelijk iets is verandert in de samenstelling van de lobby,
		 * namelijk de gekickte gamer zit waarscheinlijk weer in de lobby, wordt er een lobby
		 * commando gebroadcast.
		 */
		broadcastLobby();
	}

	/**
	 * Dit commando zort dat de volgende gamer de beurt krijgt in een bepaalde game.
	 * @param aGame de game die het betreft.
	 */
	private void nextTurn(Game aGame) {
		for(ConnectionController connection: connections) {
			/*
			 * Zoekt de connections bij de gamers uit een bepaalde game.
			 */
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					/*
					 * Als de conenctions gevonden bij de gamer zijn wordt er vervolgens een commando naar
					 * iedereen in die betreffende game gestuurd.
					 */
					connection.sendCommand("turn " + aGame.getCurrent().getName());
				}
			}
		}
	}

	/**
	 * Deze private metode zorgt er voor dat een betreffende game beeindigt wordt en
	 * dit juist afgehandeldt wordt in de model aan de server kant en stuurt het juiste commando naar
	 * iedereen in die game.
	 * @param aGame
	 */
	private void endGame(Game aGame) { 
		String command = "endgame";
		String logEntry = "Ending a game between";
		for(Gamer aGamer: aGame.getStartedWith()) {
			/*
			 * Stelt netjes een commando samen.
			 */
			if(aGame.getGamers().contains(aGamer)) {
				command = command + " " + aGame.getPointsOf(aGamer);
			} else {
				/*
				 * Als een gamer gekickt is tijdens de game krijgt hij 0 punten.
				 */
				command = command + " 0";
			}
			/*
			 * Stelt een gebrijpelijke log entry samen.
			 */
			logEntry = logEntry + ", " + aGamer.getName() + " (" + aGame.getPointsOf(aGamer) + ")";

		}
		appController.log(logEntry);

		for(ConnectionController connection: connections) {
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					/*
					 * Stuurt het commando naar de aangaande gamers.
					 */
					connection.sendCommand(command);
					/*
					 * Zet de kleur van de gamers op Slot.EMPTY zodat
					 * aGamer.isTakingPart() = false wordt.
					 */
					aGamer.setColor(Slot.EMPTY);
				}
			}
		}

		/*
		 * Haalt de game uit de lijst en zorgt er voor dat de lobby
		 * gebroadcast wordt zodat iedereen weer op de hoogte is van de gamers in de lobby
		 * die zojuist uit de game zijn gekomen.
		 */
		games.remove(aGame);
		broadcastLobby();
	}

	/**
	 * Deze methode handelt een move binnen gekregen van een gamer af.
	 * Als de move goed is gekeurt wordt deze methode aangeroepen om vervolgens de move
	 * te broadcasten naar alle gamers in de game.
	 * @param aGame
	 * @param mover
	 * @param slot
	 */
	private void moveDone(Game aGame, Gamer mover, int slot) {
		for(ConnectionController connection: connections) {
			/*
			 * Loopt door alle connections heen om de connecties te zoeken voor de aGame.
			 */
			for(Gamer aGamer: aGame.getGamers()) {
				if(aGamer == connection.getGamer()) {
					/*
					 * Stuurt movedone commando naar alle gamers in de game toe.
					 */
					connection.sendCommand("movedone " + mover.getName() + " " + slot);
				}
			}
		}
	}

	@Override
	/**
	 * Deze methode wordt aangeroepen door Observable objecten.
	 * In dit geval is dat alleen alle game objecten in de games lijst
	 * 
	 * Als het bord van een game vol is wordt het endgame commando aangeroepen
	 * en anders wordt de volgende zet afgegeven.
	 */
	public void update(Observable arg0, Object arg1) {
		if(arg0.getClass().equals(Game.class)) {
			Game game = (Game) arg0;
			/*
			 * Print netjes de situatie op het bord voor de log.
			 */
			appController.log(game.getBoard().toString());
			if(game.isEnded()) {
				/*
				 * Als de game af is stuurt hij het endgame commando naar alle deelnemende gamers
				 */
				endGame(game);
			} else {
				/*
				 * Als de game niet af is wordt de volgende beurt toegewezen.
				 */
				nextTurn(game);
			}
		}
	}
}