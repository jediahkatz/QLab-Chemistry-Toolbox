package controller.model;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeriodicTableData {
	
	public static final int NUMBER_OF_ELEMENTS = 118;
	//Each property will be a static variable for ease of use
	public static final int ATOMIC_NUMBER = 0;
	public static final int SYMBOL = 1;
	public static final int NAME = 2;
	public static final int ATOMIC_MASS = 3; //grams per mole
	public static final int CPK_COLOR = 4; //hex
	public static final int ELECTRON_CONFIGURATION = 5;
	public static final int ELECTRONEGATIVITY = 6; //Pauling
	public static final int ATOMIC_RADIUS = 7; //pm
	public static final int ION_RADIUS = 8; //pm
	public static final int VAN_DER_WAALS_RADIUS = 9; //pm
	public static final int FIRST_IONIZATION_ENERGY = 10; //kJ per mole
	public static final int ACTIVATION_ENERGY = 11; //kJ per mole
	public static final int STANDARD_STATE = 12;
	public static final int BONDING_TYPE = 13;
	public static final int MELTING_POINT = 14; //Kelvin
	public static final int BOILING_POINT = 15; //Kelvin
	public static final int DENSITY = 16; //grams per mL
	public static final int TYPE_OF_ELEMENT = 17; //eg. metal, nonmetal, halogen
	public static final int YEAR_DISCOVERED = 18;
	
	//Hash table containing the names of many compounds mapped to their formula
	private Hashtable<String,String> compoundNames = new Hashtable<String,String>();
	
	//each string in the array will be one line in the data (containing data for one element)
	private String[] mainDataUnparsed = new String[NUMBER_OF_ELEMENTS];
	private String[] oxidationDataUnparsed = new String[NUMBER_OF_ELEMENTS];
	private String[][] oxidationDataParsed = new String[NUMBER_OF_ELEMENTS][];
	//allDataParsed[element (atomic number)][type of data]
	private String[][] mainDataParsed = new String[NUMBER_OF_ELEMENTS][18];
	/* order of data: (oops, add +1 to each)
	 * -1. atomic number
	 * 0. symbol
	 * 1. name
	 * 2. atomic mass
	 * 3. cpk color
	 * 4. electron configuration
	 * 5. electronegativity
	 * 6. atomic radius
	 * 7. ion radius
	 * 8. van der waals radius
	 * 9. ionization energy
	 * 10. activation energy
	 * 11. standard state
	 * 12. bonding type
	 * 13. melting point
	 * 14. boiling point
	 * 15. density
	 * 16. type of metal
	 * 17. year discovered
	 */
	
	public PeriodicTableData() {
		loadData();
		parseData();
		createCompoundNamesHashtable();
	}
	
	private void createCompoundNamesHashtable() {
		//read the compound names
		Scanner compoundNamesScanner = new Scanner(getClass().getResourceAsStream("formulaNames.csv"));
		compoundNamesScanner.useDelimiter(",");
		while (compoundNamesScanner.hasNextLine()) {
			//iterate through each line, hash it, then put it in the hashtable
			String line = compoundNamesScanner.nextLine();
			//System.out.println(line);
			String name = line.split(",")[0];
			String formula = line.split(",")[1];
			//System.out.println(formula);
			String key = hashFormula(formula);
			if(!key.equals("-1") && !compoundNames.containsKey(key)) {
				compoundNames.put(key, name);
			}
		}
		compoundNamesScanner.close();
	}
	
	private void loadData() {
		//read all of the main data
		Scanner mainDataScanner = new Scanner(getClass().getResourceAsStream("pt-data2.csv"));
		int atomicNumberIndex = 0;
		while (mainDataScanner.hasNext()) {
			//iterate through each line in the main data and put it into string array
			mainDataUnparsed[atomicNumberIndex] = mainDataScanner.next();
			atomicNumberIndex++;
		}
		mainDataScanner.close();

		//read all of the oxidation data
		Scanner oxidationDataScanner = new Scanner(getClass().getResourceAsStream("pt-data-oxidation.csv"));
		atomicNumberIndex = 0;
		while (oxidationDataScanner.hasNext()) {
			//iterate through each line in the oxidation data and put it into string array
			oxidationDataUnparsed[atomicNumberIndex] = oxidationDataScanner.next();
			atomicNumberIndex++;
		}
		oxidationDataScanner.close();
	}
	
	private void parseData() {
		for(int i=0; i<NUMBER_OF_ELEMENTS;i++){
			String ox = oxidationDataUnparsed[i];
			//split main data by element and by data type
			mainDataParsed[i] = mainDataUnparsed[i].split(",");
			try {
				mainDataParsed[i][3] = mainDataParsed[i][3].substring(0, mainDataParsed[i][3].indexOf("("));
			} catch (StringIndexOutOfBoundsException e) {
			}
			//split oxidation data into an array of possible oxidation states for each element
			oxidationDataParsed[i] = ox.substring(ox.indexOf(",")+1,ox.length()).replace("\"", "").split(",");
		}
	}
	
	/**get a property other than oxidation state*/
	public String getProperty(int atomicNumber, int whichProperty) {
		//atomicNumber-1: arrays are 0-indexed, elements are 1-indexed
		return mainDataParsed[atomicNumber-1][whichProperty];
		
	}
	
	/**get all possible oxidation states*/
	public String[] getOxidationStates(int atomicNumber) {
		//atomicNumber-1: arrays are 0-indexed, elements are 1-indexed
		return oxidationDataParsed[atomicNumber-1];
	}
	
	public int getAtomicNumber(String symbol) {
		for(int i=0; i<mainDataParsed.length; i++) {
			if(symbol.equals(mainDataParsed[i][1])) {
				return i+1;
			}
		}
		return -1; //not an element
	}
	
	public boolean isMetal(int atomicNumber) {
		switch(getProperty(atomicNumber,TYPE_OF_ELEMENT)) {
		case "metal":
			return true;
		case "nonmetal":
			return false;
		case "halogen":
			return false;
		case "noblegas":
			return false;
		case "alkalimetal":
			return true;
		case "alkalineearthmetal":
			return true;
		case "metalloid":
			return true;
		case "lanthanoid":
			return true;
		case "actinoid":
			return true;
		case "transitionmetal":
			return true;
		default:
			return false;
		}
	}
	
	/**Generates a hash key from the formula of a (chargeless) chemical compound.
	 * The hash key will just be an alphabetically ordered list of each element
	 * and the number of atoms of that element one molecule of the compound contains.
	 */
	private String hashFormula(String compound) {
		compound = compound.replace("[", "(").replace("]", ")");
		Matcher allowableSymbols = Pattern.compile("[^A-Za-z0-9()]").matcher(compound);
		if(allowableSymbols.find() || compound.contains(" ")) {
			return "-1"; //this formula is too complicated
		}
		Group compoundGroup = EquationBalancer.parseGroup(new Tokenizer("("+compound+")"));
		String[] elements = compoundGroup.getElements();
		Arrays.sort(elements);
		String hash = "";
		for(int i=0;i<elements.length;i++) {
			hash += elements[i] + compoundGroup.countElement(elements[i]);
		}
		return hash;
	}
	
	public String nameCompound(String formula) {
		String hashedFormula = hashFormula(formula);
		return compoundNames.get(hashedFormula);
	}

}
