package rolit.client.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rolit.client.controllers.ApplicationController;
import rolit.client.models.LoggingInterface;
import rolit.sharedModels.Board;

public class MainView extends JFrame implements LoggingInterface {

	private static final long serialVersionUID = 1L;
	private JPanel mainPanel = null, connectPanel = null, boardPanel = null, chatPanel = null;
	private JButton[] places = new JButton[Board.DIMENSION * Board.DIMENSION];
	private JButton connectButton;
	private JTextField hostField, portField, nickField, chatField = null;
	private JMenuBar menuBar = null;
	private JTextArea logArea = null;
	private ApplicationController viewController;

	public MainView(ApplicationController aController) {
		super("Rolit Client");

		viewController = aController;
		initialize();
	}

	/** Initializes the components of the GUI. */
	private void initialize() {
		this.setSize(450, 450);
		this.setMinimumSize(new Dimension(400, 450));
		this.setContentPane(getContentPane());
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setVisible(true);
	}
	
	public JPanel getContentPane() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getMenu(), BorderLayout.NORTH);
			mainPanel.add(getConnectPanel(), BorderLayout.CENTER);
			mainPanel.add(getChatPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}
	
	private JMenuBar getMenu() {
		if(menuBar == null) {
			menuBar = new JMenuBar();
			
			JMenu fileMenu = new JMenu("File");
			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.addActionListener(viewController);
			exitMenuItem.setActionCommand("Exit");
			
			JMenu helpMenu = new JMenu("Help");
			JMenuItem aboutMenuItem = new JMenuItem("About");
			
			fileMenu.add(exitMenuItem);
			menuBar.add(fileMenu);
			
			helpMenu.add(aboutMenuItem);
			menuBar.add(helpMenu);
		}
		return menuBar;
	}

	private JPanel getConnectPanel() {
		if (connectPanel == null) {
			connectPanel = new JPanel(new GridBagLayout());
			JPanel pp = new JPanel(new GridLayout(3, 2));

			JLabel lbAddress = new JLabel("Hostname:");
			hostField = new JTextField(null, 12);

			JLabel lbPort = new JLabel("Port:");
			portField = new JTextField(null, 5);

			JLabel lbName = new JLabel("Name:");
			nickField = new JTextField();

			pp.add(lbAddress);
			pp.add(hostField);
			pp.add(lbPort);
			pp.add(portField);
			pp.add(lbName);
			pp.add(nickField);

			connectButton = new JButton("Connect");
			connectButton.addActionListener(viewController);
			connectButton.setActionCommand("Connect");
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 1;
			connectPanel.add(pp, constraints);
			constraints.gridx = 2;
			connectPanel.add(connectButton, constraints);
		}
		return connectPanel;
	}

	private JPanel getBoardPanel() {
		if (boardPanel == null) {
			boardPanel = new JPanel();
			boardPanel.setLayout(new GridBagLayout());

			for (int i = 0; i < places.length; i++) {
				places[i] = new JButton();
				places[i].setMargin(new Insets(5, 5, 5, 5));
				places[i].setActionCommand(i + "");
				places[i].setText(i + "");

				int row = i % Board.DIMENSION;
				int col = i / Board.DIMENSION;

				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = row;
				constraints.gridy = col;
				constraints.fill = GridBagConstraints.BOTH;

				boardPanel.add(places[i], constraints);
			}

			places[27].setBackground(new Color(255, 0, 0));
			places[28].setBackground(new Color(255, 255, 0));
			places[35].setBackground(new Color(0, 0, 255));
			places[36].setBackground(new Color(0, 255, 0));
		}
		return boardPanel;
	}

	private JPanel getChatPanel() {
		if (chatPanel == null) {
			chatPanel = new JPanel();
			chatPanel.setLayout(new BorderLayout());

			logArea = new JTextArea();			
			logArea.setEditable(false);
			logArea.setRows(7);
			logArea.setColumns(20);
			logArea.setLineWrap(true);
			JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			chatPanel.add(scrollPane, BorderLayout.NORTH);
			chatPanel.add(getChatField(), BorderLayout.SOUTH);
		}
		return chatPanel;
	}
	
	public JTextField getChatField() {
		if(chatField == null) {
			chatField = new JTextField();
			chatField.addKeyListener(viewController);
		}
		return chatField;
	}

	// Getters en setters
	public void setHost(String host) {
		if(host != null) {
			hostField.setText(host);
		}
	}

	public JButton connectButton() {
		return connectButton;
	}

	public String port() {
		return portField.getText();
	}

	public String host() {
		return hostField.getText();
	}

	public void log(String msg) {
		if(msg != null) {
			logArea.append(" " + msg + "\n");
			logArea.setCaretPosition(logArea.getText().length());
			System.out.println(" " + msg);
		}
	}

}