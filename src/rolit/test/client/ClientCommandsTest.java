package rolit.test.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import rolit.client.controllers.*;
import rolit.sharedModels.Gamer;

public class ClientCommandsTest extends ApplicationController {
	
	private NetworkController	network;
	
	/**
	 * Test class to test all the commands possibly being send by the client to the server and vice versa (server -> client)
	 */
	public static void main(String[] args) {
		InetAddress host = null;
		try {
			host = InetAddress.getByName("130.89.122.73"); //IP of most used server location
		} catch (UnknownHostException e) {
			e.printStackTrace(); //Test class so we want to see all thrown errors
		}
		int port = 1337; //Default port
		
		new ClientCommandsTest(host, port);
	}
	
	public ClientCommandsTest(InetAddress host, int port) {
		super();
		network = new NetworkController(host, port, this);
		network.start();
		
		startTests();
	}
	
	private void startTests() {
		connect(); //Send connect command to server. This class has 'Mart' as hard coded nick
		join(2); //Send join command to server. Amount of players hard coded change per test. Amount has to be from 2 to 4
		chat(); //Send chat command to server. Just a random message will be sent.
		//challenge(); //Send challenge command. Has to be implemented yet!
		//challengeresponse(); //Send a response to a challenge. Has to be implemented yet!
	}
	
	private void error(String arg) {
		log("Error detected! \n Command: " + arg);
	}
	
	private void connect() {
		network.sendCommand("connect Mart");
	}
	
	private void join(int amount) {
		network.sendCommand("join " + amount);
	}
	
	private void chat() {
		network.sendCommand("chat just a random message. And some more randomness");
		network.sendCommand("chat With even more randomness in a second message.!!!! kldsj  fal)   )#)?? @?@?? A*??");
	}
	
	private void challenge() {
		network.sendCommand("challenge ");
	}
	
	private void challengeresponse() {
		network.sendCommand("challengeresponse ");
	}
	
	//Getters and setters from super + test part added
	//These setters correspond to an action called by the NetworkController in reaction on a server command
	public void turn() {
		log("turn command received");
		super.turn();
		
		//test part
		
	}
	
	public void move(Gamer gamer, int index) {
		log("movdedone command received");
		super.move(gamer, index);
		
		//test part
		
	}
	
	public void lobbyUpdate(ArrayList<String> newLobby) {
		log("lobby command received");
		super.lobbyUpdate(newLobby);
		
		//test part
	}
	
	//Views
	public void connectionFailed() {
		log("failing to established connection");
		super.connectionFailed();
	}
	
	public void startGame(ArrayList<String> players) {
		log("startgame command received");
		super.startGame(players);
		
		//test part
		
	}
	
	public void enteringLobby() {
		log("ackconnect command received");
		super.enteringLobby();
		
		//test part
		
	}
}