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
			System.err.println("smartVSGreedy has started");
			greedyVSSmart(100);
			System.err.println("greedyVSSmart has started");
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
		smartClient.setOpponent(greedyClient);
		smartClient.setGamesToPlay(50);
		Thread.sleep(100);
		smartClient.challenge();
		
		while(!smartClient.isEnded) {
			System.out.println("Games being played! \n Smart VS Greedy");
			logWinnings("smartVSGreedy");
			Thread.sleep(1000);
		}
		
		logWinnings("smartVSGreedy");
	}
	
	public void greedyVSSmart(int games) throws InterruptedException {
		//Start [games]games of a AIController versus a SmartAIController
		greedyClient = new AITestApplicationController(host, port, "greedy");
		smartClient = new AITestApplicationController(host, port, "smart");
		greedyClient.setOpponent(smartClient);
		greedyClient.setGamesToPlay(50);
		Thread.sleep(100);
		greedyClient.challenge();
		
		while(!greedyClient.isEnded) {
			System.out.println("Games being played! \n Greedy VS Smart");
			logWinnings("greedyVSSmart");
			Thread.sleep(1000);
		}
		
		logWinnings("greedyVSSmart");
	}
	
	public void logWinnings(String method) {
		try {
			BufferedWriter file;
			
			file = new BufferedWriter(new FileWriter("Smart-"+method+".txt"));
			file.write(String.format("SmartAIController: %10s \nAIController: %15s", smartClient.getSmartWinnings(), smartClient.getGreedyWinnings()));
			file.flush();
			file.close();
			
			file = new BufferedWriter(new FileWriter("Greedy-"+method+".txt"));
			file.write(String.format("SmartAIController: %10s \nAIController: %15s", greedyClient.getSmartWinnings(), greedyClient.getGreedyWinnings()));
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace(); //Test class
		}
	}
	
	public static void main(String[] args) {
		new AITest();
	}
}