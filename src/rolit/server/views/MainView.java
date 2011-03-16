package rolit.server.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rolit.server.controllers.ApplicationController;
import rolit.server.models.LoggingInterface;



public class MainView extends JFrame implements LoggingInterface {

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

	/** Bouwt de daadwerkelijke GUI. */
	public void buildView() {
		/*setSize(645,375);

		JLabel hostLable 	= new JLabel("Hostname: ");
		hostField 			= new JTextField("", 12);
		hostField.setToolTipText("Your IP address");
		hostField.setEditable(false);

		JLabel portLable = new JLabel("Port: ");
		portField        = new JTextField("1337", 5);
		portField.setToolTipText("Set port for the server to use, you can only use ports that are not in use.");

		connectButton = new JButton("(Re)start the server");
		connectButton.addActionListener(viewController);

		JLabel logLable = new JLabel("Log:");
		logArea = new JTextArea("", 13, 74);
		logArea.setEditable(false);
		logArea.setLineWrap(true);
		logArea.setFont(new Font("Monaco", Font.PLAIN, 14));
		JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Indelen layout
		JPanel grid = new JPanel(new GridLayout(2,2));
		grid.add(hostLable);
		grid.add(hostField);
		grid.add(portLable);
		grid.add(portField);

		JPanel flowAbove = new JPanel(new FlowLayout());
		flowAbove.add(grid, BorderLayout.WEST);
		flowAbove.add(connectButton, BorderLayout.EAST);

		JPanel flowBelow = new JPanel();
		flowBelow.setLayout(new BorderLayout());
		flowBelow.add(logLable);
		flowBelow.add(scrollPane, BorderLayout.SOUTH);

		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		container.add(flowAbove); container.add(flowBelow);
		this.setResizable(false);*/
		
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
        jTextArea1.setFont(new Font("Monaco", Font.PLAIN, 14));
        jScrollPane1.setViewportView(jTextArea1);

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

	public void log(String msg) {
		if(msg != null) {
			logArea.append(" " + msg + "\n");
			logArea.setCaretPosition(logArea.getText().length());
			System.out.println(" " + msg);
		}
	}

}
