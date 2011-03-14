package rolit.sharedModels;

import java.awt.Color;
import java.util.ArrayList;

public class Board {

	public static final int DIMENTION = 8;
	private ArrayList<Slot>  	slots;
	
	public Board() {
		slots = new ArrayList<Slot>();
		
		for(int i = 0; i < DIMENTION * DIMENTION; i++) {
			Slot aSlot = new Slot();
			slots.add(aSlot);
		}
		
		slots.get(27).setValue(Slot.RED);
		slots.get(28).setValue(Slot.YELLOW);
		slots.get(35).setValue(Slot.BLUE);
		slots.get(36).setValue(Slot.GREEN);
	}
	
	public boolean checkMove(int slotNo, int color) {
		boolean result = false;
		if(checkAbove(slotNo,color,0) > -1 &&
		   checkRightAbove(slotNo,color,0) > -1 &&
		   checkRight(slotNo,color,0) > -1 &&
		   checkRightBelow(slotNo,color,0) > -1 &&
		   checkBelow(slotNo,color,0) > -1 &&
		   checkLeftBelow(slotNo,color,0) > -1 &&
		   checkLeft(slotNo,color,0) > -1 &&
		   checkLeftAbove(slotNo,color,0) > -1) {
			result = true;
		}
		return result;
	}
	
	public int checkAbove(int slotNo, int color, int steps) {
		int slotAbove = slotNo - DIMENTION;
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
		int slotRightAbove = slotNo - DIMENTION + 1;
		int result;
		if(slotRightAbove < 0 || (slotRightAbove != 0 && (slotRightAbove % 8) == 0)) {
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
		if(slotRight != 0 && (slotRight % 8) == 0) {
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
		int slotRightBelow = slotNo + DIMENTION + 1;
		int result;
		if(slotRightBelow > DIMENTION*DIMENTION || (slotRightBelow != 0 && (slotRightBelow % 8) == 0)) {
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
		int slotBelow = slotNo + DIMENTION;
		int result;
		if(slotBelow > DIMENTION*DIMENTION) {
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
		int slotLeftBelow = slotNo + DIMENTION - 1;
		int result;
		if(slotLeftBelow > DIMENTION*DIMENTION || (slotLeftBelow > 0 && (slotLeftBelow % 8) == 7)) {
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
		if(slotLeft > 0 && (slotLeft % 8) == 7) {
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
		int slotLeftAbove = slotNo - DIMENTION + 1;
		int result;
		if(slotLeftAbove < 0 || ((slotLeftAbove > 0 && (slotLeftAbove % 8) == 7))) {
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
	
	
}