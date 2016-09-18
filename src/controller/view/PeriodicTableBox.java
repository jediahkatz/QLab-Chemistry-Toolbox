package controller.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

import controller.model.PeriodicTableData;
import controller.model.RoundDecimal;

//Creates a JPanel with a box that listens for mouse events
//This is essentially a visual element block on the table
public class PeriodicTableBox extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3070727255534786488L;
	private int atomicNumber;
	private GridLayout textLayout = new GridLayout(3,1);
	private GridBagConstraints constraints = new GridBagConstraints();
	private PeriodicTableElementInfo bigElement;
	
	PeriodicTableBox(int atomicNumber, PeriodicTableData data, PeriodicTableElementInfo bigElement){
		this.bigElement=bigElement;
		this.atomicNumber = atomicNumber;
		setLayout(textLayout);
		Color color = Color.lightGray;
		//int width = 65,height = 65;
		setBackground(color);
		//Create, center, and add text for the atomic number, symbol, and mass
		JLabel atomicNumberLabel = new JLabel(String.valueOf(atomicNumber));
		atomicNumberLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel symbolLabel = new JLabel(data.getProperty(atomicNumber,PeriodicTableData.SYMBOL));
		symbolLabel.setHorizontalAlignment(JLabel.CENTER);
		symbolLabel.setFont(new Font("Cambria",1,25));
		//round molar mass to 2 decimal places
		JLabel molarMassLabel;
		try {
			double roundedMolarMass = RoundDecimal.round(Double.parseDouble(data.getProperty(atomicNumber, PeriodicTableData.ATOMIC_MASS)), 2);
			molarMassLabel = new JLabel(String.valueOf(roundedMolarMass));
		} catch (NumberFormatException e) {
			molarMassLabel = new JLabel(data.getProperty(atomicNumber, PeriodicTableData.ATOMIC_MASS));
		}
		molarMassLabel.setHorizontalAlignment(JLabel.CENTER);
		constraints.gridy = 0;
		add(atomicNumberLabel,constraints);
		constraints.gridy = 1;
        add(symbolLabel,constraints);
        constraints.gridy = 2;
        add(molarMassLabel,constraints);
        addMouseListener(this);
        //setPreferredSize(new Dimension(width, height));
        setBorder(BorderFactory.createLineBorder(Color.white));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		setBorder(BorderFactory.createLineBorder(Color.black));
		bigElement.setAtomicNumber(atomicNumber);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		setBorder(BorderFactory.createLineBorder(Color.white));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub		
	}

}
