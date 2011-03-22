package rolit.server.views;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rolit.server.controllers.ApplicationController;
import rolit.server.models.LoggingInterface;

public class MainView extends JFrame {

	private static final long serialVersionUID = 1L;
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

	/** Builds the GUI. */
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
	
	/**
	 *  
	 * @param logEntry
	 * @require logEntry != null
	 * @ensure Geplaatste string wordt in de logArea geplaast
	 */
	
	public void enterLogEntry(String logEntry) {
		if(logEntry != null) {
			logArea.append(" " + logEntry + "\n");
			logArea.setCaretPosition(logArea.getText().length());
		}
	}
}