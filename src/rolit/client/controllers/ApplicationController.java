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

import rolit.client.models.*;
import rolit.client.views.ConnectView;
import rolit.client.views.GameView;
import rolit.client.views.LobbyView;
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
	private boolean					wantsToStop;

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

	/**
	 * Deze methode kan worden aangeroepen als deze client aan de beurt is in de game.
	 */
	public void myTurn() {
		/*
		 * Er moet eerst gecheckt worden of de AI speelt.
		 */
		if(!aiIsPlaying) {
			/*
			 * Als de AI niet speelt mag de Hint button aangezet worden zodat deze gebruikt kan worden. 
			 */
			gameView.getHintButton().setEnabled(true);
			for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
				/*
				 * De knoppen uit de GameView die ingedruk kunnen worden worden hier aangezet
				 * zodat deze ingedruk kunnen worden. De knoppen die ingedruk kunnen worden
				 * krijgen ook een mooie grijze achtergrond.
				 */
				int color = gamer.getColor();
				if(game.getBoard().checkMove(i, color)) {
					gameView.getSlotsList().get(i).setEnabled(true);
					gameView.getSlotsList().get(i).setBackground(Color.GRAY);
				} else {
					gameView.getSlotsList().get(i).setEnabled(false);
				}
			}
		} else {
			/*
			 * Als de AI speelt wordt de huidige Thread inslaap gesust voor een bepaalde tijd
			 * zodat goed te zien is wat de AI nou eigenlijk doet tijdens een bepaalde zet.
			 * Met de slider kan de tijd van dit dutje ingestelt worden.
			 
			try {
				Thread.sleep(gameView.getTimeValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			int moves = gameView.getTimeValue()/500;
			/*
			 * Door de AI wordt de zet berekend en vervolgens naar de server gestuurd.
			 */
			int bestMove = ai.calculateBestMove(gamer.getColor(), game.getBoard(), moves);
			game.doMove(bestMove, gamer);
			network.sendCommand("domove "+bestMove);
			/*
			 * De game view mag vernieuwd worden omdat de situatie op het speel veld is verandert.
			 */
			updateGameView();
		}
	}

	/**
	 * Met deze methode kan de gamer opgevraagt worden die deze client moet representeren. Als de speler nog niet verbonden is
	 * en dus nog geen gamer heeft wordt er null teruggegeven.
	 * @return de gamer van deze client
	 */
	public Gamer getGamer() {
		return gamer;
	}

	/**
	 * Geeft de game terug waar deze client aan deelneemt, als deze client niet deelneemt wordt er null terug gegeven.
	 * @return de game waar deze client aan deelneemt
	 */
	public Game getGame() {
		return game;
	}

	/**
	 * Deze methode handelt een move af op het bord en kan worden gebruikt om server commando's te verwerken
	 * in het moddel. Daarnaast wordt de situatie op de GUI ook vernieuwt.
	 * @param aGamer de gamer die de zet doet
	 * @param index de index van het vakje dat door die gamer gezet wordt.
	 */
	public void handleMove(Gamer aGamer, int index) {
		game.doMove(index, aGamer);
		updateGameView();
	}

	/**
	 * Deze methode kan worden aangeroepen om de gameView up te daten.
	 * Dit zodat de game view op een juiste manier de knoppen weergeven zoals
	 * in het game en bord model op het moment staan ingestelt.
	 * 
	 * @require this.gameView != null
	 */
	private void updateGameView() {
		for(int i = 0; i < Board.DIMENSION*Board.DIMENSION; i++) {
			/*
			 * Loopt door alle vakjes op het bord heen en geeft vervolgens de
			 * corresponderen knoppen de juiste achtergrond.
			 */
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

	/**
	 * Deze methode kan aangeroepen worden als de verbinding met de server verbroken wordt
	 * of nooit tot stand kon komen. De methode zorgt er dan voor dat de juiste views weer
	 * weergegeven worden zodat de gebruiker opnieuw een verbinding kan proberen te maken.
	 */
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

	/**
	 * Deze connectie kan worden aangeroepen als een connectie tot stand gebracht kon worden.
	 * De gamer name moet megegeven worden zodat de client bij kan houden welke naam hij heeft.
	 * Tijdens deze methode wordt een gamer aangemaakt.
	 * @require gamerName != null
	 * @ensure this.getGamer() != null
	 * @param gamerName terug gegeven naam van deze client voor verwerking.
	 */
	@SuppressWarnings("serial")
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
		
		/*
		 * Voor als je de verbinding verbreekt met een server en dan verbind met een andere server
		 * die niet de challenge functie ondersteunt.
		 */
		//lobbyView.getChatArea().setText("");
		lobbyView.getChallengeList().setModel(new javax.swing.AbstractListModel() {
			String[] strings = { "No lobby command recieved" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
	}

	/**
	 * Met deze methode kan een game gestart worden. De gameView wordt zichtbaar als deze methode wordt
	 * aangeroepen. Ook wordt netjes de status van de lobby afgehandeld.
	 * @require players != null && 1 < players.size() < 5
	 * @param players Lijst met spelers waarmee de game moet starten
	 */
	public void startGame(ArrayList<String> players) {
		if(gameView == null) {
			gameView = new GameView(this);
		} else {
			gameView.setVisible(true);
		}
		lobbyView.setVisible(false);
		lobbyView.stopLoading();
		
		/*
		 * Voorkomt de dubbele alert bij het zelf afsluiten van een game.
		 */
		wantsToStop = false;

		int i = 1;
		ArrayList<Gamer> gamers = new ArrayList<Gamer>();
		for(String name: players) {
			/*
			 * Maakt voor elke value van players een gamer object aan.
			 */
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
		/*
		 * Als de AI aanstaat moet dit netjes verwerkt worden
		 * Er moet sowieso een AI geïnitaliseerd worden, 
		 * omdat de speler een hint op kan vragen
		 */
		if(lobbyView.smartComputerIsSet()) {
			ai = new SmartAIController(game.getBoard(), gamers);
			aiIsPlaying = true;
		} else if (lobbyView.computerIsSet()){
			ai = new AIController(game.getBoard(), gamers);
			aiIsPlaying = true;
		}
		else {
			ai = new SmartAIController(game.getBoard(), gamers);
		}
		updateGameView();
	}

	/**
	 * Met deze methode wordt het stoppen van een game door deze client verwerkt.
	 * Er wordt in deze methode een verkeerde zet naar de server verstuurt zodat de server
	 * deze client zal moeten kicken, de client keert terug naar de lobby en kan een nieuwe game joinen.
	 */
	public void stopGame() {
		wantsToStop = true;
		network.sendCommand("domove 31");
		lobbyView.setVisible(true);
		gameView.setVisible(false);
		logWithAlert("Game ended because you left.");
		game = null;
		gameView = null;
	}

	/**
	 * Deze methode van de Interface ActionListener handelt alle GUI events af. Zoals het drukken op knoppen etc.
	 * Er wordt geluistert naar de:
	 * connectButton van de connectView,
	 * chatButton van de gameView,
	 * chatButton van de lobbyView,
	 * hintButton van de GameView,
	 * joinButton van de lobbyView,
	 * challengeButton van de lobbyView en
	 * alle speel knoppen van de GameView
	 */
	public void actionPerformed(ActionEvent event) {
		/*
		 * Het verwerken van de connect opdracht van de client.
		 */
		if(connectView != null && event.getSource() == connectView.getConnectButton()) {
			log("Connection to the server...");

			/*
			 * Checkt of het host adres klopt
			 */
			InetAddress host;
			try {    
				host = InetAddress.getByName(connectView.getHost());
			} catch (UnknownHostException e) {
				host = null;
				logWithAlert("Hostname invalid.");
			}

			/*
			 * Checkt of het poort nummer geldig is.
			 */
			int port = -1;
			try {
				port = Integer.parseInt(connectView.getPort());
				if (port < 1 || port > 65535) {
					logWithAlert("Port has to be in the range [1-65535].");
				}
			} catch (NumberFormatException e) {
				logWithAlert("Port is not a valid number.");
			}

			if(host != null && port > 0 && port < 65536) {
				/*
				 * Maakt verbinding met de server als de poort en de host klopt.
				 */
				connectView.disableControlls();
				/*
				 * Deze functie zorgt er voor dat ale spaties uit de meegegeven nick worden gehaalt zodat
				 * de server de naam goed kan afhandelen en er fouten worden voorkomen.
				 */
				String realNick = connectView.getNick().replaceAll(" ", "");
				if(realNick.equals("")) {
					realNick = "TeLuiOmEenNaamInTeVoeren";
				}
				/*
				 * Maakt een netwerk controller aan en start de thread daarvoor.
				 */
				network = new NetworkController(host, port, this, "connect " + realNick);
				network.start();
			}
			
		/*
		 * Het verwerken van de join opdracht van de client. Dus als er gedrukt wordt op de "Quick Join" knop wordt gedrukt.
		 */	
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
			int moves = gameView.getTimeValue()/500;
			int bestMove = ai.calculateBestMove(gamer.getColor(), game.getBoard(), moves);
			gameView.getSlotsList().get(bestMove).setBackground(Color.WHITE);

		} else if(lobbyView != null && event.getSource() == lobbyView.getChallengeButton()) {
			if(lobbyView.getChallengeList().getSelectedValue() != null) {
				String selectedGamers = "challenge " + lobbyView.getChallengeList().getSelectedValue();

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

	/**
	 * 
	 * @param challenger
	 */
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
			if(!wantsToStop) {
				logWithAlert("You've got kicked!");
			} else {
				wantsToStop = false;
			}
			game = null;
			gameView = null;
		}
	}
}