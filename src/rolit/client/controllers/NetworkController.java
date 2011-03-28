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
	private String					startupCommand;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller, String aStartupCommand) {
		super();
		appController = controller;
		port = aPort;
		host = aHost;
		startupCommand = aStartupCommand;
		this.setName(startupCommand);//Debug line! 
	}

	public void run() {
		try {
			socket = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			appController.log("Connecting to server on ip, " + host + ", and port, " + port + ".");
			
			sendCommand(startupCommand);

			while(true) {
				String ingelezen = in.readLine();
				if (ingelezen != null) {
					executeCommand(ingelezen);
				} else {
					disconnect();
				}
			}
		} catch (IOException e) {
			disconnect();
		}
	}

	public void sendCommand(String msg) {
		if(msg != null) {
			//appController.log("Sending commmand (" + msg + ") to server");
			try {
				out.write(msg + "\n");
				out.flush();
			} catch (IOException e) {
				appController.log("Sending commmand " + msg + " failed!");
			}
		}
	}

	public void executeCommand(String msg) {
		System.out.println(msg);
		
		String[] regexCommand = (msg.split("\\s+"));
		ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));

		if(splitCommand.get(0).equals("ackconnect")) {
			//Handling the handshake of the server
			appController.connectionAstablished(splitCommand.get(1));
			appController.log("Connected to server with name " + splitCommand.get(1) + ".");

		}
		else if(splitCommand.get(0).equals("startgame")) {
			//Handling the start of a game
			ArrayList<String> playerList = (ArrayList<String>)splitCommand.clone();
			playerList.remove(0);
			appController.startGame(playerList);

			appController.log("Game has started with " + msg.substring(splitCommand.get(0).length() + 1));
		}
		else if(splitCommand.get(0).equals("turn")) {
			if(splitCommand.get(1).equals(appController.getGamer().getName())) {
				appController.log("Your turn!");
				appController.myTurn();
			}
			else {
				appController.log("It is now " + splitCommand.get(1) + "'s turn.");
			}
		}
		else if(splitCommand.get(0).equals("movedone")) {
			//Handling a move
			appController.log(msg);
			Gamer gamer = null;
			for(Gamer aGamer : appController.getGame().getGamers()) {
				if(aGamer.getName().equals(splitCommand.get(1)))
					gamer = aGamer;
			}
			appController.handleMove(gamer, Integer.parseInt(splitCommand.get(2)));
		}
		else if(splitCommand.get(0).equals("endgame")) {
			//Handling the end of a game
			appController.log("The game has ended");
			
			String message = "The game has ended:\n";
			int winnerIndex = 0;
			int winnerPoints = 0;
			boolean noWinner = false;
			
			for(int i = 0; i <  appController.getGame().getGamers().size() && i < splitCommand.size() + 1; i++) {
				String name = appController.getGame().getGamers().get(i).getName();
				if(name.equals(appController.getGamer().getName())) {
					name = "You have";
				} else {
					name = name + " has";
				}
				message = message + "" + name + " scored " + splitCommand.get(i+1) + " points. \n";
				if(Integer.parseInt(splitCommand.get(i+1)) > winnerPoints) {
					winnerPoints = Integer.parseInt(splitCommand.get(i+1));
					winnerIndex = i;
					noWinner = false;
				} else if(Integer.parseInt(splitCommand.get(i+1)) == winnerPoints) {
					noWinner = true;
				}
			}
			if(!noWinner) {
				message = message + "\n" + appController.getGame().getGamers().get(winnerIndex).getName() + " has won the match with " + winnerPoints + " points!";
			}
			appController.endGame(message);
		}
		else if(splitCommand.get(0).equals("kick")) {
			//Handling a kick
			Gamer kicked = null;
			boolean found = false;
			
			for(int i = 0; i < appController.getGame().getGamers().size() && !found; i++) {
				if(appController.getGame().getGamers().get(i).getName().equals(splitCommand.get(1))) {
					kicked = appController.getGame().getGamers().get(i);
					found = true;
				}
			}
			if(found && kicked == appController.getGamer()) {
				appController.gotKicked();
			} else if(found) {
				appController.log(kicked.getName()+" got kicked.");
				appController.getGame().removeGamer(kicked);
			}
		}
		else if(splitCommand.get(0).equals("message")) {
			//appController.log(splitCommand.get(1) + ": " + msg.substring(splitCommand.get(0).length() + splitCommand.get(1).length() + 2));
			appController.handleChat(msg.substring(splitCommand.get(0).length() + splitCommand.get(1).length() + 2),splitCommand.get(1));
		}
		else if(splitCommand.get(0).equals("challenged")) {
			//Handling a challenge request
			appController.challenged(splitCommand.get(1));
			appController.log("Challenge received for a game with " + msg.substring(splitCommand.get(0).length() + 1));
		}
		else if(splitCommand.get(0).equals("lobby")) {
			//Handling a lobby event
			lobby.clear();
			for(int i = 1; i < splitCommand.size(); i++) {
				lobby.add(splitCommand.get(i));
			}
			appController.updateLobby();
		}
		else  {
			appController.log("Unknown command received: "+msg);
		}
	}

	//Getters and Setters
	public void disconnect() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (NullPointerException e) {
			appController.log("Could never connect at all.");
		} catch (IOException e) {
			System.err.println("Exceptie catched: " + e.toString());
		}
		appController.connectionFailed();
	}

	public ArrayList<String> getLobby() {
		return lobby;
	}
}