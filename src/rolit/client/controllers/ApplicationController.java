package rolit.client.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import rolit.client.models.LoggingInterface;
import rolit.client.views.ConnectView;
import rolit.client.views.GameView;
import rolit.client.views.LobbyView;
import rolit.sharedModels.*;

public class ApplicationController implements Observer, ActionListener, KeyListener, LoggingInterface {
	
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
		if(lobbyView != null && lobbyView.isVisible()) {
			lobbyView.getChatArea().append("[" + logEntry + "]\n");
		} else if(gameView != null && gameView.isVisible()) {
			gameView.getChatArea().append("[" + logEntry + "]\n");
		}
	}
	
	public void logWithAlert(String logEntry) {
		log(logEntry);
		if (connectView.isVisible()) {
			connectView.alert(logEntry);
		}
	}
		
	public void myTurn() {
		for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
			int color = gamer.getColor();
			if(game.getBoard().checkMove(i, color)) {
				gameView.getSlotsList().get(i).setEnabled(true);
			} else {
				gameView.getSlotsList().get(i).setEnabled(false);
			}
		}
	}
	
	public Gamer getGamer() {
		return gamer;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void handleMove(Gamer aGamer, int index) {
		game.doMove(index, aGamer);
		updateGameView();
	}
	
	private void updateGameView() {
		for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
			int color = game.getBoard().getSlots().get(i).getValue();
			if(color == Slot.RED) {
				gameView.getSlotsList().get(i).setBackground(Color.RED);
			} else if(color == Slot.GREEN) {
				gameView.getSlotsList().get(i).setBackground(Color.GREEN);
			} else if(color == Slot.YELLOW) {
				gameView.getSlotsList().get(i).setBackground(Color.YELLOW);
			} else if(color == Slot.BLUE) {
				gameView.getSlotsList().get(i).setBackground(Color.BLUE);
			} else {
				//gameView.getSlotsList().get(i).setBackground(Color.GRAY);
			}
		}
		
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
		lobbyView.setVisible(false);
		
		int i = 1;
		ArrayList<Gamer> gamers = new ArrayList<Gamer>();
		for(String name: players) {
			Gamer participant;
			if(name.equals(gamer.getName())) {
				participant = gamer;
			} else {
				participant = new Gamer();
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
				connectView.disableControlls();
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
		} else if(gameView != null && event.getSource() == gameView.getChatButton()) {
			sendChat(gameView.getChatMessage().getText());
		} else if(lobbyView != null && event.getSource() == lobbyView.getChallengeButton()) {
			String selectedGamers = "challenge";
			Object[] selected = lobbyView.getChallengeList().getSelectedValues();
			
			for(int i = 0; i < selected.length; i++) {
				selectedGamers.concat(" " + (String) selected[i]); 
			}
			network.sendCommand(selectedGamers);
			
		} else {
			for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
				if(gameView != null && event.getSource() == gameView.getSlotsList().get(i)) {
					game.doMove(i, gamer);
					network.sendCommand("domove "+i);
					updateGameView();
					gameView.disableAllButtons();
					
				}
			}
		}
	}
	
	public void challenged(String challenger) {
		if(lobbyView != null && lobbyView.isVisible()) {
			int choice = lobbyView.challengeReceived(challenger);
			
			if(choice == JOptionPane.YES_OPTION) {
				network.sendCommand("challengeresponse " + challenger + " true");
			}
			else if(choice == JOptionPane.NO_OPTION) {
				network.sendCommand("challengeresponse " + challenger + " false");
			}
		}
	}
	
	public void handleChat(String msg, String sender) {
		if(lobbyView != null && lobbyView.isVisible()) {
			lobbyView.getChatArea().append(sender + " says: " + msg + "\n");
		} else if(gameView != null && gameView.isVisible()) {
			gameView.getChatArea().append(sender + " says: " + msg + "\n");
		}
	}

	public void keyReleased(KeyEvent event) {
		if(lobbyView != null && event.getSource().equals(lobbyView.getChatMessage()) && event.getKeyCode() == KeyEvent.VK_ENTER ) {
			sendChat(lobbyView.getChatMessage().getText());
		} else if(gameView != null && event.getSource().equals(gameView.getChatMessage()) && event.getKeyCode() == KeyEvent.VK_ENTER ) {
			sendChat(gameView.getChatMessage().getText());
		}
		
	}
	
	public void sendChat(String msg) {
		if(lobbyView != null && lobbyView.isVisible() && network != null) {
			lobbyView.getChatMessage().setText("");
			network.sendCommand("chat " + msg);
		} else if(gameView != null && gameView.isVisible() && network != null) {
			gameView.getChatMessage().setText("");
			network.sendCommand("chat " + msg);
		}
	}

	public void update(Observable o, Object arg) {
		if(((String) arg).equals("move") && o.getClass().equals(game)) {
			updateGameView();
		}
	}
	
	public void keyTyped(KeyEvent event) {}
	public void keyPressed(KeyEvent event) {}

	public void updateLobby() {
		if(lobbyView != null && lobbyView.isVisible() && network != null) {
			lobbyView.setChallengeList(network.getLobby());
			//log("Lobby Updated");
		}
	}
}