package controller.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.xswingx.PromptSupport;

import controller.model.EquationBalancer;
import controller.model.StoichiometryCalc;
import controller.model.Type;
import net.miginfocom.swing.MigLayout;

public class BalancerView extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4090075249892491469L;
	//private BoxLayout balancerBoxLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
	private MigLayout balancerBoxLayout = new MigLayout();
	private JTextField equation = new JTextField();
	private JButton balanceButton = new JButton("Balance Equation");
	private JTextArea balancedText = new JTextArea("");
	private JTextArea balEq = new JTextArea("");
	String[] occursIn; //Any excess atoms/ions/compounds in the surroundings
	private BalancerComboBox occursInBox = new BalancerComboBox(this);
	private JTextArea occursInText = new JTextArea("This reaction is occurring in");
	
	private StoichiometryCalc stoichiometry;
	private JTextField[][] reactants;
	private JTextField[][] products;
	private JPanel rpanel = new JPanel();
	private JPanel ppanel = new JPanel();
	
	private JCheckBox[] excess;
	
	public BalancerView() {
		rpanel.setLayout(new MigLayout());
		ppanel.setLayout(new MigLayout());
		rpanel.setPreferredSize(new Dimension(500,100));
		
		equation.setFont(new Font("Sans Serif",Font.PLAIN,60));
		equation.setMaximumSize(new Dimension(10000,100));
		equation.setPreferredSize(equation.getMaximumSize());
		balancedText.setEditable(false);
		balancedText.setOpaque(false);
		balEq.setEditable(false);
		balEq.setOpaque(false);
		balEq.setFont(new Font("Sans Serif",Font.PLAIN,16));
		balancedText.setFont(new Font("Sans Serif",Font.PLAIN,60));
		PromptSupport.setPrompt("Chemical equation to balance", equation);
		equation.addKeyListener(new TextFieldListener(equation, this));
		equation.getDocument().addDocumentListener(new EqnListener(equation));
		balanceButton.addActionListener(this);
		equation.setAlignmentX(Component.LEFT_ALIGNMENT);
		balanceButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		balancedText.setAlignmentX(Component.LEFT_ALIGNMENT);
		balancedText.setPreferredSize(new Dimension(10000,(int) balancedText.getPreferredSize().getHeight()));
		//occursInBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		//occursInBox.setPreferredSize(new Dimension(100,occursInBox.getPreferredSize().height));
		occursInText.setFont(occursInBox.getFont());
		//occursInText.setAlignmentX(Component.LEFT_ALIGNMENT);
		//occursInText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		occursInText.setEditable(false);
		//occursInText.setPreferredSize(new Dimension(getFontMetrics(occursInText.getFont()).stringWidth("This reaction is occurring in"),(int) occursInText.getPreferredSize().getHeight()));
		//occursInBox.setMaximumSize(new Dimension(100,occursInBox.getMaximumSize().height));
		occursInBox.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		//occursInBox.setToolTipText("A reaction occurring in solution has H2O available to react. An acidic solution contains H+ and a basic solution contains OH-." );
		occursInText.setEnabled(false);
		occursInText.setDisabledTextColor(Color.black);
		occursInText.setOpaque(false);
		
		String[] starting = {};
		setOccursIn(starting);
		createBalancerView();
	}

	private void createBalancerView() {
		
		setLayout(balancerBoxLayout);
		add(equation, "span");
		add(occursInText);//, "wrap 10");
		//TODO: why the fuck does this move when I balance an equation?
		//Seems to have something to do with the new text & boxes being added
		add(occursInBox, "span");
		add(balanceButton, "wrap 10, left");
		add(balEq,"wrap");
		add(balancedText, "span, wrap 30");
		//add(cpanel,"top, left");
		add(rpanel, "gapx 0, top, left");
		add(ppanel, "gapx 50, top, left");
	}
	
	public void setOccursIn(String[] occursIn) {
		this.occursIn = occursIn;
	}
	
	public StoichiometryCalc getStoichiometryCalc() {
		return stoichiometry;
	}
	
	public void updateTables() {
		String[][] rtable = stoichiometry.getReactantsTable();
		String[][] ptable = stoichiometry.getProductsTable();
		
		
		for(int i=1; i<reactants.length; i++) {
			for(int j=0; j<3; j++) {
				if(reactants[i][j].hasFocus()) {
					continue;
				}
				reactants[i][j].setText(rtable[i-1][j]);
				
				if(j > 0) {
					if(reactants[i][j].getText().equals("\u221E")) {
						reactants[i][j].setEditable(false);
					} else if(!reactants[i][j].isEditable()) {
						reactants[i][j].setEditable(true);
					}
				}
			}
		}

		for(int i=1; i<products.length; i++) {
			for(int j=0; j<3; j++) {
				if(products[i][j].hasFocus()) {
					continue;
				}
				products[i][j].setText(ptable[i-1][j]);
				
				if(j > 0) {
					if(products[i][j].getText().equals("\u221E")) {
						products[i][j].setEditable(false);
					} else if(!products[i][j].isEditable()){
						products[i][j].setEditable(true);
					}
				}
			}
		}
	}
	
	public void makeTables() {
		String[][] rtable = stoichiometry.getReactantsTable();
		String[][] ptable = stoichiometry.getProductsTable();
		
		reactants = new JTextField[rtable.length+1][3];
		products = new JTextField[ptable.length+1][3];
		excess = new JCheckBox[rtable.length];
		
		rpanel.removeAll();
		ppanel.removeAll();
		
		JTextField inExcess = new JTextField("\u221E");
		inExcess.setToolTipText("Select any reactants that are in excess.");
		inExcess.setFont(new Font("Sans Serif",Font.PLAIN,20));
		inExcess.setEditable(false);
		inExcess.setBorder(BorderFactory.createEmptyBorder());
		
		reactants[0][0] = new JTextField("Reactant");
		reactants[0][1] = new JTextField("Moles (mol)");
		reactants[0][2] = new JTextField("Mass (g)");
		products[0][0] = new JTextField("Product");
		products[0][1] = new JTextField("Moles (mol)");
		products[0][2] = new JTextField("Mass (g)");
		
		//Adding REACTANTS to JTextFields
		
		for(int i=1; i<reactants.length; i++) {
			for(int j=0; j<3; j++) {
				reactants[i][j] = new JTextField(rtable[i-1][j]);
				if(j == 0) {
					reactants[i][j].setEditable(false);
					reactants[i][j].setBorder(BorderFactory.createEmptyBorder());
				} else if(i != 0) {
					reactants[i][j].getDocument().addDocumentListener(new StoichiometryListener(Type.REACTANT,i,j,this,reactants[i][j]));
				}
			}
		}
		
		for(int i=0; i<reactants.length; i++) {
			if(i==0) {
				rpanel.add(inExcess, "right, gapx 0 20");
			} else {
				excess[i-1] = new JCheckBox();
				excess[i-1].addItemListener(new CheckBoxListener(i-1,this));
				rpanel.add(excess[i-1],"left, gapx 0 20");
			}
			for(int j=0; j<3; j++) {
				if(j == 2) {
					rpanel.add(reactants[i][j], "span");
				} else {
					rpanel.add(reactants[i][j]);
				}
				if(i==0) {
					reactants[i][j].setEditable(false);
					reactants[i][j].setBorder(BorderFactory.createEmptyBorder());
					reactants[i][j].setFont(new Font("Sans Serif", Font.PLAIN, 20));
				} else {
					reactants[i][j].setFont(new Font("Sans Serif", Font.PLAIN, 17));
				}
				if(j == 0) {
					reactants[i][j].setMinimumSize(new Dimension(100,((int) reactants[i][j].getPreferredSize().getHeight())));
				} else {
					reactants[i][j].setMinimumSize(new Dimension(120,((int) reactants[i][j].getPreferredSize().getHeight())));
				}
			}
		}
		
		//Adding PRODUCTS to JTextFields
		
		for(int i=1; i<products.length; i++) {
			for(int j=0; j<3; j++) {
				products[i][j] = new JTextField(ptable[i-1][j]);
				if(j == 0) {
					products[i][j].setEditable(false);
					products[i][j].setBorder(BorderFactory.createEmptyBorder());
				} else if(i != 0) {
					products[i][j].getDocument().addDocumentListener(new StoichiometryListener(Type.PRODUCT,i,j,this,products[i][j]));
				}
			}
		}
		
		for(int i=0; i<products.length; i++) {
			for(int j=0; j<3; j++) {
				if(j == 2) {
					ppanel.add(products[i][j], "span");
				} else {
					ppanel.add(products[i][j]);
				}
				if(i==0) {
					products[i][j].setEditable(false);
					products[i][j].setBorder(BorderFactory.createEmptyBorder());
					products[i][j].setFont(new Font("Sans Serif", Font.PLAIN, 20));
				} else {
					products[i][j].setFont(new Font("Sans Serif", Font.PLAIN, 17));
				}
				if(j == 0) {
					products[i][j].setMinimumSize(new Dimension(100,((int) products[i][j].getPreferredSize().getHeight())));
				} else {
					products[i][j].setMinimumSize(new Dimension(120,((int) products[i][j].getPreferredSize().getHeight())));
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String result = "";

		try {
			String eqn = equation.getText();
					
			eqn = eqn.replace("\u2192", "=");
			eqn = eqn.replace("\u2081", "1");
			eqn = eqn.replace("\u2082", "2");
			eqn = eqn.replace("\u2083", "3");
			eqn = eqn.replace("\u2084", "4");
			eqn = eqn.replace("\u2085", "5");
			eqn = eqn.replace("\u2086", "6");
			eqn = eqn.replace("\u2087", "7");
			eqn = eqn.replace("\u2088", "8");
			eqn = eqn.replace("\u2089", "9");
			result = EquationBalancer.balance(eqn,occursIn);
			balEq.setText("Balanced equation:");
			
			String stoiResult = result;
			stoiResult = stoiResult.replace("\u2192", "=");
			stoiResult = stoiResult.replace("\u2081", "1");
			stoiResult = stoiResult.replace("\u2082", "2");
			stoiResult = stoiResult.replace("\u2083", "3");
			stoiResult = stoiResult.replace("\u2084", "4");
			stoiResult = stoiResult.replace("\u2085", "5");
			stoiResult = stoiResult.replace("\u2086", "6");
			stoiResult = stoiResult.replace("\u2087", "7");
			stoiResult = stoiResult.replace("\u2088", "8");
			stoiResult = stoiResult.replace("\u2089", "9");
			stoichiometry = new StoichiometryCalc(stoiResult);
			
			makeTables();
			
			//TODO: JScrollPane? Probably unnecessary
			result = result.replace("=", "\u2192");
		} catch (Exception ex) {
			balEq.setText("");
			rpanel.removeAll();
			ppanel.removeAll();
			result = "Please try again with a valid equation.";
		}
		int fontSize = balancedText.getFont().getSize();
		while(fontSize > 1 && balancedText.getSize().getWidth() < getFontMetrics(new Font("Sans Serif",Font.PLAIN,fontSize)).stringWidth(result)) {
			fontSize--;
		}
		balancedText.setFont(new Font("Sans Serif",Font.PLAIN,fontSize));
		fontSize = balancedText.getFont().getSize();
		while (fontSize < 60 && balancedText.getSize().getWidth() > getFontMetrics(new Font("Sans Serif",Font.PLAIN,fontSize+1)).stringWidth(result)) {
			fontSize++;
		}
		balancedText.setFont(new Font("Sans Serif",Font.PLAIN,fontSize));
		fontSize = balancedText.getFont().getSize();
		balancedText.setText(result);
	}
}

class CheckBoxListener implements ItemListener {
	int row;
	BalancerView b;
	StoichiometryCalc s;
	
	CheckBoxListener(int row, BalancerView b) {
		this.row = row;
		this.b = b;
		s = b.getStoichiometryCalc();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		JCheckBox checkbox = (JCheckBox) e.getSource();
		if(checkbox.isSelected()) {
			s.setExcess(row, true);
		} else {
			s.setExcess(row, false);
		}
		s.updateProducts();
		b.updateTables();
	}
	
}

class StoichiometryListener implements DocumentListener {
	private Type type;
	private int row; //which term
	private int col; //moles or mass? 0 = moles; 1 = mass
	private BalancerView b;
	private StoichiometryCalc s;
	private JTextField source;
	
	StoichiometryListener(Type type, int row, int col, BalancerView b, JTextField source) {
		this.type = type;
		this.row = row-1; //subtract 1 to account for header columns
		this.col = col-1;
		this.b = b;
		s = b.getStoichiometryCalc();
		this.source = source;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {		
		// TODO Auto-generated method stub
		if(source.hasFocus()) {
			manageText();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		if(source.hasFocus()){
			manageText();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
	}
	
	private void manageText() {
		String text = source.getText();
		boolean validNumber = true;
		
		try {
			Double.valueOf(text);
		} catch(NumberFormatException exc) {
			validNumber = false;
		}
		
		if(validNumber) {
			if(col == 0) { //moles
				s.setMol(text, type, row);
			} else if(col == 1) { //mass
				s.setMass(text, type, row);
			}
			if(type == Type.REACTANT) {
				s.updateProducts();
			} else if(type == Type.PRODUCT) {
				s.updateFromProduct(row);
			}
			b.updateTables();
		}
	}
	
}

class EqnListener implements DocumentListener {
	private JTextField source;
	
	EqnListener(JTextField source) {
		this.source = source;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable(){
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String text = source.getText();
			String originalText = text;
			text = text.replaceAll("([-]+)>","\u2192");
			text = text.replaceAll("=", "\u2192");
			
			StringBuffer t = new StringBuffer(text);
			
			while(true) {
				Matcher subscripts = Pattern.compile("([A-Za-z][0-9]+)|(\\)[0-9]+)").matcher(t);
				if(!subscripts.find()) {
					break;
				}
				String numbers = t.substring(subscripts.start()+1, subscripts.end());
				numbers = numbers.replaceAll("1", "\u2081");
				numbers = numbers.replaceAll("2", "\u2082");
				numbers = numbers.replaceAll("3", "\u2083");
				numbers = numbers.replaceAll("4", "\u2084");
				numbers = numbers.replaceAll("5", "\u2085");
				numbers = numbers.replaceAll("6", "\u2086");
				numbers = numbers.replaceAll("7", "\u2087");
				numbers = numbers.replaceAll("8", "\u2088");
				numbers = numbers.replaceAll("9", "\u2089");
				numbers = numbers.replaceAll("0", "\u2080");
				t.replace(subscripts.start()+1, subscripts.end(), numbers);
			}
			text = t.toString();
			
			if(!text.equals(originalText)) {
				source.setText(text);
			}
		}
		
		});
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String text = source.getText();
				String originalText = text;
				text = text.replaceAll("([-]+)>","\u2192");
				text = text.replaceAll("=", "\u2192");
				
				StringBuffer t = new StringBuffer(text);
				
				while(true) {
					Matcher subscripts = Pattern.compile("([A-Za-z][0-9]+)|(\\)[0-9]+)").matcher(t);
					if(!subscripts.find()) {
						break;
					}
					String numbers = t.substring(subscripts.start()+1, subscripts.end());
					numbers = numbers.replaceAll("1", "\u2081");
					numbers = numbers.replaceAll("2", "\u2082");
					numbers = numbers.replaceAll("3", "\u2083");
					numbers = numbers.replaceAll("4", "\u2084");
					numbers = numbers.replaceAll("5", "\u2085");
					numbers = numbers.replaceAll("6", "\u2086");
					numbers = numbers.replaceAll("7", "\u2087");
					numbers = numbers.replaceAll("8", "\u2088");
					numbers = numbers.replaceAll("9", "\u2089");
					numbers = numbers.replaceAll("0", "\u2080");
					t.replace(subscripts.start()+1, subscripts.end(), numbers);
				}
				text = t.toString();
				
				if(!text.equals(originalText)) {
					source.setText(text);
				}
			}
			
			});
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

class TextFieldListener implements KeyListener {
	private JTextField text;
	private Component parent;
	
	TextFieldListener(JTextField text, Component parent) {
		this.text = text;
		this.parent = parent;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char c = e.getKeyChar();
		StringBuffer currentText = new StringBuffer(text.getText());
		if(!Character.isDigit(c) && !Character.isAlphabetic(c) && c != '+' && c != '-' && c != '^' && c != '=' && c != '>' && !Character.isSpaceChar(c) && c != '(' && c != ')') {
			e.consume();
		} else {
			int fontSize = text.getFont().getSize();
			while(fontSize > 1 && text.getSize().getWidth() < parent.getFontMetrics(new Font("Sans Serif",Font.PLAIN,fontSize)).stringWidth(currentText.toString() + c)) {
				fontSize--;
			}
			text.setFont(new Font("Sans Serif",Font.PLAIN,fontSize));
			fontSize = text.getFont().getSize();
			while (fontSize < 60 && text.getSize().getWidth() > parent.getFontMetrics(new Font("Sans Serif",Font.PLAIN,fontSize+1)).stringWidth(currentText.toString() + c)) {
				fontSize++;
			}
			text.setFont(new Font("Sans Serif",Font.PLAIN,fontSize));
			fontSize = text.getFont().getSize();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 8) {
			StringBuffer currentText = new StringBuffer(text.getText());
			int caret = text.getCaretPosition();
			String after;
			if(caret != 0) {
				after = currentText.deleteCharAt(caret-1).toString();
			} else {
				after = currentText.toString();
			}
			if(after.contains("->")) {
				e.consume();
				after = after.replaceAll("([-]+)>", "\u2192");
				text.setText(after);
			}
			int fontSize = text.getFont().getSize();
			while (fontSize < 60 && text.getSize().getWidth() > parent.getFontMetrics(new Font("Sans Serif",Font.PLAIN,fontSize+1)).stringWidth(currentText.toString())) {
				fontSize++;
			}
			text.setFont(new Font("Sans Serif",Font.PLAIN,fontSize));
			fontSize = text.getFont().getSize();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	
}

//Possibly the most disgusting thing I have ever written
/*class TextPlusComboBox extends JComponent {
	
	
	private static final long serialVersionUID = -3292723970046573549L;

	TextPlusComboBox(JTextField text, BalancerComboBox box) {
		super();
		this.setLayout(new GridBagLayout());
		text.setAlignmentX(Component.LEFT_ALIGNMENT);
		box.setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		text.setEnabled(false);
		text.setDisabledTextColor(Color.black);
		add(text,c);
		add(box,c);
		box.setMaximumSize(new Dimension(100,box.getMaximumSize().height));
		box.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	}
}*/
