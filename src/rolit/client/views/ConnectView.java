package rolit.client.views;

import java.awt.event.*;
import javax.swing.*;

import rolit.client.controllers.*;

public class ConnectView extends JFrame implements AlertableView {

	private static final long serialVersionUID = 1L;
	private JButton connectButton;
	private JTextField hostField, portField, nickField;
	private ApplicationController viewController;

	public ConnectView(ApplicationController aController) {
		super("Connect to a Rolit server");
		viewController = aController;
		buildView();
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	/** Builds the GUI. */
	public void buildView() {
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
        jTextField4.setText("Sjaakbonenstaak");

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
	}
	
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message, "Connection Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	public void enableControlls() {
		hostField.setEnabled(true);
		portField.setEnabled(true);
		nickField.setEnabled(true);
		connectButton.setEnabled(true);
	}
	
	public void disableControlls() {
		hostField.setEnabled(false);
		portField.setEnabled(false);
		nickField.setEnabled(false);
		connectButton.setEnabled(false);
	}
	
	public String getHost() {
		return hostField.getText();
	}
	
	public String getPort() {
		return portField.getText();
	}
	
	public String getNick() {
		return nickField.getText();
	}
	
	public JButton getConnectButton() {
		return connectButton;
	}	
}