package controller.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class BalancerComboBox extends JComboBox<String> implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String[] occursInOptions = {"a vacuum","acidic solution","basic solution","neutral solution"};
	private BalancerView view;
	
	BalancerComboBox(BalancerView view){
		super(occursInOptions);
		setSelectedIndex(0);
		addActionListener(this);
		this.view = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		 @SuppressWarnings("unchecked")
		 JComboBox<String> cb = (JComboBox<String>) e.getSource();
	     String selectedItem = (String) cb.getSelectedItem();
	     
	     String[] occursIn;
	     switch(selectedItem) {
	     case "a vacuum":
	    	 occursIn = new String[]{};
	    	 view.setOccursIn(occursIn);
	    	 break;
	     case "acidic solution":
	    	 occursIn = new String[]{"H2O","H^+"};
	    	 view.setOccursIn(occursIn);
	    	 break;
	     case "basic solution":
	    	 occursIn = new String[]{"H2O","OH^-"};
	    	 view.setOccursIn(occursIn);
	    	 break;
	     case "neutral solution":
	    	 occursIn = new String[]{"H2O"};
	    	 view.setOccursIn(occursIn);
	    	 System.out.println(occursIn[0]);
	    	 break;
	     default:
	    	 occursIn = new String[]{};
	    	 view.setOccursIn(occursIn);
	    	 break;
	     }
	}

}
