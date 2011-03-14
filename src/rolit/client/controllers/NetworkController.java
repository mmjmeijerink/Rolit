package rolit.client.controllers;

import java.io.*;
import java.net.*;
import java.util.*;

import rolit.sharedModels.Game;
import rolit.sharedModels.Gamer;

public class NetworkController extends Thread implements Observer {
	
	private ApplicationController				appController;
	private int									port;
	private InetAddress							host;
	private ConnectionController				socket;
	private ArrayList<ConnectionController>		connections;
	private ArrayList<ConnectionController>		waitingForGame;
	private ArrayList<Game>						games;

	public NetworkController(InetAddress aHost, int aPort, ApplicationController controller) {
		super();
		//connections 	= new ArrayList<ConnectionController>();
		//waitingForGame 	= new ArrayList<ConnectionController>();
		//games			= new ArrayList<Game>();
		appController = controller;
		port = aPort;
		host = aHost;
	}

	public void run() {
		try {
			Socket server = new Socket(host, port);
			appController.log("Connecting to server on ip, " + host + ", and port, " + port + ".");
			
			while(true) {
				ConnectionController socket = new ConnectionController(this, server, appController);
				appController.log("New connection to IP: " + server.getInetAddress());
				socket.start();
			}
		} catch (IOException e){
			appController.log("Cannot connect to server!");
			appController.connectionFailed();
		}
	}
	
	public void sendCommand(String command) {
		socket.sendCommand(command);
	}

	public void executeCommand(String msg, ConnectionController sender) {
		String[] regexCommand = (msg.split("\\s+"));
		ArrayList<String> splitCommand = new ArrayList<String>(Arrays.asList(regexCommand));

		if(splitCommand.get(0).equals(command...)) {
			
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg0.getClass().equals(Game.class)) {
			appController.log(((Game) arg0).getBoard().toString());
		}
	}
}