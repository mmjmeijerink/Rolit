
package rolit.client.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;

/**
 * The application's main frame.
 */
public class ClientGUI2 extends JFrame {

	private JPanel boardPanel;
	private JTextArea chatArea;
	private JTextField chatField;
	private JPanel chatPanel;
	private JPanel mainPanel;
	private JMenuBar menuBar;
	private JButton[] places = new JButton[rolit.game.Board.DIM * rolit.game.Board.DIM];
	private JDialog aboutBox;

	public ClientGUI2() {

		initComponents();

		boardPanel.setVisible(false);
		//connectPanel.setvisible(true);
		
		this.setVisible(true);
	}

	/*public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = ClientGUI.getApplication().getMainFrame();
			aboutBox = new ClientGUIAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		ClientGUI.getApplication().show(aboutBox);
	}*/

	private void initComponents() {

		mainPanel = new JPanel();
		chatPanel = new JPanel();
		chatField = new JTextField();
		chatArea = new JTextArea();
		boardPanel = new JPanel();
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem exitMenuItem = new JMenuItem();
		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();

		chatArea.setRows(5);
		chatArea.setColumns(20);
		chatArea.setLineWrap(true);
		//chatPanel.add(chatArea);
		//chatArea.getAccessibleContext().setAccessibleParent(chatPanel);

		GroupLayout chatPanelLayout = new GroupLayout(chatPanel);
		chatPanel.setLayout(chatPanelLayout);
		chatPanelLayout.setHorizontalGroup(chatPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(chatArea, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE).addComponent(chatField, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE));
		chatPanelLayout.setVerticalGroup(chatPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, chatPanelLayout.createSequentialGroup().addComponent(chatArea, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(chatField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		boardPanel.setLayout(new java.awt.GridBagLayout());
		

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

		javax.swing.GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);
		mainPanelLayout.setHorizontalGroup(mainPanelLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(chatPanel, GroupLayout.DEFAULT_SIZE,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(boardPanel, GroupLayout.DEFAULT_SIZE,
						541, Short.MAX_VALUE));
		mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup().addComponent(boardPanel, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(chatPanel,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)));
		
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);

		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
	}
	
	public static void main(String[] args) {
		new ClientGUI();
	}
}
