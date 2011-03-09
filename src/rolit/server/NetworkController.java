package rolit.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class NetworkController extends Thread {
	private LoggingInterface					log;
	private int									port;
	private String								host;
	private Collection<ConnectionController>  	threads;

	public NetworkController(String aHost, int aPort, LoggingInterface aLog) {
		super();
		threads = new ArrayList<ConnectionController>();
		log = aLog;
		port = aPort;
		host = aHost;
	}
	
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			while (true) {
				Socket socket = server.accept();
				ConnectionController client = new ConnectionController(this, socket);
				addConnection(client);
				client.start();
				client.announce();
			}
		} catch (IOException e){
			log.log("Het lukt niet om een socket te openen op port:" + port);
		}
	}
	
	public void broadcast(String msg) {
		for(ConnectionController client: threads){
            client.sendMessage(msg + "\n");
        }
        log.log("Broadcasted:" + msg);
	}
	
	public void addConnection(ConnectionController connection) {
		threads.add(connection);
	}
	
	public void removeConnection(ConnectionController connection) {
		if(threads.contains(connection)) {
			threads.remove(connection);
		} else {
			log.log("Tries to remove connection that does not exist.");
		}
	}
}
