package rolit.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.sun.tools.javac.util.List;

public class NetworkController extends Thread {
	private ApplicationController				appController;
	private int									port;
	private InetAddress							host;
	private Collection<ConnectionController>  	threads;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		threads = new ArrayList<ConnectionController>();
		appController = controller;
		port = aPort;
		host = aHost;
	}
	
	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			appController.log("Server started on port: " + port);
			while (true) {
				Socket socket = server.accept();
				ConnectionController client = new ConnectionController(this, socket, appController);
				appController.log("New connection from IP: " + socket.getInetAddress());
				addConnection(client);
				client.start();
			}
		} catch (IOException e){
			appController.log("Server can't start because port " + port + " is already being used");
			appController.connectionFailed();
		}
	}
	
	public void broadcastCommand(String msg) {
		if(msg != null) {
			for(ConnectionController client: threads){
	            client.sendCommand(msg);
	        }
			appController.log("Broadcasted: " + msg);
		}
	}
	
	public void executeCommand(String msg, ConnectionController sender) {
		if(msg != null && sender != null) {
			String[] regexCommand = (msg.split("\\s+"));
			ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));
			
			if(splitCommand.get(0).equals("connect")) {
				if(splitCommand.size() == 2) {
					sender.gamer().name = splitCommand.get(1);
					appController.log("Connection from " + sender.socket().getInetAddress() + " has identified itself as: " + splitCommand.get(1));
				} else {
					appController.log("Invalid command: " + msg);
				}
			} else if(splitCommand.get(0).equals("join")) {
				
			} else if(splitCommand.get(0).equals("domove")) {
				
			} else if(splitCommand.get(0).equals("chat")) {
				
			} else {
				appController.log("Command misunderstoud: " + msg);
			}
		}
	}
	
	public void addConnection(ConnectionController connection) {
		threads.add(connection);
	}
	
	public void removeConnection(ConnectionController connection) {
		if(threads.contains(connection)) {
			threads.remove(connection);
		} else {
			appController.log("Tries to remove connection that does not exist");
		}
	}
	
}
