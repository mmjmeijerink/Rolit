package rolit.sharedModels;

import java.util.*;

/**
 * Het Board model is een representatie van een board.
 * Het checkt of zetten kloppen en houdt een lijst bij met slots op een board.
 * @author  Mart Meijerink en Thijs Scheepers
 * @version 1
 */

public class Board {

	public static final int DIMENSION = 8;
	private ArrayList<Slot>  	slots;

	/**
	 * Maakt een nieuwe board instatie aan en vult this.getSlots() met de juiste waarde,
	 * set de middelste 4 vakjes met de juiste kleur.
	 * @ensure this.getSlots().size() == Board.DIMENSION*Board.DIMENSION
	 */
	public Board() {
		slots = new ArrayList<Slot>();

		for(int i = 0; i < DIMENSION * DIMENSION; i++) {
			Slot aSlot = new Slot();
			slots.add(aSlot);
		}
		
		/*
		 * Hier woden de middelste 4 vakjes
		 * volgens de juiste kleur geset aan het begin
		 * van een spel.
		 */

		slots.get(27).setValue(Slot.RED);
		slots.get(28).setValue(Slot.YELLOW);
		slots.get(35).setValue(Slot.BLUE);
		slots.get(36).setValue(Slot.GREEN);
	}
	
	/**
	 * Hulp methode voor de copy() methode zodat een board gecreert wordt met dezelfde waarde in de slots array als
	 * dit board heeft.
	 * @param aSlots
	 */
	private Board(ArrayList<Slot> aSlots) {
		slots = new ArrayList<Slot>();
		for(Slot aSlot: aSlots) {
			/*
			 * Kopieert de waarde van alle slots van de array in het nieuwe board.
			 */
			Slot newSlot = new Slot();
			newSlot.setValue(aSlot.getValue());
			slots.add(newSlot);
		}
	}
	
	/**
	 * Met deze methode kan het board gekopieerd worden, de kopie is niet hetzelfde maar een los staand object
	 * @return copy van dit board.
	 * @ensure this.equals(this.copy());
	 */
	public Board copy() {
		Board copy = new Board(slots);
		return copy;
	}

	/**
	 * Checkt of het board vol is. 
	 * @ensure er bestaat geen slot in this.getSlots().get(i).equals(Slot.EMPTY)
	 * @return true als vol is en false als hij nog niet vol is.
	 */
	public boolean isFull() {
		boolean result = true;

		for(Slot aSlot: slots) {
			/*
			 * Loop door alle slots heen om ze te checken op empty.
			 */
			if(aSlot.getValue() == Slot.EMPTY) {
				result = false;
			}
		}
		
		return result;
	}

	/**
	 * Telt het aantal vakjes dat gevult is met een bepaalde kleur. Kan ook worden gebruikt om te checken op lege vakjes.
	 * @param color de kleur volgens Slot.BLUE || Slot.RED || Slot.YELLOW || Slot.GREEN
	 * @require color == Slot.EMPTY || Slot.BLUE || Slot.RED || Slot.YELLOW || Slot.GREEN
	 * @ensure result >= 0 && result <= this.DIMENSION*this.DIMENSION
	 * @return aantal vakjes van opgegeven kleur.
	 */
	public int getPointsOfColor(int color) {
		int result = 0;
		
		for(Slot aSlot: slots) {
			/*
			 * Loopt door alle slots heen om de values te tellen.
			 */
			if(aSlot.getValue() == color) {
				result++;
			}
		}
		
		return result;
	}

	/**
	 * 
	 * @param slotNo
	 * @param color
	 */
	public void doMove(int slotNo, int color) {
		if(checkMove(slotNo,color)) {
			slots.get(slotNo).setValue(color);
			if(checkAbove(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkAbove(slotNo,color,0));
			}
			if(checkRightAbove(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkRightAbove(slotNo,color,0));
			}
			if(checkRight(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkRight(slotNo,color,0));
			}
			if(checkRightBelow(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkRightBelow(slotNo,color,0));
			}
			if(checkBelow(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkBelow(slotNo,color,0));
			}
			if(checkLeftBelow(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkLeftBelow(slotNo,color,0));
			}
			if(checkLeft(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkLeft(slotNo,color,0));
			}
			if(checkLeftAbove(slotNo,color,0) > -1) {
				flipBetween(slotNo,checkLeftAbove(slotNo,color,0));
			}
		}
	}

	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @return
	 */
	public boolean checkMove(int slotNo, int color) {
		boolean result = false;

		if(slots.get(slotNo).getValue() == Slot.EMPTY && (
				checkAbove(slotNo,color,0) > -1 ||
				checkRightAbove(slotNo,color,0) > -1 ||
				checkRight(slotNo,color,0) > -1 ||
				checkRightBelow(slotNo,color,0) > -1 ||
				checkBelow(slotNo,color,0) > -1 ||
				checkLeftBelow(slotNo,color,0) > -1 ||
				checkLeft(slotNo,color,0) > -1 ||
				checkLeftAbove(slotNo,color,0) > -1)) {
			result = true;
		} else if(!checkIfConquering(color) && slots.get(slotNo).getValue() == Slot.EMPTY && checkIfBordering(slotNo)) {
			//System.out.println(slotNo + " is not conquering and can be set.");
			result = true;
		}
		
		return result;
	}

