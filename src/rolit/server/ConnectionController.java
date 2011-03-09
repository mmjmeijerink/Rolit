package rolit.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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
				if (inlezen.startsWith("connect")) {
					String name = inlezen.substring(8);
					if(gamer == null) {
						gamer = new Gamer();
					}
					gamer.name = name;
					log.log("Gamer " + name + " connected");
				}
				
				if (inlezen.equals("Exit")) {
					shutdown();
				} else {
					network.broadcast(inlezen);
				}
			} else shutdown();
		}
	}

	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			
		}
	}


	private void shutdown() {
		network.removeConnection(this);
		this.running = false;
	}

}
