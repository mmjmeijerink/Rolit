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
	
	public void checkMove(int slotNo, int color) {
		
	}
	
	public int checkAbove(int slotNo, int color, int steps) {
		int slotAbove = slotNo - DIMENTION;
		int result;
		if(slotAbove < 0) {
			// Checkt of het volgende slot buiten het bord is.
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
	
	public int checkBelow(int slotNo, int color, int steps) {
		int slotBelow = slotNo + DIMENTION;
		int result;
		if(slotBelow > DIMENTION*DIMENTION) {
			// Checkt of het volgende slot buiten het bord is.
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
	
	public int checkLeft(int slotNo, int color, int steps) {
		int slotLeft = slotNo - 1;
		int result;
		if(slotLeft > 0 && (slotLeft % 8) == 7) {
			// Checkt of het volgende slot buiten het bord is.
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
	
	public int checkRight(int slotNo, int color, int steps) {
		int slotLeft = slotNo + 1;
		int result;
		if(slotLeft != 0 && (slotLeft % 8) == 0) {
			// Checkt of het volgende slot buiten het bord is.
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
}