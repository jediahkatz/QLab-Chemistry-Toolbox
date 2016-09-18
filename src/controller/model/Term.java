package controller.model;

import java.util.HashSet;

//A term with a list of groups or elements and a charge
public class Term {
	private int charge;
	private Item[] items;
	
	Term(Item[] items, int charge) {
		if(items.length == 0 && charge != -1) {
			throw(new IllegalArgumentException("Invalid term"));
		}
		this.charge = charge;
		this.items = items;
	}
	
	String[] getElements() {
		HashSet<String> elementsSet = new HashSet<String>();
		elementsSet.add("e");
		for(Item i : items) {
			for(String e : i.getElements()) {
				elementsSet.add(e);
			}
		}
		return elementsSet.toArray(new String[0]);
	}
	
	int countElement(String e) {
		if(e.equals("e")) {
			return -charge;
		}
		int sum = 0;
		for(Item i : items) {
			sum+=i.countElement(e);
		}
		return sum;
	}
	
	int getCharge(){ 
		return charge;
	}
	
	Item[] getItems() {
		return items;
	}
	
}