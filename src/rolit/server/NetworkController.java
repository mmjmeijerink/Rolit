package rolit.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class NetworkController extends Thread {
	private LoggingInterface					log;
	private int									port;
	private InetAddress							host;
	private Collection<ConnectionController>  	threads;

	public NetworkController(InetAddress aHost, int aPort, LoggingInterface aLog) {
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
			log.log("Server started on port:" + port);
			while (true) {
				Socket socket = server.accept();
				ConnectionController client = new ConnectionController(this, socket,log);
				addConnection(client);
				client.start();
			}
		} catch (IOException e){
			log.log("Server can't start because port " + port + " is already being used");
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
			log.log("Tries to remove connection that does not exist");
		}
	}
}
