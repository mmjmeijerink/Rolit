package rolit.client.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.*;

public class NetworkController extends Thread {
	
	private ApplicationController	appController;
	private int						port;
	private InetAddress				host;
	private Socket					socket;
	private ArrayList<String>		lobby = new ArrayList<String>();
	private BufferedReader			in;
	private BufferedWriter			out;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		appController = controller;
		port = aPort;
		host = aHost;
	}

	public void run() {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			sendCommand("connect " + appController.getGamer().getName());
			
			String inlezen = null;
			while(true) {
				try {
					inlezen = in.readLine();  
				} catch (IOException e) {
					inlezen = null;
				}
				
				if (inlezen != null) {
					executeCommand(inlezen);
				}
				else {
					disconnect();
				}
			}
		} catch (IOException e){
			appController.log("Cannot connect to server! \n");
			appController.connectionFailed();
		}
	}
	
	public void sendCommand(String msg) {
		if(msg != null) {
			appController.log("Sending commmand (" + msg + ") to server \n");
			try {
				out.write(msg + "\n");
				out.flush();
			} catch (IOException e) {
				appController.log("Sending commmand " + msg + " failed! \n");
			}
		}
	}

	public void executeCommand(String msg) {
		String[] regexCommand = (msg.split("\\s+"));
		ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));
		
		if(splitCommand.get(0).equals("ackconnect")) {
			//Handling the handshake of the server
			appController.getGamer().setName(splitCommand.get(1));
			appController.log("Connected to server with name " + splitCommand.get(1) + "\n");
			appController.enteringLobby();
		}
		else if(splitCommand.get(0).equals("startgame")) {
			//Handling the start of a game
			appController.startGame((ArrayList<String>) splitCommand.subList(1, splitCommand.size()));
			
			appController.log("Game has started with \n");
			
			for(int i = 1; i < splitCommand.size() - 2; i++) //second and third person in case of participation
				appController.log(splitCommand.get(i) + ", ");
			
			if(splitCommand.size() > 3)
				appController.log("and ");
			
			appController.log(splitCommand.get(splitCommand.size() - 1) + ". \n");
		}
		else if(splitCommand.get(0).equals("turn")) {
			//Handling a turn for the next player
			//appController.getGame().nextTurn(); //splitCommand.get(1)); //TODO: giveTurn() in class game
			
			if(splitCommand.get(1).equals(appController.getGamer().getName())) {
				appController.log("Your turn! \n");
				appController.turn();
			}
			else {
				appController.log(splitCommand.get(1) + "'s is now. \n");
			}
		}
		else if(splitCommand.get(0).equals("movedone")) {
			//Handling a move
			Gamer gamer = null;
			for(Gamer aGamer : appController.getGame().getGamers()) {
				if(aGamer.getName().equals(splitCommand.get(1)))
					gamer = aGamer;
			}
			appController.move(gamer, Integer.parseInt(splitCommand.get(2)));
		}
		else if(splitCommand.get(0).equals("endgame")) {
			//Handling the end of a game
			appController.log("The game has ended: \n");
			
			for(int i = 1; i < splitCommand.size() - 1; i ++) {
				appController.log(String.format(" %20s scored %2d points. \n", appController.getGame().getGamers().get(i).getName(), splitCommand.get(i)));
			}
			
			appController.enteringLobby();
		}
		else if(splitCommand.get(0).equals("kick")) {
			//Handling a kick
			Gamer kicked = null;
			boolean found = false;
			
			for(int i = 0; i < appController.getGame().getGamers().size() && !found; i++) {
				if(appController.getGame().getGamers().get(i).equals(splitCommand.get(1))) {
					kicked = appController.getGame().getGamers().get(i);
					found = true;
				}
			}
			appController.getGame().removeGamer(kicked);
		}
		else if(splitCommand.get(0).equals("message")) {
			//Handling a chat message
			appController.log(splitCommand.get(1) + ": ");
			
			for(int i = 2; i < splitCommand.size() - 2; i++)
				appController.log(splitCommand.get(i));
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
	
	//Getters and Setters
	public void disconnect() {
		appController.log("[Verbinding verbreken] \n");
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.err.println("Exceptie afgevangen: " + e.toString());
		}
		appController.log("[Verbinding verbroken] \n");
		appController.connectionFailed();
	}
	
	public ArrayList<String> getLobby() {
		return lobby;
	}
}