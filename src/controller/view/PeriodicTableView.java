package controller.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import controller.model.PeriodicTableData;

public class PeriodicTableView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8577896054377069735L;
	//private JPanel ptableMainTablePanel = new JPanel();
	private GridBagLayout ptableBagLayout = new GridBagLayout();
	private GridBagConstraints constraints = new GridBagConstraints();
	private PeriodicTableData data;
	private PeriodicTableElementInfo bigElement;
	//when rows = 0, layout uses columns to determine grid
	//if rows != 0 then columns field is disregarded
	
	public PeriodicTableView(PeriodicTableData data) {//, JFrame frame) {
		this.data = data;
		//QLabFrame = frame;
		bigElement = new PeriodicTableElementInfo(data);
        createPTable();
	}
	
	private void createPTable() {
		setLayout(ptableBagLayout);
		setBackground(Color.white);
		addElements();
	}
	
	//TODO Figure out how to make all columns the same size
	private void addElements() {
		final int NUMBER_OF_ELEMENTS_EXCLUDING_ORBITAL_F = 90;
		
		PeriodicTableBox[] elements = new PeriodicTableBox[NUMBER_OF_ELEMENTS_EXCLUDING_ORBITAL_F];
		//Excluding the 28 elements in the F orbital
		for(int i=0; i<NUMBER_OF_ELEMENTS_EXCLUDING_ORBITAL_F; i++) {
			if(i <= 56) {
				elements[i] = new PeriodicTableBox(i+1,data,bigElement);
			} else if(i > 56 && i <= 74) {
				elements[i] = new PeriodicTableBox(i+1+14,data,bigElement);
			} else {
				elements[i] = new PeriodicTableBox(i+1+28,data,bigElement);
			}
			//don't worry about the numbers, it works
		}
		
		//blocks expand horizontally and vertically when window is resized
		constraints.fill = GridBagConstraints.BOTH;
		//center? play around with this
		//constraints.anchor = GridBagConstraints.ABOVE_BASELINE;
		//?? don't know what this does but can't take it out
		constraints.weightx = 1;
		constraints.weighty = 1;
		//1 gridwidth = 1 column
		constraints.gridwidth = 1;
		constraints.insets = new Insets(5,5,0,0);
		setConstraints(0,0);
		add(elements[0],constraints); //Hydrogen
		setConstraints(1,0);
		addHole(1);
		setConstraints(5,0);
		addHole(12);
		setConstraints(17,0);
		add(elements[1],constraints); //Helium
		setConstraints(0,1);
		add(elements[2],constraints); //Lithium
		setConstraints(1,1);
		add(elements[3],constraints); //Beryllium
		setConstraints(5,1);
		addHole(7);
		for(int i=4;i<12;i++) {
			int row = (int) Math.ceil((i+1)/10.0); //after the tenth one, make a new row
			int column = (i + 8) - (18*(row-1)); 
			/* Add 8 to account for +10 block hole and -2 elements before 
			 * the hole. Then reset column (subtract 18) each time
			 * there is a new row */
			setConstraints(column, row);
			add(elements[i],constraints); //Boron thru Magnesium
		}
		
		setConstraints(5,2);
		addHole(7);
		for(int i=12;i<90;i++){
			int row = ((int) Math.ceil((i+1)/18.0)) + 1;
			int column = (i+18) - (18*(row-1));
			setConstraints(column, row);
			add(elements[i],constraints); //Aluminum thru Ununoctium
		}
		
		setConstraints(2,0);
		constraints.gridwidth = 3;
		constraints.gridheight = 3;
		add(bigElement, constraints); //Big element block that displays more info about current element
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
	}
	
	//create a "hole" that is n blocks long
	private void addHole(int n) {
		//JPanel panel = ptableMainTablePanel;
		GridBagConstraints c = constraints;
		c.gridwidth = n;
		//setConstraints(gridx,gridy,c);
		add(new EmptyPeriodicTableBox(), constraints);
		c.gridwidth = 1;
	}
	
	//set the row and column of the component
	private void setConstraints(int gridx, int gridy) {
		GridBagConstraints c = constraints;
		c.gridx = gridx;
		c.gridy = gridy;
	}
}
