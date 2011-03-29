package rolit.server.views;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import rolit.server.controllers.*;

/**
 * De MainView zorgt er voor dat er een GUI aan de gebruiker wordt getoont en dat de gebruiker deze GUI kan gebruiken.
 * De acties, zoals het drukken op een knop wordt afgehandeld door de ViewController van een View. En dat is in dit geval een instantie van ApplicationController.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
@SuppressWarnings("serial")
public class MainView extends JFrame {

	private JButton   				connectButton;
	private JTextField				portField;
	private JTextArea 				logArea;
	private JTextField				hostField;
	private ApplicationController	viewController;

	/**
	 * Maakt een instantie van MainView aan en zorgt ervoor dat de applicatie een GUI aan de gebruiker presenteert.
	 * Doormiddel van interne methoden wordt er een GUI gebouwd. Zodra deze gebouwd is kunnen er strings van textFields etc. opgevraagt worden van dit object.
	 * 
	 * @require aController != null
	 * @param aController
	 */
	public MainView(ApplicationController aController) {
		/*
		 * Zorgt er voor dat er een JFrame geopent wordt met de naam Rolit Server in de titel balk
		 */
		super("Rolit Server");

		viewController = aController;
		buildView();
		/*
		 * Maakt de GUI zichtbaar nadat hij gebouwd is.
		 */
		setVisible(true);
		
		/*
		 * Voegt een WindowListener toe die er naar luister of het venster gesloten wordt.
		 * Als het venster sluit wordt de hele applicatie afgesloten. 
		 */
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

	/**
	 * Interne methoden voor het bouwen van de GUI.
	 * Dit gebeurt met behulp van de swing-layout library.
	 * @require this.viewController != null;
	 */
	private void buildView() {
		
		/*
		 * Vreemde namen staan hier zodat ze overeenkomen met de NetBeans gegenereerde code.
		 */
		
		JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JTextField jTextField1 = new javax.swing.JTextField();
        JTextField jTextField2 = new javax.swing.JTextField();
        JButton jButton1 = new javax.swing.JButton();
        JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        JTextArea jTextArea1 = new javax.swing.JTextArea();
        
        hostField = jTextField1;
        portField = jTextField2;
        connectButton = jButton1;
        logArea = jTextArea1;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Hostname:");
        jLabel2.setText("Port:");
        jTextField2.setText("1337");

        jButton1.setText("Start the server");
        jButton1.addActionListener(viewController);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setFont(new Font("Monaco", Font.PLAIN, 12));
        jTextArea1.setEditable(false);
        jScrollPane1.setViewportView(jTextArea1);
        
        /*
         * NetBeans generator code vanaf hier.
         * Voor deze code is dan ook de extra library swing-layout-1.0.4.jar nodig
         */

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                            .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        
        /*
         * Einde NetBeans gegenereerde code.
         */
	}

	/**
	 * Zorgt er voor dat de gebruiker niet meer wijzigingen kan aanbrengen aan de textFields van de GUI
	 * en zorgt er voor dat de gebruiker niet meer kan drukken op de knoppen in deze GUI.
	 * @ensure this.getConnectButton().isEnabled = false;
	 */
	public void disableControls() {
		portField.setEnabled(false);
		hostField.setEnabled(false);
		connectButton.setEnabled(false);
	}
	
	/**
	 * Zorgt er voor dat de gebruiker wijzigingen kan aanbrengen aan de textFields van de GUI
	 * en zorgt er voor dat de gebruiker kan drukken op de knoppen in deze GUI.
	 * @ensure this.getConnectButton().isEnabled = true;
	 */
	public void enableControls() {
		portField.setEnabled(true);
		hostField.setEnabled(true);
		connectButton.setEnabled(true);
	}

	/**
	 * Vul het textField van de host in. Deze methoden wordt gebruikt door het huidige IP aders van de computer in het host veld te zetten.
	 * Uiteindelijk wordt het hostField helemaal nergens voor gebruikt behalve dan het informeren van de gebruiker van het huidige IP adres.
	 * @require host != null
	 * @param host
	 */
	public void setHostField(String host) {
		if(host != null) {
			hostField.setText(host);
		}
	}
	
	/**
	 * Vraag het JButton object van deze view op zodat je kan checken of deze is ingedrukt in de ViewController.
	 * @return != null
	 */
	public JButton getConnectButton() {
		return connectButton;
	}

	/**
	 * Vraag de string waarde van het textField voor de poort op. Zodat je kan gebruiken wat de gebruiker heeft ingevult in de GUI.
	 * @return != null
	 */
	public String getPortString() {
		return portField.getText();
	}
	
	/**
	 * Zorgt er voor dat de logEntry netjes in de logArea geplaatst wordt zodat de gebruiker kan zien wat er gelogt wordt.
	 * @param logEntry
	 * @require logEntry != null
	 * @ensure Geplaatste string wordt in de logArea geplaast
	 */
	public void enterLogEntry(String logEntry) {
		if(logEntry != null) {
			logArea.append(" " + logEntry + "\n");
			/*
			 * Deze regel code zorgt er voor dat op het moment dat er een nieuwe logEntry geplaast wordt
			 * de textArea netjes naar beneden gescroolt wordt.
			 */
			logArea.setCaretPosition(logArea.getText().length());
		}
	}
}