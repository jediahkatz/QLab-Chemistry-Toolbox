package controller.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.model.PeriodicTableData;

public class QLabView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1188394279870427848L;
	private JFrame QLabFrame;
	private PeriodicTableView pTable;
	private BalancerView balancer;
	private CompoundInfoView compoundInfo;
	private QLabToolbar toolbar;
	private int frame = 0;
	
	public QLabView(PeriodicTableData data) {
		setLayout(new BorderLayout());
		QLabFrame = new JFrame("QLab");
		pTable = new PeriodicTableView(data);
		balancer = new BalancerView();
		compoundInfo = new CompoundInfoView(data);
		toolbar = new QLabToolbar(this);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

	private void createAndShowGUI() {
		QLabFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		QLabFrame.setMinimumSize(new Dimension(600,400));
		QLabFrame.setPreferredSize(new Dimension(1400,650));
		QLabFrame.pack();
		QLabFrame.setVisible(true);
		QLabFrame.add(this);
		add(pTable,BorderLayout.CENTER);
		add(toolbar,BorderLayout.PAGE_START);
	}
	
	void setFrame(int newFrame) {
		if(frame != newFrame) {
			switch(newFrame) {
			case 0: switchCurrentPanelTo(pTable);
				frame = 0;
				break;
			case 1:
				switchCurrentPanelTo(balancer);
				frame = 1;
				break;
			case 2:
				switchCurrentPanelTo(compoundInfo);
				frame = 2;
				break;
			} 
		}
	}
	
	void switchCurrentPanelTo(JPanel newPanel) { //removes current JPanel and adds specified
		for(Component c : getComponents()) {
			if(!c.equals(toolbar)) {
				remove(c);
			}
		}
		add(newPanel);
		revalidate();
		repaint();
	}
	
}
