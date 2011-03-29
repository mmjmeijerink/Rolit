package rolit.sharedModels;

/**
 * Het Gamer model is de representatie van een gamer die gebruikt wordt door Client en Server.
 * Een gamer heeft een naam en een kleur. Daarnaast wordt er ook bijgehouden of de gamer ingame is, en
 * wat de games voorkeur voor game groote is.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */
public class Gamer {
	
	private String name;
	private boolean takesPart;
	private int requestedGameSize;
	private int color; // Volgens de kleuren gedefineerd in Slot
	
	/**
	 * Bij het instancieren van een gamer worden de volgende waarde geset, name, takespart en color.
	 * @ensure this.getName().equals("[NOT CONNECTED]") && this.isTakingPart() == false && this.getColor() == Slot.EMPTY
	 */
	public Gamer() {
		name = "[NOT CONNECTED]"; // Mag want op een andere manier kan name nooit geset worden met een spatie, dus deze is uniek
		takesPart = false;
		color = Slot.EMPTY;
	}
	
	/**
	 * Set de game size voor het join commando. De parameter size moet tussen de 2 en de 4 zitten omdat dit de mogelijke
	 * game sizes zijn. Dit conform met het protocol van INF2.
	 * @ensure getRequestedGameSize() == size && result = true
	 * @require size > 1 && size < 5
	 * @param size de te setten size
	 * @return true als het gelukt is false als het niet gelukt is
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
	 * Geeft de requrestedGameSize terug die eerder geset is bij dit object.
	 * Als hij niet geset is geeft hij null terug.
	 * @return null || int tussen de 2 en 4
	 */
	public int getRequestedGameSize() {
		return requestedGameSize;
	}
	
	/**
	 * Met deze methode kan de kleur van deze gamer geset worden.
	 * Dit dient een int waarde te zijn met de inhoud van Slot.EMPTY, Slot.BLUE, Slot.RED, Slot.YELLO of Slot.GREEN.
	 * 
	 * 
	 * @param aColor de te setten kleur dient een te zijn van Slot.EMPTY, Slot.BLUE, Slot.RED, Slot.YELLO of Slot.GREEN
	 * @require aColor == Slot.EMPTY || Slot.BLUE || Slot.RED || Slot.YELLO || Slot.GREE
	 * @return true als de preconditie opgevolgt wordt, false als er iets fout gaat
	 */
	public boolean setColor(int aColor) {
		boolean result = false;
		
		if(aColor >= Slot.EMPTY && aColor <= Slot.BLUE) { 
			color = aColor;
			result = true;
			if(aColor == Slot.EMPTY) { 
				/*
				 * Als er kleur mee wordt gegeven die een leeg vlakje representeert zorgt dit er voor
				 * dat de gamer wordt gezien als niet meer in game.
				 * Dit wordt voor de zekerheid even gelogt. 
				 */
				takesPart = false;
				System.out.println("Player is not taking part.");
			} else {
				takesPart = true;
			}
		} else {
			/*
			 * Schijnbaar wordt er een foute setColor ingevoert,
			 * dit wordt even gelogt voor debug redenen.
			 * Het hoeft geen ramp te zijn omdat er een boolean terug wordt gegeven.
			 */
			System.out.println("Error: could not change gamer color value because value is invalid.");
		}
		
		return result;
	}
	
	/**
	 * Geeft de kleur van deze gamer terug
	 * @ensure als this.isTakingPart == false dan result == Slot.EMPTY
	 * @return een van de statische kleueren van Slot bijvoorbeeld Slot.EMPTY, Slot.BLUE, Slot.RED, Slot.YELLO of Slot.GREEN
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Geeft de naam van deze gamer terug, deze hoeft niet uniek te zijn.
	 * Dit moet de beheerder van het model zelf checken.
	 * @return de naam van deze gamer
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Hiermee set je de naam van een gamer. Er wordt niet gegarandeerd dat deze name uniek is,
	 * dit zal voor deze methode gecheckt moeten worden.
	 * 
	 * @require aName != null
	 * @ensure this.getName() == aName
	 * @param aName
	 */
	public void setName(String aName) {
		name = aName;
	}
	
	/**
	 * Geeft een boolean terug of de gamer in game zit. Je kan de gamer uit game halen door this.setColor(Slot.EMPTY);
	 * @return true als this.getColor() != Slot.EMPTY || false als this.getColor() == Slot.EMPTY
	 */
	public boolean isTakingPart() {
		return takesPart;
	}
	
	/**
	 * Geeft een nette string beschrijving terug van de gamer inclusief de naam van de gamer en de kleur die hij heeft.
	 * @return bijv: "Sjaak takes part in game with color: Red"
	 */
	public String toString() {
		String result = name;
		if (takesPart) {
			result = result + " takes part in game with color: " + Slot.colorIntToString(color);
		}
		return result;
	}
}