	/**
	 * 
	 * @param color
	 * @return
	 */
	private boolean checkIfConquering(int color) {
		boolean result = false;
		
		for(Slot aSlot: slots) {
			int slotNo = slots.indexOf(aSlot);
			//System.out.println(slotNo);
			
			if(aSlot.getValue() == Slot.EMPTY && (
					checkAbove(slotNo,color,0) > -1 ||
					checkRightAbove(slotNo,color,0) > -1 ||
					checkRight(slotNo,color,0) > -1 ||
					checkRightBelow(slotNo,color,0) > -1 ||
					checkBelow(slotNo,color,0) > -1 ||
					checkLeftBelow(slotNo,color,0) > -1 ||
					checkLeft(slotNo,color,0) > -1 ||
					checkLeftAbove(slotNo,color,0) > -1)) {
				result = true;
			}
		}
		
		return result;
	}
	/**
	 * 
	 * @param slotNo
	 * @return
	 */
	private boolean checkIfBordering(int slotNo) {
		boolean result = false;
		
		if(
			checkAbove(slotNo,Slot.RED,1) > -1 ||
			checkRightAbove(slotNo,Slot.RED,1) > -1 ||
			checkRight(slotNo,Slot.RED,1) > -1 ||
			checkRightBelow(slotNo,Slot.RED,1) > -1 ||
			checkBelow(slotNo,Slot.RED,1) > -1 ||
			checkLeftBelow(slotNo,Slot.RED,1) > -1 ||
			checkLeft(slotNo,Slot.RED,1) > -1 ||
			checkLeftAbove(slotNo,Slot.RED,1) > -1 ||

			checkAbove(slotNo,Slot.GREEN,1) > -1 ||
			checkRightAbove(slotNo,Slot.GREEN,1) > -1 ||
			checkRight(slotNo,Slot.GREEN,1) > -1 ||
			checkRightBelow(slotNo,Slot.GREEN,1) > -1 ||
			checkBelow(slotNo,Slot.GREEN,1) > -1 ||
			checkLeftBelow(slotNo,Slot.GREEN,1) > -1 ||
			checkLeft(slotNo,Slot.GREEN,1) > -1 ||
			checkLeftAbove(slotNo,Slot.GREEN,1) > -1 ||

			checkAbove(slotNo,Slot.BLUE,1) > -1 ||
			checkRightAbove(slotNo,Slot.BLUE,1) > -1 ||
			checkRight(slotNo,Slot.BLUE,1) > -1 ||
			checkRightBelow(slotNo,Slot.BLUE,1) > -1 ||
			checkBelow(slotNo,Slot.BLUE,1) > -1 ||
			checkLeftBelow(slotNo,Slot.BLUE,1) > -1 ||
			checkLeft(slotNo,Slot.BLUE,1) > -1 ||
			checkLeftAbove(slotNo,Slot.BLUE,1) > -1 ||

			checkAbove(slotNo,Slot.YELLOW,1) > -1 ||
			checkRightAbove(slotNo,Slot.YELLOW,1) > -1 ||
			checkRight(slotNo,Slot.YELLOW,1) > -1 ||
			checkRightBelow(slotNo,Slot.YELLOW,1) > -1 ||
			checkBelow(slotNo,Slot.YELLOW,1) > -1 ||
			checkLeftBelow(slotNo,Slot.YELLOW,1) > -1 ||
			checkLeft(slotNo,Slot.YELLOW,1) > -1 ||
			checkLeftAbove(slotNo,Slot.YELLOW,1) > -1
		) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkAbove(int slotNo, int color, int steps) {
		int slotAbove = slotNo - DIMENSION;
		int result;
		steps++;
		
		if(slotAbove < 0) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotAbove).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotAbove;
		} else {
			result = checkAbove(slotAbove, color, steps);
			//System.out.println("Above: "+slotNo+" "+result+" step "+steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkRightAbove(int slotNo, int color, int steps) {
		int slotRightAbove = slotNo - DIMENSION + 1;
		int result;
		steps++;
		
		if(slotRightAbove < 0 || (slotRightAbove != 0 && (slotRightAbove % DIMENSION) == 0)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotRightAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotRightAbove).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotRightAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRightAbove;
		} else {
			result = checkRightAbove(slotRightAbove, color, steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkRight(int slotNo, int color, int steps) {
		int slotRight = slotNo + 1;
		int result;
		steps++;
		
		if(slotRight != 0 && (slotRight % DIMENSION) == 0) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotRight).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotRight).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotRight).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRight;
		} else {
			result = checkRight(slotRight, color, steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkRightBelow(int slotNo, int color, int steps) {
		int slotRightBelow = slotNo + DIMENSION + 1;
		int result;
		steps++;
		
		if(slotRightBelow > DIMENSION*DIMENSION-1 || (slotRightBelow != 0 && (slotRightBelow % DIMENSION) == 0)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotRightBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotRightBelow).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotRightBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRightBelow;
		} else {
			result = checkRightBelow(slotRightBelow, color, steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkBelow(int slotNo, int color, int steps) {
		int slotBelow = slotNo + DIMENSION;
		int result;
		steps++;
		
		if(slotBelow > DIMENSION*DIMENSION-1) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotBelow).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotBelow;
		} else {
			result = checkBelow(slotBelow, color, steps);
		}
		//System.out.println("Below: "+slotNo+" "+result+" step "+steps);
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkLeftBelow(int slotNo, int color, int steps) {
		int slotLeftBelow = slotNo + DIMENSION - 1;
		int result;
		steps++;
		
		if(slotLeftBelow > DIMENSION*DIMENSION-1 || (slotLeftBelow > 0 && (slotLeftBelow % DIMENSION) == DIMENSION - 1)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotLeftBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotLeftBelow).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotLeftBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeftBelow;
		} else {
			result = checkLeftBelow(slotLeftBelow, color, steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkLeft(int slotNo, int color, int steps) {
		int slotLeft = slotNo - 1;
		int result;
		steps++;
		
		if(slotLeft < 0 || (slotLeft > 0 && (slotLeft % DIMENSION) == DIMENSION - 1)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotLeft).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotLeft).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotLeft).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeft;
		} else {
			result = checkLeft(slotLeft, color, steps);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private int checkLeftAbove(int slotNo, int color, int steps) {
		int slotLeftAbove = slotNo - DIMENSION - 1;
		int result;
		steps++;
		
		if(slotLeftAbove < 0 || ((slotLeftAbove > 0 && (slotLeftAbove % DIMENSION) == DIMENSION - 1))) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		} else if (slots.get(slotLeftAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -2;
		} else if(steps == 1 && slots.get(slotLeftAbove).getValue() == color) {
			// Checkt of eigenkleur is directe buur
			result = -3;
		} else if(steps > 1 && slots.get(slotLeftAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeftAbove;
		} else {
			result = checkLeftAbove(slotLeftAbove, color, steps);
		}
		//System.out.println("LeftAbove: "+result+" step "+steps);
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	public String toString() {
		String result = "\n";
		
		for(Slot aSlot: slots){
			result = result + " | " + aSlot.getValue();
			if(slots.indexOf(aSlot) != 0 && (slots.indexOf(aSlot) % DIMENSION) == DIMENSION - 1) {
				result = result + " |\n ";
				for(int i = 0; i < DIMENSION ; i++) {
					result = result + "----";
				}
				result = result + "-\n";
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	public String layoutToString() {
		String result = "\n";
		
		for(Slot aSlot: slots){
			result = result + " | ";
			if(slots.indexOf(aSlot) < 10) {
				result = result + "0";
			}
			result = result + slots.indexOf(aSlot);
			
			if(slots.indexOf(aSlot) != 0 && (slots.indexOf(aSlot) % DIMENSION) == DIMENSION - 1) {
				result = result + " |\n ";
				for(int i = 0; i < DIMENSION ; i++) {
					result = result + "-----";
				}
				result = result + "-\n";
			}
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */
	private void flipBetween(int a, int b) {
		int color = slots.get(a).getValue();
		if(a < b) {
			int aTmp = a;
			a = b;
			b = aTmp;
		}
		int aModulo = a % DIMENSION;
		int bModulo = b % DIMENSION;
		int difference = a - b;

		if(difference <= aModulo) { // Staan op de zelfde rij horizontaal
			for(int i = 1; i < difference; i++) {
				slots.get(a-i).setValue(color);
			}
		} else if(aModulo == bModulo) { // Staan op de zelfde rij verticaal
			int aTmp = a;
			while(aTmp > b) {
				if(aTmp != a) {
					slots.get(aTmp).setValue(color);
				}
				aTmp = aTmp - DIMENSION;
			}	
		} else if(bModulo < aModulo) { // Staan op de dezelfde diagonaal, a rechts van b
			int aTmp = a;
			while(aTmp > b) {
				if(aTmp != a) {
					slots.get(aTmp).setValue(color);
				}
				aTmp = aTmp - DIMENSION - 1;
			}
		} else if(bModulo > aModulo) { // Staan op de dezelfde diagonaal, a links van b
			int aTmp = a;
			while(aTmp > b) {
				if(aTmp != a) {
					slots.get(aTmp).setValue(color);
				}
				aTmp = aTmp - DIMENSION + 1;
			}
		}
	}
	
	/**
	 * 
	 * @param slotNo
	 * @param color
	 * @param steps
	 * @return
	 */	
	public ArrayList<Slot> getSlots() {
		return slots;
	}
}