package rolit.client.views;

import java.awt.event.*;
import javax.swing.*;

import rolit.client.controllers.*;

/**
 * De ConnectView zorgt voor de GUI waarmee de gebruiker kan instellen met welke server hij wil verbinden.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class ConnectView extends JFrame implements AlertableView {

	private JButton connectButton;
	private JTextField hostField, portField, nickField;
	private ApplicationController viewController;
	
	/**
	 * Maakt een instantie van ConnectView aan en zorgt ervoor dat de applicatie een GUI aan de gebruiker presenteert.
	 * Doormiddel van interne methoden wordt er een GUI gebouwd. Zodra deze gebouwd is kunnen er strings van textFields etc. opgevraagt worden van dit object.
	 * 
	 * @require aController != null
	 * @param aController
	 */
	public ConnectView(ApplicationController aController) {
		/*
		 * Zorgt er voor dat er een JFrame geopent wordt met de juiste naam in de titel balk
		 */
		super("Connect to a Rolit server");
		
		viewController = aController;
		buildView();
		/*
		 * Maakt de GUI zichtbaar nadat hij gebouwd is.
		 */
		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			/*
			 * Als het venster gesloten wordt mag de applicatie afgsloten worden.
			 */
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	/**
	 * Interne methoden voor het bouwen van de GUI.
	 * Dit gebeurt met behulp van de swing-layout library.
	 * @require this.viewController != null;
	 */
	public void buildView() {
		
		/*
		 * Vreemde namen staan hier zodat ze overeenkomen met de NetBeans gegenereerde code.
		 */
		
		JLabel jLabel5 = new javax.swing.JLabel();
        JButton jButton2 = new javax.swing.JButton();
        JLabel jLabel6 = new javax.swing.JLabel();
        JTextField jTextField6 = new javax.swing.JTextField();
        JTextField jTextField5 = new javax.swing.JTextField();
        JLabel jLabel7 = new javax.swing.JLabel();
        JTextField jTextField4 = new javax.swing.JTextField();
        
        connectButton = jButton2;
        hostField = jTextField6;
        portField = jTextField5;
        nickField = jTextField4;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        jButton2.setText("Connect");
        jButton2.addActionListener(viewController);
        
        jLabel5.setText("Server address:");
        jLabel6.setText("Poort:");
        jTextField6.setText("127.0.0.1");
        jTextField5.setText("1337");
        jLabel7.setText("Nickname:");
        jTextField4.setText("");
        
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
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 99, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        
        /*
         * Einde NetBeans gegenereerde code.
         */
        
	}
	/**
	 * Met deze methode kan een error bericht aan de gebruiker getoont worden.
	 * @require message != null
	 * @param message Het te tonen bericht
	 */
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message, "Connection Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Zorgt er voor dat de gebruiker wijzigingen kan aanbrengen aan de textFields van de GUI
	 * en zorgt er voor dat de gebruiker kan drukken op de knoppen in deze GUI.
	 * @ensure this.getConnectButton().isEnabled = true;
	 */
	public void enableControlls() {
		hostField.setEnabled(true);
		portField.setEnabled(true);
		nickField.setEnabled(true);
		connectButton.setEnabled(true);
	}
	
	/**
	 * Zorgt er voor dat de gebruiker niet meer wijzigingen kan aanbrengen aan de textFields van de GUI
	 * en zorgt er voor dat de gebruiker niet meer kan drukken op de knoppen in deze GUI.
	 * @ensure this.getConnectButton().isEnabled = false;
	 */
	public void disableControlls() {
		hostField.setEnabled(false);
		portField.setEnabled(false);
		nickField.setEnabled(false);
		connectButton.setEnabled(false);
	}
	
	/**
	 * Geeft de teskt van het hostField terug.
	 * @return De tekst die het hostField bevat
	 */
	public String getHost() {
		return hostField.getText();
	}
	
	/**
	 * Geeft de teskt van het portField terug.
	 * @return De tekst die het portField bevat
	 */
	public String getPort() {
		return portField.getText();
	}
	
	/**
	 * Geeft de teskt van het nickField terug.
	 * @return De tekst die het nickField bevat
	 */
	public String getNick() {
		return nickField.getText();
	}
	
	/**
	 * Geeft de connectButton terug zodat deze vergeleken kan worden in de actionListener.
	 * @return De connectButton
	 */
	public JButton getConnectButton() {
		return connectButton;
	}	
}