package rolit.client.views;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import rolit.client.controllers.*;
import rolit.sharedModels.*;

/**
 * De GameView zorgt voor het weergeven van een GUI van een game.
 * Als de gebruiker in een game zit kan hij via deze GUI zetten doen en chat berichten versturen.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
@SuppressWarnings("serial")
public class GameView extends JFrame implements AlertableView, ComponentListener, WindowListener {
	
	private ArrayList<JButton> places = new ArrayList<JButton>();
	private JButton chatButton;
	private JTextArea chatArea;
	private JTextField chatMessage;
	private JButton hintButton;
	private JSlider timeSlider;
	private ApplicationController viewController;

	/**
	 * Maakt een instantie van GameView aan en zorgt ervoor dat de applicatie een GUI aan de gebruiker presenteert.
	 * Doormiddel van interne methoden wordt er een GUI gebouwd. Zodra deze gebouwd is kunnen er strings van textFields etc. opgevraagt worden van dit object.
	 * 
	 * @require aController != null
	 * @param aController de viewController die kan dienen als ActionListener
	 */
	public GameView(ApplicationController aController) {
		/*
		 * Zorgt er voor dat er een JFrame geopent wordt met de juiste naam in de titel balk
		 */
		super("Game on!");
		
		viewController = aController;
		buildView();
		/*
		 * Maakt de GUI zichtbaar nadat hij gebouwd is.
		 */
		this.setVisible(true);
		
		/*
		 * Er moet worden geluistert naar het sluiten van het venster,
		 * als dit gebeurt moet er namelijk worden terug gegaan naar de lobby.
		 */
		this.addWindowListener(this);
		this.setDefaultCloseOperation ( JFrame.DO_NOTHING_ON_CLOSE );
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
		
        JPanel jPanel2 = new javax.swing.JPanel();
        JTextField jTextField1 = new javax.swing.JTextField();
        JButton jButton3 = new javax.swing.JButton();
        JButton jButton4 = new javax.swing.JButton();
        JScrollPane jScrollPane2 = new javax.swing.JScrollPane();
        JTextArea jTextArea1 = new javax.swing.JTextArea();
        JSlider jSlider1 = new javax.swing.JSlider();
        
        chatArea = jTextArea1;
        chatMessage = jTextField1;
        chatButton = jButton3;
        hintButton = jButton4;
        timeSlider = jSlider1;
        
        JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridBagLayout());
		
		/*
		 * In deze loop wordt het Grid voor de knoppen gebouwt.
		 */

		for (int i = 0; i < Board.DIMENSION * Board.DIMENSION; i++) {
			places.add(i, new JButton());
			places.get(i).setActionCommand(i + "");
			places.get(i).setText("");
			/*
			 * opaque is nodig voor het juist weergeven van de knoppen op een Mac 
			 */
			places.get(i).setOpaque(true);
			places.get(i).setEnabled(false);
			places.get(i).addActionListener(viewController);

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
        
        /*
         * Hint wordt alleen actief is als een menselijke speler aan de beurt is.
         */
        jButton4.setText("Hint?");
        jButton4.addActionListener(viewController);
        jButton4.setEnabled(false);
        
        jTextField1.addKeyListener(viewController);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jScrollPane2.setViewportView(jTextArea1);
        
        jSlider1.setMaximum(3000);
        jSlider1.setValue(3000);
        
        /*
         * NetBeans generator code vanaf hier.
         * Voor deze code is dan ook de extra library swing-layout-1.0.4.jar nodig
         */

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
                        .add(jButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSlider1)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
            	.add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    
                    .add(jButton3)
                    .add(jButton4))
                .add(jSlider1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
        
        /*
         * Einde NetBeans gegenereerde code.
         */
        
        this.addComponentListener(this);
        /*
         * De Component listener metoden worden gebruikt voor het resizen van de knoppen als de window ook geresized wordt.
         */
	}
	
	/**
	 * Als de windows wordt geresized moeten de knoppen ook mee resizen.
	 * Daarvoor is deze methode. Omdat dit object een componentListener is wordt deze methode
	 * automatisch aangenomen.
	 * @require e == this
	 */
	public void componentResized(ComponentEvent e) {
        if(e.getSource() == this) {
        	/*
        	 * Er wordt een breete en hoogte bepaalt die netjes de buttons weergeeft.
        	 * Deze waarde zijn bepaalt door het uit proberen
        	 */
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
	    		/*
	    		 * Op Mac en Windows worden knoppen anders weergegeven, daar wordt hier voor gecompenseert.
	    		 */
	    		String osName = System.getProperty("os.name");
	    		if(osName.startsWith("Mac OS X")) {
	    			button.setMargin(new Insets((int)heightButton, (int)widthButton, (int)heightButton, (int)widthButton));
	    		} else {
	    			button.setMargin(new Insets((int)heightButton, (int)heightButton, (int)heightButton, (int)heightButton));
	    		}
	    	}
        }
    }
	
	/**
	 * Met deze methode kan een error bericht aan de gebruiker getoont worden.
	 * @require message != null
	 * @param message Het te tonen bericht
	 */
	public void alert(String message) {
		JOptionPane.showMessageDialog(this, message, "Lobby Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Met deze methode worden alle spel knoppen op het bord uitgezet en kan de gebruiker
	 * ze dus niet meer indrukken. Met this.getSlotsList() kunnen bepaalde knoppen vervolgens weer aangezet worden
	 * als dat nodig is voor het spel.
	 */
	public void disableAllButtons() {
		for(JButton aButton: places) {
			aButton.setEnabled(false);
		}
	}
	
	/**
	 * Geeft een lijst terug met alle JButtons die voor het spel bord gebruikt worden.
	 * Dit is bedoelt dat de aanroeper deze knoppen in kan schakelen en achtergrond kleuren kan geven.
	 * @return lijst met knoppen die voor de spel knoppen moeten staan.
	 */
	public ArrayList<JButton> getSlotsList() {
		return places;
	}
	
	/**
	 * Geeft het JTextField terug met daarin de chatberichten zodat deze kan worden vergeleken in de KeyListener
	 * @return het textField met het te versturen chat bericht
	 */
	public JTextField getChatMessage() {
		return chatMessage;
	}
	
	/**
	 * Geeft de chatArea terug zodat daar berichten aan toegevoegd kunnen worden als dat nodig is.
	 * @return geeft de chatArea.
	 */
	public JTextArea getChatArea() {
		return chatArea;
	}
	
	/**
	 * Geeft de chatButton terug zodat deze vergeleken kan worden in de actionListener.
	 * @return De chatButton
	 */
	public JButton getChatButton() {
		return chatButton;
	}
	
	/**
	 * Geeft de hintButton terug zodat deze vergeleken kan worden in de actionListener.
	 * @return De hintButton
	 */
	public JButton getHintButton() {
		return hintButton;
	}
	
	/**
	 * Geeft de huidige waarde van de slider terug.
	 * @return de waarde tussen 0 en 3000 van de slider
	 */
	public int getTimeValue() {
		return timeSlider.getValue();
	}
	
	/**
	 * Als het window gesloten wordt moet de game gestopt worden en
	 * de gebruiker de GUI van de lobbyView te zien krijgen daarvoor zorgt de functie
	 * van de viewController. Deze methode wordt automatisch aangeroepen omdat dit object
	 * ook een windowListener is.
	 */
	public void windowClosing(WindowEvent arg0) {
		viewController.stopGame();
	}

	/*
	 * Methode die er in moeten zitten volgens de interface maar niet gebruikt worden.
	 */
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
}