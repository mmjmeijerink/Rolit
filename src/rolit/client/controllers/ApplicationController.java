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

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rolit.client.models.LoggingInterface;
import rolit.client.views.MainView;
import rolit.sharedModels.*;

public class ApplicationController implements Observer, ActionListener, KeyListener, ChangeListener, LoggingInterface {
	
	private MainView			view;
	private NetworkController	network;
	private Game				game = null;
	private Gamer				gamer;
	
	public ApplicationController() {
		view = new MainView(this);
		gamer = new Gamer();
	}
	
	//Getters and setters
	public MainView view() {
		return view;
	}
	
	public void log(String logEntry) {
		view.log(logEntry);
	}
		
	public void turn() {
		//TODO: Ask player for turn and send to server
		view.enableBoard(game.getBoard());
	}
	
	public Gamer getGamer() {
		return gamer;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void move(Gamer gamer, int index) {
		game.doMove(index, gamer);
	}
	
	//Views
	public void connectionFailed() {
		view.connectMode();
	}
	
	public void startGame(ArrayList<String> players) {
		view.gameMode();
		
		int i = 1;
		ArrayList<Gamer> gamers = new ArrayList<Gamer>();
		for(String name : players) {
			Gamer participant = new Gamer();
			participant.setName(name);
			participant.setColor(i);
			gamers.add(participant);
			i++;
		}
		
		game = new Game(gamers);
	}
	
	public void enteringLobby() {
		view.lobbyMode();
	}
	
	//Event handlers
	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource().equals(view.connectButton())) {
			log("Connection to the server...");
			
			InetAddress host;
			try {    
				host = InetAddress.getByName(view.host());
			} catch (UnknownHostException e) {
				host = null;
				log("Hostname invalid.");
			}
			
			int port = 1337; // Default port
			try {
				port = Integer.parseInt(view.port());
				if (port < 1 && port > 65535) {
					log("Port has to be in the range [1-65535], using 1337 default.");
					port = 1337; // Default port
				}
			} catch (NumberFormatException e) {
				log("Port is not a valid number, using 1337 default.");
			}
			
			gamer.setName(view.nick());
			
			if(host != null) {
				network = new NetworkController(host, port, this);
				network.start();
			}
		}
		else if(event.getActionCommand().equals("Join")) {
			network.sendCommand("join " + view.amount());
		}
		else if(event.getActionCommand().equals("Exit")) {
			view.dispose();
		}
		else {
			//TODO: check op button en geef index door aan de server
			network.sendCommand("move " + event.getActionCommand());
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent event) {
		if(event.getSource().equals(view.getChatField()) && event.getKeyCode() == KeyEvent.VK_ENTER && network != null) {
			String msg = view.getChatField().getText();
			view.getChatField().setText(null);
			log(msg + "\n");
			network.sendCommand("chat " + msg);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if(((String) arg).equals("move") && o.getClass().equals(game)) {
			view.moveDone(game.getBoard().getSlots());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider slider = (JSlider) e.getSource();
		slider.setToolTipText(slider.getValue() + " players");
	}
}