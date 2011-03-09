package rolit.server;

public class Gamer {
	public String name;
	public boolean takesPart;
	
	public Gamer() {
		name = "[NOT CONNECTED]"; // Mag want op een andere manier kan name nooit geset worden met een spatie, dus deze is uniek
		takesPart = false;
	}
}
