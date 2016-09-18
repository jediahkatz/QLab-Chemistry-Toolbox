package controller.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;

import controller.model.PeriodicTableData;

public class PeriodicTableElementInfo extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6001385167901507380L;
	private int atomicNumber = 1;
	private JLabel atomicNumberLabel;
	private JLabel symbolLabel;
	private JLabel nameLabel;
	private JLabel massLabel;
	private PeriodicTableData data;
	private GridLayout layout = new GridLayout(0,1);
	
	PeriodicTableElementInfo(PeriodicTableData data) {
		this.data=data;
		setLayout(layout);
		Color color = Color.pink;
		setBackground(color);
		atomicNumberLabel = new JLabel(String.valueOf(atomicNumber),SwingConstants.CENTER);
		atomicNumberLabel.setFont(new Font("Sans Serif",Font.PLAIN,25));
		symbolLabel = new JLabel(data.getProperty(atomicNumber, PeriodicTableData.SYMBOL),SwingConstants.CENTER);
		symbolLabel.setFont(new Font("Sans Serif",Font.BOLD,60));
		nameLabel = new JLabel(data.getProperty(atomicNumber, PeriodicTableData.NAME),SwingConstants.CENTER);
		nameLabel.setFont(new Font("Sans Serif",Font.PLAIN,20));
		massLabel = new JLabel(data.getProperty(atomicNumber, PeriodicTableData.ATOMIC_MASS),SwingConstants.CENTER);
		massLabel.setFont(new Font("Sans Serif",Font.PLAIN,20));
		add(atomicNumberLabel);
		add(symbolLabel);
		add(nameLabel);
		add(massLabel);
	}
	
	private void updateInfo() {
		atomicNumberLabel.setText(String.valueOf(atomicNumber));
		symbolLabel.setText(data.getProperty(atomicNumber,PeriodicTableData.SYMBOL));
		nameLabel.setText(data.getProperty(atomicNumber, PeriodicTableData.NAME));
		massLabel.setText(data.getProperty(atomicNumber, PeriodicTableData.ATOMIC_MASS));
	}
	
	void setAtomicNumber(int n) {
		atomicNumber = n;
		updateInfo();
	}

}
