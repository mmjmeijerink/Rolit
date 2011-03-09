package rolit.server;

public class Gamer {
	public String name;
	public boolean takesPart;
	private int requestedGameSize;
	
	public Gamer() {
		name = "[NOT CONNECTED]"; // Mag want op een andere manier kan name nooit geset worden met een spatie, dus deze is uniek
		takesPart = false;
	}
	
	public boolean setRequestedGameSize(int size) {
		boolean result;
		if(size >1 && size < 5) {
			result = true;
			requestedGameSize = size;
		} else {
			result = false;
		}
		return result;
	}
}
