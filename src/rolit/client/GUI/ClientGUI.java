package rolit.client.GUI;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

public class ClientGUI extends JFrame implements Observer, KeyListener, ActionListener {

	private static final Color RED = new Color(255, 0, 0);
	private static final Color GREEN = new Color(0, 255, 0);
	private static final Color BLUE = new Color(0, 0, 255);
	private static final Color YELLOW = new Color(255, 255, 0);
	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel = null, connectPanel = null, boardPanel = null, chatPanel = null;
	private JTextArea chatArea = null;
	private JButton bConnect;
	private JTextField tfAddress, tfPort, tfName, chatField = null;
	private JButton[] places = new JButton[rolit.game.Board.DIM * rolit.game.Board.DIM];
	private JMenuBar menuBar = null;

	/**
	 * This is the default constructor
	 */
	public ClientGUI() {
		super();
		initialize();
	}
	
	public void connect() {
		System.out.println("remove");
		mainPanel.remove(getConnectPanel());
		System.out.println("add new");
		mainPanel.add(getBoardPanel());
		System.out.println("krijgen we toch niet te zien");
		this.repaint();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}
	
	public void actionPerformed(ActionEvent e) {
    	if(e.getActionCommand().equals("Connect")) {
    		connect();
    	}
    	else if(e.getActionCommand().equals("")) {
    		this.dispose();
    	}
    }
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource().equals(chatField)) {
			String msg = chatField.getText();
			chatField.setText(null);
			//client.sendMessage(msg);
			//addMessage(msg);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if((e.getSource().equals(tfAddress) || e.getSource().equals(tfPort) || e.getSource().equals(tfName))
				&& (tfAddress.getText() != null && !tfAddress.getText().equals("")) 
				&& (tfPort.getText() != null && !tfPort.getText().equals("")) 
				&& (tfName.getText() != null && !tfName.getText().equals(""))) {
			bConnect.setEnabled(true);
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450, 450);
		this.setMinimumSize(new Dimension(400, 450));
		this.setContentPane(getContentPane());
		this.setTitle("Rolit client");

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
			exitMenuItem.addActionListener(this);
			
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
			connectPanel = new JPanel();
			JPanel pp = new JPanel(new GridLayout(3, 2));

			JLabel lbAddress = new JLabel("Hostname:");
			tfAddress = new JTextField(null, 12);
			tfAddress.addKeyListener(this);

			JLabel lbPort = new JLabel("Port:");
			tfPort = new JTextField(null, 5);
			tfPort.addKeyListener(this);

			JLabel lbName = new JLabel("Name:");
			tfName = new JTextField();
			tfName.addKeyListener(this);

			pp.add(lbAddress);
			pp.add(tfAddress);
			pp.add(lbPort);
			pp.add(tfPort);
			pp.add(lbName);
			pp.add(tfName);

			bConnect = new JButton("Connect");
			bConnect.setEnabled(false);
			bConnect.addActionListener(this);
			bConnect.setActionCommand("Connect");

			connectPanel.add(pp, BorderLayout.WEST);
			connectPanel.add(bConnect, BorderLayout.EAST);
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

				int row = i % rolit.game.Board.DIM;
				int col = i / rolit.game.Board.DIM;

				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = row;
				constraints.gridy = col;
				constraints.fill = GridBagConstraints.BOTH;

				boardPanel.add(places[i], constraints);
			}

			places[27].setBackground(ClientGUI.RED);
			places[28].setBackground(ClientGUI.YELLOW);
			places[35].setBackground(ClientGUI.BLUE);
			places[36].setBackground(ClientGUI.GREEN);
		}
		return boardPanel;
	}

	private JPanel getChatPanel() {
		if (chatPanel == null) {
			chatPanel = new JPanel();
			chatPanel.setLayout(new BorderLayout());

			chatField = new JTextField();
			chatArea = new JTextArea();
			
			
			chatField.addKeyListener(this);
			
			chatArea.setEditable(false);
			chatArea.setRows(7);
			chatArea.setColumns(20);
			chatArea.setLineWrap(true);
			JScrollPane scrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			chatPanel.add(scrollPane, BorderLayout.NORTH);
			chatPanel.add(chatField, BorderLayout.SOUTH);
		}
		return chatPanel;
	}

	public static void main(String[] args) {
		new ClientGUI();
	}
}