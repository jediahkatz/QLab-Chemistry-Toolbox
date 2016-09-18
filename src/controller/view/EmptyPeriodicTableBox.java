package controller.view;

import java.awt.Color;

//import java.awt.Dimension;

import javax.swing.JPanel;

//Empty JPanel. Placeholder for the "holes" in the periodic table
public class EmptyPeriodicTableBox extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8596142449629759311L;

	//Yeah this is basically just a white JPanel right now
	//But I guess the name helps me remember what it's for
	EmptyPeriodicTableBox() {
		setBackground(Color.white);
		setOpaque(true);
		//int width=65,height=65;
		//setPreferredSize(new Dimension(width,height));
	}
}
