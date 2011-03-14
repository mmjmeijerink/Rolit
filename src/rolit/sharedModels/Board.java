package rolit.sharedModels;

import java.util.ArrayList;

public class Board {

	public static final int DIMENSION = 8;
	private ArrayList<Slot>  	slots;
	
	public Board() {
		slots = new ArrayList<Slot>();
		
		for(int i = 0; i < DIMENSION * DIMENSION; i++) {
			Slot aSlot = new Slot();
			slots.add(aSlot);
		}
		
		slots.get(27).setValue(Slot.RED);
		slots.get(28).setValue(Slot.YELLOW);
		slots.get(35).setValue(Slot.BLUE);
		slots.get(36).setValue(Slot.GREEN);
	}
	
	public void doMove(int slotNo, int color) {
			if(checkMove(slotNo,color)) {
				slots.get(slotNo).setValue(color);
				if(checkAbove(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkAbove(slotNo,color,0));
				}
				if(checkRightAbove(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkRightAbove(slotNo,color,0));
				}
				if(checkRight(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkRight(slotNo,color,0));
				}
				if(checkRightBelow(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkRightBelow(slotNo,color,0));
				}
				if(checkBelow(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkBelow(slotNo,color,0));
				}
				if(checkLeftBelow(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkLeftBelow(slotNo,color,0));
				}
				if(checkLeft(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkLeft(slotNo,color,0));
				}
				if(checkLeftAbove(slotNo,color,0) != -1) {
					flipBetween(slotNo,checkLeftAbove(slotNo,color,0));
				}
			}
	}
	
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
		}
		return result;
	}
	
	public int checkAbove(int slotNo, int color, int steps) {
		int slotAbove = slotNo - DIMENSION;
		int result;
		if(slotAbove < 0) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotAbove;
		} else {
			result = checkAbove(slotAbove, color, ++steps);
		}
		return result;
	}
	
	public int checkRightAbove(int slotNo, int color, int steps) {
		int slotRightAbove = slotNo - DIMENSION + 1;
		int result;
		if(slotRightAbove < 0 || (slotRightAbove != 0 && (slotRightAbove % DIMENSION) == 0)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotRightAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotRightAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRightAbove;
		} else {
			result = checkRightAbove(slotRightAbove, color, ++steps);
		}
		return result;
	}
	
	public int checkRight(int slotNo, int color, int steps) {
		int slotRight = slotNo + 1;
		int result;
		if(slotRight != 0 && (slotRight % DIMENSION) == 0) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotRight).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotRight).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRight;
		} else {
			result = checkRight(slotRight, color, ++steps);
		}
		return result;
	}
	
	public int checkRightBelow(int slotNo, int color, int steps) {
		int slotRightBelow = slotNo + DIMENSION + 1;
		int result;
		if(slotRightBelow > DIMENSION*DIMENSION || (slotRightBelow != 0 && (slotRightBelow % DIMENSION) == 0)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotRightBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotRightBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotRightBelow;
		} else {
			result = checkRightBelow(slotRightBelow, color, ++steps);
		}
		return result;
	}
	
	public int checkBelow(int slotNo, int color, int steps) {
		int slotBelow = slotNo + DIMENSION;
		int result;
		if(slotBelow > DIMENSION*DIMENSION) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotBelow;
		} else {
			result = checkBelow(slotBelow, color, ++steps);
		}
		return result;
	}
	
	public int checkLeftBelow(int slotNo, int color, int steps) {
		int slotLeftBelow = slotNo + DIMENSION - 1;
		int result;
		if(slotLeftBelow > DIMENSION*DIMENSION || (slotLeftBelow > 0 && (slotLeftBelow % DIMENSION) == DIMENSION - 1)) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotLeftBelow).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotLeftBelow).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeftBelow;
		} else {
			result = checkLeftBelow(slotLeftBelow, color, ++steps);
		}
		return result;
	}
	
	public int checkLeft(int slotNo, int color, int steps) {
		int slotLeft = slotNo - 1;
		int result;
		if(slotLeft > 0 && (slotLeft % DIMENSION) == DIMENSION - 1) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotLeft).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotLeft).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeft;
		} else {
			result = checkLeft(slotLeft, color, ++steps);
		}
		return result;
	}
	
	public int checkLeftAbove(int slotNo, int color, int steps) {
		int slotLeftAbove = slotNo - DIMENSION + 1;
		int result;
		if(slotLeftAbove < 0 || ((slotLeftAbove > 0 && (slotLeftAbove % DIMENSION) == DIMENSION - 1))) {
			// Checkt of het volgende slot buiten het bord is.
			result = -1;
		}
		else if (slots.get(slotLeftAbove).getValue() == Slot.EMPTY) {
			// Checkt of het volgende vakje leeg is.
			result = -1;
		}
		else if(steps > 0 && slots.get(slotLeftAbove).getValue() == color) {
			// Checkt of het volgende slot de juiste kleur heeft.
			result = slotLeftAbove;
		} else {
			result = checkLeftAbove(slotLeftAbove, color, ++steps);
		}
		return result;
	}
	
	public String toString() {
		String result = "";
		for(Slot aSlot: slots){
			result = result + "| " + aSlot.getValue();
			if(slots.indexOf(aSlot) != 0 && (slots.indexOf(aSlot) % DIMENSION) == DIMENSION - 1) {
				result = result + " |\n";
				for(int i = 0; i < DIMENSION ; i++) {
					result = result + "---";
				}
				result = result + "\n";
			}
		}
		return result;
	}
	
	public void flipBetween(int a, int b) {
		int color = slots.get(a).getValue();
		if(a < b) {
			int aTmp = a;
			a = b;
			b = aTmp;
		}
		int aModulo = a % DIMENSION;
		int bModulo = b % DIMENSION;
		int difference = a - b;
		
		if(difference < aModulo) { // Staan op de zelfde rij horizontaal
			for(int i = 1; i < difference; i++) {
				slots.get(a-i).setValue(color);
			}
		} else if(aModulo == bModulo) { // Staan op de zelfde rij verticaal
			int aTmp = a;
			while(aTmp != b && aTmp > 0) { // aTmp > 0 voor de zekerheid om infinite loop te voorkomen
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
}