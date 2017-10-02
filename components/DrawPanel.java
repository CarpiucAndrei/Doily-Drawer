package components;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.JPanel;

public class DrawPanel extends JPanel 
{
	public DrawComponent drawComponent = new DrawComponent(this);;

	public DrawPanel() {
		setLayout(new GridLayout(1, 1));
		add(drawComponent);
	}

	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
}
