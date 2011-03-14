package rolit.sharedModels;

public class Gamer {
	private String name;
	private boolean takesPart;
	private int requestedGameSize;
	private int color; // Volgens de kleuren gedefineerd in Slot
	
	public Gamer() {
		name = "[NOT CONNECTED]"; // Mag want op een andere manier kan name nooit geset worden met een spatie, dus deze is uniek
		takesPart = false;
		color = Slot.EMPTY;
	}
	
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
	
	public int getRequestedGameSize() {
		return requestedGameSize;
	}
	
	public boolean setColor(int aColor) {
		boolean result = false;
		if(aColor > -1 && aColor < 5) { 
			color = aColor;
			result = true;
			if(aColor != 0) { 
				takesPart = true;
			} else {
				takesPart = false;
			}
		} else {
			System.out.println("Error: could not change gamer color value because value is invalid.");
		}
		return result;
	}
	
	public int getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String aName) {
		name = aName;
	}
	
	public boolean isTakingPart() {
		return takesPart;
	}
}
