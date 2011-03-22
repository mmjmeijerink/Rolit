package rolit.server.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import rolit.server.models.LoggingInterface;
import rolit.server.views.MainView;

/**
 * De ApplicationController beheert alle gebruiker interactie met het programma. De ApplicationController beheert dus de View's.
 * Daarnaast zorgt de application controller er voor dat als de user interface doorgeeft dat de gebruiker de server wil starten dat er een NetworkController instantie wordt geopend.
 * Eindopdracht Programmeren 2
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */

public class ApplicationController implements ActionListener,LoggingInterface {
	private MainView 			view;
	private NetworkController 	network;
	
	/**
     * Creert een ApplicationController en maakt een MainView insantie aan. Vervolgens geeft de ApplicationController de MainView het juiste locale IP aderes door.
     * @ensure this.view() != null
     */
	public ApplicationController() {
		view = new MainView(this);
		
		/* Geeft het IP aders van de computer door aan de View zodat de gebruiker deze goed kan zien.
		 * Het opvragen van het IP adres is inprincipe niet nodig voor het openen van een socket maar wel handig voor de gebruiker zodat deze weet waarmee de client moet verbinden.
		 */
		try {
            InetAddress ip = InetAddress.getLocalHost();
            view.setHost(ip.getHostAddress());
        } catch (UnknownHostException e) {
        	log("Your system does not allow the server to know it's IP, you will not be able to start the server.");
        	view.disableControls();
        }
	}
	
	/**
     * Logt een speficieke string in de System.out en daarnaast zorg hij er voor dat het log bericht doorgestuurd wordt naar de View.
     * @require logEntry != null
     */
	public void log(String logEntry) {
		if(logEntry != null) {
			System.out.println(" " + logEntry);
			view.enterLogEntry(logEntry);
		}
	}
	
	/**
     * Roep deze methoden aan als het niet lukt om verbindingen op te zetten zodat de ApplicationController aan de view door kan geven dat deze de controlls weer moet enablen.
     * LETOP: Roep via ApplicationController connectionFailed aan zodat de View maar via 1 controller wordt aangestuurd zodat de toestand van de view gegarandeerd wordt.
     */
	public void connectionFailed() {
		// Roep via ApplicationController connectionFailed aan zodat de View maar via 1 controller wordt aangestuurd zodat de toestand van de view gegarandeerd wordt.
		view.enableControls();
	}

	@Override
	/**
     * Deze ApplicationController is de controller van de MainView en handelt dus ook alle actions van die MainView af.
     * De MainView bevat 1 action namelijk het op de knop "Start server" drukken. Deze wordt hier afgehandelt door, als de textfields goed ingevuld zijn, een NetworkController thread te instanticeren en te starten.
     * Als het netwerk goed gestart is worden de knoppen op de view gedisabled zodat deze niet meer gebruikt kunnen worden terwijl de server aan staat.
     */
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == view.connectButton()) {
			log("Starting the server...");

			/*
			 * Als er een verkeerde poort in de MainView staat wordt standaard 1337 gebruikt.
			 * Dit wordt ook aan de gebruiker doorgegeven doormiddel van de log.
			 */
			int port = 1337; // Default port
			try {
				port = Integer.parseInt(view.port());
				if (port<1 && port > 65535) {
					log("Port has to be in the range [1-65535], using 1337 default.");
					port = 1337; // Default port
				}
			} catch (NumberFormatException e) {
				log("Port is not a valid number, using 1337 default.");
			}
			
			network = new NetworkController(port, this);
			network.start();
			/* Als er verbinding gemaakt wordt met de server wordt er naar de view een disableControls commando gestuurd.
			 * Zodat de gebruiker niet meer de velden met poort, ip aders kan veranderen en goed kan blijven zien op welke poort de server gestart is.
			 * Het network object roept this.connectionFailed() aan als er iets mis gaat zodat de controls weer geenabled wordt.
			 */
			view.disableControls();
		}
	}
}