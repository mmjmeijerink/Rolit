package rolit.server;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainView extends JFrame {
	
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
        setSize(640,375);

        JLabel hostLable 	= new JLabel("Hostname: ");
        hostField 			= new JTextField("", 12);
        hostField.setToolTipText("Your IP address");
        hostField.setEditable(false);
        
        JLabel portLable = new JLabel("Port: ");
        portField        = new JTextField("1337", 5);
        portField.setToolTipText("Set port for the server to use, you can only use ports that are not in use.");
        
        connectButton = new JButton("Start the server");
        connectButton.addActionListener(viewController);
        
        JLabel logLable = new JLabel("Log:");
        logArea = new JTextArea("", 15, 50);
        logArea.setEditable(false);
        
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
        flowBelow.add(logArea, BorderLayout.SOUTH);

        Container container = getContentPane();
        container.setLayout(new FlowLayout());
        container.add(flowAbove); container.add(flowBelow);
        this.setResizable(false);
    }
	
    public void setHost(String host) {
    	if(host != null) {
    		hostField.setText(host);
    	}
    }
    
    public JButton connectButton() {
    	return connectButton;
    }
	
}
