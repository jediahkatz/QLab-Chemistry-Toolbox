package controller.model;

public class Element extends Item {
	private String name;
	private int count;
	
	
	Element(String name, int count) {
		if(count < 1) {
			throw(new IllegalArgumentException("Must be a positive integer"));
		}
		this.name = name;
		this.count = count;
	}
	
	String getName() {
		return name;
	}
	
	int getCount() {
		return count;
	}
	
	@Override
	int countElement(String n) {
		//TODO System.out.println(n + ": " + count);
		return n.equals(name) ? count : 0;
	}
	
	@Override
	String[] getElements() {
		String[] e = new String[1];
		e[0] = this.getName();
		return e;
	}
	
	public String toString() {
		return name + count;
	}
	
	//Override Object.equals so that HashSets don't add duplicate elements
	/*boolean equals(Element e) {
		return this.getName().equals(e.getName());
	}*/
	
}
