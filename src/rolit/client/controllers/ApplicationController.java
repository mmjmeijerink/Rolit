package rolit.client.controllers;

import rolit.client.models.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import rolit.client.models.*;
import rolit.client.views.*;
import rolit.sharedModels.*;

/**
 * De ApplicationController beheert alle gebruiker interactie met het programma. De ApplicationController beheert dus de View's.
 * Daarnaast verwerkt de ApplicationController de acties op de GUI's en zorgt er vervolgens voor dat die op de juiste manier doorgegeven worden naar de NetworkController.
 * De ApplicationController luistert ook goed wat de NetworkController te vertellen heeft en verwerkt de commando's die van de server af komen en laat het resultaat zien op de
 * GUI's
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class ApplicationController implements Observer, ActionListener, KeyListener, LoggingInterface {

	private GameView				gameView;
	private ConnectView				connectView;
	private LobbyView				lobbyView;
	private NetworkController		network;
	private Game					game = null;
	private Gamer					gamer;
	private AIControllerInterface	ai;
	private boolean					aiIsPlaying;

	/**
	 * Met de constructor wordt automatisch een connectView aangemaakt en vervolgens weergegeven.
	 */
	public ApplicationController() {
		connectView = new ConnectView(this);		
	}

	/**
	 * Implementatie van de LoggingInterface
	 * Log zorgt er voor dat de log naar de jusite view gestuurdt wordt als deze zichtbaar is.
	 * Er wordt sowieso in de console gelogt maar het chat field van de zichtbare view kan ook.
	 * @require logEntry != null
	 * @param logEntry de te loggen tekst
	 */
	public void log(String logEntry) {
		System.out.println(" " + logEntry);
		/*
		 * Checkt welke views openstaan en als dat een view
		 * is met een textArea logt hij ook daarin.
		 */
		if(lobbyView != null && lobbyView.isVisible()) {
			lobbyView.getChatArea().append("[" + logEntry + "]\n");
			lobbyView.getChatArea().setCaretPosition(lobbyView.getChatArea().getText().length());
		} else if(gameView != null && gameView.isVisible()) {
			gameView.getChatArea().append("[" + logEntry + "]\n");
			gameView.getChatArea().setCaretPosition(gameView.getChatArea().getText().length());
		}
	}

	/**
	 * Logt het bericht net als de log(); methode maar zorgt er daarnaast ook
	 * dat het bericht in een alert box aan de gebruiker getoont wordt.
	 * @require logEntry != null
	 * @param logEntry de te loggen tekst en te tonen
	 */
	public void logWithAlert(String logEntry) {
		log(logEntry);
		/*
		 * Stuurt de alert door naar de openstaande view's 
		 */
		if (connectView != null && connectView.isVisible()) {
			connectView.alert(logEntry);
		}
		if (lobbyView != null && lobbyView.isVisible()) {
			lobbyView.alert(logEntry);
		}
		if (gameView != null && gameView.isVisible()) {
			gameView.alert(logEntry);
		}
	}

	public void myTurn() {
		if(!aiIsPlaying) {
			gameView.getHintButton().setEnabled(true);
			for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
				int color = gamer.getColor();
				if(game.getBoard().checkMove(i, color)) {
					gameView.getSlotsList().get(i).setEnabled(true);
					gameView.getSlotsList().get(i).setBackground(Color.GRAY);
				} else {
					gameView.getSlotsList().get(i).setEnabled(false);
				}
			}
		} else {
			try {
				Thread.sleep(gameView.getTimeValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int bestMove = ai.calculateBestMove(gamer.getColor());
			game.doMove(bestMove, gamer);
			network.sendCommand("domove "+bestMove);
			updateGameView();
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
				gameView.getSlotsList().get(i).setBackground(Color.BLACK);
			}
		}
	}

	//Views
	public void connectionFailed() {
		logWithAlert("Connection failure, the server may be down.");
		connectView.enableControlls();
		connectView.setVisible(true);
		if(lobbyView != null && lobbyView.isVisible()) {
			lobbyView.setVisible(false);
		}
		if(gameView != null && gameView.isVisible()) {
			gameView.setVisible(false);
		}
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

		aiIsPlaying = lobbyView.computerIsSet();
		lobbyView.setVisible(false);
		lobbyView.stopLoading();

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
		if(lobbyView.smartComputerIsSet()) {
			ai = new SmartAIController(game.getBoard());
		} else {
			ai = new AIController(game.getBoard(), gamers);
		}
		updateGameView();
	}

	public void stopGame() {
		network.sendCommand("domove 31");
		lobbyView.setVisible(true);
		gameView.setVisible(false);
		logWithAlert("Game ended because you left.");
		game = null;
		gameView = null;
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
				/*
				 * Deze functie zorgt er voor dat ale spaties uit de meegegeven nick worden gehaalt zodat
				 * de server de naam goed kan afhandelen en er fouten worden voorkomen.
				 */
				String realNick = connectView.getNick().replaceAll(" ", "");
				network = new NetworkController(host, port, this, "connect " + realNick);
				network.start();
			}
		} else if(lobbyView != null && event.getSource() == lobbyView.getJoinButton()) {
			if(network != null) {
				network.sendCommand("join "+lobbyView.getSpinnerValue());
				lobbyView.startLoading();
				lobbyView.computerIsSet();
			}
		} else if(lobbyView != null && event.getSource() == lobbyView.getChatButton()) {
			sendChat(lobbyView.getChatMessage().getText());
		} else if(gameView != null && event.getSource() == gameView.getChatButton()) {
			sendChat(gameView.getChatMessage().getText());
		} else if(gameView != null && event.getSource() == gameView.getHintButton()) {
			int bestMove = ai.calculateBestMove(gamer.getColor());
			gameView.getSlotsList().get(bestMove).setBackground(Color.WHITE);

		} else if(lobbyView != null && event.getSource() == lobbyView.getChallengeButton()) {
			if(lobbyView.getChallengeList().getSelectedValue() != null) {
				String selectedGamers = "challenge " + lobbyView.getChallengeList().getSelectedValue();

				/*//Object[] selected = lobbyView.getChallengeList().getSelectedValues();

				for(int i = 0; i < selected.length; i++) {
					selectedGamers.concat(" " + (String) selected[i]); 
				}*/

				if(!gamer.getName().equals((String) lobbyView.getChallengeList().getSelectedValue())) {
					network.sendCommand(selectedGamers);
					lobbyView.startLoading();
					log("You challengd " + lobbyView.getChallengeList().getSelectedValue());
				} else {
					logWithAlert("You can not challenge yourself.");
				}
			} else {
				logWithAlert("You need to select someone if you want to challenge.");
			}
		} else {
			for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
				if(gameView != null && event.getSource() == gameView.getSlotsList().get(i)) {
					game.doMove(i, gamer);
					network.sendCommand("domove "+i);
					updateGameView();
					gameView.disableAllButtons();
					gameView.getHintButton().setEnabled(false);
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
	/**
	 * Met deze functie kan een ontvangen chat bericht verwerkt worden.
	 * Als de lobby view open staat zal hij aan de lobbyview toegevoegd worden als de game view openstaat zal het gebeuren bij de game view.
	 * @require msg != null && sender != null
	 * @param msg De te vewererken chat message
	 * @param sender De verstuurder van het chatbericht
	 */
	public void handleChat(String msg, String sender) {
		if(lobbyView != null && lobbyView.isVisible()) {
			/*
			 * Als de lobbyView zichtbaar is zal het bericht aan de textArea daar toegevoegd worden
			 * de setCaretPosition zort er voor dat het chat venster netjes naar beneden gesrolt wordt.
			 */
			lobbyView.getChatArea().append(sender + " says: " + msg + "\n");
			lobbyView.getChatArea().setCaretPosition(lobbyView.getChatArea().getText().length());
		} else if(gameView != null && gameView.isVisible()) {
			/*
			 * Als de gameView zichtbaar is zal het bericht aan de textArea daar toegevoegd worden
			 * de setCaretPosition zort er voor dat het chat venster netjes naar beneden gesrolt wordt.
			 */
			gameView.getChatArea().append(sender + " says: " + msg + "\n");
			gameView.getChatArea().setCaretPosition(gameView.getChatArea().getText().length());
		}
	}
	
	/**
	 * Deze functie wordt gebruikt om chat berichten te versturen naar de server als er door de gebruiker
	 * in het chat textfield op enter wordt gedrukt.
	 * @require event.getSource.equals(lobbyView.getChatMessage()) || event.getSource.equals(gameView.getChatMessage())
	 */
	public void keyReleased(KeyEvent event) {
		if(lobbyView != null && event.getSource().equals(lobbyView.getChatMessage()) && event.getKeyCode() == KeyEvent.VK_ENTER ) {
			sendChat(lobbyView.getChatMessage().getText());
		} else if(gameView != null && event.getSource().equals(gameView.getChatMessage()) && event.getKeyCode() == KeyEvent.VK_ENTER ) {
			sendChat(gameView.getChatMessage().getText());
		}
	}

	/**
	 * Met deze methode kan een chat bericht verstuurd worden naar de server.
	 * @param msg het te versturen chat bericht
	 * @require msg != null
	 */
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

	public void endGame(String message) {
		if(gameView != null  && lobbyView != null && gameView.isVisible() && network != null) {
			gameView.setVisible(false);
			lobbyView.stopLoading();
			lobbyView.setVisible(true);
			if(game != null && game.getGamers() != null && game.getGamers().size() > 1) {
				lobbyView.message(message);
			} else {
				lobbyView.message("Game ended because somebody disconnected.");
			}
			game = null;
			gameView = null;
		}
	}

	public void gotKicked() {
		if(gameView != null  && lobbyView != null && gameView.isVisible() && network != null) {
			gameView.setVisible(false);
			lobbyView.stopLoading();
			lobbyView.setVisible(true);
			logWithAlert("You've got kicked!");
			game = null;
			gameView = null;
		}
	}
}