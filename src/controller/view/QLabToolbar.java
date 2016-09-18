package controller.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class QLabToolbar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3523375739018046009L;
	private QLabView parent;
	
	QLabToolbar(QLabView parent) {
		setPreferredSize(new Dimension(1300,50));
		setFloatable(false);
		setBorderPainted(true);
		//setLayout(new GridLayout(0,20));
		addButtons();
		this.parent = parent;
	}
	
	private void addButtons() {
		JButton ptableButton = new JButton("Periodic Table ");
		JButton balanceButton = new JButton("Balancing Equations ");
		JButton compoundInfoButton = new JButton("Chemical Compounds ");
		ptableButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setColor(ptableButton);
				parent.setFrame(0);
			}
			
		});
		
		balanceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setColor(balanceButton);
				parent.setFrame(1);
			}
			
		});
		
		compoundInfoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				setColor(compoundInfoButton);
				parent.setFrame(2);
			}
			
		});
		Image balanceIcon = null;
		Image ptableIcon = null;
		Image compoundInfoIcon = null;
		try {
		    balanceIcon = ImageIO.read(getClass().getResourceAsStream("balance.png"));
		    ptableIcon = ImageIO.read(getClass().getResourceAsStream("hydrogen.png"));
		    compoundInfoIcon = ImageIO.read(getClass().getResourceAsStream("h2o.png"));
		} catch (IOException e) {
		}
		ptableButton.setIcon(new ImageIcon(ptableIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		balanceButton.setIcon(new ImageIcon(balanceIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		compoundInfoButton.setIcon(new ImageIcon(compoundInfoIcon.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
		add(ptableButton);
		add(balanceButton);
		add(compoundInfoButton);
	}
	
	private void setColor(JButton button) {
		for(Component j : getComponents()) {
			if(!j.equals(button)) {
				j.setBackground(new Color(200,200,200));
			}
		}
		button.setBackground(new Color(240,240,240));
	}
}
