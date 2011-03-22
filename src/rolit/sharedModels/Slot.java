package rolit.sharedModels;

/**
 * 
 * @author Thijs
 *
 */
public class Slot {
	
	public static final int EMPTY = 0;
	public static final int RED = 1;
	public static final int YELLOW = 3;
	public static final int BLUE = 4;
	public static final int GREEN = 2;
	
	private int value;
	
	/**
	 * 
	 */
	public Slot() {
		value = EMPTY;
	}
	
	/**
	 * 
	 * @param aValue
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
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * 
	 * @param color
	 * @return
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