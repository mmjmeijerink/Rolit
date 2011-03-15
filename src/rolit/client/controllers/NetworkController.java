package rolit.client.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.*;

public class NetworkController extends Thread implements Observer {
	
	private ApplicationController				appController;
	private int									port;
	private InetAddress							host;
	private ConnectionController				socket;
	private ArrayList<String>					lobby = new ArrayList<String>();
	

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		appController = controller;
		port = aPort;
		host = aHost;
	}

	public void run() {
		try {
			Socket server = new Socket(host, port);
			appController.log("Connecting to server on ip, " + host + ", and port, " + port + ". \n");
			
			socket = new ConnectionController(this, server, appController);
			socket.start();
		} catch (IOException e){
			appController.log("Cannot connect to server! \n");
			appController.connectionFailed();
		}
	}
	
	public void sendCommand(String command) {
		socket.sendCommand(command);
	}

	public void executeCommand(String msg) {
		String[] regexCommand = (msg.split("\\s+"));
		ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));
		
		if(splitCommand.get(0).equals("ackconnect")) {
			//Handling the handshake of the server
			socket.getGamer().setName(splitCommand.get(1));
			appController.log("Connected to server with name " + splitCommand.get(1) + "\n");
		}
		else if(splitCommand.get(0).equals("startgame")) {
			//Handling the start of a game
			appController.startGame((ArrayList<String>) splitCommand.subList(1, splitCommand.size()));
			
			appController.log("Game has started with ");
			
			for(int i = 1; i < splitCommand.size() - 2; i++) //second and third person in case of participation
				appController.log(splitCommand.get(i) + ", ");
			
			if(splitCommand.size() > 3)
				appController.log("and ");
			
			appController.log(splitCommand.get(splitCommand.size() - 1) + ".\n");
		}
		else if(splitCommand.get(0).equals("turn")) {
			//Handling a turn for the next player
			if(appController.getGame() != null) {
				appController.getGame().giveTurn(splitCommand.get(1)); //TODO: giveTurn() in class game
				
				if(splitCommand.get(1).equals(socket.getGamer().getName())) {
					appController.log("Your turn! \n");
					appController.turn();
				}
				else {
					appController.log(splitCommand.get(1) + "'s is now \n");
				}
			}
			else {
				appController.log("Received a turn command from the server while not in game!");
			}
		}
		else if(splitCommand.get(0).equals("movedone")) {
			//Handling a move 
			//TODO: Show move
		}
		else if(splitCommand.get(0).equals("endgame")) {
			//Handling the end of a game
			//TODO: End game, show winner and return to lobby
		}
		else if(splitCommand.get(0).equals("kick")) {
			//Handling a kick
			//TODO: Add action on player kicked
		}
		else if(splitCommand.get(0).equals("message")) {
			//Handling a chat message
			appController.log(splitCommand.get(1) + ": ");
			
			for(int i = 2; i < splitCommand.size() - 2; i++)
				appController.log(splitCommand.get(i));
			
			appController.log("\n");
		}
		else if(splitCommand.get(0).equals("challenged")) {
			//Handling a challenge request
			//TODO: Add challenge mode
		}
		else if(splitCommand.get(0).equals("lobby")) {
			//Handling a lobby event
			lobby.clear();
			for(int i = 1; i < splitCommand.size(); i++) {
				lobby.add(splitCommand.get(i));
			}
		}
		else //TODO: Remove in final version! Wrong commands will be ignored!
			appController.log("Unknown command received: \n");
			for(int i = 0; i < splitCommand.size(); i++)
				appController.log(splitCommand.get(i));
	}
	
	public ArrayList<String> getLobby() {
		return lobby;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0.getClass().equals(Game.class)) {
			appController.log(((Game) arg0).getBoard().toString());
		}
	}
}