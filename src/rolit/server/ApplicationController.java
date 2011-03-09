package rolit.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ApplicationController implements ActionListener {
	private MainView view;
	
	public ApplicationController() {
		view = new MainView(this);
		
		try {
            InetAddress ip = InetAddress.getLocalHost();
            view.setHost(ip.getHostAddress());
        } catch (UnknownHostException e) {
        	System.out.println("Your system does not allow the server to know it's IP, you will not be able to start the server.");
        }
        
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == view.connectButton()) {
			System.out.println("Tries to connect!");
		}
		
	}
}
