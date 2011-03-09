package rolit.client.GUI;

import java.awt.*;
import javax.swing.*;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel boardPanel = null;
	private JTextArea chatArea = null;
	private JTextField chatField = null;
	private JPanel chatPanel = null;
	private JPanel mainPanel = null;
	private JMenuBar menuBar = null;
	private JButton[] places = new JButton[rolit.game.Board.DIM * rolit.game.Board.DIM];
	//private JDialog aboutBox;

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
		this.setSize(600, 600);
		this.setContentPane(getContentPane());
		this.setTitle("Rolit client");
		this.setVisible(true);
	}
	
	public JPanel getContentPane() {
		if(mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getBoardPanel(), BorderLayout.NORTH);
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
				//places[i].setMargin(new java.awt.Insets(10, 10, 10, 10));
				places[i].setMaximumSize(new Dimension(100, 100));
				places[i].setMinimumSize(new Dimension(25, 25));
				places[i].setName("place" + i);
				places[i].setPreferredSize(new Dimension(30, 30));
				
				int row = i / rolit.game.Board.DIM;
				int col = i % rolit.game.Board.DIM;
				
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = row;
				constraints.gridy = col;
				
				boardPanel.add(places[i], constraints);
			}
		}
		return boardPanel;
	}
	
	private JPanel getChatPanel() {	
		if(chatPanel == null) {
			chatPanel = new JPanel();
			chatPanel.setLayout(new BorderLayout());
			
			chatField = new JTextField();
			chatArea = new JTextArea();
			menuBar = new JMenuBar();
			
			chatArea.setRows(5);
			chatArea.setColumns(20);
			chatArea.setLineWrap(true);
			chatPanel.add(chatArea, BorderLayout.NORTH);
			chatPanel.add(chatField, BorderLayout.SOUTH);
			//chatArea.getAccessibleContext().setAccessibleParent(chatPanel);
		}
		return chatPanel;
	}

	/* 
	GroupLayout chatPanelLayout = new GroupLayout(chatPanel);
	chatPanel.setLayout(chatPanelLayout);
	chatPanelLayout.setHorizontalGroup(chatPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(chatArea, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE).addComponent(chatField, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE));
	chatPanelLayout.setVerticalGroup(chatPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, chatPanelLayout.createSequentialGroup().addComponent(chatArea, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(chatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	*/	

	/*
	javax.swing.GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
	mainPanel.setLayout(mainPanelLayout);
	mainPanelLayout.setHorizontalGroup(mainPanelLayout
			.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE,
					GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(boardPanel, GroupLayout.DEFAULT_SIZE,
					541, Short.MAX_VALUE));
	mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup().addComponent(boardPanel, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(chatPanel,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
	*/
	
	/*
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
}