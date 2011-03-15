package rolit.test.server;

import java.net.*;
import java.io.*;

/**
 * P2 prac wk4. <br>
 * Client. Een Thread-klasse voor het onderhouden van een 
 * Socket-verbinding met een Server. De Thread leest berichten uit
 * de socket en stuurt die door naar zijn MessageUI.
 * @author  Theo Ruys
 * @version 2005.02.21
 */
public class Client extends Thread {

	private MessageUI       mui;
	private Socket          sock;
	private BufferedReader  in;
	private BufferedWriter  out;

	/**
	 * Construeert een Client-object en probeert een socketverbinding
	 * met een Server op te starten.
	 */
	public Client(InetAddress host, int port, MessageUI mui) throws IOException {
		sock = new Socket(host, port);
		this.mui = mui;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        mui.addMessage("[Verbonden]");
	}

	/**
	 * Leest berichten uit de socketverbinding. Elk berichtje wordt
	 * gestuurd naar de MessageUI van deze Client. Als er iets fout
	 * gaat bij het lezen van berichten, sluit deze methode de
	 * socketverbinding.
	 */
	public void run() {
		try {
			while(true) {
				String ingelezen = in.readLine();
				if (ingelezen != null) {
					mui.addMessage(ingelezen);
				}
			}

		} catch (IOException e) {
			shutdown();
		}
	}

	/** Stuurt een bericht over de socketverbinding naar de ClientHandler. */
	public void sendMessage(String msg) {
		try {
			out.write(msg + "\n");
			out.flush();
		} catch (IOException e) {
			//e.printStackTrace();
			sendMessage(msg);
		}

	}

	/** Sluit de socketverbinding van deze client. */
	public void shutdown() {
		mui.addMessage("[Verbinding verbreken]");
		try {
			in.close();
			out.close();
			sock.close();
			//System.exit(0);
		} catch (IOException e) {
			System.err.println("Exceptie afgevangen: " + e.toString());
		}
		mui.addMessage("[Verbinding verbroken]");
	}


} // end of class Client