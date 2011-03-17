package rolit.client.views;

import rolit.client.controllers.*;
import rolit.sharedModels.Board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class GameView extends JFrame implements AlertableView,ComponentListener {
	private ArrayList<JButton> places = new ArrayList<JButton>();
	private JButton chatButton;
	private JTextArea chatArea;
	private JTextField chatMessage;
	private ApplicationController viewController;

	public GameView(ApplicationController aController) {
		super("Game on!");
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
		
        JPanel jPanel2 = new javax.swing.JPanel();
        JTextField jTextField1 = new javax.swing.JTextField();
        JButton jButton3 = new javax.swing.JButton();
        JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        JTextArea jTextArea1 = new javax.swing.JTextArea();
        
        chatArea = jTextArea1;
        chatMessage = jTextField1;
        chatButton = jButton3;
        
        JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridBagLayout());

		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			places.add(i, new JButton());
			places.get(i).setActionCommand(i + "");
			places.get(i).setText("");
			places.get(i).setOpaque(true);

			int row = i % Board.DIMENSION;
			int col = i / Board.DIMENSION;

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = row;
			constraints.gridy = col;
			constraints.fill = GridBagConstraints.BOTH;

			boardPanel.add(places.get(i), constraints);
		}

		places.get(27).setBackground(new Color(255, 0, 0));
		places.get(28).setBackground(new Color(255, 255, 0));
		places.get(35).setBackground(new Color(0, 0, 255));
		places.get(36).setBackground(new Color(0, 255, 0));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton3.setText("Chat");
        jButton3.addActionListener(viewController);
        
        jTextField1.addKeyListener(viewController);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton3)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(boardPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(20, 20, 20)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(20, 20, 20)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
            	.addContainerGap()
            	.add(boardPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
       
        this.addComponentListener(this);
	}
	
	public void componentResized(ComponentEvent e) {
        // This is only called when the user releases the mouse button.
    	double widthButton = (((JFrame)e.getSource()).getWidth() - 40 - 8*50)/16;
    	double heightButton = widthButton*1.5;
    	if(heightButton < 15) {
    		heightButton = 15;
    		widthButton = 0;
    	}
    	if((heightButton+10)*16 > ((JFrame)e.getSource()).getHeight() - 200 ) {
    		heightButton = ((((JFrame)e.getSource()).getHeight() - 200)/16)-10;
    		widthButton = heightButton/1.5;
    	}
    	
    	for(JButton button : places) {
    		button.setMargin(new Insets((int)heightButton, (int)widthButton, (int)heightButton, (int)widthButton));
    	}
        //System.out.println("componentResized");
    }
	
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message,
                "Lobby Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	public JTextField getChatMessage() {
		return chatMessage;
	}
	
	public JTextArea getChatArea() {
		return chatArea;
	}
	
	public JButton getChatButton() {
		return chatButton;
	}

	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
	
}