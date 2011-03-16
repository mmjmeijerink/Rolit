package rolit.client.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import rolit.client.models.LoggingInterface;
import rolit.client.views.MainView;


public class ApplicationController implements ActionListener, KeyListener, LoggingInterface {
	
	private MainView			view;
	private NetworkController	network;
	
	public ApplicationController() {
		view = new MainView(this);
	}
	
	//Getters en setters
	public MainView view() {
		return view;
	}
	
	public void log(String logEntry) {
		view.log(logEntry);
	}
	
	public void connectionFailed() {
		view.connectMode();
	}

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
			
			if(host != null) {
				network = new NetworkController(host, port, this);
				network.start();
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		if(event.getSource().equals(view.getChatField()) && event.getKeyCode() == KeyEvent.VK_ENTER) {
			String msg = view.getChatField().getText();
			view.getChatField().setText(null);
			log(msg + "\n");
			network.sendCommand("chat " + msg);
		}
	}
}