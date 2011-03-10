package rolit.client.GUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

public class ClientGUI extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private JPanel boardPanel = null;
	private JTextArea chatArea = null;
	private JTextField chatField = null;
	private JPanel chatPanel = null;
	private JPanel mainPanel = null;
	private JButton[] places = new JButton[rolit.game.Board.DIM * rolit.game.Board.DIM];
	//private JMenuBar menuBar = null;

	/**
	 * This is the default constructor
	 */
	public ClientGUI() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450, 400);
		this.setMinimumSize(new Dimension(400, 400));
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
		if(mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getBoardPanel(), BorderLayout.CENTER);
			mainPanel.add(getChatPanel(), BorderLayout.SOUTH);
		}
		return mainPanel;
	}
	
	private JPanel getBoardPanel() {
		if(boardPanel == null) {
			boardPanel = new JPanel();
			boardPanel.setLayout(new GridBagLayout());
			
			for(int i = 0; i < places.length; i++) {
				places[i] = new JButton();
				places[i].setMargin(new Insets(5, 5, 5, 5));
				//places[i].setMaximumSize(new Dimension(500, 500));
				//places[i].setMinimumSize(new Dimension(25, 25));
				places[i].setActionCommand(i + "");
				places[i].setText(i+"");
				places[i].setPreferredSize(new Dimension(30, 30));
				
				int row = i % rolit.game.Board.DIM;
				int col = i / rolit.game.Board.DIM;
				
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = row;
				constraints.gridy = col;
				
				boardPanel.add(places[i], constraints);
			}
			
			places[27].setBackground(new Color(255, 0, 0));
			places[28].setBackground(new Color(0, 0, 255));
			places[35].setBackground(new Color(0, 255, 0));
			places[36].setBackground(new Color(255, 255, 0));
		}
		return boardPanel;
	}
	
	private JPanel getChatPanel() {	
		if(chatPanel == null) {
			chatPanel = new JPanel();
			chatPanel.setLayout(new BorderLayout());
			
			chatField = new JTextField();
			chatArea = new JTextArea();
			
			//chatArea.setEditable(false);
			chatArea.setRows(5);
			chatArea.setColumns(20);
			chatArea.setLineWrap(true);
			
			chatPanel.add(chatArea, BorderLayout.NORTH);
			chatPanel.add(chatField, BorderLayout.SOUTH);
		}
		return chatPanel;
	}
	
	/*
	menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu();
	JMenuItem exitMenuItem = new JMenuItem();
	JMenu helpMenu = new JMenu();
	JMenuItem aboutMenuItem = new JMenuItem();
	
	fileMenu.add(exitMenuItem);
	menuBar.add(fileMenu);

	helpMenu.add(aboutMenuItem);
	menuBar.add(helpMenu);*/
	
	public static void main(String[] args) {
		new ClientGUI();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}