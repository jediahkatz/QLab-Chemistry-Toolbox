package controller.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.xswingx.PromptSupport;

import controller.model.CompoundInfo;
import controller.model.PeriodicTableData;
import net.miginfocom.swing.MigLayout;

public class CompoundInfoView extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -164320260546870763L;
	//private BoxLayout compoundInfoBoxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
	private MigLayout compoundInfoBoxLayout = new MigLayout();
	//private JTextField compound = new JTextField("");
	private JTextField compound = new JTextField("");
	private JTextArea properties = new JTextArea("");
	private JTable composition = new JTable();
	private JButton getInfoButton = new JButton("Get Compound Info");
	private PeriodicTableData data;
	private JPanel tpanel = new JPanel();

	public CompoundInfoView(PeriodicTableData data) {
		this.data = data;
		compound.setFont(new Font("Sans Serif", Font.PLAIN, 60));
		compound.setMaximumSize(new Dimension(10000, 100));
		compound.setPreferredSize(compound.getMaximumSize());
		compound.setAlignmentX(LEFT_ALIGNMENT);
		PromptSupport.setPrompt("Chemical compound to analyze", compound);
		properties.setAlignmentX(LEFT_ALIGNMENT);
		composition.setAlignmentX(LEFT_ALIGNMENT);
		compound.addKeyListener(new TextListener(compound, this));
		getInfoButton.setAlignmentX(LEFT_ALIGNMENT);
		getInfoButton.addActionListener(this);
		properties.setEditable(false);
		properties.setFont(new Font("Sans Serif", Font.PLAIN, 30));
		properties.setPreferredSize(new Dimension(10000, 300));
		composition.setFont(new Font("Sans Serif", Font.PLAIN, 18));
		tpanel.setLayout(new GridLayout());
		//tpanel.setPreferredSize(new Dimension(10000,0));
		tpanel.setAlignmentX(LEFT_ALIGNMENT);
		tpanel.setAlignmentY(TOP_ALIGNMENT);
		properties.setOpaque(false);
		tpanel.setOpaque(false);
		createCompoundInfoView();
	}

	public void createCompoundInfoView() {
		setLayout(compoundInfoBoxLayout);
		add(compound, "span");
		//add(Box.createVerticalStrut(10));
		add(getInfoButton,"wrap 10");
		//add(Box.createVerticalStrut(10));
		add(properties,"top");//,"span, grow 101, shrinky 1");
		//add(Box.createVerticalStrut(10));
		add(tpanel,"top");//,"span, shrink 150");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cpd = compound.getText();
		cpd = cpd.replace("\u2192", "=");
		cpd = cpd.replace("\u207A", "+");
		cpd = cpd.replace("\u207B", "-");
		
		cpd = cpd.replace("\u2081", "1");
		cpd = cpd.replace("\u2082", "2");
		cpd = cpd.replace("\u2083", "3");
		cpd = cpd.replace("\u2084", "4");
		cpd = cpd.replace("\u2085", "5");
		cpd = cpd.replace("\u2086", "6");
		cpd = cpd.replace("\u2087", "7");
		cpd = cpd.replace("\u2088", "8");
		cpd = cpd.replace("\u2089", "9");
		cpd = cpd.replace("\u2080", "0");
		
		cpd = cpd.replace("\u00B9", "1");
		cpd = cpd.replace("\u00B2", "2");
		cpd = cpd.replace("\u00B3", "3");
		cpd = cpd.replace("\u2074", "4");
		cpd = cpd.replace("\u2074", "4");
		cpd = cpd.replace("\u2075", "5");
		cpd = cpd.replace("\u2076", "6");
		cpd = cpd.replace("\u2077", "7");
		cpd = cpd.replace("\u2078", "8");
		cpd = cpd.replace("\u2079", "9");
		try {
			CompoundInfo compoundData = new CompoundInfo(data, cpd);
			if (!compoundData.isValid()) {
				properties.setText("Please try again with a valid compound or element.");
				tpanel.removeAll();
				//tpanel.add(new JPanel());
			} else if (!compoundData.canExist()) {
				properties.setText("This compound could not exist!");
				tpanel.removeAll();
			} else {
				if (compoundData.numAtoms() > 1) {
					String molarMass = compoundData.molarMass();
					String typeOfBond = compoundData.typeOfBond();
					String name = compoundData.getName();
					if(name == null) {
						properties.setText("Type of bonding: " + typeOfBond
								+ "\nMolar mass: " + molarMass + " g/mol\n");
					} else {
					properties.setText(name 
							+ "\nType of bonding: " + typeOfBond
							+ "\nMolar mass: " + molarMass + " g/mol\n");
					}
					composition = new JTable(new TableModel(compoundData));
					composition.setFont(new Font("Sans Serif", Font.PLAIN, 20));
					composition.setRowHeight(55);
					JScrollPane tablePanel = new JScrollPane(composition);
					tablePanel.setPreferredSize(
							new Dimension(10000, 27 + (55 * composition.getRowCount())));
					tpanel.removeAll();
					tpanel.add(tablePanel);
					removeAll();
					createCompoundInfoView();//TODO: wtf
				} else {
					tpanel.removeAll();
					int number = data.getAtomicNumber(cpd);
					String name = data.getProperty(number, PeriodicTableData.NAME);
					String atomicMass = data.getProperty(number, PeriodicTableData.ATOMIC_MASS);
					String yearDiscovered = data.getProperty(number, PeriodicTableData.YEAR_DISCOVERED);
					String series = data.getProperty(number, PeriodicTableData.TYPE_OF_ELEMENT);
					String[] statesArray = data.getOxidationStates(number);
					String states = "";
					for (int i = 0; i < statesArray.length; i++) {
						states += statesArray[i];
						if (i < statesArray.length - 1)
							states += ", ";
					}
					if (states.equals(""))
						states = "unknown";
					switch (series) {
					case "alkalineearthmetal":
						series = "alkaline earth metal";
						break;
					case "transitionmetal":
						series = "transition metal";
						break;
					case "noblegas":
						series = "noble gas";
						break;
					case "alkalimetal":
						series = "alkali metal";
						break;
					case "metal":
						series = "post-transition metal";
						break;
					case "nonmetal":
						series = "other nonmetal";
						break;
					}
					properties.setText(name + "\nAtomic number: " + number + "\n" + series.substring(0, 1).toUpperCase()
							+ series.substring(1) + " series\n" + "Atomic mass: " + atomicMass + " g/mol\n"
							+ "Discovered: " + yearDiscovered + "\nPossible oxidation states: " + states);
				}
			}
		} catch (IllegalArgumentException f) {
			properties.setText("Please enter a compound to analyze!");
			tpanel.removeAll();
		}
	}
}

class TableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2558120209899157917L;
	private final String[] columnNames = { "Element", "Atomic Number", "Molar Mass (g/mol)", "Number of Atoms",
			"Total Mass (g)", "Mass Percentage" };
	private String[][] table;

	TableModel(CompoundInfo compoundData) {
		table = compoundData.getTable();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return table.length;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return table[rowIndex][columnIndex];
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

}

class TextListener implements KeyListener {
	private JTextField text;
	private Component parent;

	TextListener(JTextField text, Component parent) {
		this.text = text;
		this.parent = parent;
	}

	private void addUnicode(String str, JTextField text, StringBuffer currentText, int caret) {
		text.setText(currentText.insert(caret, str).toString());
		text.setCaretPosition(caret + 1);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char c = e.getKeyChar();
		StringBuffer currentText = new StringBuffer(text.getText());
		int caret = text.getCaretPosition();
		if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '(' && c != ')') {
			e.consume();
		} else {
			if (text.getSize().getWidth() - 5 < parent.getFontMetrics(text.getFont())
					.stringWidth(currentText.toString() + c)) {
				e.consume();
			}
		}
		if (Character.isDigit(c) && caret == 0) {
			e.consume();
		} else if (Character.isDigit(c)) {
			e.consume();
			switch (c) {
			case '0':
				addUnicode("\u2080", text, currentText, caret);
				break;
			case '1':
				addUnicode("\u2081", text, currentText, caret);
				break;
			case '2':
				addUnicode("\u2082", text, currentText, caret);
				break;
			case '3':
				addUnicode("\u2083", text, currentText, caret);
				break;
			case '4':
				addUnicode("\u2084", text, currentText, caret);
				break;
			case '5':
				addUnicode("\u2085", text, currentText, caret);
				break;
			case '6':
				addUnicode("\u2086", text, currentText, caret);
				break;
			case '7':
				addUnicode("\u2087", text, currentText, caret);
				break;
			case '8':
				addUnicode("\u2088", text, currentText, caret);
				break;
			case '9':
				addUnicode("\u2089", text, currentText, caret);
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
