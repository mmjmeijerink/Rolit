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
	private ArrayList<ConnectionController>  	threads;
	private ArrayList<ConnectionController>		waitingForGame;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		threads 		= new ArrayList<ConnectionController>();
		waitingForGame 	= new ArrayList<ConnectionController>();
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
				/* Execute command "connect" */
				
				if(splitCommand.size() == 2) {
					if(sender.gamer().name.equals("[NOT CONNECTED]")) {
						String possibleName = splitCommand.get(1);
						int i = 1;
						while(!checkName(possibleName)) {
							possibleName = possibleName+Integer.toString(i);
							i++;
						}
						sender.gamer().name = possibleName;
						appController.log("Connection from " + sender.socket().getInetAddress() + " has identified itself as: " + possibleName);
						sender.sendCommand("ackconnect "+possibleName);
					} else {
						appController.log(sender.toString() + " tries to but is already identified.");
					}
				} else {
					appController.log("Connect command from " + sender.toString() + " has more than 1 parameter: " + msg);
				}
				
			} else if(splitCommand.get(0).equals("join")) {
				/* Execute command "join" */
				if(!sender.gamer().name.equals("[NOT CONNECTED]")) {
					if(splitCommand.size() == 2) {
						try {
							int requestedSize = Integer.parseInt(splitCommand.get(1));
							if(sender.gamer().setRequestedGameSize(requestedSize)) {
								if(!waitingForGame.contains(sender)) {
									waitingForGame.add(sender);
								}
								appController.log(sender.toString() + " wants to join with " + splitCommand.get(1) + " players");
								checkForGameStart();
							} else {
								appController.log("Join command from " + sender.toString() + " 1st parameter between [2-4]: " + msg);
							}
						} catch(NumberFormatException e) {
							appController.log("Join command from " + sender.toString() + " 1st parameter needs to be a int: " + msg);
						}
					} else {
						appController.log("Join command from " + sender.toString() + " has more than 1 parameter: " + msg);
					}
				} else {
					appController.log("Join command from " + sender.toString() + " but has not identified hisself.");
				}
				
			} else if(splitCommand.get(0).equals("domove")) {

			} else if(splitCommand.get(0).equals("chat")) {

			} else {
				appController.log("Command from " + sender.toString() + " misunderstoud: " + msg);
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

	public boolean checkName(String name) {
		boolean result = true;
		if(name != null) {
			for(ConnectionController client: threads){
				if(client.gamer().name.equals(name)) {
					result = false;
				}
			}
		}
		return result;
	}
	
	public void checkForGameStart() {
		
	}

}
