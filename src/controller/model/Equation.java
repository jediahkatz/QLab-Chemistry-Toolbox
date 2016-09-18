package controller.model;

import java.util.HashSet;

public class Equation {
	private Term[] reactants;
	private Term[] products;
	
	Equation(Term[] reactants, Term[] products) {
		this.reactants = reactants;
		this.products = products;
	}
	
	Term[] getReactants(){
		return reactants;
	}
	
	Term[] getProducts(){
		return products;
	}
	
	String[] getElements() {
		HashSet<String> elementsSet = new HashSet<String>();
		for(Term t : reactants) {
			for(String e : t.getElements()) {
				elementsSet.add(e);
			}
		}
		for(Term t : products) {
			for(String e : t.getElements()) {
				elementsSet.add(e);
			}
		}

		return elementsSet.toArray(new String[0]);
	}
}