package rolit.server.controllers;

import java.io.*;
import java.net.*;
import rolit.server.models.*;
import rolit.sharedModels.*;

/**
 * De ConnectionController beheert een connectie met een specifieke client. Je kan via de ConnectionController commando's sturen naar een client en commando's ontvangen van een client.
 * Een connectie wordt gekoppelt aan een gamer en vandaar heeft een instantie van ConnectionController ook automatisch een gamer instantie er aan gekoppelt.
 * Elke connectie heeft zijn eigen Thread zodat er constant naar elke client kan worden geluisterd.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class ConnectionController extends Thread {

	private NetworkController   network;
	private Socket           	socket;
	private BufferedReader   	in;
	private BufferedWriter   	out;

	private boolean			 	running;
	private LoggingInterface	log;
	private Gamer				gamer;

	/**
	 * Tijdens het instantieren van een ConnectionController worden de instantie variablenen toegewezen en wordt er een socket verbinding opgezet.
	 * Dit gebeurt met behulp van Input en Output streams van de Socket.
	 * Als een ConnectionController geinstancieerd wordt kan er vanuit worden gegaan dat een gamer aangemaakt is.
	 * 
	 * @require aNetwork != null && aSocket != null
	 * @ensure this.getGamer() !=  null
	 * 
	 * @param aNetwork 		Geef de NetworkController mee waaraan deze connectie gekoppelt is zodat de connectie zich zelf er van kan verwijderen als dat nodig is. En daarnaast moet de connectie ook commando's doorsturen naar de NetworkController om deze af te handelen.
	 * @param aSocket		Geef de opgezette socket verbinding mee zodat er Input en Output streams mee gegenereerd kunnen worden.
	 * @param aLog			Als de ConnectionController iets te loggen heeft moet dat gebeuren met deze LoggingInterface.
	 * @throws IOException	Als er iets fout gaat met het maken van de Streams throwt deze functie een IOException.
	 */
	public ConnectionController(NetworkController aNetwork, Socket aSocket, LoggingInterface aLog) throws IOException,NullPointerException {
		
		if(aNetwork != null) {
			network = aNetwork;
		} else {
			/*
			 * Als een gebruiker niet voldoet aan de preconditie wordt er een exception getrowt.
			 */
			throw new NullPointerException();
		}
		
		if(aSocket != null) {
			socket = aSocket;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} else {
			/*
			 * Als een gebruiker niet voldoet aan de preconditie wordt er een exception getrowt.
			 */
			throw new NullPointerException();
		}
		
		/*
		 * Running is een hulp variable die er voor zorgt dat de loop in run() gestopt wordt als disconnect aangeroepen wordt.
		 */
		running = true; 
		log = aLog;
		gamer = new Gamer();
	}
	
	/**
	 * Dit is de methode die gaat draaien op het moment dat de connectie en de thread juist is gestart. Dit kan gedaan worden met this.start();
	 * Als de connectie door de gebruiker wordt verbroken zorgt deze methode dat dit jusit afgehandeld wordt door de server door middel van een private methode.
	 */
	public void run() {
		String inlezen = null;
		
		while(this.running) {
			try {
				inlezen = in.readLine();  
			} catch (IOException e) {
				inlezen = null;
			}
			if (inlezen != null) {
				/*
				 * Als er iets zinnigs in is gelezen mag dit uitgevoerd afgehandeld worden door de NetworkController.
				 */
				network.executeCommand(inlezen,this);
			} else {
				/*
				 * Sluit de connectie als het niet meer mogelijk is iets binnen ter krijgen van de connectie.
				 */
				disconnect();
			}
		}
	}
	
	/**
	 * Met deze methode kan een commando over de connectie worden gestuurd naar de client die aan deze connectie hangt.
	 * @param aCommand Het specifieke string commando, dient conform te zijn met het protocol van INF2.
	 */
	public void sendCommand(String aCommand) {
		if(aCommand != null) {
			if(log != null) {
				log.log("Sending commmand (" + aCommand + ") to " + toString());
			}
			try {
				/*
				 * Over de streamwriter wordt een commando gestuurd en daarna wordt deze geflusht zodat hij mooi klaar staat voor het volgende commando.
				 */
				out.write(aCommand + "\n");
				out.flush();
				
			} catch (IOException e) {
				if(log != null) {
					/*
					 * Als het commando faalt wordt dit netjes in de log weergegeven.
					 */
					log.log("Sending commmand " + aCommand + " failed!");
				}
			}
		}
	}
	
	/**
	 * Met deze metode zorg je ervoor dat de connectie gesloten wordt en de thread ophout met draaien.
	 * Deze methode zal er ook voor zorgen dat de network controller er bericht van krijgt dat de gebruiker de connectie heeft gesloten.
	 */
	private void disconnect() {
		network.removeConnection(this);
		this.running = false;
	}
	
	/**
	 * Deze methode geeft de gamer terug die onlosmakelijk aan deze connectie gekoppelt is.
	 * Op deze manier kan de server goed bijhouden welke gamer bij welke connectie hoort.
	 * @return de instantie van gamer die aan deze connectie gekoppelt is.
	 */
	public Gamer getGamer() {
		return gamer;
	}
	
	/**
	 * Deze methode geeft de socket gekopelt aan deze connectie terug. Deze socket moet opgegeven worden tijdens het instantiseren van de connectie.
	 * @return de instantie van socket die gekoppelt is aan deze connectie.
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Geeft een string representatie van deze connectie op met de naam van de gamer en het IP aders dat hij gebruikt voor de connectie.
	 * @return een mooie string representatie van deze connectie.
	 */
	public String toString() {
		return gamer.getName() + " (" + socket.getInetAddress() + ")";
	}
}