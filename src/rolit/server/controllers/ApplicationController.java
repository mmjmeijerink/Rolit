package rolit.server.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import rolit.server.models.LoggingInterface;
import rolit.server.views.MainView;


public class ApplicationController implements ActionListener,LoggingInterface {
	private MainView 			view;
	private NetworkController 	network;
	
	public ApplicationController() {
		view = new MainView(this);
		
		try {
            InetAddress ip = InetAddress.getLocalHost();
            view.setHost(ip.getHostAddress());
        } catch (UnknownHostException e) {
        	view.log("Your system does not allow the server to know it's IP, you will not be able to start the server.");
        	view.disableControls();
        }
        
	}
	
	//Getters en setters
	public MainView view() {
		return view;
	}
	
	public void log(String logEntry) {
		view.log(logEntry);
	}
	
	public void connectionFailed() {
		view.enableControls();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == view.connectButton()) {
			log("Starting the server...");
			
			/*InetAddress host;
			try {    
				host = InetAddress.getByName(view.host());
			} catch (UnknownHostException e) {
				host = null;
				log("Hostname invalid.");
			}*/
			
			int port = 1337; // Default port
			try {
				port = Integer.parseInt(view.port());
				if (port<1 && port > 65535) {
					log("Port has to be in the range [1-65535], using 1337 default.");
					port = 1337; // Default port
				}
			} catch (NumberFormatException e) {
				log("Port is not a valid number, using 1337 default.");
			}
			
			network = new NetworkController(port, this);
			network.start();
			view.disableControls();
			
		}
		
	}
}
