package controller.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoichiometryCalc {
	private CoefficientTerm[] reactants;
	private CoefficientTerm[] products;
	
	public StoichiometryCalc(String equation) {
		parseEquation(equation);
	}
	
	public String[][] getReactantsTable() {
		String[][] table = new String[reactants.length][3];
		for(int i=0; i<reactants.length; i++) {
			table[i][0] = reactants[i].getTerm();
			table[i][1] = reactants[i].getMol();
			table[i][2] = reactants[i].getMass();
		}
		return table;
	}
	
	public String[][] getProductsTable() {
		String[][] table = new String[products.length][3];
		for(int i=0; i<products.length; i++) {
			table[i][0] = products[i].getTerm();
			table[i][1] = products[i].getMol();
			table[i][2] = products[i].getMass();
		}
		return table;
	}
	
	/**Sets mol and mass of reactants and other products based on the specified product.*/
	public void updateFromProduct(int product) {
		CoefficientTerm base = products[product];
		
		String proportionalProductStr = base.getProportionalProduct();
		int sigFigs = CoefficientTerm.getSigFigs(proportionalProductStr);
		double proportionalProduct = Double.valueOf(proportionalProductStr);
		
		for(CoefficientTerm r : reactants) {
			int coeff = r.getCoefficient();
			r.setMol(CoefficientTerm.roundToSignificantFigures(proportionalProduct * coeff, sigFigs));
		}
		
		for(CoefficientTerm p : products) {
			if(p.equals(base)) {
				continue;
			}
			int coeff = p.getCoefficient();
			p.setMol(CoefficientTerm.roundToSignificantFigures(proportionalProduct * coeff, sigFigs));
		}
	}
	
	/**Sets mol and mass of products based on the limiting reactant.*/
	public void updateProducts() {
		CoefficientTerm limiting = getLimitingReactant();
		System.out.println(limiting.isExcess());
		if(limiting.isExcess()) {
			for(CoefficientTerm p : products) {
				p.setExcess(true);
			}
		} else {
			if(products[0].isExcess()) {
				for(CoefficientTerm p : products) {
					p.setExcess(false);
				}
			}
			
			String proportionalProductStr = limiting.getProportionalProduct();
			int sigFigs = CoefficientTerm.getSigFigs(proportionalProductStr);
			double proportionalProduct = Double.valueOf(proportionalProductStr);

			for(CoefficientTerm p : products) {
				int coeff = p.getCoefficient();
				p.setMol(CoefficientTerm.roundToSignificantFigures(proportionalProduct * coeff, sigFigs));
			}
		}
	}
	
	public void setExcess(int reactant, Boolean inExcess) {
		reactants[reactant].setExcess(inExcess);
	}
	
	public void setMass(String mass, Type type, int term) {
		if(type == Type.REACTANT) {
			reactants[term].setMass(mass);
		} else if (type == Type.PRODUCT) {
			products[term].setMass(mass);
		}
	}
	
	public void setMol(String mol, Type type, int term) {
		if(type == Type.REACTANT) {
			reactants[term].setMol(mol);
		} else if (type == Type.PRODUCT) {
			products[term].setMol(mol);
		}
	}
	
	private CoefficientTerm getLimitingReactant() {
		double[] proportionalProducts = new double[reactants.length];
		String[] proportionalProductsStr = new String[reactants.length];
		for(int i=0;i<reactants.length;i++) {
			proportionalProductsStr[i] = reactants[i].getProportionalProduct();
			try {
				proportionalProducts[i] = Double.valueOf(proportionalProductsStr[i]);
			} catch(NumberFormatException e) {
				proportionalProducts[i] = -1;
			}
		}
		ArrayIndexComparator comparator = new ArrayIndexComparator(proportionalProducts);
		Integer[] indices = comparator.createIndexArray();
		Arrays.sort(indices, comparator);
		//TODO: Account for equal proportional products in terms of limiting reactant
		CoefficientTerm limiting = reactants[indices[0]];
		int limit = 0;
		
		for(int i=0; i<proportionalProducts.length; i++) {
			if(proportionalProducts[indices[i]] == -1) {
				if(limit+1 < proportionalProducts.length) {
					limit++;
				}
				limiting = reactants[indices[limit]];
			}
		}
		
		for(int i=limit+1; i<reactants.length; i++) {
			if(proportionalProducts[indices[i]] != proportionalProducts[indices[i-1]]) {
				break;
			}
			if(CoefficientTerm.getSigFigs(proportionalProductsStr[indices[i]]) < CoefficientTerm.getSigFigs(proportionalProductsStr[indices[i-1]])) {
				limiting = reactants[indices[i]];
			}
		}
		
		return limiting;
	}
	
	/**Returns an array containing an array of reactants and an array of products.
	 * Reactants and products are expressed as CoefficientTerm objects.*/
	private void parseEquation(String eq) {
		eq = eq.replaceAll(" ", "");
		StringBuffer buffer = new StringBuffer(eq);
		Matcher plus = Pattern.compile("\\+[A-Z]|\\+[0-9]|\\+e").matcher(eq);
		while(plus.find()) {
			buffer.replace(plus.start(), plus.start()+1, "&");
		}
		
		eq = buffer.toString();
		eq = eq.replaceAll("&e(\\^-)*", "");
		eq = eq.replaceAll("^e(\\^-)*&", "");
		eq = eq.replaceAll("=e(\\^-)*", "=");
		
		String[] split = eq.split("=");
		String reactants = split[0];
		String products = split[1];
		String[] reactantTerms = reactants.split("&");
		String[] productTerms = products.split("&");
		
		CoefficientTerm[] rTerms = new CoefficientTerm[reactantTerms.length];
		CoefficientTerm[] pTerms = new CoefficientTerm[productTerms.length];
		
		for(int i=0;i<rTerms.length;i++) {
			rTerms[i]=separateCoefficient(reactantTerms[i]);
		}
		for(int i=0;i<pTerms.length;i++) {
			pTerms[i]=separateCoefficient(productTerms[i]);
		}
		
		this.reactants = rTerms;
		this.products = pTerms;
	}
	
	private static CoefficientTerm separateCoefficient(String t) {
		Matcher coeffMatch = Pattern.compile("^[0-9]+").matcher(t);
		int coeff;
		String term;
		if(coeffMatch.find()) {
			coeff = Integer.valueOf(coeffMatch.group());
			term = t.substring(coeffMatch.end());
		} else {
			coeff = 1;
			term = t;
		}
		return new CoefficientTerm(coeff,term);
	}

}

