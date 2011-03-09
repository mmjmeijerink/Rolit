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
	
	private JButton     connectButton;
    private JTextField  portField;
    private JTextArea   logArea;
	
	public MainView() {
        super("Rolit Server");

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

        // Panel p1 - Listen

        JPanel p1 = new JPanel(new FlowLayout());
        JPanel pp = new JPanel(new GridLayout(2,2));

        JLabel hostLable = new JLabel("Hostname: ");
        JTextField hostField = new JTextField("", 12);
        hostField.setEditable(false);

        JLabel portLable = new JLabel("Port:");
        portField        = new JTextField("2727", 5);

        pp.add(hostLable);
        pp.add(hostField);
        pp.add(portLable);
        pp.add(portField);

        connectButton = new JButton("Start the server");
        //connectButton.addActionListener(this);

        p1.add(pp, BorderLayout.WEST);
        p1.add(connectButton, BorderLayout.EAST);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        JLabel logLable = new JLabel("Log:");
        logArea = new JTextArea("", 15, 50);
        
        logArea.setEditable(false);
        p2.add(logLable);
        p2.add(logArea, BorderLayout.SOUTH);

        Container cc = getContentPane();
        cc.setLayout(new FlowLayout());
        cc.add(p1); cc.add(p2);
    }
	
	
}
