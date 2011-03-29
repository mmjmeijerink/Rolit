package rolit.sharedModels;

/**
 * 
 * @author Thijs
 *
 */
public class Gamer {
	
	private String name;
	private boolean takesPart;
	private int requestedGameSize;
	private int color; // Volgens de kleuren gedefineerd in Slot
	
	/**
	 * 
	 */
	public Gamer() {
		name = "[NOT CONNECTED]"; // Mag want op een andere manier kan name nooit geset worden met een spatie, dus deze is uniek
		takesPart = false;
		color = Slot.EMPTY;
	}
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public boolean setRequestedGameSize(int size) {
		boolean result;
		
		if(size > 1 && size < 5) {
			result = true;
			requestedGameSize = size;
		} else {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRequestedGameSize() {
		return requestedGameSize;
	}
	
	/**
	 * 
	 * @param aColor
	 * @return
	 */
	public boolean setColor(int aColor) {
		boolean result = false;
		
		if(aColor >= Slot.EMPTY && aColor <= Slot.BLUE) { 
			color = aColor;
			result = true;
			if(aColor == Slot.EMPTY) { 
				takesPart = false;
				System.out.println("Player is not taking part.");
			} else {
				takesPart = true;
			}
		} else {
			System.out.println("Error: could not change gamer color value because value is invalid.");
		}
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param aName
	 */
	public void setName(String aName) {
		name = aName;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isTakingPart() {
		return takesPart;
	}
	
	/**
	 * Geeft een nette string beschrijving terug van de gamer inclusief de naam van de gamer en de kleur die hij heeft.
	 * @return
	 */
	public String toString() {
		String result = name;
		if (takesPart) {
			result = result + " takes part in game with color: " + Slot.colorIntToString(color);
		}
		return result;
	}
}