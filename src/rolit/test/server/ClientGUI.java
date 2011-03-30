package rolit.test.server;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClientGUI extends JFrame implements KeyListener, ActionListener, MessageUI {

	private static final long serialVersionUID = 1L;
	private JButton bConnect;
	private JTextField tfAddress, tfPort, tfMyMessage;
	private JTextArea taMessages;
	private Client client;

	/** Construeert een ClientGUI object. */
	public ClientGUI() {
		super("Client GUI");
		buildGUI();
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		}
		);
	}

	/** Bouwt de daadwerkelijke GUI. */
	private void buildGUI() {
		setSize(700,500);

		// Panel p1 - Listen

		JPanel p1 = new JPanel(new FlowLayout());
		JPanel pp = new JPanel(new GridLayout(3,2));

		JLabel lbAddress = new JLabel("Adres: ");
		tfAddress = new JTextField("127.0.0.1", 12);
		tfAddress.addKeyListener(this);

		JLabel lbPort = new JLabel("Poort:");
		tfPort        = new JTextField("1337", 5);
		tfPort.addKeyListener(this);


		pp.add(lbAddress);
		pp.add(tfAddress);
		pp.add(lbPort);
		pp.add(tfPort);


		bConnect = new JButton("Connect");
		bConnect.setEnabled(true);
		bConnect.addActionListener(this);
		bConnect.setActionCommand("Connect");

		p1.add(pp, BorderLayout.WEST);
		p1.add(bConnect, BorderLayout.EAST);

		// Panel p2 - My Message

		JPanel p2 = new JPanel();
		p2.setLayout(new BorderLayout());

		JLabel lbMyMessage = new JLabel("Mijn bericht:");
		tfMyMessage = new JTextField(50);
		tfMyMessage.setEditable(false);
		tfMyMessage.addKeyListener(this);
		p2.add(lbMyMessage);
		p2.add(tfMyMessage, BorderLayout.SOUTH);

		// Panel p3 - IncomingMessages

		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());

		JLabel lbMessages = new JLabel("Messages:");
		taMessages = new JTextArea("", 15, 50);
		taMessages.setLineWrap(true);
		taMessages.setEditable(false);
		p3.add(lbMessages);
		p3.add(taMessages, BorderLayout.SOUTH);

		Container cc = getContentPane();
		GridBagLayout lo = new GridBagLayout();
		cc.setLayout(lo);
		GridBagConstraints gc1 = new GridBagConstraints();
		gc1.gridx = 1;
		gc1.gridy = 1;
		GridBagConstraints gc2 = new GridBagConstraints();
		gc2.gridx = 1;
		gc2.gridy = 2;
		GridBagConstraints gc3 = new GridBagConstraints();
		gc3.gridx = 1;
		gc3.gridy = 3;
		cc.add(p1, gc1); cc.add(p2, gc2); cc.add(p3, gc3);
	}

	/** Afhandeling van een actie van het GUI. */
	public void actionPerformed(ActionEvent ev) {
		if(ev.getActionCommand().equals("Connect")) {
			connect();
		}
	}

	/**
	 * Probeert een socket-verbinding op te zetten met de Server.
	 * Als alle parametervelden geldig zijn, dan wordt getracht een
	 * Client-object te construeren die de daadwerkelijke 
	 * socketverbinding met de Server maakt. Als dit gelukt is 
	 * wordt een aparte thread (van Client) opgestart, die de 
	 * berichten van de Server leest.
	 * Daarna worden alle parametervelden en de "Connect" Button
	 * niet selecteerbaar gemaakt.
	 */

	public void connect() {
		
		InetAddress iaddress;
		try {    
			iaddress = InetAddress.getByName(tfAddress.getText());
		} catch (UnknownHostException e) {
			iaddress = null;
		}
		
		int port = 2727; // Default port
		boolean negPort = true;
		try {
			port = Integer.parseInt(tfPort.getText());
			if (port<1) {
				addMessage("]Het port nummer mag niet negatief zijn]");
				negPort = true;
			} else {
				negPort = false;
			}
		} catch (NumberFormatException e) {
			addMessage("[Er is geen geldig poort nummer ingevuld]");
		}

		if (!negPort && iaddress != null) {
			try {
				client = new Client(iaddress, port, this);
				client.start();
				tfMyMessage.setEditable(true);
				tfAddress.setEnabled(false);
				tfPort.setEnabled(false);
			} catch (IOException e) {
				addMessage("[Kan geen verbinding maken met " + iaddress + " : " + port + "]");
			}
		}
	}

	/** Voegt een bericht toe aan de TextArea op het frame. */
	public void addMessage(String msg) {
		taMessages.append(msg + "\n");
	}

	/** Start een ClientGUI applicatie op. */
	public static void main(String args[]) {
		System.out.println("Een nieuwe client");
		new ClientGUI();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource().equals(tfMyMessage)) {
			String msg = tfMyMessage.getText();
			tfMyMessage.setText(null);
			client.sendMessage(msg);
			//addMessage("Ik zeg: " + msg);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if((e.getSource().equals(tfAddress) || e.getSource().equals(tfPort)
				&& (tfAddress.getText() != null && !tfAddress.getText().equals("")) 
				&& (tfPort.getText() != null && !tfPort.getText().equals("")) 
				)) {
			bConnect.setEnabled(true);
		}
	}

} // end of class ClientGUI

