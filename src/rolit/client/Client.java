package rolit.client;

import rolit.game.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class Client extends Observable implements Observer {
	
	private Game game;
	private Socket sock;
	private BufferedWriter server;
	
	public Client() {
		
	}
	
	public void connect(InetAddress addr, int port, String name) {
		try {
			sock = new Socket(addr, port);
			server = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			setChanged();
			notifyObservers("connectError");
			
			if(Main.debug) {
				e.printStackTrace();
			}
		}
	}
	
	public void startGame() {
		game = new Game();
		game.addObserver(this);
		
		setChanged();
		notifyObservers("Start");
	}
	
	public Game getGame() {
		return game;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}