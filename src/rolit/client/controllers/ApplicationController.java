package rolit.client.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rolit.client.models.LoggingInterface;
import rolit.client.views.ConnectView;
import rolit.client.views.GameView;
import rolit.client.views.LobbyView;
import rolit.client.views.MainView;
import rolit.sharedModels.*;

public class ApplicationController implements Observer, ActionListener, KeyListener, ChangeListener, LoggingInterface {
	
	private GameView			gameView;
	private ConnectView			connectView;
	private LobbyView			lobbyView;
	private NetworkController	network;
	private Game				game = null;
	private Gamer				gamer;
	
	public ApplicationController() {
		connectView = new ConnectView(this);
	}
	
	//Getters and setters
	
	public void log(String logEntry) {
		System.out.println(" " + logEntry);
	}
	
	public void logWithAlert(String logEntry) {
		log(logEntry);
		if (connectView.isVisible()) {
			connectView.alert(logEntry);
		}
	}
		
	public void myTurn() {
		//TODO: Ask player for turn and send to server
	}
	
	public Gamer getGamer() {
		return gamer;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void handleMove(Gamer gamer, int index) {
		game.doMove(index, gamer);
	}
	
	//Views
	public void connectionFailed() {
		logWithAlert("Connection failure, the server may be down.");
		connectView.enableControlls();
		connectView.setVisible(true);
	}
	
	public void connectionAstablished(String gamerName) {
		connectView.setVisible(false);
		if(gamer == null) {
			gamer = new Gamer();
		} 
		gamer.setName(gamerName);
		if(lobbyView == null) {
			lobbyView = new LobbyView(this);
		} else {
			lobbyView.setVisible(true);
		}
		
		
	}
	
	public void startGame(ArrayList<String> players) {
		if(gameView == null) {
			gameView = new GameView(this);
		} else {
			gameView.setVisible(true);
		}
		
		int i = 1;
		ArrayList<Gamer> gamers = new ArrayList<Gamer>();
		for(String name: players) {
			Gamer participant;
			if(name.equals(gamer.getName())) {
				participant = new Gamer();
			} else {
				participant = gamer;
			}
			participant.setName(name);
			participant.setColor(i);
			gamers.add(participant);
			i++;
		}
		
		game = new Game(gamers);
	}
	
	//Event handlers
	public void actionPerformed(ActionEvent event) {
		if(connectView != null && event.getSource() == connectView.getConnectButton()) {
			log("Connection to the server...");
			connectView.disableControlls();
			
			InetAddress host;
			try {    
				host = InetAddress.getByName(connectView.getHost());
			} catch (UnknownHostException e) {
				host = null;
				logWithAlert("Hostname invalid.");
			}
			
			int port = -1;
			try {
				port = Integer.parseInt(connectView.getPort());
				if (port < 1 && port > 65535) {
					logWithAlert("Port has to be in the range [1-65535].");
				}
			} catch (NumberFormatException e) {
				logWithAlert("Port is not a valid number.");
			}
			
			if(host != null && port > 0) {
				network = new NetworkController(host, port, this,"connect " + connectView.getNick());
				network.start();
				
			}
		} else if(lobbyView != null && event.getSource() == lobbyView.getJoinButton()) {
			if(network != null) {
				network.sendCommand("join "+lobbyView.getSpinnerValue());
				lobbyView.startLoading();
			}
		} else if(lobbyView != null && event.getSource() == lobbyView.getChatButton()) {
			sendChat(lobbyView.getChatMessage().getText());
		}
	}
	
	public void handleChat(String msg, String sender) {
		if(lobbyView != null && lobbyView.isVisible()) {
			lobbyView.getChatArea().append(sender + " says: " + msg + "\n");
		}
	}

	public void keyReleased(KeyEvent event) {
		if(lobbyView != null && event.getSource().equals(lobbyView.getChatMessage()) && event.getKeyCode() == KeyEvent.VK_ENTER ) {
			sendChat(lobbyView.getChatMessage().getText());
		}
		
	}
	
	public void sendChat(String msg) {
		if(lobbyView != null && network != null) {
			lobbyView.getChatMessage().setText("");
			log(msg + "\n");
			network.sendCommand("chat " + msg);
		}
	}

	public void update(Observable o, Object arg) {
		if(((String) arg).equals("move") && o.getClass().equals(game)) {
			//view.moveDone(game.getBoard().getSlots());
		}
	}
	
	public void keyTyped(KeyEvent event) {}
	public void keyPressed(KeyEvent event) {}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}
}