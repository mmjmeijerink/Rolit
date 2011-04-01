package rolit.client.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.*;

/**
 * De NetworkController zorgt voor de communicatie tussen de ApplicationController en de ServerSocket.
 * In de NetworkController komen de commando's van de server binnen en hij verstuurt ze naar de server.
 * 
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class NetworkController extends Thread {

	private ApplicationController	appController;
	private int						port;
	private InetAddress				host;
	private Socket					socket;
	private ArrayList<String>		lobby = new ArrayList<String>();
	private BufferedReader			in;
	private BufferedWriter			out;
	private String					startupCommand;
	
	/**
	 * Het IP adres waar de server op draait en de poort waar hij op luistert worden mee gegeven.
	 * Verder wordt zijn ApplicationController toegewezen en wordt er een String meegegeven die er als volgt uitziet: "connect <name>"
	 * 
	 * @param aHost het IP adres waar de server draait
	 * @param aPort de poort waar de server op luistert
	 * @param controller de ApplicationController waarmee deze NetworkController samenwerkt
	 * @param aStartupCommand het eerste command dat wordt verstuurt bij het starten van de NetworkController
	 */
	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller, String aStartupCommand) {
		super();
		appController = controller;
		port = aPort;
		host = aHost;
		startupCommand = aStartupCommand;
	}
	
	/**
	 * Start methode van de thread
	 * In deze methode wordt ervoor gezorgd dat er een medium is om de commando's tussen de server en de client te versturen en ontvangen
	 */
	public void run() {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			appController.log("Connecting to server on ip, " + host + ", and port, " + port + ".");
			
			sendCommand(startupCommand);

			while(true) {
				String ingelezen = in.readLine();
				if (ingelezen != null) {
					executeCommand(ingelezen);
				} else {
					disconnect();
				}
			}
		} catch (IOException e) {
			disconnect();
		}
	}
	
	/**
	 * Deze methode stuurt de commando's naar de server
	 * 
	 * @param msg het commando dat naar de server gestuurd moet worden
	 */
	public void sendCommand(String msg) {
		if(msg != null) {
			//appController.log("Sending commmand (" + msg + ") to server");
			try {
				out.write(msg + "\n");
				out.flush();
			} catch (IOException e) {
				appController.log("Sending commmand " + msg + " failed!");
			}
		}
	}
	
	/**
	 * Deze methode krijgt de commando's van de server en zorgt dat de actie wordt ondernomen
	 * @param msg het ontvangen command van de server
	 */
	@SuppressWarnings("unchecked")
	public void executeCommand(String msg) {
		System.out.println(msg);
		
		/*
		 * Het commando wordt eerst opgedeeld op basis van zijn spaties.
		 * Dit stoppen we vervolgens in een ArrayList zodat we er makkelijk mee overweg kunnen. 
		 */
		String[] regexCommand = (msg.split("\\s+"));
		ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));

		if(splitCommand.get(0).equals("ackconnect")) {
			/*
			 * Afhandeling 'ackconnect' commando
			 * Als de client een 'ackconnect' ontvangt is hij verbonden met de server,
			 * de naam die de client heeft vind hij op de 2e plaats in de lijst.
			 */
			appController.connectionAstablished(splitCommand.get(1));
			appController.log("Connected to server with name " + splitCommand.get(1) + ".");

		}
		else if(splitCommand.get(0).equals("startgame")) {
			/*
			 * Afhandeling 'startgame' commando
			 * Als de client een 'startgame' ontvangt, wordt dit doorgegeven aan de ApplicationController.
			 * Die start vervolgens een game op met de desbetreffende spelers.
			 */
			ArrayList<String> playerList = (ArrayList<String>)splitCommand.clone();
			playerList.remove(0);
			appController.startGame(playerList);

			appController.log("Game has started with " + msg.substring(splitCommand.get(0).length() + 1));
		}
		else if(splitCommand.get(0).equals("turn")) {
			/*
			 * Afhandeling 'turn' commando
			 * Als de client aan de beurt is wordt dit aan de ApplicationController doorgegeven.
			 * Anders wordt er alleen gelogd welke gebruiker er op dat moment aan de beurt is.
			 */
			if(splitCommand.get(1).equals(appController.getGamer().getName())) {
				appController.log("Your turn!");
				appController.myTurn();
			}
			else {
				appController.log("It is now " + splitCommand.get(1) + "'s turn.");
			}
		}
		else if(splitCommand.get(0).equals("movedone")) {
			/*
			 * Afhandeling 'move' commando
			 * Hier wordt aan de ApplicationController doorgegeven welke Gamer instantie welke zet heeft gedaan.
			 */
			appController.log(msg);
			Gamer gamer = null;
			for(Gamer aGamer : appController.getGame().getGamers()) {
				if(aGamer.getName().equals(splitCommand.get(1)))
					gamer = aGamer;
			}
			appController.handleMove(gamer, Integer.parseInt(splitCommand.get(2)));
		}
		else if(splitCommand.get(0).equals("endgame")) {
			/*
			 * Afhandeling 'endgame' commando
			 * Hier wordt het einde van een Rolit spelletje afgehandeld.
			 * De client krijgt van de server door hoeveel punten er zijn gescoord, de volgorde van de punten
			 * corronspondeert met de volgorde van de spelers in het 'startgame' commando.
			 * Er wordt een mooi bericht van de gescoorde punten door de verschillende spelers gemaakt en dit wordt aan de ApplicationController doorgegeven.
			 */
			appController.log("The game has ended");
			
			String message = "The game has ended:\n";
			int winnerIndex = 0;
			int winnerPoints = 0;
			boolean noWinner = false;
			
			for(int i = 0; i <  appController.getGame().getGamers().size() && i < splitCommand.size() + 1; i++) {
				String name = appController.getGame().getGamers().get(i).getName();
				if(name.equals(appController.getGamer().getName())) {
					name = "You have";
				} else {
					name = name + " has";
				}
				message = message + "" + name + " scored " + splitCommand.get(i+1) + " points. \n";
				if(Integer.parseInt(splitCommand.get(i+1)) > winnerPoints) {
					winnerPoints = Integer.parseInt(splitCommand.get(i+1));
					winnerIndex = i;
					noWinner = false;
				} else if(Integer.parseInt(splitCommand.get(i+1)) == winnerPoints) {
					noWinner = true;
				}
			}
			if(!noWinner) {
				message = message + "\n" + appController.getGame().getGamers().get(winnerIndex).getName() + " has won the match with " + winnerPoints + " points!";
			}
			appController.endGame(message);
		}
		else if(splitCommand.get(0).equals("kick")) {
			/*
			 * Afhandeling 'kick' commando
			 * Hier wordt gekeken welke speler er door de server gekicked is.
			 * Vervolgens wordt aan de ApplicationController doorgegeven welke gamer uit het spel verwijderd moet worden.
			 * Of als deze client gekicked wordt, wordt dat appart doorgegeven. 
			 */
			Gamer kicked = null;
			boolean found = false;
			
			for(int i = 0; i < appController.getGame().getGamers().size() && !found; i++) {
				if(appController.getGame().getGamers().get(i).getName().equals(splitCommand.get(1))) {
					kicked = appController.getGame().getGamers().get(i);
					found = true;
				}
			}
			if(found && kicked == appController.getGamer()) {
				appController.gotKicked();
			} else if(found) {
				appController.log(kicked.getName()+" got kicked.");
				appController.getGame().removeGamer(kicked);
			}
		}
		else if(splitCommand.get(0).equals("message")) {
			/*
			 * Afhandeling 'message' commando
			 * De chatberichten die de client van de server krijgt worden naar de ApplicaitonController gestuurd
			 */
			appController.handleChat(msg.substring(splitCommand.get(0).length() + splitCommand.get(1).length() + 2),splitCommand.get(1));
		}
		else if(splitCommand.get(0).equals("challenged")) {
			/*
			 * Afhandeling 'challenge' commando
			 * Aan de ApplicationController wordt doorgegeven dat en door wie de gebruiker gechallenged is.
			 */
			appController.challenged(splitCommand.get(1));
			appController.log("Challenge received for a game with " + msg.substring(splitCommand.get(0).length() + 1));
		}
		else if(splitCommand.get(0).equals("lobby")) {
			/*
			 * Afhandeling 'lobby' commando
			 * Als er een nieuwe lobby door de server gestuurd wordt, wordt de ApplicationController daarvan op de hoogte gebracht.
			 * De NetworkController houdt de lobby bij, de ApplicationController moet de view aanpassen.
			 */
			lobby.clear();
			for(int i = 1; i < splitCommand.size(); i++) {
				lobby.add(splitCommand.get(i));
			}
			appController.updateLobby();
		}
		else  {
			appController.log("Unknown command received: "+msg);
		}
	}

	/**
	 * Sluit de verbinding met de server of geeft aan dat deze nooit heeft bestaan.
	 * De ApplicationController wordt aangeroepen om de juiste views weer te geven.
	 */
	public void disconnect() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (NullPointerException e) {
			appController.log("Could not connect at all.");
		} catch (IOException e) {
			System.err.println("Exceptie catched: " + e.toString());
		}
		appController.connectionFailed();
	}
	
	/**
	 * Geeft de lobby, zoals doorgekregen van de server, terug.
	 * @return de lobby, zoals doorgekregen van de server
	 */
	public ArrayList<String> getLobby() {
		return lobby;
	}
}