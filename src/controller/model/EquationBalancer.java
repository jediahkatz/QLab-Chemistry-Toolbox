package controller.model;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Arrays;
import java.util.HashSet;

public class EquationBalancer {
	
	public static String balance(String formulaStr, String[] extras) {
		String attempt = balance(formulaStr);
		if(attempt != null) {
			return attempt;
		}
		
		String[] reactantTerms = equationToTermStrings(formulaStr)[0];
		String[] productTerms = equationToTermStrings(formulaStr)[1];
		ArrayList<String> addable = new ArrayList<String>();
		String formula = formulaStr;
		
		//System.out.println(extras.length);
		for(String extra : extras) {
			System.out.println(extra);
			boolean add = true;
			for(String r : reactantTerms) {
				if(r.equals(extra)) {
					add = false;
				}
			}
			for(String p : productTerms) {
				if(p.equals(extra)) {
					add = false;
				}
			}
			
			if(add) {
				addable.add(extra);
				formula += " + " + extra;
			}
		}
		
		if(!addable.isEmpty()) {
			attempt = balance(formula);
			if(attempt != null) {
				return attempt;
			}
		}
		
		for(String a : addable) {
			formula = formulaStr;
			formula += " + " + a;
			attempt = balance(formula);
			if(attempt != null) {
				return attempt;
			}
		}
		
		return null;
	}
	
