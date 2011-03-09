package rolit.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ConnectionController extends Thread {
	private NetworkController   server;
	private Socket           	sock;
	private BufferedReader   	in;
	private BufferedWriter   	out;
	private String           	clientName;
	private boolean			 	running = true;

	/**
	 * Construeert een ClientHandler object.
	 * Initialiseert de beide Data streams.
	 * @require server != null && sock != null
	 */
	public ConnectionController(NetworkController server, Socket sock) throws IOException {
		this.server = server;
		this.sock = sock;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}

	/**
	 * Leest de naam van de Client uit de inputstream en stuurt de
	 * een broadcast-berichtje naar de Server om te melden dat de
	 * Client nu deelneemt aan de chatbox. Merk op dat deze methode
	 * meteen na het construeren van een ClientHandler dient te
	 * worden aangeroepen.
	 */
	public void announce() throws IOException {
		clientName = in.readLine();
		server.broadcast("- Welkom " + clientName + "! -");
	}

	/**
	 * Deze methode zorgt voor het doorzenden van berichten van
	 * de Client. Aan elk berichtje dat ontvangen wordt, wordt de
	 * naam van de Client toegevoegd en het nieuwe berichtje wordt
	 * ter broadcast aan de Server aangeboden. Als er bij het lezen 
	 * van een bericht een IOException gegooid wordt, concludeert 
	 * de methode dat de socketverbinding is verbroken en wordt
	 * shutdown() aangeroepen.
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
				if (inlezen.equals("Exit")) {
					shutdown();
				} else {
					server.broadcast(clientName + " zegt: " +inlezen);
				}
			} else shutdown();
		}
	}

	/**
	 * Deze methode kan gebruikt worden om een bericht over de 
	 * socketverbinding naar de Client te sturen. Als het schrijven
	 * van het bericht mis gaat, dan concludeert de methode dat de
	 * socketverbinding verbroken is en roept shutdown() aan.
	 */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			System.err.println("Exceptie afgevangen: " + e.toString());
		}
	}

	/**
	 * Deze ClientHandler meldt zich af bij de Server en stuurt
	 * vervolgens een laatste broadcast naar de Server om te melden
	 * dat de Client niet langer deelneemt aan de chatbox.
	 */
	private void shutdown() {
		server.removeConnection(this);
		server.broadcast("- Doei doei " + clientName + "! -");
		this.running = false;
	}

}
