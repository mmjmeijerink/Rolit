package rolit.test.AITest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AITest {
	
	private rolit.server.controllers.ApplicationController server = new rolit.server.controllers.ApplicationController();
	private AITestApplicationController	smartClient;
	private AITestApplicationController	greedyClient;
	
	private InetAddress					host;
	private int							port = 1337;
	
	public AITest() {
		(new rolit.server.controllers.NetworkController(port, server)).start();
		
		try {
			host = InetAddress.getByName("localhost");
			smartVSGreedy(100);
			greedyVSSmart(100);
		} catch (UnknownHostException e) {
			e.printStackTrace(); //Test class
		} catch (InterruptedException e) {
			e.printStackTrace(); //Test class
		}
	}
	
	public void smartVSGreedy(int games) throws InterruptedException {
		//Start [games] games of a SmartAIController versus a AIController
		smartClient = new AITestApplicationController(host, port, "smart");
		greedyClient = new AITestApplicationController(host, port, "greedy");
		Thread.sleep(100);
		greedyClient.challenge(smartClient);
		
		while(!smartClient.isEnded) {
			System.out.println("Games being played! \n Smart VS Greedy");
			Thread.sleep(100);
		}
		
		logWinnings();
	}
	
	public void greedyVSSmart(int games) throws InterruptedException {
		//Start [games]games of a AIController versus a SmartAIController
		greedyClient = new AITestApplicationController(host, port, "greedy");
		smartClient = new AITestApplicationController(host, port, "smart");
		Thread.sleep(100);
		smartClient.challenge(greedyClient);
		
		while(!greedyClient.isEnded) {
			System.out.println("Games being played! \n Greedy VS Smart");
			Thread.sleep(100);
		}
		
		logWinnings();
	}
	
	public void logWinnings() {
		try {
			BufferedWriter file;
			
			file = new BufferedWriter(new FileWriter("Smart.txt"));
			file.write(String.format("SmartAIController: %10s \nAIController: %14s", smartClient.getSmartWinnings(), smartClient.getGreedyWinnings()));
			
			file = new BufferedWriter(new FileWriter("Greedy.txt"));
			file.write(String.format("SmartAIController: %10s \nAIController: %14s", greedyClient.getSmartWinnings(), greedyClient.getGreedyWinnings()));
		} catch (IOException e) {
			e.printStackTrace(); //Test class
		}
	}
	
	public static void main(String[] args) {
		new AITest();
	}
}