	public static String balance(String formulaStr) {
		formulaStr = removeCoefficients(formulaStr);
		Equation eqn = parseEquation(new Tokenizer(formulaStr));
		System.out.println("ok");
		int rCharge = 0; //charge of reactants
		int pCharge = 0; //charge of products
		Term[] reactants = eqn.getReactants();
		Term[] products = eqn.getProducts();
		
		for(Term r : reactants) {
			rCharge += r.countElement("e");
		}
		rCharge = -rCharge;
		for(Term p : products) {
			pCharge += p.countElement("e");
		}
		pCharge = -pCharge;
		
		try {
			Matrix matrix = buildMatrix(eqn);
			for(int i=0; i<matrix.rowCount();i++) {
				for(int j=0; j<matrix.columnCount();j++) {
					System.out.println("["+i+"]["+j+"]:" + matrix.get(i, j));
				}
			}
			GaussianElimination.gaussJordan(matrix);
			System.out.println("**SOLVED**");
			for(int i=0; i<matrix.rowCount();i++) {
				for(int j=0; j<matrix.columnCount();j++) {
					System.out.println("["+i+"]["+j+"]:" + matrix.get(i, j));
				}
			}
			BigRational[] coefs = extractCoefficients(matrix);
			checkAnswer(eqn, coefs);
			return addCoefficients(formulaStr,coefs);
		} catch (Exception e) {
			System.out.println("hi!");
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/** Removes the coefficients from a formula string. */
	private static String removeCoefficients(String formula) {
		System.out.println(formula);
		formula = formula.replaceAll("\\s", ""); //remove spaces - unnecessary
		formula = formula.replaceAll("\\+[0-9]+","+"); //remove coefficients after +
		formula = formula.replaceAll("=[0-9]+","="); //remove coefficients after =
		formula = formula.replaceAll("^[0-9]+", ""); //remove coefficients at start
		System.out.println(formula);
		return formula;
	}
	
	//Trying something... didn't work
	/*public static String bal(String formulaStr) {
		Equation eqn = parseEquation(new Tokenizer(formulaStr));
		try {
			Matrix matrix = buildMatrix(eqn);
			//for(int i=0; i<matrix.rowCount();i++) {
			//	for(int j=0; j<matrix.columnCount();j++) {
			//		System.out.println("["+i+"]["+j+"]:" + matrix.get(i, j));
			//	}
			//}
			matrix.gaussJordanEliminate();
			//System.out.println("**SOLVED**");
			int i;
			for(i=0; i<matrix.rowCount() - 1; i++) {
				if(countNonzeroCoeffs(matrix,i)>1) {
					break;
				}
			}
			if(i == matrix.rowCount() - 1) {
				return null;
			}
			matrix.set(matrix.rowCount()-1, i, BigRational.ONE);
			matrix.set(matrix.rowCount()-1, matrix.columnCount()-1,BigRational.ONE);
			matrix.gaussJordanEliminate();
			
			BigRational[] coefs = extractCoefficients(matrix);
			checkAnswer(eqn, coefs);
			return addCoefficients(formulaStr,coefs);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}*/
	
	/*private static int countNonzeroCoeffs(Matrix matrix,int row) {
		int count = 0;
		for(int i=0; i<matrix.columnCount(); i++) {
			if(!matrix.get(row, i).isZero()) {
				count++;
			}
		}
		return count;
	}*/
	
	private static String[][] equationToTermStrings(String eqn) {
		eqn = eqn.replace(" ", "");
		String reactantSide = eqn.split("=")[0];
		String productSide = eqn.split("=")[1];
		int i = 0;
		Matcher rPlus = Pattern.compile("\\+([A-Z]|[0-9]|e)").matcher(reactantSide);
		Matcher pPlus = Pattern.compile("\\+([A-Z]|[0-9]|e)").matcher(productSide);
		ArrayList<String> products = new ArrayList<String>();
		ArrayList<String> reactants = new ArrayList<String>();
		System.out.println("reactant " + reactantSide);
		System.out.println("product " + productSide);
		while(rPlus.find()) {
			reactants.add(reactantSide.substring(i, rPlus.start()));
			i=rPlus.end()-1;
		}
		reactants.add(reactantSide.substring(i));
		i=0;
		
		while(pPlus.find()) {
			products.add(productSide.substring(i, pPlus.start()));
			i=pPlus.end()-1;
		}
		products.add(productSide.substring(i));
		
		for(String r : reactants) {
			System.out.println("r :" + r);
		}
		for(String p : products) {
			System.out.println("p :" + p);
		}
		
		String[][] terms = {reactants.toArray(new String[0]),products.toArray(new String[0])};
		return terms;
	}
	
	private static BigRational[] getOptimalFlips(BigRational[] coefs) {
		int forwardFlips = 0;
		int reverseFlips = 0;
		BigRational[] reverseCoefs = new BigRational[coefs.length];
		for(int i=0;i<coefs.length;i++) {
			reverseCoefs[i]=coefs[i].neg();
			if(coefs[i].isNegative()) {
				forwardFlips++;
			} else {
				reverseFlips++;
			}
		}
		
		if(forwardFlips <= reverseFlips) {
			return coefs;
		}
		return reverseCoefs;
	}
	
	private static String addCoefficients(String unbalanced, BigRational[] coefs) {
		String[][] terms = equationToTermStrings(unbalanced);
		String[] reactantTerms = terms[0];
		String[] productTerms = terms[1];
		ArrayList<String> rTerms = new ArrayList<String>();
		ArrayList<String> pTerms = new ArrayList<String>();
		if(reactantTerms.length + productTerms.length != coefs.length) {
			throw(new ArrayIndexOutOfBoundsException("Number of coefficients & number of terms don't match"));
		}
		
		coefs = getOptimalFlips(coefs);
		
		//Check if *every* coefficient except that of e is negative
		//Which means that e is on the wrong side
		/*for(int i=0;i<reactantTerms.length;i++) {
			if(reactantTerms[i].charAt(0)!='e' && coefs[i].isPositive()) {
				flip = false;
			}
		}
		for(int i=0;i<productTerms.length;i++) {
			if(productTerms[i].charAt(0)!='e' && coefs[i+reactantTerms.length].isPositive()) {
				flip = false;
			}
		}*/
		
		//anything on the wrong side will be negative and can mess
		//EVERYTHING up!!! How can we determine which direction is "right"
		//and which is wrong?
		//Solution: "right" direction is the direction that needs least flips?
		
		String finalEquation = "";
		for(int i=0;i<reactantTerms.length;i++) {
			while(Character.isDigit(reactantTerms[i].charAt(0))) {
				reactantTerms[i] = reactantTerms[i].substring(1);
			}
			
			//Add subscripts
			reactantTerms[i] = reactantTerms[i].replace("1", "\u2081");
			reactantTerms[i] = reactantTerms[i].replace("2", "\u2082");
			reactantTerms[i] = reactantTerms[i].replace("3", "\u2083");
			reactantTerms[i] = reactantTerms[i].replace("4", "\u2084");
			reactantTerms[i] = reactantTerms[i].replace("5", "\u2085");
			reactantTerms[i] = reactantTerms[i].replace("6", "\u2086");
			reactantTerms[i] = reactantTerms[i].replace("7", "\u2087");
			reactantTerms[i] = reactantTerms[i].replace("8", "\u2088");
			reactantTerms[i] = reactantTerms[i].replace("9", "\u2089");
			
			System.out.println(i + " coeff: " + coefs[i] + ": "+ reactantTerms[i]);
			if(coefs[i].isOne()) {
				rTerms.add(reactantTerms[i]);
			} else if(coefs[i].isNegative()) {
				if(coefs[i].isMinusOne()) {
					pTerms.add(reactantTerms[i]);
				} else {
					pTerms.add(coefs[i].neg() + reactantTerms[i]);
				}
			} else {
				rTerms.add(coefs[i] + reactantTerms[i]);
			}
			
			/*if(reactantTerms[i].charAt(0) == 'e' && flip) {
				if(coefs[i].isOne()) {
					pTerms.add(reactantTerms[i]);
				} else {
					pTerms.add(coefs[i] + reactantTerms[i]);
				}
			} else if(coefs[i].isOne()) {
				rTerms.add(reactantTerms[i]);
			} else if(coefs[i].isNegative()) {
				if((reactantTerms[i].charAt(0) != 'e')) {
					flip = true;
					if(coefs[i].isMinusOne()) {
						rTerms.add(reactantTerms[i]);
					} else {
						rTerms.add(coefs[i].neg().intValue() + reactantTerms[i]);
					}
					try {
						if(reactantTerms[i-1].charAt(0) == 'e') {
							if(coefs[i-1].isOne()) {
								pTerms.add(reactantTerms[i-1]);
							} else {
								pTerms.add(coefs[i-1] + reactantTerms[i-1]);
							}
						}
					} catch(Exception e) {
						
					}
				} else {
					if(coefs[i].isMinusOne()) {
						pTerms.add(reactantTerms[i]);
					} else {
						pTerms.add(coefs[i].neg().intValue() + reactantTerms[i]);
					}
				}
			} else {
				rTerms.add(coefs[i].intValue() + reactantTerms[i]);
			}*/
			
			/*finalEquation += reactantTerms[i];
			if(i == reactantTerms.length-1) {
				finalEquation += " = ";
			} else {
				finalEquation += " + ";
			}*/
		}
		for(int i=0;i<productTerms.length;i++) {
			while(Character.isDigit(productTerms[i].charAt(0))) {
				productTerms[i] = productTerms[i].substring(1);
			}
			
			//Add subscripts
			productTerms[i] = productTerms[i].replace("1", "\u2081");
			productTerms[i] = productTerms[i].replace("2", "\u2082");
			productTerms[i] = productTerms[i].replace("3", "\u2083");
			productTerms[i] = productTerms[i].replace("4", "\u2084");
			productTerms[i] = productTerms[i].replace("5", "\u2085");
			productTerms[i] = productTerms[i].replace("6", "\u2086");
			productTerms[i] = productTerms[i].replace("7", "\u2087");
			productTerms[i] = productTerms[i].replace("8", "\u2088");
			productTerms[i] = productTerms[i].replace("9", "\u2089");
			
			System.out.println(i + " coeff: " + coefs[i+reactantTerms.length] + ": " + productTerms[i]);
			
			if(coefs[i+reactantTerms.length].isOne()) {
				pTerms.add(productTerms[i]);
			} else if(coefs[i+reactantTerms.length].isNegative()) {
				if(coefs[i+reactantTerms.length].isMinusOne()) {
					rTerms.add(productTerms[i]);
				} else {
					rTerms.add(coefs[i+reactantTerms.length].neg() + productTerms[i]);
				}
			} else {
				pTerms.add(coefs[i+reactantTerms.length] + productTerms[i]);
			}
			
			/*if(productTerms[i].charAt(0) == 'e' && flip) {
				if(coefs[i+reactantTerms.length].isOne()) {
					rTerms.add(reactantTerms[i]);
				} else {
					rTerms.add(coefs[i+reactantTerms.length] + productTerms[i]);
				}
			} else if(coefs[i+reactantTerms.length].isOne()) {
				pTerms.add(productTerms[i]);
			} else if(coefs[i+reactantTerms.length].isNegative()) {
				if((productTerms[i].charAt(0) != 'e')) {
					flip = true;
					if(coefs[i].isMinusOne()) {
						pTerms.add(productTerms[i]);
					} else {
						pTerms.add(coefs[i+reactantTerms.length].neg().intValue() + productTerms[i]);
					}
				} else {
					if(coefs[i].isMinusOne()) {
						rTerms.add(productTerms[i]);
					} else {
						rTerms.add(coefs[i+reactantTerms.length].neg().intValue() + productTerms[i]);
					}
				}
			} else {
				pTerms.add(coefs[i+reactantTerms.length].intValue() + productTerms[i]);
			}*/
			
			/*finalEquation += productTerms[i];
			if(i == productTerms.length - 1) {
				continue;
			}
			finalEquation += " + ";*/
		}
		for(int i=0; i<rTerms.size(); i++) {
			finalEquation += rTerms.get(i);
			if(i>=rTerms.size()-1) {
				continue;
			}
			finalEquation += " + ";
		}
		finalEquation += " = ";
		for(int i=0; i<pTerms.size(); i++) {
			finalEquation += pTerms.get(i);
			if(i>=pTerms.size()-1) {
				continue;
			}
			finalEquation += " + ";
		}
		return finalEquation;
	}
	
	// Parses and returns an equation.
	@SuppressWarnings("unused")
	private static Equation parseEquation(Tokenizer tok) {
		ArrayList<Term> lhs = new ArrayList<Term>();
		ArrayList<Term> rhs = new ArrayList<Term>();

		lhs.add(parseTerm(tok));
		while (true) {
			String next = tok.peek();
			if(next == null) {
				throw(new IllegalArgumentException("Plus sign expected"));
			} else if (next.equals("=")) {
				tok.consume("=");
				break;
			} else if (next == null) {
				throw(new IllegalArgumentException("Plus or equals sign expected"));
			} else if (next.equals("+")) {
				tok.consume("+");
				lhs.add(parseTerm(tok));
			} else {
				throw(new IllegalArgumentException("Plus sign expected"));
			}
		}
		
		rhs.add(parseTerm(tok));
		while (true) {
			String next = tok.peek();
			if (next == null)
				break;
			else if (next.equals("+")) {
				tok.consume("+");
				rhs.add(parseTerm(tok));
			} else {
				throw(new IllegalArgumentException("Plus sign or end expected"));
			}
		}
		
		return new Equation(lhs.toArray(new Term[0]), rhs.toArray(new Term[0]));
	}
	
	// Parses and returns a term.
	public static Term parseTerm(Tokenizer tok) {
		
		// Parse groups and elements
		ArrayList<Item> items = new ArrayList<Item>();
		while (true) {
			String next = tok.peek();
			System.out.println("NEXT :" + next);
			if(next == null) {
				break;
			}
			
			Matcher element = Pattern.compile("^[A-Za-z][a-z]*$").matcher(next);
			if (next.equals("(")) {
				//System.out.println(next);
				items.add(parseGroup(tok));
			} else if (element.find()) {
				items.add(parseElement(tok));
			} else {
				break;
			}
		}
		
		// Parse optional charge
		int charge = 0;
		String next = tok.peek();
		//System.out.println(next);
		if (next != null && next.equals("^")) {
			tok.consume("^");
			next = tok.peek();
			//System.out.println(next);
			if (next == null) {
				throw(new IllegalArgumentException("Number or sign expected"));
			} else {
				charge = parseOptionalNumber(tok);
			}
			next = tok.peek();
			if(next == null) {
				throw(new IllegalArgumentException("Sign expected"));
			} else if (next.equals("+")) {
				charge = +charge;  // No-op
			} else if (next.equals("-")) {
				charge = -charge;
			} else {
				throw(new IllegalArgumentException("Sign expected"));
			}
			tok.take();  // Consume the sign
		}
		
		// Check if term is valid
		HashSet<String> elements = new HashSet<String>();
		for (Item i : items) {
			for(String e : i.getElements()) {
				elements.add(e);
			}
		}
		String[] elems = elements.toArray(new String[0]); // List of all elements used in this term, with no repeats
		if (items.size() == 0) {
			throw(new IllegalArgumentException("Empty term"));
		} else if (Arrays.asList(elems).contains("e")) {  // If it's the special electron element
			if (items.size() > 1) {
				throw(new IllegalArgumentException("Electron must stand alone"));
			} else if (charge != 0 && charge != -1) {
				throw(new IllegalArgumentException("Invalid charge for electron"));
			}
			// Tweak data
			items.clear();
			charge = -1;
		} else {  // Otherwise, a term must not contain an element that starts with lowercase
			for (int i = 0; i < elems.length; i++) {
				Matcher lowercase = Pattern.compile("^[a-z]+$").matcher(elems[i]);
				if (lowercase.find()) {
					throw(new IllegalArgumentException("Element cannot start with lowercase letter"));
				}
			}
		}
		
		return new Term(items.toArray(new Item[0]), charge);
	}
	
	// Parses and returns a group.
	public static Group parseGroup(Tokenizer tok) {
		tok.consume("(");
		ArrayList<Item> items = new ArrayList<Item>();
		while (true) {
			String next = tok.peek();
			Matcher match = Pattern.compile("^[A-Za-z][a-z]*$").matcher(next);
			if (next == null) {
				throw(new IllegalArgumentException("Element, group, or closing parenthesis expected"));
			} else if (next.equals("(")) {
				items.add(parseGroup(tok));
			} else if (match.find()) {
				items.add(parseElement(tok));
			} else if (next.equals(")")) {
				tok.consume(")");
				if (items.size() == 0)
					throw(new IllegalArgumentException("Empty group"));
				break;
			} else {
				throw(new IllegalArgumentException("Element, group, or closing parenthesis expected"));
			}
		}
		
		return new Group(items.toArray(new Item[0]), parseOptionalNumber(tok));
	}


	// Parses and returns an element.
	private static Element parseElement(Tokenizer tok) {
		String name = tok.take();
		Matcher match = Pattern.compile("^[A-Za-z][a-z]*$").matcher(name);
		if (!match.find()) {
			throw(new IllegalArgumentException());
		}
		return new Element(name, parseOptionalNumber(tok));
	}


	private static int parseOptionalNumber(Tokenizer tok) {
		String next = tok.peek();
		Matcher match;
		try {
			match = Pattern.compile("^[0-9]+$").matcher(next);
		} catch(Exception e) {
			return 1;
		}
		if (next != null && match.find()) {
			return Integer.valueOf(tok.take());
		} else {
			return 1;
		}
	}
	
	private static Matrix buildMatrix(Equation eqn) {
		String[] elements = eqn.getElements();
		Term[] reactants = eqn.getReactants();
		Term[] products = eqn.getProducts();
		//int rows = elements.length+1;
		//int cols = reactants.length + products.length + 1;
		int rows = elements.length;// + 1;
		int cols = reactants.length + products.length + 1;
		Matrix matrix = new Matrix(rows,cols);
		for(int i=0; i<elements.length; i++) {
			int j=0;
			for(int k=0; k<reactants.length; j++, k++) {
				//System.out.println(reactants[k].countElement(elements[i]) + elements[i]);
				matrix.set(i, j, new BigRational(reactants[k].countElement(elements[i])));
			}
			for(int k=0; k<products.length; j++, k++) {
				matrix.set(i, j, new BigRational(products[k].countElement(elements[i])).negate());
			}
		}
		/*for(int i=0;i<reactants.length;i++) {
			matrix.set(rows-1, i, new BigRational(reactants[i].getCharge()));
		}
		for(int i=reactants.length;i<products.length+reactants.length;i++) {
			matrix.set(rows-1, i, new BigRational(products[i-reactants.length].getCharge()));
		}*/
		/*for(int i=0;i<matrix.rowCount();i++) {
			for(int j=0;j<matrix.columnCount();j++){
				System.out.println(i + ":" + j + ": " + matrix.get(i, j));
			}
		}*/
		return matrix;
	}
	
	//TODO more exceptions
	private static BigRational[] extractCoefficients(Matrix matrix) {//throws Exception {
		int rows = matrix.rowCount();
		int cols = matrix.columnCount();
		
		if(cols - 1 > rows || matrix.get(cols - 2, cols -2).isZero()) {
			//throw(new Exception("Multiple independent solutions"));
		}
		
		BigRational lcd = BigRational.ONE; //least common denominator
		BigRational[] coefs = new BigRational[cols - 1];
		for(int i=0; i<cols-1; i++) {
			coefs[i] = BigRational.ONE;
		}
		for(int i=0;i<rows;i++) {
			if(matrix.get(i, cols-2).isZero()) {
				continue;
			}
			lcd = lcd.lcd(matrix.get(i, cols-2));
			coefs[i] = matrix.get(i, cols-2).negate();
		}
		//System.out.println("lcd: " + lcd);
		//TODO clean this up
		boolean allzero = true;
		for(int i=0;i<cols - 1;i++){
			coefs[i] = coefs[i].multiply(lcd);
		}
		if(allzero) {
			//throw(new Exception("All-zero solution"));
		}
		return coefs;
	}
	
	//Throws an exception if there is a problem
	//Otherwise has no effect
	private static void checkAnswer(Equation eqn, BigRational[] coefs) throws Exception {
		if(coefs.length != eqn.getReactants().length + eqn.getProducts().length) {
			throw(new Exception("Mismatched length"));
		}
		boolean allzero = true;
		for(BigRational coef : coefs) {
			allzero &= coef.isZero();
		}
		if(allzero) {
			throw(new Exception("All-zero solution"));
		}
		
		String[] elements = eqn.getElements();
		Term[] reactants = eqn.getReactants();
		Term[] products = eqn.getProducts();
		
		for(int i=0;i<elements.length;i++){
			int sum=0;
			int j=0;
			for(int k=0; k<reactants.length; j++, k++) {
				sum += coefs[j].multiply(reactants[k].countElement(elements[i])).intValue();
			}
			for(int k=0; k<products.length; j++, k++) {
				sum += coefs[j].negate().multiply(products[k].countElement(elements[i])).intValue();
			}
			if(sum!=0) {
				throw(new Exception("Incorrect balance"));
			}
		}
	}
}

class GaussianElimination {
	
	static void gaussJordan(Matrix m) {
		BigRational[][] matrix = new BigRational[m.rowCount()][m.columnCount()];
		for(int i=0; i<m.rowCount(); i++) {
			for(int j=0; j<m.columnCount(); j++) {
				matrix[i][j] = m.get(i, j);
			}
		}
        final int rows = matrix.length, cols = matrix[0].length;
        int row = 0, col = 0, i, j;
        BigRational[] swapBuffer;
        BigRational multKoef;
        while (row < rows && col < cols) {
            i = row; //pos of non-null row
            while (i < rows && matrix[i][col].equals(BigRational.ZERO)) {
                i++;
            }
            if (i == rows) {
                //no pivots at all
                col++;
                continue;
            }
            if (i != row) {
                //need to swap
                swapBuffer = matrix[i];
                matrix[i] = matrix[row];
                matrix[row] = swapBuffer;
            }
 
            //set pivot to 1
            multKoef = matrix[row][col].invert();
            matrix[row][col] = BigRational.ONE;
            for (j = col + 1; j < cols; j++) {
                matrix[row][j] = matrix[row][j].multiply(multKoef);
            }
 
            //set 0 over and under pivot
            for (i = 0; i < rows; i++) {
                if (i == row) {
                    continue;
                }
                if (matrix[i][col].equals(BigRational.ZERO)) {
                    continue;
                }
                multKoef = matrix[i][col].negate();
                matrix[i][col] = BigRational.ZERO;
                for (j = col + 1; j < cols; j++) {
                    matrix[i][j] = matrix[i][j].add(matrix[row][j].multiply(multKoef));
                }
            }
 
            //now move to next pivot
            col++;
            row++;
        }
        for(int g=0; g<rows; g++) {
        	for(int h=0; h<cols; h++) {
        		m.set(g, h, matrix[g][h]);
        	}
        }
    }
}

//A matrix of integers

class Matrix {
	private BigRational[][] cells;
	private int rows;
	private int cols;
	
	Matrix(int rows, int cols) {
		if (rows < 0 || cols < 0) {
			throw(new IllegalArgumentException("Must be a positive int"));
		}
		this.rows = rows;
		this.cols = cols;
		
		BigRational[][] cells = new BigRational[rows][cols];
		for(int i=0;i<rows;i++){
			for(int j=0;j<cols;j++){
				cells[i][j]=new BigRational(0);
			}
		}
		this.cells = cells;
	}
	
	int rowCount() {
		return rows;
	}
	
	int columnCount() {
		return cols;
	}
	
	BigRational get(int row, int col) {
		return cells[row][col];
	}
	
	void set(int row, int col, BigRational val) {
		cells[row][col] = val;
	}
	
	//Swaps two rows in the matrix. Case i==j is allowed but does nothing
	void swapRows(int i, int j){
		BigRational[] tempRow = cells[i];
		cells[i] = cells[j];
		cells[j] = tempRow;
	}
	
	//Returns a new row that is the sum of the two given rows
	BigRational[] addRows(BigRational[] ds, BigRational[] ds2) {
		BigRational[] sumRow = ds;
		for(int i=0; i<sumRow.length;i++){
			sumRow[i] = sumRow[i].add(ds2[i]);
		}
		return sumRow;
	}
	
	//Returns a new row that is the product of the given row with the given scalar
	BigRational[] multiplyRow(BigRational[] cells2, BigRational d) {
		BigRational[] productRow = cells2;
		for(int i=0; i<productRow.length;i++){
			productRow[i]=productRow[i].multiply(d);
		}
		return productRow;
	}
	
	//Returns the GCF of all numbers in the row
	BigRational gcfRow(BigRational[] x) {
		BigRational gcf = new BigRational(0);
		for(int i=0;i<x.length;i++){
			gcf = gcf(x[i],gcf);
		}
		return gcf;
	}
	
	//Returns a new row where the leading non-zero number (if any) is positive, and the GCF of the row is 0 or 1
	//Ex: simplifyRow([0,-2,2,4]) = [0,1,-1,2]
	BigRational[] simplifyRow(BigRational[] x) {
		int sign = 0;
		for(int i=0; i<x.length; i++) {
			if(x[i].compareTo(0) == 1) {
				sign = 1;
				break;
			} else if(x[i].compareTo(0) == -1) {
				sign = -1;
				break;
			}
		}
		BigRational[] y = x;
		if(sign == 0) return y;
		BigRational g = gcfRow(x).multiply(sign);
		for(int i=0; i<y.length; i++) {
			y[i] = y[i].divide(g);
		}
		return y;
	}
	
	//Trying something... didn't work, but thanks to nayuki.io anyway
	/*void gaussJordanEliminate() {
		//Simplify all rows
		for(int i=0; i<rows; i++){
			cells[i] = simplifyRow(cells[i]);
		}
		
		//Compute row echelon form
		int numPivots = 0;
		for(int i=0; i<cols; i++) {
			//Find pivot
			int pivotRow = numPivots;
			while (pivotRow < rows && get(pivotRow,i).isZero()) {
				pivotRow++;
			}
			if (pivotRow == rows) {
				continue; //Cannot eliminate on this column
			}
			BigRational pivot = cells[pivotRow][i];
			swapRows(numPivots, pivotRow);
			numPivots++;
			
			//Eliminate below
			for(int j = numPivots; j<rows; j++) {
				BigRational g = gcf(pivot,cells[j][i]);
				cells[j] = simplifyRow(addRows(multiplyRow(cells[j], pivot.divide(g)), multiplyRow(cells[i], cells[j][i].negate().divide(g))));
			}
		}
		
		//Compute reduced row echelon form, but the leading coefficient need not be 1
		for(int i = rows - 1; i >= 0; i--) {
			//Find pivot
			int pivotCol = 0;
			while(pivotCol < cols && cells[i][pivotCol].isZero()) {
				pivotCol++;
			}
			if(pivotCol == cols) {
				continue;
			}
			BigRational pivot = cells[i][pivotCol];
			
			//Eliminate above
			for(int j = i-1; j>= 0; j--) {
				BigRational g = gcf(pivot, cells[j][pivotCol]);
				cells[j] = simplifyRow(addRows(multiplyRow(cells[j], pivot.divide(g)), multiplyRow(cells[i], cells[j][pivotCol].negate().divide(g))));
			}
		}
	}*/
	
	/**Returns the greatest common factor of two BigRationals.*/
	static BigRational gcf(BigRational x, BigRational y) {
		x = x.abs();
		y = y.abs();
		while(!y.isZero()) {
			BigRational z = x.mod(y);
			x = y;
			y = z;
		}
		return x;
	}
}

