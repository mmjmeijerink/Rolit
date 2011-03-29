package rolit.sharedModels;

/**
 * Dit is een van de DIM*DIM slots op een bord. Een bord van 8*8 heeft dus 64 slot instanties nodig.
 * Een slot houdt bij welke kleur hij heeft. En je kan de kleur van een Slot veranderen. Tevens heeft
 * de Slot klasse een aantal statische methoden en waarde voor kleur representatie in int.
 * 
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class Slot {
	
	public static final int EMPTY = 0;
	public static final int RED = 1;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	public static final int GREEN = 2;
	
	private int value;
	
	/**
	 * Maakt een een Slot object aan en vult hem met een leeg vakje.
	 * @ensure this.getValue() == Slot.EMPTY
	 */
	public Slot() {
		value = EMPTY;
	}
	
	/**
	 * Zet de kleur van het vakje.
	 * @require aValue == Slot.EMPTY || Slot.BLUE || Slot.RED || Slot.YELLO || Slot.GREEN
	 * @param aValue de int value van de kleur die geset moet worden
	 */
	public void setValue(int aValue) {
		/*
		 * Nul kan niet ingevoerd worden omdat als een vakje bestaat
		 * hij niet meer leeg kan worden nadat hij geset is.
		 */
		if(aValue >= 0 && aValue <= 4) { 
			value = aValue;
		} else {
			/*
			 * Staat hier voor debugging zodat de programmeur het weet als er een foute slot waarde wordt toegewezen.
			 */
			System.out.println("Error: could not change slot value because value is invalid.");
		}
	}
	
	/**
	 * Geeft de kleur terug van het huidige vakje.
	 * @return Slot.EMPTY || Slot.BLUE || Slot.RED || Slot.YELLO || Slot.GREEN
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Deze methode geeft een string terug die overeenkomt met de ingegeven kleur representatie.
	 * Bijvoorbeeld: Slot.colorIntToString(Slot.Blue).equals("Blue")
	 * @require color == Slot.BLUE || Slot.RED || Slot.YELLOW || Slot.GREEN
	 * @param color de om te zetten kleur representatie.
	 * @return Geeft een string terug die overeenkomt met de ingegeven kleur representatie
	 */
	public static String colorIntToString(int color) {
		String result = "Not valid";
		if(color == RED) {
			result = "Red";
		} else if (color == GREEN) {
			result = "Green";
		} else if (color == BLUE) {
			result = "Blue";
		} else if (color == YELLOW) {
			result = "Yellow";
		}
		return result;
	}
}