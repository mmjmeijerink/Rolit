package rolit.client.views;

import rolit.client.controllers.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ConnectView extends JFrame {
	private JButton connectButton;
	private JTextField hostField, portField, nickField;
	private ApplicationController viewController;

	public ConnectView(ApplicationController aController) {
		super("Rolit Client");
		viewController = aController;
		buildView();
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	/** Bouwt de daadwerkelijke GUI. */
	public void buildView() {
		this.setSize(500, 150);
		this.setMinimumSize(new Dimension(500, 150));
		
		JLabel hostLable 	= new JLabel("Hostname: ");
		hostField 			= new JTextField("127.0.0.1", 12);
		hostField.setToolTipText("Server IP address");

		JLabel portLable = new JLabel("Port: ");
		portField        = new JTextField("1337", 5);
		portField.setToolTipText("Set port for the server to use, you can only use ports that are not in use.");
		
		JLabel nickLable = new JLabel("Nickname: ");
		nickField        = new JTextField("Sjaakbonenstaak", 12);
		nickField.setToolTipText("Set the nickname you whant to use.");

		connectButton = new JButton("Connect");
		connectButton.addActionListener(viewController);

		// Indelen layout
		JPanel grid = new JPanel(new GridLayout(3,2));
		grid.add(hostLable);
		grid.add(hostField);
		grid.add(portLable);
		grid.add(portField);
		grid.add(nickLable);
		grid.add(nickField);
			
		Container container = getContentPane();
		container.setLayout(new FlowLayout(FlowLayout.CENTER,0,15));
		container.add(grid, BorderLayout.WEST);
		container.add(connectButton, BorderLayout.EAST);
		this.setResizable(false);
	}
	
}
