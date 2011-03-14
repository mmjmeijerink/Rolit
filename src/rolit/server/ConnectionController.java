package rolit.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import rolit.sharedModels.Gamer;

public class ConnectionController extends Thread {

	private NetworkController   network;
	private Socket           	socket;
	private BufferedReader   	in;
	private BufferedWriter   	out;

	private boolean			 	running = true;
	private LoggingInterface	log;
	private Gamer				gamer;

	public ConnectionController(NetworkController aNetwork, Socket aSocket, LoggingInterface aLog) throws IOException {
		network = aNetwork;
		socket = aSocket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		log = aLog;
		gamer = new Gamer();
	}

	public void run() {
		String inlezen = null;
		while(this.running) {
			try {
				inlezen = in.readLine();  
			} catch (IOException e) {
				inlezen = null;
			}
			if (inlezen != null) {
				network.executeCommand(inlezen,this);
			} else disconnect();
		}
	}

	public void sendCommand(String msg) {
		if(msg != null) {
			log.log("Sending commmand (" + msg + ") to " + toString());
			try {
				out.write(msg + "\n");
				out.flush();
			} catch (IOException e) {
				log.log("Sending commmand " + msg + " failed!");
			}
		}
	}

	public void disconnect() {
		network.removeConnection(this);
		this.running = false;
	}

	public Gamer getGamer() {
		return gamer;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public String toString() {
		return gamer.getName() + " (" + socket.getInetAddress() + ")";
	}
}
