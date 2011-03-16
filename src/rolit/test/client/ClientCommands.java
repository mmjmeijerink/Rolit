package rolit.test.client;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import rolit.client.controllers.*;

public class ClientCommands {
	
	private ApplicationController client;
	
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
		
		new ClientCommands(host, port);
	}
	
	public ClientCommands(InetAddress host, int port) {
		client = new ApplicationController();
		new NetworkController(host, port, client);
		
		if(connect()) {
			receive();
		}
		else {
			error("connect");
		}
		
		if(join()) {
			receive();
		}
		else {
			error("join");
		}
		
		if(domove()) {
			receive();
		}
		else {
			error("domove");
		}
		
		if(chat()) {
			receive();
		}
		else {
			error("chat");
		}
		
		if(challenge()) {
			receive();
		}
		else {
			error("challenge");
		}
		
		if(challengeresponse()) {
			receive();
		}
		else {
			error("challengeresponse");
		}
	}
	
	private void receive() {
		client.log("Server response received: \n");
		client.
	}
	
	private void error(String arg) {
		System.out.print("Error detected! \n Command: " + arg);
	}
	
	private boolean connect() {
		boolean result = ;
		
		
		return result;
	}
	
	private boolean join() {
		boolean result = ;
		
		
		return result;
	}
	
	private boolean domove() {
		boolean result = ;
		
		
		return result;
	}
	
	private boolean chat() {
		boolean result = ;
		
		
		return result;
	}
	
	private boolean challenge() {
		boolean result = ;
		
		
		return result;
	}
	
	private boolean challengeresponse() {
		boolean result = ;
		
		
		return result;
	}
}