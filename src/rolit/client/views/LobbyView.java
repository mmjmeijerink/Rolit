package rolit.client.views;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import rolit.client.controllers.*;

/**
 * De LobbyView zorgt voor het weergeven van een Lobby GUI aan de gebruiker en dat de gebruiker op de juiste knoppen kan drukken voor
 * het mogelijk starten van een game en het chatten met anderen mensen in de lobby.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
@SuppressWarnings("serial")
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
	private JRadioButton	humanChoice;
	private JRadioButton	computerChoice;
	private JRadioButton	smartComputerChoice;

	/**
	 * Maakt een instantie van LobbyView aan en zorgt ervoor dat de applicatie een GUI aan de gebruiker presenteert.
	 * Doormiddel van interne methoden wordt er een GUI gebouwd. Zodra deze gebouwd is kunnen er strings van textFields etc. opgevraagt worden van dit object.
	 * 
	 * @require aController != null
	 * @param aController
	 */
	public LobbyView(ApplicationController aController) {
		/*
		 * Zorgt er voor dat er een JFrame geopent wordt met de juiste naam in de titel balk
		 */
		super("Welcome to the Lobby");
		
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
        JRadioButton jRadioButton3 = new javax.swing.JRadioButton();
        
        jRadioButton1.isSelected();
        
        joinSpinner = jSpinner1;
        joinButton = jButton1;
        humanChoice = jRadioButton1;
        computerChoice = jRadioButton2;
        smartComputerChoice = jRadioButton3;
        joinLoader = jProgressBar1;
        challengeButton = jButton2;
        chatArea = jTextArea1;
        chatMessage = jTextField1;
        chatButton = jButton3;
        challengeList = jList1;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Minimal players:");

        jButton2.setText("Challenge");
        jButton2.addActionListener(viewController);

        /*
         * Voor de lijst moet eerst een standaard model ingevoerd worden.
         */
        jList1.setModel(new javax.swing.AbstractListModel() {
			private static final long serialVersionUID = 2912992567185913876L;
			String[] strings = { "No lobby command recieved" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(jList1);

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(2, 2, 4, 1));

        jLabel2.setText("In the lobby:");

        jButton1.setText("Join");
        jButton1.addActionListener(viewController);
        
        joinLoader.setVisible(false);

        /*
         * Standaard moet een menselijke speler ingesteld worden
         * omdat wordt verwacht dat dit het vaakst gaat gebeuren,
         * en zo wordt ook voorkomen dat er niets is ingesteld.
         */
        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Human");
        jRadioButton1.addActionListener(viewController);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Dull AI");
        jRadioButton2.addActionListener(viewController);

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Smart AI");
        jRadioButton3.addActionListener(viewController);
        
        jButton3.setText("Chat");
        jButton3.addActionListener(viewController);
        
        /*
         * Het textfield moet luisteren naar de enter voor het versturen
         * van chat berichten.
         */
        jTextField1.addKeyListener(viewController);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setEditable(false);
        jScrollPane2.setViewportView(jTextArea1);
        
        /*
         * NetBeans generator code vanaf hier.
         * Voor deze code is dan ook de extra library swing-layout-1.0.4.jar nodig
         */

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
		    .add(jRadioButton3, 0, 0, Short.MAX_VALUE)
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
			.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jProgressBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton1))
                .add(17, 17, 17))
        );

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
		JOptionPane.showMessageDialog(this, message, "Lobby Alert", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Met deze methode kan een informatie bericht aan de gebruiker getoont worden.
	 * @require message != null
	 * @param message Het te tonen bericht
	 */
	public void message(String message) {
		JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Als er een challenge ontvangen wordt moet de gebruiker hier op antwoorden.
	 * Dit gebeurt met hulp van deze methode die er voor zorgt dat er een dialoog getoondt met daarin een YES en NO optie,
	 * dit voor het accepten van de challenge
	 * @param challenger De naam van de challenger
	 * @return 1 als er op ja gedrukt wordt en 2 als dat niet zo is.
	 */
	public int challengeReceived(String challenger) {
		String message = "You received a challenge from " + challenger;
		return JOptionPane.showConfirmDialog(this, message, "Challenge received", JOptionPane.YES_NO_OPTION);
	}
	
	/**
	 * Geeft de waarde van de minimaal aantal speler join terug.
	 * @ensure 1 < result < 5
	 * @return waarde tussen de 2 en 4
	 */
	public int getSpinnerValue() {
		javax.swing.SpinnerNumberModel myModel = (javax.swing.SpinnerNumberModel)(joinSpinner.getModel());
		return myModel.getNumber().intValue();
	}
	
	/**
	 * Geeft de joinButton terug zodat deze vergeleken kan worden in de actionListener.
	 * @return De joinButton
	 */
	public JButton getJoinButton() {
		return joinButton;
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
	 * Geeft de challengeList terug zodat de geselecteerde rij opgevraagt kan worden
	 * @return De challengeList
	 */
	public JList getChallengeList() {
		return challengeList;
	}
	
	/**
	 * Geeft de challengeButton terug zodat deze vergeleken kan worden in de actionListener.
	 * @return De challengeButton
	 */
	public JButton getChallengeButton() {
		return challengeButton;
	}

	/**
	 * Geeft true terug als de in de groep met radio buttons "Smart AI" geselecteerd is
	 * @return true bij "Smart AI" anders false
	 */
	public boolean smartComputerIsSet() {
		return smartComputerChoice.isSelected();
	}

	/**
	 * Geeft true terug als de in de groep met radio buttons "Dull AI" geselecteerd is
	 * @return true bij "Dull AI" anders false
	 */
	public boolean computerIsSet() {
		return computerChoice.isSelected() || smartComputerChoice.isSelected();
	}
	
	/**
	 * Geeft true terug als de in de groep met radio buttons "Human" geselecteerd is
	 * @return true bij "Human" anders false
	 */
	public boolean humanIsSet() {
		return humanChoice.isSelected();
	}
	
	/**
	 * Met deze methode kan de lijst in de interface gevult worden met een array van strings.
	 * Het is de bedoeling dat hier de namen van de gamers doorgegeven worden zodra er een lobby commando
	 * verwerkt wordt door de Application Controller
	 * @param list de in te voeren lijst met strings
	 */
	public void setChallengeList(ArrayList<String> list) {
		DefaultListModel listModel = new DefaultListModel();
		for(String aString: list) {
			listModel.addElement(aString);
		}
		challengeList.setModel(listModel);
	}
	
	/**
	 * Start de loader en geeft deze weer
	 */
	public void startLoading() {
		joinLoader.setVisible(true);
		joinLoader.setIndeterminate(true);
	}
	
	/**
	 * Verbergt de loader
	 */
	public void stopLoading() {
		joinLoader.setVisible(false);
	}	
}