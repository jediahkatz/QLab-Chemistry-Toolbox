package controller.model;

//A superclass for groups and elements
//Probably not supposed to do that but fuck you!
public abstract class Item {
	String[] elements;
	Item[] items;
	
	//these functions will all be overridden
	String[] getElements() {
		return elements;
	}
	
	int countElement(String name) {
		return 69;
	}
	
	Item[] getItems() {
		return items;
	}

	public String toString() {
		return "Twenty-eight young men bathe by the shore,"
				+ "Twenty-eight young men and all so friendly;"
				+ "Twenty-eight years of womanly life and all so lonesome."
				+ "She owns the fine house by the rise of the bank,"
				+ "She hides handsome and richly drest aft the blinds of the window."
				+ "Which of the young men does she like the best?"
				+ "Ah the homeliest of them is beautiful to her."
				+ "Where are you off to, lady? for I see you,"
				+ "You splash in the water there, yet stay stock still in your room."
				+ "Dancing and laughing along the beach came the twenty-ninth bather,"
				+ "The rest did not see her, but she saw them and loved them."
				+ "The beards of the young men glisten�d with wet, it ran from their long hair,"
				+ "Little streams pass�d all over their bodies."
				+ "An unseen hand also pass�d over their bodies,"
				+ "It descended tremblingly from their temples and ribs."
				+ "The young men float on their backs, their white bellies bulge to the sun,"
				+ "they do not ask who seizes fast to them,"
				+ "They do not know who puffs and declines with pendant and bending arch,"
				+ "They do not think whom they souse with spray.";
	}
}