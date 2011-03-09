package rolit.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainView extends JFrame implements LoggingInterface {

	private JButton   				connectButton;
	private JTextField				portField;
	private JTextArea 				logArea;
	private JTextField				hostField;
	private ApplicationController	viewController;

	public MainView(ApplicationController aController) {
		super("Rolit Server");

		viewController = aController;
		buildView();
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
	public void buildView() {
		setSize(645,375);

		JLabel hostLable 	= new JLabel("Hostname: ");
		hostField 			= new JTextField("", 12);
		hostField.setToolTipText("Your IP address");
		hostField.setEditable(false);

		JLabel portLable = new JLabel("Port: ");
		portField        = new JTextField("1337", 5);
		portField.setToolTipText("Set port for the server to use, you can only use ports that are not in use.");

		connectButton = new JButton("(Re)start the server");
		connectButton.addActionListener(viewController);

		JLabel logLable = new JLabel("Log:");
		logArea = new JTextArea("", 15, 50);
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Indelen layout
		JPanel grid = new JPanel(new GridLayout(2,2));
		grid.add(hostLable);
		grid.add(hostField);
		grid.add(portLable);
		grid.add(portField);

		JPanel flowAbove = new JPanel(new FlowLayout());
		flowAbove.add(grid, BorderLayout.WEST);
		flowAbove.add(connectButton, BorderLayout.EAST);

		JPanel flowBelow = new JPanel();
		flowBelow.setLayout(new BorderLayout());
		flowBelow.add(logLable);
		flowBelow.add(scrollPane, BorderLayout.SOUTH);

		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		container.add(flowAbove); container.add(flowBelow);
		this.setResizable(false);
	}

	public void disableControls() {
		portField.setEnabled(false);
		hostField.setEnabled(false);
		connectButton.setEnabled(false);
	}

	public void enableControls() {
		portField.setEnabled(true);
		hostField.setEnabled(true);
		connectButton.setEnabled(true);
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
		}
	}

}
