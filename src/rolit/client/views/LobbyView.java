package rolit.client.views;

import rolit.client.controllers.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class LobbyView extends JFrame implements AlertableView {
	private JSlider amount;
	private JList lobbyList;
	private ApplicationController viewController;

	public LobbyView(ApplicationController aController) {
		super("Connect to a Rolit server");
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
		
		JPanel lobbyPanel = new JPanel();
		GridBagLayout grid = new GridBagLayout();
		lobbyPanel.setLayout(grid);
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLabel players = new JLabel("Amount of players: ");
		constraints.gridx = 1;
		lobbyPanel.add(players, constraints);
		
		amount = new JSlider();
		amount.setMaximum(4);
		amount.setMinimum(2);
		amount.setSnapToTicks(true);
		amount.setToolTipText(amount.getValue() + " players");
		amount.addChangeListener(viewController);
		constraints.gridy = 2;
		lobbyPanel.add(amount, constraints);
		
		JButton joinButton = new JButton("Join");
		joinButton.addActionListener(viewController);
		joinButton.setActionCommand("Join");
		constraints.gridy = 3;
		lobbyPanel.add(joinButton, constraints);
		
		lobbyList = new JList();
		//lobbyList.setListData(viewController.getLobby().toArray());
		Object[] namen = {"Mart", "Thijs", "iemand", "en nog iemand"};
		lobbyList.setListData(namen);
		constraints.gridx = 2;
		constraints.gridy = 1;
		constraints.gridheight = 3;
		lobbyPanel.add(lobbyList, constraints);
			
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		container.add(lobbyPanel);
		this.setResizable(false);
	}
	
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message,
                "Lobby Alert", JOptionPane.ERROR_MESSAGE);
	}
	
}