class CoefficientTerm {
	private CompoundInfo info;
	
	private boolean excess = false; //in excess - ie. infinite amount is present
	
	private int coeff;
	private String term;
	
	//Using strings to preserve sig figs... too lazy to make a class for it
	private String mol;
	private String mass;
	
	private final int mmSigFigs;
	private final double molarMass;
	
	CoefficientTerm(int c, String t) {
		coeff = c;
		term = t;
		
		Matcher charge = Pattern.compile("\\^").matcher(t);
		if(charge.find()) {
			t = t.substring(0,charge.start());
		}

		info = new CompoundInfo(new PeriodicTableData(), t);
		String mm = info.molarMass();
		
		mmSigFigs = getSigFigs(mm);
		molarMass = Double.valueOf(mm);
		
		mol = String.valueOf(c) + ".00";
		int sigFigs = Math.min(mmSigFigs, getSigFigs(mol));
		mass = roundToSignificantFigures(c*molarMass, sigFigs);
	}
	
	void setExcess(Boolean inExcess) {
		excess = inExcess;
	}
	
	boolean isExcess() {
		return excess;
	}
	
	String getMass() {
		if(excess) {
			return "\u221E";
		}
		return mass;
	}
	
	String getMol() {
		if(excess) {
			return "\u221E";
		}
		return mol;
	}
	
	void setMass(String m) {
		mass = m;
		int sigFigs = Math.min(getSigFigs(m),mmSigFigs);
		
		mol = roundToSignificantFigures(Double.valueOf(m) / molarMass, sigFigs);
	}
	
	void setMol(String m) {
		mol = m;
		int sigFigs = Math.min(getSigFigs(m), mmSigFigs);
		
		mass = roundToSignificantFigures(Double.valueOf(m) * molarMass, sigFigs);
		System.out.println(sigFigs + "," + m + "," + mass);
	}
	
	int getCoefficient() {
		return coeff;
	}
	
	String getTerm() {
		return term;
	}
	
	/**Returns the number of moles of general product this will form.
	 * Specific product = general product * coefficient of the product.
	 * The reactant that returns the lowest value is limiting.*/
	String getProportionalProduct() {
		if(excess) {
			return "\u221E";
		}
		double mol = Double.valueOf(this.mol);
		int sigfigs = getSigFigs(this.mol);
		
		return roundToSignificantFigures(mol / coeff, sigfigs);
	}
	
	//Partial credit to pyrolistical on stackoverflow
	static String roundToSignificantFigures(double num, int n) {
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
		System.out.println(num + "," + n + ": " + rounded);
		if(rlen >= n + 2 && rounded.substring(rlen-2).equals(".0")) {
			rounded = rounded.substring(0, rlen-2);
		} else if(rounded.contains(".")) {
			//Re-add trailing zeros to match sig figs
			while(rounded.length() < n + 1) {
				rounded += "0";
			}
		} else if(rlen < n) {
			rounded += ".";
			while(rounded.length() < n + 1) {
				rounded += "0";
			}
		}
		return rounded;
	}
	
	static int getSigFigs(String number) {
		Matcher front = Pattern.compile("[0-9]").matcher(number);
		front.find();
		number = number.substring(front.start());
		if(!number.contains(".")) {
			Matcher back = Pattern.compile("0*$").matcher(number);
			back.find();
			number = number.substring(0, back.start());
		}
		return number.replace(".", "").length();
	}
}

//Partial thanks to John Skeet from StackOverflow
class ArrayIndexComparator implements Comparator<Integer>
{
    private final double[] array;

    public ArrayIndexComparator(double[] array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
         if(array[index1] == array[index2]) {
        	 return 0;
         } else if(array[index1] > array[index2]) {
        	 return 1;
         } else {
        	 return -1;
         }
    }
}
