package rolit.test.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import rolit.client.controllers.ApplicationController;
import rolit.client.controllers.NetworkController;

public class ServerTester extends NetworkController {
	
	private static ServerTester	tester = null;
	private static ServerTester	opponent = null;
	private static InetAddress	host = null;
	private static int			port = 1337;
	
	private int turn = 0;
	private boolean ackconnectReceived = false;
	private boolean challengedReceived = false;
	private boolean startgameReceived = false;
	private boolean turnReceived = false;
	private boolean movedoneReceived = false;
	private boolean endgameReceived = false;
	private boolean kickReceived = false;
	private boolean messageReceived = false;
	private boolean lobbyReceived = false;
	
	public ServerTester(String name) {
		super(host, port, new ApplicationController(), "connect "+name);
		super.start();
		System.err.println(name+" starting");
	}
	
	public void startTest() throws InterruptedException {
		while(!ackconnectReceived) {
			System.out.println("Waiting for ackconnect");
			Thread.sleep(1000);
		}
		joinCommando();
		
		while(!startgameReceived) {
			System.out.println("Waiting for startgame");
			Thread.sleep(1000);
		}
		for(int i = 1; i < 4; i++) {
			testMove(i);
			while(!movedoneReceived && !kickReceived && !endgameReceived) {
				System.out.println("Waiting for movedone");
				Thread.sleep(1000);
			}
			if(i < 2)
				movedoneReceived = false;
		}
		
		while(!kickReceived) {
			System.out.println("Waiting for kick");
			Thread.sleep(1000);
		}
		
		while(!endgameReceived && !kickReceived) {
			System.out.println("Waiting for endgame");
			Thread.sleep(1000);
		}
		
		System.err.println("sending challenge command");
		tester.sendCommand("challenge Opponent");
		while(!opponent.challengedReceived) {
			System.out.println("Waiting for challenged");
			Thread.sleep(1000);
		}
		System.err.println("sending challengeresponse command");
		opponent.sendCommand("challengeresponse Tester false");
		
		System.err.println("sending chat command");
		tester.sendCommand("chat hallo");
		while(!messageReceived) {
			System.out.println("Waiting for message");
			Thread.sleep(1000);
		}
		
		if(!lobbyReceived) {
			System.out.println("No lobby command received from server.");
			System.out.println("All other commands are working as expected");
		}
		else {
			System.out.println("All commands are working as expected!");
		}
	}
	
	public void joinCommando() {
		System.err.println("sending join command");
		tester.sendCommand("join 2");
		opponent.sendCommand("join 2");
	}
	
	public void testMove(int move) throws InterruptedException {
		System.err.println("sending domove command");
		if(move == 1 && turn == 1) {
			tester.sendCommand("domove 29");
			turnReceived = false;
		} else if(move == 2 && turn == 2) {
			opponent.sendCommand("domove 20");
			turnReceived = false;
		} else if(move == 3 && turn == 3) {
			tester.sendCommand("domove 0");
		}
		else {
			while(!turnReceived) {
				System.out.println("Waiting for turn");
				Thread.sleep(1000);
			}
			testMove(move);
		}
	}
	
	//Overided methods from super
	public void executeCommand(String msg) {
		if(msg.contains("ackconnect")) {
			ackconnectReceived = true;
		} else if(msg.contains("challenged")) {
			challengedReceived = true;
		} else if(msg.contains("startgame")) {
			startgameReceived = true;
		} else if(msg.contains("turn")) {
			turn++;
			turnReceived = true;
		} else if(msg.contains("movedone")) {
			movedoneReceived = true;
		} else if(msg.contains("end")) {
			endgameReceived = true;
		} else if(msg.contains("kick")) {
			kickReceived = true;
		} else if(msg.contains("message")) {
			messageReceived = true;
		} else if(msg.contains("lobby")) {
			lobbyReceived = true;
		}
		
		if(!msg.contains("lobby"))
			System.err.println("Command received:\n"+msg);
	}
	
	public static void main(String[] args) {
		try {
			host = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace(); //Test Class
		}
		
		(new rolit.server.controllers.NetworkController(1337, new rolit.server.controllers.ApplicationController())).start();
		
		opponent = new ServerTester("Opponent");
		tester = new ServerTester("Tester");
		try {
			tester.startTest();
		} catch (InterruptedException e) {
			e.printStackTrace(); //Test class
		}
		//tester.start();
		//opponent.start();
	}
}