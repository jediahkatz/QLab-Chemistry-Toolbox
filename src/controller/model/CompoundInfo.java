package controller.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompoundInfo {
	private PeriodicTableData data;
	private Group compound;
	private String[] elements;
	private int[] elementsNums;
	private String name;
	
	public CompoundInfo(PeriodicTableData data, String compoundStr) {
		this.data = data;
		compound = EquationBalancer.parseGroup(new Tokenizer("(" + compoundStr + ")"));
		elements = compound.getElements();
		elementsNums = new int[elements.length];
		for(int i=0;i<elements.length;i++) {
			elementsNums[i] = data.getAtomicNumber(elements[i]);
		}
		name = data.nameCompound(compoundStr);
	}
	
	public String[][] getTable() {
		String[][] table = new String[getElements().length][6];
		double mm = Double.parseDouble(molarMass());
		int sigfigs = getSigFigs(molarMass());
		for(int i=0;i<getElements().length;i++) {
			String m = (data.getProperty(getElementsNums()[i], PeriodicTableData.ATOMIC_MASS)).replace("[", "").replace("]", "");
			double massInCompound = Double.parseDouble(m)*compound.countElement(getElements()[i]);
			if(getSigFigs(m) < sigfigs) {
				sigfigs = getSigFigs(m);
				//your answer can only have as many sig figs as the factor with the least
			}
			table[i][0] = " " + data.getProperty(getElementsNums()[i],PeriodicTableData.NAME);
			table[i][1] = " " + String.valueOf(getElementsNums()[i]);
			table[i][2] = " " + data.getProperty(getElementsNums()[i], PeriodicTableData.ATOMIC_MASS);
			table[i][3] = " " + String.valueOf(compound.countElement(getElements()[i]));
			table[i][4] = " " + roundToSignificantFigures(massInCompound,getSigFigs(m));
			table[i][5] = " " + roundToSignificantFigures(100*((massInCompound)/mm),sigfigs) + "%";
		}
		return table;
	}
	
	public int numAtoms() {
		int n = 0;
		for(int i=0;i<getElements().length;i++) {
			n+=countElement(getElements()[i]);
		}
		return n;
	}
	
	@SuppressWarnings("unused")
	private int metalAtom() {
		for(int i=0;i<getElements().length;i++) {
			if(data.isMetal(getElementsNums()[i])) {
				return getElementsNums()[i];
			}
		}
		return 0;
	}
	
	public String getName() {
		return name;
	}
	
	/**Ionic or covalent*/
	public String typeOfBond() {
		for(int i=0;i<getElements().length;i++) {
			if(data.isMetal(getElementsNums()[i])) {
				return "Ionic";
			}
		}
		return "Covalent";
	}
	
	public boolean canExist() {
		//There can only be one metal in a compound
		int nMetals = 0;
		for(int i=0;i<getElements().length;i++) {
			//String type = data.getProperty(getElementsNums()[i], PeriodicTableData.TYPE_OF_ELEMENT);
			if(data.isMetal(getElementsNums()[i])) nMetals++;
			if(nMetals >= 2) return false;
		}
		return true;
	}
	
	/**Get molar mass of compound*/
	public String molarMass() {
		double mm = 0;
		int sigfigs = Integer.MAX_VALUE;
		for(int e : getElementsNums()) {
			String m = (data.getProperty(e, PeriodicTableData.ATOMIC_MASS)).replace("[", "").replace("]", "");
			if(getSigFigs(m) < sigfigs) {
				sigfigs = getSigFigs(m);
				//your answer can only have as many sig figs as the factor with the least
			}
			mm += (compound.countElement(data.getProperty(e, PeriodicTableData.SYMBOL)) * Double.parseDouble(m));
		}
		return roundToSignificantFigures(mm,sigfigs);
	}
	
	public int countElement(String element) {
		return compound.countElement(element);
	}
	
	private static int getSigFigs(String number) {
		Matcher front = Pattern.compile("[1-9]").matcher(number);
		front.find();
		number = number.substring(front.start());
		if(!number.contains(".")) {
			Matcher back = Pattern.compile("0*$").matcher(number);
			back.find();
			number = number.substring(0, back.start());
		}
		return number.replace(".", "").length();
	}
	
	public boolean isValid() {
		for(int i=0;i<getElements().length;i++) {
			if(!isElement(getElements()[i])) {
				return false;
			}
		}
		return true;
	}
	
	private boolean isElement(String symbol) {
		if(data.getAtomicNumber(symbol) == -1) {
			return false;
		}
		return true;
	}
	
	public String[] getElements() {
		return elements;
	}
	
	public int[] getElementsNums() {
		return elementsNums;
	}
	
	//Credit to pyrolistical on stackoverflow
		private String roundToSignificantFigures(double num, int n) {
		    if(num == 0) {
		        return "0";
		    }

		    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
		    final int power = n - (int) d;

		    final double magnitude = Math.pow(10, power);
		    final long shifted = Math.round(num*magnitude);
		    String rounded = String.valueOf(shifted/magnitude);
		    int rlen = rounded.length();
		    //Convert to int in this special case
		    if(rlen >= n + 2 && rounded.substring(rlen-2).equals(".0")) {
		    	rounded = rounded.substring(0, rlen-2);
		    } else if(rounded.contains(".")) {
		    //Re-add trailing zeros to match sig figs
		    	while(rounded.length() < n + 1) {
		    		rounded += "0";
		    	}
		    }
		    return rounded;
		}

}
