package controller;
import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import controller.model.*;
import controller.view.*;

public class QLabMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
	            // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();
		defaults.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		PeriodicTableData data = new PeriodicTableData();
		@SuppressWarnings("unused")
		QLabView view = new QLabView(data);
		//@SuppressWarnings("unused")
		//BalancerController controller = new BalancerController(view);
	}

}
