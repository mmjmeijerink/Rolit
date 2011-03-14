package rolit.sharedModels;

public class Slot {
	public static final int EMPTY = 0;
	public static final int RED = 1;
	public static final int YELLOW = 2;
	public static final int BLUE = 3;
	public static final int GREEN = 4;
	
	private int value;
	
	public Slot() {
		value = EMPTY;
	}
	
	public void setValue(int aValue) {
		// Nul kan niet ingevoerd worden omdat als een vakje bestaat
		// hij niet meer leeg kan worden nadat hij geset is.
		if(aValue > 0 && aValue < 5) { 
			value = aValue;
		} else {
			System.out.println("Error: could not change slot value because value is invalid.");
		}
	}
	
	public int getValue() {
		return value;
	}
	
}
