package rolit.client.views;

import rolit.client.controllers.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class LobbyView extends JFrame implements AlertableView {
	private JSpinner joinSpinner;
	private JButton joinButton;
	private JButton challengeButton;
	private JButton chatButton;
	private JTextArea chatArea;
	private JTextField chatMessage;
	private JList challengeList;
	private JProgressBar joinLoader;
	private ApplicationController viewController;

	public LobbyView(ApplicationController aController) {
		super("Welcome to the Lobby");
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
		
		
		JPanel jPanel1 = new javax.swing.JPanel();
        JLabel jLabel1 = new javax.swing.JLabel();
        JButton jButton2 = new javax.swing.JButton();
        JButton jButton1 = new javax.swing.JButton();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        JList jList1 = new javax.swing.JList();
        JSpinner jSpinner1 = new javax.swing.JSpinner();
        JLabel jLabel2 = new javax.swing.JLabel();
        JPanel jPanel2 = new javax.swing.JPanel();
        JTextField jTextField1 = new javax.swing.JTextField();
        JButton jButton3 = new javax.swing.JButton();
        JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        JTextArea jTextArea1 = new javax.swing.JTextArea();
        JProgressBar jProgressBar1 = new javax.swing.JProgressBar();
        ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        JRadioButton jRadioButton1 = new javax.swing.JRadioButton();
        JRadioButton jRadioButton2 = new javax.swing.JRadioButton();
        
        joinSpinner = jSpinner1;
        joinButton = jButton1;
        joinLoader = jProgressBar1;
        challengeButton = jButton2;
        chatArea = jTextArea1;
        chatMessage = jTextField1;
        chatButton = jButton3;
        challengeList = jList1;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Minimal players:");

        jButton2.setText("Challange");
        jButton2.addActionListener(viewController);

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(2, 2, 4, 1));

        jLabel2.setText("In the lobby:");

        jButton1.setText("Quick Join");
        jButton1.addActionListener(viewController);
        
        joinLoader.setVisible(false);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Human");
        jRadioButton1.addActionListener(viewController);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Computer");
        jRadioButton2.addActionListener(viewController);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jRadioButton2, 0, 0, Short.MAX_VALUE)
                    .add(jRadioButton1, 0, 0, Short.MAX_VALUE)
                    .add(jButton1, 0, 0, Short.MAX_VALUE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jSpinner1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jProgressBar1, 0, 0, Short.MAX_VALUE))
                .add(24, 24, 24)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .add(17, 17, 17))
        );

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
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        
       
	}
	
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message,
                "Lobby Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	public int getSpinnerValue() {
		javax.swing.SpinnerNumberModel myModel = (javax.swing.SpinnerNumberModel)(joinSpinner.getModel());
        int value = myModel.getNumber().intValue();
		return value;
		
	}
	
	public JButton getJoinButton() {
		return joinButton;
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
	
	public void startLoading() {
		joinLoader.setVisible(true);
		joinLoader.setIndeterminate(true);
	}
	
	public void stopLoading() {
		joinLoader.setVisible(false);
	}
	
}