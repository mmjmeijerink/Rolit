package rolit.game;

import java.util.*;


/**
 * P2 week5.
 * Spel. Klasse voor het onderhouden van het BoterKaasEieren-spel.
 * Practicumopdracht Programmeren 1.
 * @author  Theo Ruys en Arend Rensink
 * @version 2002.01.22
 */
public class Game extends Observable {

    // -- Instance variables -----------------------------------------

    /**
     * Het speelbord.
     * @invariant bord != null
     */
    private Bord bord;

    /**
     * De huidige Mark.
     * @invariant huidig == Mark.XX || huidig == Mark.OO
     */
    private Mark huidig;

    // -- Constructors -----------------------------------------------

    /**
     * Construeert een nieuw Spel object.
     */
    public Game() {
        bord   = new Bord();
        huidig = Mark.XX;
    }

    // -- Queries ----------------------------------------------------

    /**
     * Levert het bord op.
     */
    public Bord getBord() {
        return bord;
    }

    /**
     * Levert de Mark op die aan de beurt is.
     */
    public Mark getHuidig() {
        return huidig;
    }

    // -- Commands ---------------------------------------------------

    /**
     * Reset dit spel. <br>
     * Het speelbord wordt weer leeggemaakt en Mark.XX wordt de 
     * huidige Mark.
     */
    public void reset() {
        huidig = Mark.XX;
        
        bord.reset();
        setChanged();
        notifyObservers();
    }

    /**
     * Zet de huidige mark in het vakje i. 
     * Geef de beurt aan de andere Mark.
     * @require  0<=i && i <Bord.DIM*Bord.DIM && this.getBord().isLeegVakje(i)
     * @param    i de index waar de mark zal worden gezet.
     */
    public void doeZet(int i) {
        bord.setVakje(i, huidig);
        setChanged();
        notifyObservers();
        huidig = huidig.other();
    }
} 


