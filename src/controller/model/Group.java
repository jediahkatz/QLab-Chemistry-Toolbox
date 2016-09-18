package controller.model;

import java.util.HashSet;

//A group in a term. Has a list of groups or elements
//Ex: (OH)3 or (Na(OH)3)2
public class Group extends Item {
	private Item[] items;
	int count;
	
	Group(Item[] items, int count) {
		if(count < 1) {
			throw(new IllegalArgumentException("Must be a positive integer"));
		}
		this.items = items;
		this.count = count;
	}
	
	@Override
	Item[] getItems(){
		return items;
	}
	
	int getCount() {
		return count;
	}
	
	@Override
	String[] getElements() {
		HashSet<String> elementsSet = new HashSet<String>();
		for(Item i : items) {
			for(String e : i.getElements()) {
				elementsSet.add(e);
			}
		}
		return elementsSet.toArray(new String[0]);
	}
	
	@Override
	int countElement(String name) {
		int sum = 0;
		for(Item i : items) {
			sum += (i.countElement(name) * count);
		}
		return sum;
	}
	
	public String toString() {
		String toString = "";
		for(Item i : items) {
			toString += i.toString();
		}
		return "(" + toString + ")" + count;
	}
}